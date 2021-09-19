package kevin.module.modules.movement

import kevin.event.EventTarget
import kevin.event.JumpEvent
import kevin.event.MoveEvent
import kevin.event.UpdateEvent
import kevin.module.*
import kevin.utils.BlockUtils.getBlock
import kevin.utils.MovementUtils
import net.minecraft.block.BlockPane
import net.minecraft.util.BlockPos
import kotlin.math.cos
import kotlin.math.sin

class HighJump : Module("HighJump", "Allows you to jump higher.", category = ModuleCategory.MOVEMENT) {
    private val heightValue = FloatValue("Height", 2f, 1.1f, 5f)
    private val modeValue = ListValue("Mode", arrayOf("Vanilla", "Damage", "AACv3", "DAC", "Mineplex", "Timer"), "Vanilla")
    private val glassValue = BooleanValue("OnlyGlassPane", false)
    private val timerValue = FloatValue("Timer",0.1f,0.01f,1f)
    private val waitTimeValue = IntegerValue("WaitTime",1,0,5)
    private val flyValue = BooleanValue("Fly",false)

    private var state = 1
    private var fly = false
    private var flyState = 0
    private var timer = -1
    private var timerlock = false

    override fun onDisable() {
        when(modeValue.get()){
            "Timer" -> {
                mc.timer.timerSpeed = 1F
                state = 1
                fly = false
                flyState = 0
            }
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        val thePlayer = mc.thePlayer!!

        if (modeValue equal "Timer"){
            if(mc.thePlayer!!.onGround){
                if (fly) {
                    toggle(false)
                    return
                }
                mc.timer.timerSpeed = timerValue.get()
                mc.thePlayer!!.jump()
                state = 2
            } else {
                if(state == 2) {
                    mc.timer.timerSpeed = 1F
                    if (!flyValue.get()) toggle(false)
                    if (!timerlock) {
                        timerlock = true
                        timer = 0
                    }
                    if(timer >= waitTimeValue.get())
                        timerlock = false
                    fly = true
                    timer = -1
                }

                if (state != 2){
                    state = 2
                }
            }
            if (timer != -1)
                timer += 1

            if(fly){
                flyState += 1
                if (flyState >= 6){
                    mc.thePlayer!!.motionY = .015
                    flyState = 0
                }
            }
            return
        }

        if (glassValue.get() && (getBlock(BlockPos(thePlayer.posX, thePlayer.posY, thePlayer.posZ)))!is BlockPane)
            return

        when (modeValue.get().toLowerCase()) {
            "damage" -> if (thePlayer.hurtTime > 0 && thePlayer.onGround) thePlayer.motionY += 0.42f * heightValue.get()
            "aacv3" -> if (!thePlayer.onGround) thePlayer.motionY += 0.059
            "dac" -> if (!thePlayer.onGround) thePlayer.motionY += 0.049999
            "mineplex" -> if (!thePlayer.onGround) MovementUtils.strafe(0.35f)
        }
    }

    @EventTarget
    fun onMove(event: MoveEvent?) {
        val thePlayer = mc.thePlayer ?: return

        if (glassValue.get() && (getBlock(BlockPos(thePlayer.posX, thePlayer.posY, thePlayer.posZ)))!is BlockPane)
            return
        if (!thePlayer.onGround) {
            if ("mineplex" == modeValue.get().toLowerCase()) {
                thePlayer.motionY += if (thePlayer.fallDistance == 0.0f) 0.0499 else 0.05
            }
        }
    }

    @EventTarget
    fun onJump(event: JumpEvent) {
        val thePlayer = mc.thePlayer ?: return
        if (modeValue equal "Timer"){
            event.motion = heightValue.get()
            return
        }

        if (glassValue.get() && (getBlock(BlockPos(thePlayer.posX, thePlayer.posY, thePlayer.posZ)))!is BlockPane)
            return
        when (modeValue.get().toLowerCase()) {
            "vanilla" -> event.motion = event.motion * heightValue.get()
            "mineplex" -> event.motion = 0.47f
        }
    }

    override val tag: String
        get() = modeValue.get()
}