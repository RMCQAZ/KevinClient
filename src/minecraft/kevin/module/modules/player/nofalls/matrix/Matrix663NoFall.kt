package kevin.module.modules.player.nofalls.matrix

import kevin.event.PacketEvent
import kevin.event.UpdateEvent
import kevin.module.modules.player.nofalls.NoFallMode
import kevin.utils.PacketUtils
import net.minecraft.network.play.client.C03PacketPlayer

object Matrix663NoFall : NoFallMode("Matrix6.6.3") {
    private var matrixSend = false
    override fun onEnable() {
        matrixSend = false
    }
    override fun onNoFall(event: UpdateEvent) {
        if (mc.thePlayer.fallDistance - mc.thePlayer.motionY > 3) {
            mc.thePlayer.fallDistance = 0.0f
            matrixSend = true
            mc.timer.timerSpeed = 0.5f
            noFall.wasTimer = true
        }
    }

    override fun onPacket(event: PacketEvent) {
        if (event.packet is C03PacketPlayer && matrixSend) {
            matrixSend = false
            event.cancelEvent()
            PacketUtils.sendPacketNoEvent(C03PacketPlayer.C04PacketPlayerPosition(event.packet.x, event.packet.y, event.packet.z, true))
            PacketUtils.sendPacketNoEvent(C03PacketPlayer.C04PacketPlayerPosition(event.packet.x, event.packet.y, event.packet.z, false))
        }
    }
}