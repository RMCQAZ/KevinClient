package kevin.module.modules.player.nofalls.packet

import kevin.event.UpdateEvent
import kevin.module.modules.player.nofalls.NoFallMode
import net.minecraft.network.play.client.C03PacketPlayer

object PacketNoFall : NoFallMode("Packet") {
    override fun onNoFall(event: UpdateEvent) {
        if (mc.thePlayer!!.fallDistance > 2f) {
            mc.netHandler.addToSendQueue(C03PacketPlayer(true))
        }
    }
}