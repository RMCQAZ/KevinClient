package kevin.command.commands

import kevin.command.ICommand
import kevin.main.KevinClient
import kevin.utils.ChatUtils

class DisableAllCommand : ICommand {
    override fun run(args: Array<out String>?) {
        KevinClient.moduleManager.getModules().forEach { it.state = false }
        ChatUtils.messageWithStart("§aDisabled all modules.")
    }
}