package kevin.command.commands

import kevin.command.ICommand
import kevin.main.KevinClient
import kevin.utils.ChatUtils

class HideCommand : ICommand {
    override fun run(args: Array<out String>?) {
        if (args.isNullOrEmpty()) {
            ChatUtils.messageWithStart("§cUsage: .hide <ModuleName>")
            return
        }
        KevinClient.moduleManager.getModules().filter { it.name.equals(args[0],true) }.forEach {
            it.array = !it.array
            if (it.array)
                ChatUtils.messageWithStart("§aModule ${it.name} is unhidden.")
            else
                ChatUtils.messageWithStart("§aModule ${it.name} is hidden.")
            KevinClient.fileManager.saveConfig(KevinClient.fileManager.modulesConfig)
            return
        }
        ChatUtils.messageWithStart("§cNo module called ${args[0]}.")
    }
}