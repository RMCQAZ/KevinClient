package kevin.module.modules.player.nofalls.aac

import kevin.event.UpdateEvent
import kevin.module.modules.player.nofalls.NoFallMode
import net.minecraft.network.play.client.C03PacketPlayer

object AAC3315NoFall : NoFallMode("AAC3.3.15") {
    override fun onNoFall(event: UpdateEvent) {
        if (mc.thePlayer!!.fallDistance > 2) {
            if (!mc.isIntegratedServerRunning) mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer!!.posX, Double.NaN, mc.thePlayer!!.posZ, false))
            mc.thePlayer!!.fallDistance = (-9999).toFloat()
        }
    }
}