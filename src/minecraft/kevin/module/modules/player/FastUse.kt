package kevin.module.modules.player

import kevin.event.EventTarget
import kevin.event.MoveEvent
import kevin.event.UpdateEvent

//import kevin.event.UpdateState

import kevin.module.*
import kevin.utils.MSTimer
import kevin.utils.RotationUtils
import net.minecraft.item.ItemBow
import net.minecraft.item.ItemBucketMilk
import net.minecraft.item.ItemFood
import net.minecraft.item.ItemPotion
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing

class FastUse : Module("FastUse", "Allows you to use items faster.", category = ModuleCategory.PLAYER) {
    private val modeValue = ListValue("Mode", arrayOf("Instant", "NCP", "AAC", "Custom"), "NCP")

    private val noMoveValue = BooleanValue("NoMove", false)

    private val delayValue = IntegerValue("CustomDelay", 0, 0, 300)
    private val customSpeedValue = IntegerValue("CustomSpeed", 2, 1, 35)
    private val customTimer = FloatValue("CustomTimer", 1.1f, 0.5f, 2f)

    val fastBow = BooleanValue("FastBow",false)
    private val packetsValue = IntegerValue("FastBowPackets", 20, 3, 20)

    private val msTimer = MSTimer()
    private var usedTimer = false

    @EventTarget
    fun onUpdate(event: UpdateEvent) {

        //if (event.eventState == UpdateState.OnUpdate) return

        val thePlayer = mc.thePlayer ?: return

        if (usedTimer) {
            mc.timer.timerSpeed = 1F
            usedTimer = false
        }

        if (!thePlayer.isUsingItem) {
            msTimer.reset()
            return
        }

        val usingItem = thePlayer.itemInUse!!.item

        if (fastBow.get()){
            val currentItem = thePlayer.inventory.getCurrentItem()

            if (currentItem != null && (currentItem.item)is ItemBow) {

                mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(BlockPos.ORIGIN, 255, currentItem, 0F, 0F, 0F))

                val yaw = if (RotationUtils.targetRotation != null)
                    RotationUtils.targetRotation.yaw
                else
                    thePlayer.rotationYaw

                val pitch = if (RotationUtils.targetRotation != null)
                    RotationUtils.targetRotation.pitch
                else
                    thePlayer.rotationPitch

                for (i in 0 until packetsValue.get())
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C05PacketPlayerLook(yaw, pitch, true))

                mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN))
                thePlayer.itemInUseCount = currentItem.maxItemUseDuration - 1
            }
        }

        if ((usingItem)is ItemFood || (usingItem)is ItemBucketMilk || (usingItem)is ItemPotion) {
            when (modeValue.get().toLowerCase()) {
                "instant" -> {
                    repeat(35) {
                        mc.netHandler.addToSendQueue(C03PacketPlayer(thePlayer.onGround))
                    }

                    mc.playerController.onStoppedUsingItem(thePlayer)
                }

                "ncp" -> if (thePlayer.itemInUseDuration > 14) {
                    repeat(20) {
                        mc.netHandler.addToSendQueue(C03PacketPlayer(thePlayer.onGround))
                    }

                    mc.playerController.onStoppedUsingItem(thePlayer)
                }

                "aac" -> {
                    mc.timer.timerSpeed = 1.22F
                    usedTimer = true
                }

                "custom" -> {
                    mc.timer.timerSpeed = customTimer.get()
                    usedTimer = true

                    if (!msTimer.hasTimePassed(delayValue.get().toLong()))
                        return

                    repeat(customSpeedValue.get()) {
                        mc.netHandler.addToSendQueue(C03PacketPlayer(thePlayer.onGround))
                    }

                    msTimer.reset()
                }
            }
        }
    }

    @EventTarget
    fun onMove(event: MoveEvent?) {
        val thePlayer = mc.thePlayer

        if (thePlayer == null || event == null)
            return
        if (!state || !thePlayer.isUsingItem || !noMoveValue.get())
            return

        val usingItem = thePlayer.itemInUse!!.item

        if ((usingItem)is ItemFood || (usingItem)is ItemBucketMilk || (usingItem)is ItemPotion)
            event.zero()
    }

    override fun onDisable() {
        if (usedTimer) {
            mc.timer.timerSpeed = 1F
            usedTimer = false
        }
    }

    override val tag: String
        get() = modeValue.get()
}