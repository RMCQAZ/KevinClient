package kevin.module.modules.player

import kevin.event.EventTarget
import kevin.event.UpdateEvent
import kevin.main.Kevin
import kevin.module.BooleanValue
import kevin.module.Module
import kevin.module.ModuleCategory
import net.minecraft.client.gui.GuiGameOver

class AutoRespawn : Module("AutoRespawn", "Automatically respawns you after dying.", category = ModuleCategory.PLAYER) {

    private val instantValue = BooleanValue("Instant", true)

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val thePlayer = mc.thePlayer

        if (thePlayer == null || Kevin.getInstance.moduleManager.getModule("Ghost")!!.getToggle())
            return

        if (if (instantValue.get()) thePlayer.health == 0F || thePlayer.isDead else (mc.currentScreen)is GuiGameOver
                    && (mc.currentScreen!! as GuiGameOver).enableButtonsTimer >= 20) {
            thePlayer.respawnPlayer()
            mc.displayGuiScreen(null)
        }
    }
}