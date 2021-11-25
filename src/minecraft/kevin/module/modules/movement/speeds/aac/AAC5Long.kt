package kevin.module.modules.movement.speeds.aac

import kevin.event.UpdateEvent
import kevin.module.modules.movement.speeds.SpeedMode
import kevin.utils.MovementUtils

object AAC5Long : SpeedMode("AAC5Long") {
    override fun onUpdate(event: UpdateEvent){
        if (!MovementUtils.isMoving) return
        if (mc.thePlayer.isInWater || mc.thePlayer.isInLava || mc.thePlayer.isOnLadder || mc.thePlayer.isInWeb) return

        if (mc.thePlayer.onGround) {
            mc.gameSettings.keyBindJump.pressed = false
            mc.thePlayer.jump()
        }
        if (!mc.thePlayer.onGround && mc.thePlayer.fallDistance <= 0.1) {
            mc.thePlayer.speedInAir = 0.02F
            mc.timer.timerSpeed = 1.5F
        }
        if (mc.thePlayer.fallDistance > 0.1 && mc.thePlayer.fallDistance < 1.3) {
            mc.timer.timerSpeed = 0.7F
        }
        if (mc.thePlayer.fallDistance >= 1.3) {
            mc.timer.timerSpeed = 1F
            mc.thePlayer.speedInAir = 0.02F
        }
    }
}