package kevin.module.modules.render

import kevin.event.EventTarget
import kevin.event.UpdateEvent
import kevin.module.Module
import kevin.module.ModuleCategory

class NoBob : Module("NoBob", "Disables the view bobbing effect.", category = ModuleCategory.RENDER) {
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        mc.thePlayer?.distanceWalkedModified = 0f
    }
}