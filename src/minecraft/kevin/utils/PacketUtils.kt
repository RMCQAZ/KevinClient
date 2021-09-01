package kevin.utils

import net.minecraft.network.Packet

object PacketUtils : MinecraftInstance() {
    val packetList = arrayListOf<Packet<*>>()

    fun sendPacketNoEvent(packet: Packet<*>){
        packetList.add(packet)
        mc.netHandler.addToSendQueue(packet)
    }
}