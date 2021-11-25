package kevin.command.commands

import kevin.command.ICommand
import kevin.main.KevinClient
import kevin.utils.ChatUtils

class StateCommand : ICommand {
    override fun run(args: Array<out String>?) {
        ChatUtils.messageWithStart("§9Modules State")
        KevinClient.moduleManager.getModules().forEach {
            if (it.name!="Targets") ChatUtils.messageWithStart("§6${it.name} §9State: ${if (it.state) "§aOn" else "§cOff"}")
        }
    }
}