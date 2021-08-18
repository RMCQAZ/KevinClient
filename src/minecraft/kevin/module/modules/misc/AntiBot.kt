package kevin.module.modules.misc

import kevin.event.EventTarget
import kevin.event.PacketEvent
import kevin.event.TextEvent
import kevin.event.UpdateEvent
import kevin.main.Kevin
import kevin.module.ListValue
import kevin.module.Module
import kevin.module.ModuleCategory
import kevin.module.modules.render.HUD
import net.minecraft.item.ItemArmor
import net.minecraft.network.play.server.S02PacketChat

class AntiBot : Module("AntiBot","Prevents KillAura from attacking AntiCheat bots.", category = ModuleCategory.MISC) {
    private val modeValue = ListValue("Mode", arrayOf("ZQAT-ArmorAndColor(BW)"),"ZQAT-ArmorAndColor(BW)")

    override val tag: String
        get() = modeValue.get()

    private var gameStarted = false

    override fun onEnable() {
        gameStarted = false
    }

    @EventTarget
    fun onPacket(event: PacketEvent){
        val packet = event.packet
        if (packet is S02PacketChat){
            if (packet.chatComponent.formattedText.contains("获得胜利",true)){
                gameStarted = false
                (Kevin.getInstance.moduleManager.getModule("HUD") as HUD).addNotification(HUD.Notification("GameEnd!"),"Antibot")
            }

            if (packet.chatComponent.formattedText.contains("游戏开始",true)){
                gameStarted = true
                (Kevin.getInstance.moduleManager.getModule("HUD") as HUD).addNotification(HUD.Notification("GameStart!"),"Antibot")
            }
        }
    }

    @EventTarget
    fun onText(event: TextEvent){
        val text = event.text
        if (text != null && text.contains("游戏结束",true) && gameStarted){
            gameStarted = false
            (Kevin.getInstance.moduleManager.getModule("HUD") as HUD).addNotification(HUD.Notification("GameEnd!"),"Antibot")
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent){
        val mode = modeValue.get()
        if (mode.equals("ZQAT-ArmorAndColor(BW)",true)){
            if (!gameStarted) return
            for (entity in mc.theWorld.playerEntities){
                if (entity == mc.thePlayer) continue
                var isBot = false
                val armorInventory = entity.inventory.armorInventory
                if (armorInventory[2] != null
                    && armorInventory[3] != null
                    && armorInventory[2].item is ItemArmor
                    && armorInventory[3].item is ItemArmor
                ){
                    if ((armorInventory[2].item as ItemArmor).armorMaterial != ItemArmor.ArmorMaterial.LEATHER) isBot = true
                    if ((armorInventory[3].item as ItemArmor).armorMaterial != ItemArmor.ArmorMaterial.LEATHER) isBot = true
                }
                if (armorInventory[0] != null
                    && armorInventory[1] != null
                    && armorInventory[0].item is ItemArmor
                    && armorInventory[1].item is ItemArmor
                ){
                    if ((armorInventory[0].item as ItemArmor).armorMaterial != (armorInventory[1].item as ItemArmor).armorMaterial) isBot = true
                }
                if (((armorInventory[0] != null && armorInventory[0].item is ItemArmor) && armorInventory[1] == null)
                    || ((armorInventory[1] != null && armorInventory[1].item is ItemArmor) && armorInventory[0] == null)
                    || ((armorInventory[2] != null && armorInventory[2].item is ItemArmor) && armorInventory[3] == null)
                    || ((armorInventory[3] != null && armorInventory[3].item is ItemArmor) && armorInventory[2] == null)
                ) isBot = true

                for (a in armorInventory) {
                    if ((a != null
                                && a.item is ItemArmor
                                && ((a.item as ItemArmor).armorMaterial == ItemArmor.ArmorMaterial.LEATHER
                                && a.tagCompound == null
                                ))
                    ) isBot = true
                }

                if (isBot) {
                    mc.theWorld.removeEntityFromWorld(entity.entityId)
                    val hud = Kevin.getInstance.moduleManager.getModule("HUD") as HUD
                    hud.addNotification(HUD.Notification("Removed Bot"),"AntiBot")
                }
            }
        }
    }
}