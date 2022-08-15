package kevin.module.modules.movement.speeds.matrix

import kevin.event.UpdateEvent
import kevin.module.modules.movement.speeds.SpeedMode
import kevin.utils.MovementUtils

object MatrixNew : SpeedMode("MatrixNew") { //from FDP
    override fun onUpdate(event: UpdateEvent) {
        mc.timer.timerSpeed = 1.0f

        if (!MovementUtils.isMoving || mc.thePlayer.isInWater || mc.thePlayer.isInLava ||
            mc.thePlayer.isOnLadder || mc.thePlayer.isRiding) return

        if (mc.thePlayer.onGround) {
            mc.thePlayer.jump()
            mc.timer.timerSpeed = 0.9f
        } else {
            if (mc.thePlayer.fallDistance <= 0.1) {
                mc.timer.timerSpeed = 1.5f
            } else if (mc.thePlayer.fallDistance < 1.3) {
                mc.timer.timerSpeed = 0.7f
            } else {
                mc.timer.timerSpeed = 1.0f
            }
        }
    }
}