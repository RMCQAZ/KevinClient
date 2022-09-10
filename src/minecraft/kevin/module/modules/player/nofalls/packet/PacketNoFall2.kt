package kevin.module.modules.player.nofalls.packet

import kevin.event.UpdateEvent
import kevin.module.modules.player.nofalls.NoFallMode
import net.minecraft.network.play.client.C03PacketPlayer

object PacketNoFall2 : NoFallMode("Packet2") {
    override fun onNoFall(event: UpdateEvent) {
        if (mc.thePlayer.fallDistance - mc.thePlayer.motionY > 3f){
            mc.netHandler.addToSendQueue(C03PacketPlayer(true))
            mc.thePlayer.fallDistance = 0f
        }
    }
}