package kevin.module.modules.render

import kevin.event.*
import kevin.hud.designer.GuiHudDesigner
import kevin.main.Kevin
import kevin.module.Module
import kevin.module.ModuleCategory

class HUD : Module("HUD","Toggles visibility of the HUD.",category = ModuleCategory.RENDER) {
    @EventTarget
    fun onRender2D(event: Render2DEvent?) {
        if ((mc.currentScreen) is GuiHudDesigner)
            return

        Kevin.getInstance.hud.render(false)
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {

        //if (event!!.eventState == UpdateState.OnUpdate) return

        Kevin.getInstance.hud.update()
    }

    @EventTarget
    fun onKey(event: KeyEvent) {
        Kevin.getInstance.hud.handleKey('a', event.key)
    }
}