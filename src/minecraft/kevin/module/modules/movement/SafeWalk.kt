package kevin.module.modules.movement

import kevin.event.EventTarget
import kevin.event.MoveEvent
import kevin.module.BooleanValue
import kevin.module.Module
import kevin.module.ModuleCategory

class SafeWalk : Module("SafeWalk", "Prevents you from falling down as if you were sneaking.", category = ModuleCategory.MOVEMENT) {
    private val airSafeValue = BooleanValue("AirSafe", false)

    @EventTarget
    fun onMove(event: MoveEvent) {
        if (airSafeValue.get() || mc.thePlayer!!.onGround)
            event.isSafeWalk = true
    }
}