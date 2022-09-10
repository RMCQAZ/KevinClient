package kevin.module.modules.player.nofalls.normal

import kevin.event.PacketEvent
import kevin.module.modules.player.nofalls.NoFallMode
import net.minecraft.network.play.client.C03PacketPlayer

object DamageNoFall : NoFallMode("Damage") {
    override fun onPacket(event: PacketEvent) {
        if(event.packet is C03PacketPlayer && mc.thePlayer != null && mc.thePlayer.fallDistance > 3.5) {
            event.packet.onGround = true
        }
    }
}