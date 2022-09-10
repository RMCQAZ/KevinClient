package kevin.module.modules.player.nofalls.normal

import kevin.event.UpdateEvent
import kevin.module.FloatValue
import kevin.module.modules.player.nofalls.NoFallMode

object MotionFlagNoFall : NoFallMode("MotionFlag") {
    private val flySpeedValue = FloatValue("${valuePrefix}MotionSpeed", -0.01f, -5f, 5f)
    override fun onNoFall(event: UpdateEvent) {
        if (mc.thePlayer.fallDistance > 3) {
            mc.thePlayer.motionY = flySpeedValue.get().toDouble()
        }
    }
}