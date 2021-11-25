package kevin.module.modules.movement.speeds.other

import kevin.module.modules.movement.speeds.SpeedMode
import kevin.utils.MovementUtils

object AutoJump : SpeedMode("AutoJump") {
    override fun onPreMotion() {
        if (mc.thePlayer.onGround
            && mc.thePlayer.jumpTicks == 0
            && MovementUtils.isMoving
            && !mc.thePlayer.isInLava
            && !mc.thePlayer.isInWater
            && !mc.thePlayer.inWeb
            && !mc.thePlayer.isOnLadder
            && !mc.gameSettings.keyBindJump.isKeyDown) {
            mc.thePlayer.jump()
        }
    }
}