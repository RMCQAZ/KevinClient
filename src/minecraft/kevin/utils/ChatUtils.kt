package kevin.utils

import kevin.main.KevinClient
import net.minecraft.client.Minecraft
import net.minecraft.util.ChatComponentText

object ChatUtils {
    fun message(message: String){
        Minecraft.getMinecraft().ingameGUI.chatGUI.printChatMessage(ChatComponentText(message))
    }
    fun messageWithStart(message: String){
        Minecraft.getMinecraft().ingameGUI.chatGUI.printChatMessage(ChatComponentText("${KevinClient.cStart} $message"))
    }
}