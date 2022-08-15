package kevin.command.commands

import kevin.command.ICommand
import kevin.main.KevinClient
import kevin.script.ScriptManager
import kevin.utils.ChatUtils

class ClearMainConfigCommand : ICommand {
    override fun run(args: Array<out String>?) {
        KevinClient.moduleManager.getModules().forEach { KevinClient.eventManager.unregisterListener(it) }
        KevinClient.eventManager.unregisterListener(KevinClient.moduleManager)
        KevinClient.moduleManager.getModules().clear()
        KevinClient.moduleManager.load()
        ScriptManager.reAdd()
        KevinClient.fileManager.saveConfig(KevinClient.fileManager.modulesConfig)
        ChatUtils.messageWithStart("§aCleared main config.")
    }
}