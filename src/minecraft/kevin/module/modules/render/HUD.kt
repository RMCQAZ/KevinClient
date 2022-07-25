package kevin.module.modules.render

import kevin.event.*
import kevin.hud.designer.GuiHudDesigner
import kevin.hud.element.elements.ScoreboardElement
import kevin.main.KevinClient
import kevin.module.BooleanValue
import kevin.module.Module
import kevin.module.ModuleCategory

class HUD : Module("HUD","Toggles visibility of the HUD.",category = ModuleCategory.RENDER) {
    var keepScoreboard = BooleanValue("KeepScoreboard", true)

    @EventTarget
    fun onRender2D(event: Render2DEvent?) {
        if ((mc.currentScreen) is GuiHudDesigner)
            return

        KevinClient.hud.render(false)
    }

    @EventTarget(true)
    fun renderScoreboard(event: Render2DEvent) {
        if (!this.state && keepScoreboard.get() && KevinClient.hud.elements.filterIsInstance<ScoreboardElement>().isNotEmpty()) {
            KevinClient.hud.renderScoreboardOnly()
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {

        //if (event!!.eventState == UpdateState.OnUpdate) return

        KevinClient.hud.update()
    }

    @EventTarget(true)
    fun updateScoreboard(event: UpdateEvent) {
        if (!this.state && keepScoreboard.get() && KevinClient.hud.elements.filterIsInstance<ScoreboardElement>().isNotEmpty()) {
            KevinClient.hud.updateScoreboardOnly()
        }
    }

    @EventTarget
    fun onKey(event: KeyEvent) {
        KevinClient.hud.handleKey('a', event.key)
    }
}