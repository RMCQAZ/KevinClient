package kevin.command.commands

import kevin.command.ICommand
import kevin.main.KevinClient
import kevin.utils.ChatUtils

class StateCommand : ICommand {
    override fun run(args: Array<out String>?) {
        ChatUtils.messageWithStart("§9Modules State")
        KevinClient.moduleManager.getModules().forEach {
            if (it.getName()!="Targets") ChatUtils.messageWithStart("§6${it.getName()} §9State: ${if (it.getToggle()) "§aOn" else "§cOff"}")
        }
    }
}