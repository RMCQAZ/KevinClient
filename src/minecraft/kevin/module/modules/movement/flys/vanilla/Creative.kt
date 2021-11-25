package kevin.module.modules.movement.flys.vanilla

import kevin.event.UpdateEvent
import kevin.module.modules.movement.flys.FlyMode

object Creative : FlyMode("Creative") {
    override fun onEnable() {
        mc.thePlayer.capabilities.allowFlying = true
    }
    override fun onUpdate(event: UpdateEvent) {
        if (!mc.thePlayer.capabilities.allowFlying) mc.thePlayer.capabilities.allowFlying = true
    }
    override fun onDisable() {
        mc.thePlayer.capabilities.allowFlying = mc.playerController.isInCreativeMode || mc.playerController.isSpectatorMode
    }
}