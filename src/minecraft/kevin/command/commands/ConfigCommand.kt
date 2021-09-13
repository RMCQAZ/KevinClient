package kevin.command.commands

import kevin.command.ICommand
import kevin.file.ConfigManager
import kevin.utils.ChatUtils

class ConfigCommand : ICommand {
    override fun run(args: Array<out String>?) {
        if (args.isNullOrEmpty()){
            usageMessage()
            return
        }
        when(args.size){
            1 -> {
                when{
                    args[0].equals("Save",true) || args[0].equals("Load",true) -> {
                        ChatUtils().messageWithStart("§cUsage: §9.§6config §b${args[0]} §c<ConfigName>")
                    }
                    else -> usageMessage()
                }
            }
            else -> {
                var name = ""
                val list = args.toMutableList()
                list.removeFirst()
                var c = 0
                list.forEach {
                    c+=1
                    name += if (c == list.size){
                        it
                    }else "$it "
                }
                when{
                    args[0].equals("Save",true) -> {
                        try {
                            ConfigManager.saveConfig(name)
                            ChatUtils().messageWithStart("§aSuccessfully saved config §b$name.")
                        }catch (e: Exception){
                            ChatUtils().messageWithStart("§cError: $e")
                        }
                    }
                    args[0].equals("Load",true) -> {
                        try {
                            when(ConfigManager.loadConfig(name)){
                                0 -> {
                                    ChatUtils().messageWithStart("§aSuccessfully loaded config §b$name.")
                                }
                                1 -> {
                                    ChatUtils().messageWithStart("§eWarning: §eThe §eModules §econfig §efile §eis §emissing.§eSuccessfully §eloaded §eHUD §econfig §b$name.")
                                }
                                2 -> {
                                    ChatUtils().messageWithStart("§eWarning: §eThe §eHUD §econfig §efile §eis §emissing.§eSuccessfully §eloaded §eModules §econfig §b$name.")
                                }
                                3 -> {
                                    ChatUtils().messageWithStart("§cFailed to load config §b$name.§cFile not found.")
                                }
                            }
                        }catch (e: Exception){
                            ChatUtils().messageWithStart("§cError: $e")
                        }
                    }
                    else -> usageMessage()
                }
            }
        }
    }
    private fun usageMessage() = ChatUtils().messageWithStart("§cUsage: .config <Save/Load> <ConfigName>")
}