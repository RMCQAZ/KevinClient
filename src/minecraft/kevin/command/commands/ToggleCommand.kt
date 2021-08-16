package kevin.command.commands

import kevin.command.ICommand
import kevin.main.Kevin
import kevin.module.modules.render.HUD
import kevin.utils.ChatUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.util.ResourceLocation

class ToggleCommand : ICommand {
    override fun run(args: Array<out String>?) {
        if (args == null || args.isEmpty()) {
            ChatUtils().message("${Kevin.getInstance.cStart} §cUsage: §c.t §c<ModuleName> §c<on/off> §cor §c.toggle §c<ModuleName> §c<on/off>")
            return
        }
        for (module in Kevin.getInstance.moduleManager.getModules()){
            if (module.getName().equals(args[0],ignoreCase = true)){
                if (args.size > 1){
                    val hud = Kevin.getInstance.moduleManager.getModule("HUD") as HUD
                    if (args[1].equals("on",ignoreCase = true)){
                        module.toggle(true)
                        hud.addNotification(HUD.Notification("Enabled ${module.getName()}"))
                        Minecraft.getMinecraft().soundHandler.playSound(
                            PositionedSoundRecord.create(
                                ResourceLocation("gui.button.press"),
                                1f
                            )
                        )
                        ChatUtils().message("${Kevin.getInstance.cStart} §aEnable §e${module.getName()} §9Module")
                        return
                    }else if (args[1].equals("off",ignoreCase = true)){
                        module.toggle(false)
                        hud.addNotification(HUD.Notification("Disabled ${module.getName()}"))
                        Minecraft.getMinecraft().soundHandler.playSound(
                            PositionedSoundRecord.create(
                                ResourceLocation("gui.button.press"),
                                0.6114514191981f
                            )
                        )
                        ChatUtils().message("${Kevin.getInstance.cStart} §cDisable §e${module.getName()} §9Module")
                        return
                    }else {
                        module.toggle()
                        ChatUtils().message("${Kevin.getInstance.cStart} §9${if (module.getToggle()) "§aEnable" else "§cDisable"} §e${module.getName()} §9Module")
                        return
                    }
                }else{
                    module.toggle()
                    ChatUtils().message("${Kevin.getInstance.cStart} §9${if (module.getToggle()) "§aEnable" else "§cDisable"} §e${module.getName()} §9Module")
                    return
                }
            }
        }
        ChatUtils().message("${Kevin.getInstance.cStart} §cNo module called ${args[0]}")
    }
}