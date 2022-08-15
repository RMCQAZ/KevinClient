package kevin.utils

import kevin.event.EventTarget
import kevin.event.Listenable
import kevin.event.WorldEvent
import net.minecraft.network.Packet

object PacketUtils : MinecraftInstance(), Listenable {
    val packetList = arrayListOf<Packet<*>>()

    fun sendPacketNoEvent(packet: Packet<*>){
        packetList.add(packet)
        mc.netHandler.addToSendQueue(packet)
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        packetList.clear()
    }

    override fun handleEvents() = true
}