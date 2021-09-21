package kevin.module.modules.world

import kevin.event.EventTarget
import kevin.event.UpdateEvent
import kevin.event.WorldEvent
import kevin.module.BooleanValue
import kevin.module.FloatValue
import kevin.module.Module
import kevin.module.ModuleCategory
import kevin.utils.MovementUtils

class Timer : Module("Timer", "Changes the speed of the entire game.", category = ModuleCategory.WORLD) {
    private val speedValue = FloatValue("Speed", 2F, 0.1F, 30F)
    private val onMoveValue = BooleanValue("OnlyOnMove", true)
    //private val autoDisable = BooleanValue("AutoDisable",true)

    override fun onDisable() {
        if (mc.thePlayer == null)
            return
        mc.timer.timerSpeed = 1F
    }
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if(MovementUtils.isMoving || !onMoveValue.get()) {
            mc.timer.timerSpeed = speedValue.get()
            return
        }
        mc.timer.timerSpeed = 1F
    }
/**
    @EventTarget
    fun onWorld(event: WorldEvent) {
        if (event.worldClient != null) return
        if (!autoDisable.get()) return
        this.toggle(false)
    }
**/
    override val tag: String
        get() = "Speed:${speedValue.get()}"
}