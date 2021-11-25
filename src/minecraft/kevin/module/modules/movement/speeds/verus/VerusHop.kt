package kevin.module.modules.movement.speeds.verus

import kevin.event.MoveEvent
import kevin.module.modules.movement.speeds.SpeedMode
import kevin.utils.MovementUtils

object VerusHop : SpeedMode("VerusHop") {
    private var movementSpeed = .0
    override fun onEnable() {
        movementSpeed = .0
    }
    override fun onMove(event: MoveEvent) {
        if (MovementUtils.isMoving&&
            !mc.thePlayer.isInWeb&&
            !mc.thePlayer.isInLava&&
            !mc.thePlayer.isInWater&&
            !mc.thePlayer.isOnLadder&&
            mc.thePlayer.ridingEntity == null) {
            if (mc.thePlayer.onGround) {
                movementSpeed = .612
                mc.thePlayer.motionY = .41999998688697815
                event.y = .41999998688697815
            } else {
                movementSpeed = .36
            }
            MovementUtils.strafe(movementSpeed.toFloat() - 1.0E-4F)
        }
    }
}