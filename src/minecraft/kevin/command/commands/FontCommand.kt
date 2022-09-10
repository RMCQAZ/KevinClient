package kevin.command.commands

import kevin.command.ICommand
import kevin.main.KevinClient
import kevin.utils.ChatUtils

class FontCommand : ICommand {
    override fun run(args: Array<out String>?) {
        if (args.isNullOrEmpty()) {
            ChatUtils.messageWithStart("§cUsage: .font/fonts <reload/list>")
            return
        }
        val c = args[0]
        when {
            c.equals("list", true) -> listFonts()
            c.equals("reload", true) -> reloadFonts()
            else -> ChatUtils.messageWithStart("§cUsage: .font/fonts <reload/list>")
        }
    }
    private fun listFonts() {
        ChatUtils.messageWithStart("§b<Fonts>")
        KevinClient.fontManager.getFonts()
            .map { KevinClient.fontManager.getFontDetails(it) }
            .forEachIndexed { index, fontInfo ->
                if (fontInfo != null) {
                    ChatUtils.messageWithStart("§c${index+1}.§7Name:§a${fontInfo.name}§7Size:§9${fontInfo.fontSize}")
                }
            }
    }
    private fun reloadFonts() {
        ChatUtils.messageWithStart("§bReloading Fonts...")
        val l = System.currentTimeMillis()
        KevinClient.fontManager.reloadFonts()
        ChatUtils.messageWithStart("§aSuccessfully reload fonts,${System.currentTimeMillis()-l}ms.")
    }
}