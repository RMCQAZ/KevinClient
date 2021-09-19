package kevin.module.modules.world

import kevin.event.EventTarget
import kevin.event.PacketEvent
import kevin.hud.element.elements.Notification
import kevin.main.KevinClient
import kevin.module.ListValue
import kevin.module.Module
import kevin.module.ModuleCategory
import kevin.utils.ChatUtils
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity

object LightningDetector : Module("LightningDetector","Detect lightning.",category = ModuleCategory.WORLD) {
    private val mode = ListValue("MessageMode", arrayOf("Chat","Notification"),"Notification")
    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (event.packet is S2CPacketSpawnGlobalEntity) {
            val packet = event.packet
            if(packet.func_149053_g() != 1) return
            when(mode.get()){
                "Chat" -> ChatUtils.messageWithStart("§cLightning at §cX:${packet.func_149051_d()} §cY:${packet.func_149050_e()} §cZ:${packet.func_149049_f()}")
                "Notification" -> KevinClient.hud.addNotification(Notification("Lightning at X:${packet.func_149051_d()} Y:${packet.func_149050_e()} Z:${packet.func_149049_f()}"),getName())
            }
        }
    }
}