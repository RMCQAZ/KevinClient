package kevin.command.commands

import kevin.command.ICommand
import kevin.main.KevinClient
import kevin.utils.ChatUtils
import org.lwjgl.input.Keyboard

class BindsCommand : ICommand {
    override fun run(args: Array<out String>?) {
        if (args != null && args.isNotEmpty()){
            if (args[0].equals("clear",true)){
                for (module in KevinClient.moduleManager.getModules()) module.setKeyBind(Keyboard.KEY_NONE)
                ChatUtils.messageWithStart("§9Removed All Binds!")
                return
            }
        }
        ChatUtils.messageWithStart("§9Binds:")
        KevinClient.moduleManager.getModules().filter { it.getKeyBind() != Keyboard.KEY_NONE }.forEach {
            ChatUtils.messageWithStart("§b> §9${it.getName()}: §a§l${Keyboard.getKeyName(it.getKeyBind())}")
        }
    }
}