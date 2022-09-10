package kevin.module.modules.player.nofalls.packet

import kevin.event.EventState
import kevin.event.MotionEvent
import kevin.event.PacketEvent
import kevin.module.modules.player.nofalls.NoFallMode
import kevin.utils.PacketUtils
import net.minecraft.client.Minecraft
import net.minecraft.network.play.client.C03PacketPlayer

object C03C04NoFall : NoFallMode("C03->C04") {
    override fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is C03PacketPlayer) {
            if (mc.thePlayer!!.capabilities.isFlying ||
                Minecraft.getMinecraft().thePlayer.capabilities.disableDamage ||
                mc.thePlayer!!.motionY >= 0.0) return
            if (packet.isMoving) {
                if (mc.thePlayer!!.fallDistance > 2.0f && isBlockUnder()) {
                    event.cancelEvent()
                    PacketUtils.sendPacketNoEvent(
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
    override fun onMotion(event: MotionEvent) {
        if (event.eventState == EventState.PRE){
            if (mc.thePlayer!!.capabilities.isFlying ||
                Minecraft.getMinecraft().thePlayer.capabilities.disableDamage ||
                mc.thePlayer!!.motionY >= 0.0) return
            if (mc.thePlayer!!.fallDistance > 3.0f)
                if (isBlockUnder())
                    PacketUtils.sendPacketNoEvent(C03PacketPlayer(true))
        }
    }
    private fun isBlockUnder(): Boolean{
        for (y in 0..((mc.thePlayer?.posY?:return false) + (mc.thePlayer?.eyeHeight ?:return false)).toInt()){
            val boundingBox = mc.thePlayer!!.entityBoundingBox.offset(0.0,-y.toDouble(),0.0)
            if (mc.theWorld!!.getCollidingBoundingBoxes(mc.thePlayer!!,boundingBox).isNotEmpty()) return true
        }
        return false
    }
}