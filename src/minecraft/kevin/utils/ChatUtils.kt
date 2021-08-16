package kevin.utils

import kevin.main.Kevin
import net.minecraft.client.Minecraft
import net.minecraft.util.ChatComponentText

open class ChatUtils {
    open fun message(message: String){
        Minecraft.getMinecraft().ingameGUI.chatGUI.printChatMessage(ChatComponentText(message))
    }
    open fun messageWithStart(message: String){
        Minecraft.getMinecraft().ingameGUI.chatGUI.printChatMessage(ChatComponentText("${Kevin.getInstance.cStart} $message"))
    }
}