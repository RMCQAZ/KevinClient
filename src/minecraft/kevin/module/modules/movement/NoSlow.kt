package kevin.module.modules.movement

import kevin.event.*
import kevin.main.KevinClient
import kevin.module.*
import kevin.module.modules.combat.KillAura
import kevin.utils.MSTimer
import kevin.utils.MovementUtils
import kevin.utils.PacketUtils
import net.minecraft.item.*
import net.minecraft.network.Packet
import net.minecraft.network.play.INetHandlerPlayServer
import net.minecraft.network.play.client.*
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import java.util.*

class NoSlow : Module("NoSlow", "Cancels slowness effects caused by soulsand and using items.", category = ModuleCategory.MOVEMENT) {

    private val blockForwardMultiplier = FloatValue("BlockForwardMultiplier", 1.0F, 0.2F, 1.0F)
    private val blockStrafeMultiplier = FloatValue("BlockStrafeMultiplier", 1.0F, 0.2F, 1.0F)

    private val consumeForwardMultiplier = FloatValue("ConsumeForwardMultiplier", 1.0F, 0.2F, 1.0F)
    private val consumeStrafeMultiplier = FloatValue("ConsumeStrafeMultiplier", 1.0F, 0.2F, 1.0F)

    private val bowForwardMultiplier = FloatValue("BowForwardMultiplier", 1.0F, 0.2F, 1.0F)
    private val bowStrafeMultiplier = FloatValue("BowStrafeMultiplier", 1.0F, 0.2F, 1.0F)

    private val packetMode = ListValue("PacketMode", arrayOf("None","AntiCheat","AntiCheat2","AAC","AAC5","Matrix","Vulcan"),"None")

    val soulsandValue = BooleanValue("Soulsand", true)
    val liquidPushValue = BooleanValue("LiquidPush", true)

    private var lastBlockingStat = false
    private var nextTemp = false
    private var waitC03 = false
    private val msTimer = MSTimer()
    private var packetBuf = LinkedList<Packet<INetHandlerPlayServer>>()
    private val isBlocking: Boolean
        get() = (mc.thePlayer.isUsingItem || (KevinClient.moduleManager.getModule(KillAura::class.java)).blockingStatus) && mc.thePlayer.heldItem != null && mc.thePlayer.heldItem.item is ItemSword

    override fun onDisable() {
        msTimer.reset()
        nextTemp = false
        waitC03 = false
        packetBuf.clear()
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        val thePlayer = mc.thePlayer ?: return
        val heldItem = thePlayer.heldItem ?: return

        if ((heldItem.item) !is ItemSword || !MovementUtils.isMoving)
            return

        val aura = KevinClient.moduleManager.getModule(KillAura::class.java)
        if (!thePlayer.isBlocking && !aura.blockingStatus)
            return

        when(packetMode.get()){
            "AntiCheat" -> {
                when (event.eventState) {
                    EventState.PRE -> {
                        val digging = C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos(0, 0, 0), EnumFacing.DOWN)
                        mc.netHandler.addToSendQueue(digging)
                    }
                    EventState.POST -> {
                        val blockPlace = C08PacketPlayerBlockPlacement(BlockPos(-1, -1, -1), 255, mc.thePlayer!!.inventory.getCurrentItem(), 0.0F, 0.0F, 0.0F)
                        mc.netHandler.addToSendQueue(blockPlace)
                    }
                }
            }
            "AntiCheat2" -> {
                if (event.eventState == EventState.PRE) {
                    mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN))
                } else {
                    mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(BlockPos(-1, -1, -1), 255, null, 0.0f, 0.0f, 0.0f))
                }
            }
            "AAC" -> {
                if (mc.thePlayer.ticksExisted % 3 == 0 && event.eventState == EventState.PRE) {
                    mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos(-1, -1, -1), EnumFacing.DOWN))
                } else if (mc.thePlayer.ticksExisted % 3 == 1 && event.eventState == EventState.POST) {
                    C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem())
                }
            }
            "AAC5" -> {
                if (event.eventState == EventState.POST && (mc.thePlayer.isUsingItem || mc.thePlayer.isBlocking || aura.blockingStatus)) {
                    mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0f, 0f, 0f))
                }
            }
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if(mc.thePlayer == null || mc.theWorld == null)
            return
        if((packetMode equal "Matrix" || packetMode equal "Vulcan") && (lastBlockingStat || isBlocking)) {
            if(msTimer.hasTimePassed(230) && nextTemp) {
                nextTemp = false
                PacketUtils.sendPacketNoEvent(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos(-1, -1, -1), EnumFacing.DOWN))
                if(packetBuf.isNotEmpty()) {
                    var canAttack = false
                    for(packet in packetBuf) {
                        if(packet is C03PacketPlayer) {
                            canAttack = true
                        }
                        if(!((packet is C02PacketUseEntity || packet is C0APacketAnimation) && !canAttack)) {
                            PacketUtils.sendPacketNoEvent(packet)
                        }
                    }
                    packetBuf.clear()
                }
            }
            if(!nextTemp) {
                lastBlockingStat = isBlocking
                if (!isBlocking) {
                    return
                }
                PacketUtils.sendPacketNoEvent(C08PacketPlayerBlockPlacement(BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0f, 0f, 0f))
                nextTemp = true
                waitC03 = packetMode equal "Vulcan"
                msTimer.reset()
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if(mc.thePlayer == null || mc.theWorld == null)
            return
        val packet = event.packet
        if((packetMode equal "Matrix" || packetMode equal "Vulcan") && nextTemp) {
            if((packet is C07PacketPlayerDigging || packet is C08PacketPlayerBlockPlacement) && isBlocking) {
                event.cancelEvent()
            }else if (packet is C03PacketPlayer || packet is C0APacketAnimation || packet is C0BPacketEntityAction || packet is C02PacketUseEntity || packet is C07PacketPlayerDigging || packet is C08PacketPlayerBlockPlacement) {
                if (packetMode equal "Vulcan" && waitC03 && packet is C03PacketPlayer) {
                    waitC03 = false
                    return
                }
                packetBuf.add(packet as Packet<INetHandlerPlayServer>)
                event.cancelEvent()
            }
        }
    }

    override val tag: String
        get() = packetMode.get()

    @EventTarget
    fun onSlowDown(event: SlowDownEvent) {
        val heldItem = mc.thePlayer!!.heldItem?.item

        event.forward = getMultiplier(heldItem, true)
        event.strafe = getMultiplier(heldItem, false)
    }

    private fun getMultiplier(item: Item?, isForward: Boolean): Float {
        return when {
            (item)is ItemFood || (item)is ItemPotion || (item)is ItemBucketMilk -> {
                if (isForward) this.consumeForwardMultiplier.get() else this.consumeStrafeMultiplier.get()
            }
            (item)is ItemSword -> {
                if (isForward) this.blockForwardMultiplier.get() else this.blockStrafeMultiplier.get()
            }
            (item)is ItemBow -> {
                if (isForward) this.bowForwardMultiplier.get() else this.bowStrafeMultiplier.get()
            }
            else -> 0.2F
        }
    }
}