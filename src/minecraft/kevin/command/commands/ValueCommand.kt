package kevin.command.commands

import kevin.command.ICommand
import kevin.main.KevinClient
import kevin.module.*
import kevin.utils.ChatUtils
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.util.ResourceLocation
import java.util.*

class ValueCommand : ICommand {
    override fun run(args: Array<out String>?) {
        if (args == null || args.isEmpty()) {
            ChatUtils.messageWithStart("§cUsage: .<ModuleName> <ValueName> <Value>")
            return
        }
        for (m in KevinClient.moduleManager.getModules()){
            if (m.getName().equals(args[0],true)){
                if (args.size == 1){
                    var values = ""
                    for (v in m.values){
                        if (v != m.values.last()) values += v.name +"/"
                        else values += v.name
                    }
                    ChatUtils.messageWithStart("§6${m.getName()} §c<$values>")
                    return
                }
                val value = m.getValue(args[1])
                if (value == null) {
                    ChatUtils.messageWithStart("§cModule '${m.getName()}' has no '${args[1]}' option!")
                    return
                }
                if (value is BooleanValue) {
                    if (args.size == 2) {
                        val newValue = !value.get()
                        value.set(newValue)
                        ChatUtils.messageWithStart("§6${m.getName()} §b${args[1]}§9 was toggled ${if (newValue) "§aOn§9" else "§cOff§9"}.")
                        playSound()
                        return
                    }else if (args[2].equals("On",true)){
                        val newValue = true
                        value.set(newValue)
                        ChatUtils.messageWithStart("§6${m.getName()} §b${args[1]}§9 was toggled §aOn§9.")
                        playSound()
                        return
                    }else if (args[2].equals("Off",true)){
                        val newValue = false
                        value.set(newValue)
                        ChatUtils.messageWithStart("§6${m.getName()} §b${args[1]}§9 was toggled §cOff§9.")
                        playSound()
                        return
                    }else{
                        val newValue = !value.get()
                        value.set(newValue)
                        ChatUtils.messageWithStart("§6${m.getName()} §b${args[1]}§9 was toggled ${if (newValue) "§aOn§9" else "§cOff§9"}.")
                        playSound()
                        return
                    }
                } else {
                    if (args.size < 3) {
                        if (value is IntegerValue || value is FloatValue || value is TextValue)
                            ChatUtils.messageWithStart("§cUsage: §9.§6${m.getName()} §b${args[1].toLowerCase()} §c<value>")
                        else if (value is ListValue)
                            ChatUtils.messageWithStart("§6${m.getName()} §b${args[1].toLowerCase()} §c<${value.values.joinToString(separator = "/").toLowerCase()}>")
                        return
                    }

                    try {
                        when (value) {
                            is BlockValue -> {

                                val id: Int = try {
                                    args[2].toInt()
                                } catch (exception: NumberFormatException) {
                                    val tmpId = Block.getBlockFromName(args[2])?.let { Block.getIdFromBlock(it) }

                                    if (tmpId == null || tmpId <= 0) {
                                        ChatUtils.messageWithStart("§9Block §c${args[2]}§9 does not exist!")
                                        return
                                    }

                                    tmpId
                                }

                                value.set(id)
                                ChatUtils.messageWithStart("§6${m.getName()} §b${args[1].toLowerCase()}§9 was set to §b${Block.getBlockById(id).localizedName}§9.")
                                playSound()
                                return
                            }
                            is IntegerValue -> value.set(args[2].toInt())
                            is FloatValue -> value.set(args[2].toFloat())
                            is ListValue -> {
                                if (!value.contains(args[2])) {
                                    ChatUtils.messageWithStart("§6${m.getName()} §b${args[1].toLowerCase()} §c<${value.values.joinToString(separator = "/").toLowerCase()}>")
                                    return
                                }

                                value.set(args[2])
                            }
                            is TextValue -> value.set(toCompleteString(args, 2))
                        }
                        ChatUtils.messageWithStart("§6${m.getName()} §b${args[1]}§9 was set to §b${value.get()}§9.")
                        playSound()
                        return
                    } catch (e: NumberFormatException) {
                        ChatUtils.messageWithStart("§cError! ${args[2]}§9 cannot be converted to number!")
                    }
                }
            }
        }
    }
}

private fun toCompleteString(args: Array<out String>, start: Int): String {
    return if (args.size <= start) "" else java.lang.String.join(" ", *Arrays.copyOfRange(args, start, args.size))
}

private fun playSound(){
    Minecraft.getMinecraft().soundHandler.playSound(
        PositionedSoundRecord.create(
            ResourceLocation("random.levelup"),
            1F
        ))
}