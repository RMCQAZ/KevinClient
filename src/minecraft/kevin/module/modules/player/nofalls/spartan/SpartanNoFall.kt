package kevin.module.modules.player.nofalls.spartan

import kevin.event.UpdateEvent
import kevin.module.modules.player.nofalls.NoFallMode
import kevin.utils.TickTimer
import net.minecraft.network.play.client.C03PacketPlayer

object SpartanNoFall : NoFallMode("Spartan") {
    private val spartanTimer = TickTimer()
    override fun onNoFall(event: UpdateEvent) {
        spartanTimer.update()
        if (mc.thePlayer!!.fallDistance > 1.5 && spartanTimer.hasTimePassed(10)) {
            mc.netHandler.addToSendQueue(
                C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer!!.posX,
                mc.thePlayer!!.posY + 10, mc.thePlayer!!.posZ, true))
            mc.netHandler.addToSendQueue(
                C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer!!.posX,
                mc.thePlayer!!.posY - 10, mc.thePlayer!!.posZ, true))
            spartanTimer.reset()
        }
    }
}