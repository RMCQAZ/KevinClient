package kevin.module.modules.player.nofalls.other

import kevin.event.PacketEvent
import kevin.module.modules.player.nofalls.NoFallMode
import net.minecraft.network.play.client.C03PacketPlayer

object HypixelNoFall : NoFallMode("Hypixel") {
    override fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is C03PacketPlayer && mc.thePlayer != null && mc.thePlayer!!.fallDistance > 1.5)
            packet.onGround = mc.thePlayer!!.ticksExisted % 2 == 0
    }
}