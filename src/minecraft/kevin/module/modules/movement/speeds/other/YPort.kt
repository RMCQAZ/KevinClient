package kevin.module.modules.movement.speeds.other

import kevin.module.modules.movement.speeds.SpeedMode
import kevin.utils.MovementUtils
import kotlin.math.cos
import kotlin.math.sin

object YPort : SpeedMode("YPort") {
    private var jumps = 0
    override fun onPreMotion() {
        if (mc.thePlayer!!.isOnLadder
            || mc.thePlayer!!.isInWater
            || mc.thePlayer!!.isInLava
            || mc.thePlayer!!.isInWeb
            || !MovementUtils.isMoving
            || mc.gameSettings.keyBindJump.isKeyDown) return
        if (jumps >= 4 && mc.thePlayer!!.onGround) jumps = 0
        if (mc.thePlayer!!.onGround) {
            mc.thePlayer!!.motionY = if (jumps <= 1) 0.42 else 0.4
            val f = mc.thePlayer!!.rotationYaw * 0.017453292f
            mc.thePlayer!!.motionX -= sin(f) * 0.2f
            mc.thePlayer!!.motionZ += cos(f) * 0.2f
            jumps++
        } else if (jumps <= 1) mc.thePlayer!!.motionY = -5.0
        MovementUtils.strafe()
    }
    override fun onDisable() {
        jumps = 0
    }
}