package kevin.module.modules.player.nofalls.normal

import kevin.event.PacketEvent
import kevin.module.modules.player.nofalls.NoFallMode
import net.minecraft.network.play.client.C03PacketPlayer

object SpoofGroundNoFall : NoFallMode("SpoofGround") {
    override fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is C03PacketPlayer)
            packet.onGround = true
    }
}