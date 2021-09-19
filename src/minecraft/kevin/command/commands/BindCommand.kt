package kevin.command.commands

import kevin.command.ICommand
import kevin.main.KevinClient
import kevin.utils.ChatUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.util.ResourceLocation
import org.lwjgl.input.Keyboard

class BindCommand : ICommand {
    override fun run(args: Array<out String>?) {
        if (args == null || args.isEmpty() || args.size == 1) {
            ChatUtils.messageWithStart("§cUsage: .bind <ModuleName> <Key/None>")
            return
        }
        val module = KevinClient.moduleManager.getModule(args[0])
        if (module == null) {
            ChatUtils.messageWithStart("§9Module §c§l" + args[0] + "§9 not found.")
            return
        }
        val key = Keyboard.getKeyIndex(args[1].toUpperCase())
        module.setKeyBind(key)
        ChatUtils.messageWithStart("§9Bound module §b§l${module.getName()}§9 to key §a§l${Keyboard.getKeyName(key)}§3.")
        Minecraft.getMinecraft().soundHandler.playSound(
            PositionedSoundRecord.create(
            ResourceLocation("random.anvil_use"),
                1F
        ))
        return
    }
}