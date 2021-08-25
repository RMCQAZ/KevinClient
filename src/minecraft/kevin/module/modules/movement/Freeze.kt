package kevin.module.modules.movement

import kevin.event.EventTarget
import kevin.event.UpdateEvent
import kevin.module.Module
import kevin.module.ModuleCategory

class Freeze : Module("Freeze", "Allows you to stay stuck in mid air.", category = ModuleCategory.MOVEMENT) {
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val thePlayer = mc.thePlayer!!

        thePlayer.isDead = true
        thePlayer.rotationYaw = thePlayer.cameraYaw
        thePlayer.rotationPitch = thePlayer.cameraPitch
    }

    override fun onDisable() {
        mc.thePlayer?.isDead = false
    }
}