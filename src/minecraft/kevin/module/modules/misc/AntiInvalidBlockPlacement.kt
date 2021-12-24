package kevin.module.modules.misc

import kevin.event.EventTarget
import kevin.event.PacketEvent
import kevin.module.BooleanValue
import kevin.module.Module
import kevin.via.ViaVersion
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement

class AntiInvalidBlockPlacement : Module("AntiInvalidBlockPlacement", "Anti invalid block placement caused by via-version.") {
    private val versionCheck = BooleanValue("VersionCheck", true)
    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if ((!versionCheck.get() || ViaVersion.nowVersion > 210) && packet is C08PacketPlayerBlockPlacement) {
            packet.facingX = 0.5F
            packet.facingY = 0.5F
            packet.facingZ = 0.5F
        }
    }
}