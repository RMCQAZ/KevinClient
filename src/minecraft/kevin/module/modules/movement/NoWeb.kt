package kevin.module.modules.movement

import kevin.event.EventTarget
import kevin.event.JumpEvent
import kevin.event.UpdateEvent
import kevin.module.FloatValue
import kevin.module.ListValue
import kevin.module.Module
import kevin.module.ModuleCategory
import kevin.utils.MovementUtils
import java.util.*

class NoWeb : Module("NoWeb", "Prevents you from getting slowed down in webs.", category = ModuleCategory.MOVEMENT) {

    private val modeValue = ListValue("Mode", arrayOf("None", "AAC", "LAAC", "Rewinside", "Horizon", "Spartan", "AAC4", "AAC5", "Matrix", "Test"), "None")
    private val horizonSpeed = FloatValue("HorizonSpeed", 0.1F, 0.01F, 0.8F)

    private var usedTimer = false

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (usedTimer) {
            mc.timer.timerSpeed = 1F
            usedTimer = false
        }
        val thePlayer = mc.thePlayer ?: return

        if (!thePlayer.isInWeb)
            return

        when (modeValue.get().lowercase(Locale.getDefault())) {
            "none" -> thePlayer.isInWeb = false
            "aac" -> {
                thePlayer.jumpMovementFactor = 0.59f

                if (!mc.gameSettings.keyBindSneak.isKeyDown)
                    thePlayer.motionY = 0.0
            }
            "laac" -> {
                thePlayer.jumpMovementFactor = if (thePlayer.movementInput.moveStrafe != 0f) 1.0f else 1.21f

                if (!mc.gameSettings.keyBindSneak.isKeyDown)
                    thePlayer.motionY = 0.0

                if (thePlayer.onGround)
                    thePlayer.jump()
            }
            "aac4" -> {
                mc.timer.timerSpeed = 0.99F
                mc.thePlayer.jumpMovementFactor = 0.02958f
                mc.thePlayer.motionY -= 0.00775
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.motionY = 0.4050
                    mc.timer.timerSpeed = 1.35F
                }
            }
            "horizon" -> {
                if (mc.thePlayer.onGround) {
                    MovementUtils.strafe(horizonSpeed.get())
                }
            }
            "spartan" -> {
                MovementUtils.strafe(0.27F)
                mc.timer.timerSpeed = 3.7F
                if (!mc.gameSettings.keyBindSneak.isKeyDown) {
                    mc.thePlayer.motionY = 0.0
                }
                if (mc.thePlayer.ticksExisted % 2 == 0) {
                    mc.timer.timerSpeed = 1.7F
                }
                if (mc.thePlayer.ticksExisted % 40 == 0) {
                    mc.timer.timerSpeed = 3F
                }
                usedTimer = true
            }
            "matrix" -> {
                mc.thePlayer.jumpMovementFactor = 0.12425f
                mc.thePlayer.motionY = -0.0125
                if (mc.gameSettings.keyBindSneak.isKeyDown) mc.thePlayer.motionY = -0.1625

                if (mc.thePlayer.ticksExisted % 40 == 0) {
                    mc.timer.timerSpeed = 3.0F
                    usedTimer = true
                }
            }
            "aac5" -> {
                mc.thePlayer.jumpMovementFactor = 0.42f

                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump()
                }
            }
            "test" -> {
                if (mc.thePlayer.ticksExisted % 7 == 0) {
                    mc.thePlayer.jumpMovementFactor = 0.42f
                }
                if (mc.thePlayer.ticksExisted % 7 == 1) {
                    mc.thePlayer.jumpMovementFactor = 0.33f
                }
                if (mc.thePlayer.ticksExisted % 7 == 2) {
                    mc.thePlayer.jumpMovementFactor = 0.08f
                }
            }
            "rewinside" -> {
                thePlayer.jumpMovementFactor = 0.42f

                if (thePlayer.onGround)
                    thePlayer.jump()
            }
        }
    }

    @EventTarget
    fun onJump(event: JumpEvent) {
        if (modeValue.equals("AAC4")) {
            event.cancelEvent()
        }
    }

    override fun onDisable() {
        mc.timer.timerSpeed = 1.0F
    }

    override val tag: String
        get() = modeValue.get()
}