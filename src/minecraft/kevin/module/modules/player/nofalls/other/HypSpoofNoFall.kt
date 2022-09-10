package kevin.module.modules.player.nofalls.other

import kevin.event.PacketEvent
import kevin.module.modules.player.nofalls.NoFallMode
import kevin.utils.PacketUtils
import net.minecraft.network.play.client.C03PacketPlayer

object HypSpoofNoFall : NoFallMode("HypSpoof") {
    override fun onPacket(event: PacketEvent) {
        if(event.packet is C03PacketPlayer) PacketUtils.sendPacketNoEvent(C03PacketPlayer.C04PacketPlayerPosition(event.packet.x, event.packet.y, event.packet.z, true))
    }
}