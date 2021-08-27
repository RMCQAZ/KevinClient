package kevin.module.modules.player

import kevin.event.*
import kevin.main.Kevin
import kevin.module.ListValue
import kevin.module.Module
import kevin.module.ModuleCategory
import kevin.utils.BlockUtils.collideBlock
import kevin.utils.TickTimer
import net.minecraft.block.BlockLiquid
import net.minecraft.client.Minecraft
import net.minecraft.network.Packet
import net.minecraft.network.play.INetHandlerPlayServer
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.util.*

class NoFall : Module("NoFall","Prevents you from taking fall damage.", category = ModuleCategory.PLAYER) {
    @JvmField
    val modeValue = ListValue("Mode", arrayOf("SpoofGround", "NoGround", "Packet", "AAC", "LAAC", "AAC3.3.11", "AAC3.3.15", "Spartan", "CubeCraft", "Hypixel", "Test1", "Test2"), "SpoofGround")
    private val spartanTimer = TickTimer()
    private var currentState = 0
    private var jumped = false

    @EventTarget(ignoreCondition = true)
    fun onUpdate(event: UpdateEvent?) {
        if (event!!.eventState == UpdateState.OnUpdate) return
        if (mc.thePlayer!!.onGround)
            jumped = false

        if (mc.thePlayer!!.motionY > 0)
            jumped = true

        if (!this.getToggle() || Kevin.getInstance.moduleManager.getModule("FreeCam")!!.getToggle())
            return

        if (collideBlock(mc.thePlayer!!.entityBoundingBox, fun(block: Any?) = block is BlockLiquid) ||
            collideBlock(AxisAlignedBB(mc.thePlayer!!.entityBoundingBox.maxX, mc.thePlayer!!.entityBoundingBox.maxY, mc.thePlayer!!.entityBoundingBox.maxZ, mc.thePlayer!!.entityBoundingBox.minX, mc.thePlayer!!.entityBoundingBox.minY - 0.01, mc.thePlayer!!.entityBoundingBox.minZ), fun(block: Any?) = block is BlockLiquid))
            return

        when (modeValue.get().toLowerCase()) {
            "test2" -> {
                if(mc.thePlayer.fallDistance > 2.9 ) {
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true))
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false))
                    mc.timer.timerSpeed = 0.59647625F
                    mc.thePlayer.motionY *=0.62515425
                }
            }
            "packet" -> {
                if (mc.thePlayer!!.fallDistance > 2f) {
                    mc.netHandler.addToSendQueue(C03PacketPlayer(true))
                }
            }
            "cubecraft" -> if (mc.thePlayer!!.fallDistance > 2f) {
                mc.thePlayer!!.onGround = false
                mc.thePlayer!!.sendQueue.addToSendQueue(C03PacketPlayer(true))
            }
            "aac" -> {
                if (mc.thePlayer!!.fallDistance > 2f) {
                    mc.netHandler.addToSendQueue(C03PacketPlayer(true))
                    currentState = 2
                } else if (currentState == 2 && mc.thePlayer!!.fallDistance < 2) {
                    mc.thePlayer!!.motionY = 0.1
                    currentState = 3
                    return
                }
                when (currentState) {
                    3 -> {
                        mc.thePlayer!!.motionY = 0.1
                        currentState = 4
                    }
                    4 -> {
                        mc.thePlayer!!.motionY = 0.1
                        currentState = 5
                    }
                    5 -> {
                        mc.thePlayer!!.motionY = 0.1
                        currentState = 1
                    }
                }
            }
            "laac" -> if (!jumped && mc.thePlayer!!.onGround && !mc.thePlayer!!.isOnLadder && !mc.thePlayer!!.isInWater
                && !mc.thePlayer!!.isInWeb) mc.thePlayer!!.motionY = (-6).toDouble()
            "aac3.3.11" -> if (mc.thePlayer!!.fallDistance > 2) {
                mc.thePlayer!!.motionZ = 0.0
                mc.thePlayer!!.motionX = mc.thePlayer!!.motionZ
                mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer!!.posX,
                    mc.thePlayer!!.posY - 10E-4, mc.thePlayer!!.posZ, mc.thePlayer!!.onGround))
                mc.netHandler.addToSendQueue(C03PacketPlayer(true))
            }
            "aac3.3.15" -> if (mc.thePlayer!!.fallDistance > 2) {
                if (!mc.isIntegratedServerRunning) mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer!!.posX, Double.NaN, mc.thePlayer!!.posZ, false))
                mc.thePlayer!!.fallDistance = (-9999).toFloat()
            }
            "spartan" -> {
                spartanTimer.update()
                if (mc.thePlayer!!.fallDistance > 1.5 && spartanTimer.hasTimePassed(10)) {
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer!!.posX,
                        mc.thePlayer!!.posY + 10, mc.thePlayer!!.posZ, true))
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer!!.posX,
                        mc.thePlayer!!.posY - 10, mc.thePlayer!!.posZ, true))
                    spartanTimer.reset()
                }
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        val mode = modeValue.get()
        if (packet is C03PacketPlayer) {
            if (mode.equals("SpoofGround", ignoreCase = true)) packet.onGround = true
            if (mode.equals("NoGround", ignoreCase = true)) packet.onGround = false
            if (mode.equals("Hypixel", ignoreCase = true)
                && mc.thePlayer != null && mc.thePlayer!!.fallDistance > 1.5) packet.onGround =
                mc.thePlayer!!.ticksExisted % 2 == 0
            if (mode.equals("Test1",ignoreCase = true)){
                if (
                    mc.thePlayer!!.capabilities.isFlying ||
                    Minecraft.getMinecraft().thePlayer.capabilities.disableDamage ||
                    mc.thePlayer!!.motionY >= 0.0 ||
                    testPackets.contains(packet) ||
                    testPackets.contains(packet)
                ) return
                if (packet.isMoving) {
                    if (mc.thePlayer!!.fallDistance > 2.0f && isBlockUnder()) {
                        event.cancelEvent()
                        sendPacketNoEvent(
                            C03PacketPlayer.C04PacketPlayerPosition(
                                packet.x,
                                packet.y,
                                packet.z,
                                packet.onGround
                            )
                        )
                    }
                }
            }
        }
    }

    override fun onDisable() {
        testPackets.clear()
        if (modeValue.get().equals("Test2",true)) mc.timer.timerSpeed = 1F
    }

    @EventTarget
    fun onMove(event: MoveEvent) {
        if (collideBlock(mc.thePlayer!!.entityBoundingBox, fun(block: Any?) = block is BlockLiquid) || collideBlock(AxisAlignedBB(mc.thePlayer!!.entityBoundingBox.maxX, mc.thePlayer!!.entityBoundingBox.maxY, mc.thePlayer!!.entityBoundingBox.maxZ, mc.thePlayer!!.entityBoundingBox.minX, mc.thePlayer!!.entityBoundingBox.minY - 0.01, mc.thePlayer!!.entityBoundingBox.minZ), fun(block: Any?) = block is BlockLiquid))
            return

        if (modeValue.get().equals("laac", ignoreCase = true)) {
            if (!jumped && !mc.thePlayer!!.onGround && !mc.thePlayer!!.isOnLadder && !mc.thePlayer!!.isInWater && !mc.thePlayer!!.isInWeb && mc.thePlayer!!.motionY < 0.0) {
                event.x = 0.0
                event.z = 0.0
            }
        }
    }

    private val testPackets = arrayListOf<Packet<INetHandlerPlayServer>>()

    private fun sendPacketNoEvent(packet: Packet<INetHandlerPlayServer>){
        testPackets.add(packet)
        mc.netHandler.addToSendQueue(packet)
    }

    private fun isBlockUnder(): Boolean{
        for (y in 0..((mc.thePlayer?.posY?:return false) + (mc.thePlayer?.eyeHeight ?:return false)).toInt()){
            val boundingBox = mc.thePlayer!!.entityBoundingBox.offset(0.0,-y.toDouble(),0.0)
            if (mc.theWorld!!.getCollidingBoundingBoxes(mc.thePlayer!!,boundingBox).isNotEmpty()) return true
        }
        return false
    }

    @EventTarget
    private fun onMotionUpdate(event: MotionEvent) {
        if (modeValue.get() == "Test1" && event.eventState == EventState.PRE){
            if (
                mc.thePlayer!!.capabilities.isFlying ||
                Minecraft.getMinecraft().thePlayer.capabilities.disableDamage ||
                mc.thePlayer!!.motionY >= 0.0
            ) return
            if (mc.thePlayer!!.fallDistance > 3.0f)
                if (isBlockUnder())
                    sendPacketNoEvent(C03PacketPlayer(true))
        }
    }

    @EventTarget(ignoreCondition = true)
    fun onJump(event: JumpEvent?) {
        jumped = true
    }

    override val tag: String
        get() = modeValue.get()
}