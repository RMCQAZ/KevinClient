package kevin.module.modules.player.nofalls.aac

import kevin.event.PacketEvent
import kevin.module.modules.player.nofalls.NoFallMode
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.server.S12PacketEntityVelocity

object AAC44XFlagNoFall : NoFallMode("AAC4.4.X-Flag") {
    override fun onPacket(event: PacketEvent) {
        if (event.packet is S12PacketEntityVelocity && mc.thePlayer.fallDistance > 1.8) {
            event.packet.motionY = (event.packet.motionY * -0.1).toInt()
        }
        if(event.packet is C03PacketPlayer) {
            if (mc.thePlayer.fallDistance > 1.6) {
                event.packet.onGround = true
            }
        }
    }
}