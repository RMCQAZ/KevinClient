package kevin.module.modules.player.nofalls.aac

import kevin.event.UpdateEvent
import kevin.module.modules.player.nofalls.NoFallMode
import net.minecraft.network.play.client.C03PacketPlayer

object AACNoFall : NoFallMode("AAC") {
    private var currentState = 0
    override fun onNoFall(event: UpdateEvent) {
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
}