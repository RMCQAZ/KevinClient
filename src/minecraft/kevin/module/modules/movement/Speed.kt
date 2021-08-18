package kevin.module.modules.movement

import kevin.event.*
import kevin.module.BooleanValue
import kevin.module.ListValue
import kevin.module.Module
import kevin.utils.MovementUtils
import kotlin.math.cos
import kotlin.math.sin

class Speed : Module("Speed") {
    private val mode = ListValue("Mode", arrayOf("AAC5Long","AAC5Fast","YPort","AutoJump"),"AAC5Long")
    private val keepSprint = BooleanValue("KeepSprint",false)

    private var jumps = 0

    override val tag: String
        get() = mode.get()

    override fun onDisable() {
        mc.timer.timerSpeed = 1F
        mc.thePlayer.speedInAir = 0.02F
        jumps = 0
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent){
        if (event.eventState == UpdateState.OnUpdate) return
        when(mode.get()){
            "AAC5Long" -> {
                if (!MovementUtils.isMoving) return
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
            "AAC5Fast" -> {
                if (!MovementUtils.isMoving)
                    return
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump()
                    mc.thePlayer.speedInAir = 0.0201F
                    mc.timer.timerSpeed = 0.94F
                }
                if (mc.thePlayer.fallDistance > 0.7 && mc.thePlayer.fallDistance < 1.3) {
                    mc.thePlayer.speedInAir = 0.02F
                    mc.timer.timerSpeed = 1.8F
                }
            }
        }
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (mc.thePlayer.isSneaking || event.eventState != EventState.PRE) return
        if (MovementUtils.isMoving && keepSprint.get()) mc.thePlayer.isSprinting = true
        when(mode.get()) {
            "YPort" -> {
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
            "AutoJump" -> {
                if (mc.thePlayer.onGround
                    && mc.thePlayer.jumpTicks == 0
                    && MovementUtils.isMoving
                    && !mc.thePlayer.isInLava
                    && !mc.thePlayer.isInWater
                    && !mc.thePlayer.inWeb
                    && !mc.thePlayer.isOnLadder
                    && !mc.gameSettings.keyBindJump.isKeyDown){
                    mc.thePlayer.jump()
                }
            }
        }
    }
}