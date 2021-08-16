package kevin.command.commands

import kevin.command.ICommand
import kevin.utils.ChatUtils

class HelpCommand : ICommand {
    override fun run(args: Array<out String>?) {
        ChatUtils().message("§b<Help>")
        ChatUtils().message("§a.t/.toggle <ModuleName> <on/off> §9Enable/Disable module.")
        ChatUtils().message("§a.h/.help §9Show this message.")
    }
}