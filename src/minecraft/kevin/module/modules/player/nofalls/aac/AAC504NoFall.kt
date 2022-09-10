package kevin.module.modules.player.nofalls.aac

import kevin.event.PacketEvent
import kevin.event.UpdateEvent
import kevin.module.modules.player.nofalls.NoFallMode
import net.minecraft.network.play.client.C03PacketPlayer

object AAC504NoFall : NoFallMode("AAC5.0.4") {
    private var isDmgFalling = false
    override fun onNoFall(event: UpdateEvent) {
        if (mc.thePlayer.fallDistance > 3) {
            isDmgFalling = true
        }
    }

    override fun onPacket(event: PacketEvent) {
        if(event.packet is C03PacketPlayer) {
            if(isDmgFalling) {
                if (event.packet.onGround && mc.thePlayer.onGround) {
                    isDmgFalling = false
                    event.packet.onGround = true
                    mc.thePlayer.onGround = false
                    event.packet.y += 1.0
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(event.packet.x, event.packet.y - 1.0784, event.packet.z, false))
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(event.packet.x, event.packet.y - 0.5, event.packet.z, true))
                }
            }
        }
    }
}