package kevin.module.modules.movement.speeds.aac

import kevin.event.UpdateEvent
import kevin.main.KevinClient
import kevin.module.modules.movement.Strafe
import kevin.module.modules.movement.speeds.SpeedMode
import kevin.utils.MovementUtils

object AAC5Fast : SpeedMode("AAC5Fast") {
    override fun onUpdate(event: UpdateEvent){
        if (!MovementUtils.isMoving) return
        if (mc.thePlayer.isInWater || mc.thePlayer.isInLava || mc.thePlayer.isOnLadder || mc.thePlayer.isInWeb) return
        if (mc.thePlayer.onGround) {
            val strafe = KevinClient.moduleManager.getModule("Strafe") as Strafe
            if (strafe.state && strafe.allDirectionsJumpValue.get()) {
                val yaw = mc.thePlayer.rotationYaw
                mc.thePlayer.rotationYaw = strafe.getMoveYaw()
                mc.thePlayer.jump()
                mc.thePlayer.rotationYaw = yaw
            } else {
                mc.thePlayer.jump()
            }
            mc.thePlayer.speedInAir = 0.0201F
            mc.timer.timerSpeed = 0.94F
        }
        if (mc.thePlayer.fallDistance > 0.7 && mc.thePlayer.fallDistance < 1.3) {
            mc.thePlayer.speedInAir = 0.02F
            mc.timer.timerSpeed = 1.8F
        }
    }
}