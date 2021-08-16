package kevin.command.commands

import kevin.command.ICommand
import kevin.utils.ChatUtils
import net.minecraft.client.Minecraft
import net.minecraft.network.play.client.C01PacketChatMessage

class SayCommand : ICommand {
    override fun run(args: Array<out String>?) {
        if (args == null || args.isEmpty()) {
            ChatUtils().messageWithStart("§cUsage: .say <Message>")
            return
        }
        Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(C01PacketChatMessage(args[0]))
    }
}