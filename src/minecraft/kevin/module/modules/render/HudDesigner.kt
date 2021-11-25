package kevin.module.modules.render

import kevin.hud.designer.GuiHudDesigner
import kevin.module.*
import org.lwjgl.input.Keyboard

class HudDesigner : Module("HudDesigner","HUD designer.",Keyboard.KEY_RCONTROL,category = ModuleCategory.RENDER) {
    override fun onEnable() {
        mc.displayGuiScreen(GuiHudDesigner())
        state = false
    }
}