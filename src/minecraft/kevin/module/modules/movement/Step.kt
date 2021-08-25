package kevin.module.modules.movement

import kevin.event.*
import kevin.main.Kevin
import kevin.module.*
import kevin.utils.BlockUtils.collideBlock
import kevin.utils.MSTimer
import kevin.utils.MovementUtils
import net.minecraft.block.Block
import net.minecraft.block.BlockLiquid
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.stats.StatList
import net.minecraft.util.AxisAlignedBB
import kotlin.math.cos
import kotlin.math.sin

class Step : Module("Step", "Allows you to step up/down blocks.", category = ModuleCategory.MOVEMENT) {

    private val modeValue = ListValue("Mode", arrayOf(
        "Vanilla", "Jump", "NCP", "MotionNCP", "OldNCP", "AAC", "LAAC", "AAC3.3.4", "Spartan", "Rewinside"
    ), "NCP")

    private val heightValue = FloatValue("Height", 1F, 0.6F, 10F)
    private val jumpHeightValue = FloatValue("JumpHeight", 0.42F, 0.37F, 0.42F)
    private val delayValue = IntegerValue("Delay", 0, 0, 500)

    private val reverse = BooleanValue("ReverseStep",false)
    private val motionValue = FloatValue("Motion", 1f, 0.21f, 1f)
    private var jumped = false

    @EventTarget(ignoreCondition = true)
    fun onJump(event: JumpEvent?) {
        jumped = true
    }

    /**
     * VALUES
     */

    private var isStep = false
    private var stepX = 0.0
    private var stepY = 0.0
    private var stepZ = 0.0

    private var ncpNextStep = 0
    private var spartanSwitch = false
    private var isAACStep = false

    private val timer = MSTimer()

    override fun onDisable() {
        val thePlayer = mc.thePlayer ?: return

        // Change step height back to default (0.5 is default)
        thePlayer.stepHeight = 0.5F
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val mode = modeValue.get()
        val thePlayer = mc.thePlayer ?: return

        // Motion steps
        when {
            mode.equals("jump", true) && thePlayer.isCollidedHorizontally && thePlayer.onGround
                    && !mc.gameSettings.keyBindJump.isKeyDown -> {
                fakeJump()
                thePlayer.motionY = jumpHeightValue.get().toDouble()
            }

            mode.equals("laac", true) -> if (thePlayer.isCollidedHorizontally && !thePlayer.isOnLadder
                && !thePlayer.isInWater && !thePlayer.isInLava && !thePlayer.isInWeb) {
                if (thePlayer.onGround && timer.hasTimePassed(delayValue.get().toLong())) {
                    isStep = true

                    fakeJump()
                    thePlayer.motionY += 0.620000001490116

                    val f = thePlayer.rotationYaw * 0.017453292F
                    thePlayer.motionX -= sin(f) * 0.2
                    thePlayer.motionZ += cos(f) * 0.2
                    timer.reset()
                }

                thePlayer.onGround = true
            } else
                isStep = false

            mode.equals("aac3.3.4", true) -> if (thePlayer.isCollidedHorizontally
                && MovementUtils.isMoving) {
                if (thePlayer.onGround && couldStep()) {
                    thePlayer.motionX *= 1.26
                    thePlayer.motionZ *= 1.26
                    thePlayer.jump()
                    isAACStep = true
                }

                if (isAACStep) {
                    thePlayer.motionY -= 0.015

                    if (!thePlayer.isUsingItem && thePlayer.movementInput.moveStrafe == 0F)
                        thePlayer.jumpMovementFactor = 0.3F
                }
            } else
                isAACStep = false
        }
        if (reverse.get()){
            if (thePlayer.onGround)
                jumped = false

            if (thePlayer.motionY > 0)
                jumped = true

            if (!getToggle())
                return

            if (collideBlock(thePlayer.entityBoundingBox, fun(block:Block?):Boolean {return block is BlockLiquid}) || collideBlock(AxisAlignedBB(thePlayer.entityBoundingBox.maxX, thePlayer.entityBoundingBox.maxY, thePlayer.entityBoundingBox.maxZ, thePlayer.entityBoundingBox.minX, thePlayer.entityBoundingBox.minY - 0.01, thePlayer.entityBoundingBox.minZ), fun(block:Block?):Boolean {return block is BlockLiquid})) return

            if (!mc.gameSettings.keyBindJump.isKeyDown && !thePlayer.onGround && !thePlayer.movementInput.jump && thePlayer.motionY <= 0.0 && thePlayer.fallDistance <= 1f && !jumped)
                thePlayer.motionY = (-motionValue.get()).toDouble()
        }
    }

    @EventTarget
    fun onMove(event: MoveEvent) {
        val mode = modeValue.get()
        val thePlayer = mc.thePlayer ?: return

        // Motion steps
        when {
            mode.equals("motionncp", true) && thePlayer.isCollidedHorizontally && !mc.gameSettings.keyBindJump.isKeyDown -> {
                when {
                    thePlayer.onGround && couldStep() -> {
                        fakeJump()
                        thePlayer.motionY = 0.0
                        event.y = 0.41999998688698
                        ncpNextStep = 1
                    }

                    ncpNextStep == 1 -> {
                        event.y = 0.7531999805212 - 0.41999998688698
                        ncpNextStep = 2
                    }

                    ncpNextStep == 2 -> {
                        val yaw = MovementUtils.direction

                        event.y = 1.001335979112147 - 0.7531999805212
                        event.x = -sin(yaw) * 0.7
                        event.z = cos(yaw) * 0.7

                        ncpNextStep = 0
                    }
                }
            }
        }
    }

    @EventTarget
    fun onStep(event: StepEvent) {
        val thePlayer = mc.thePlayer ?: return

        // Phase should disable step
        if (Kevin.getInstance.moduleManager.getModule("Phase")!!.getToggle()) {
            event.stepHeight = 0F
            return
        }

        // Some fly modes should disable step
        val fly = Kevin.getInstance.moduleManager.getModule("Fly") as Fly
        if (fly.getToggle()) {
            val flyMode = fly.mode.get()

            if (flyMode.equals("Hypixel", ignoreCase = true) ||
                flyMode.equals("OtherHypixel", ignoreCase = true) ||
                flyMode.equals("LatestHypixel", ignoreCase = true) ||
                flyMode.equals("Rewinside", ignoreCase = true) ||
                flyMode.equals("Mineplex", ignoreCase = true) && thePlayer.inventory.getCurrentItem() == null) {
                event.stepHeight = 0F
                return
            }
        }

        val mode = modeValue.get()

        // Set step to default in some cases
        if (!thePlayer.onGround || !timer.hasTimePassed(delayValue.get().toLong()) ||
            mode.equals("Jump", ignoreCase = true) || mode.equals("MotionNCP", ignoreCase = true)
            || mode.equals("LAAC", ignoreCase = true) || mode.equals("AAC3.3.4", ignoreCase = true)) {
            thePlayer.stepHeight = 0.5F
            event.stepHeight = 0.5F
            return
        }

        // Set step height
        val height = heightValue.get()
        thePlayer.stepHeight = height
        event.stepHeight = height

        // Detect possible step
        if (event.stepHeight > 0.5F) {
            isStep = true
            stepX = thePlayer.posX
            stepY = thePlayer.posY
            stepZ = thePlayer.posZ
        }
    }

    @EventTarget(ignoreCondition = true)
    fun onStepConfirm(event: StepConfirmEvent) {
        val thePlayer = mc.thePlayer

        if (thePlayer == null || !isStep) // Check if step
            return

        if (thePlayer.entityBoundingBox.minY - stepY > 0.5) { // Check if full block step
            val mode = modeValue.get()

            when {
                mode.equals("NCP", ignoreCase = true) || mode.equals("AAC", ignoreCase = true) -> {
                    fakeJump()

                    // Half legit step (1 packet missing) [COULD TRIGGER TOO MANY PACKETS]
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(stepX,
                        stepY + 0.41999998688698, stepZ, false))
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(stepX,
                        stepY + 0.7531999805212, stepZ, false))
                    timer.reset()
                }

                mode.equals("Spartan", ignoreCase = true) -> {
                    fakeJump()

                    if (spartanSwitch) {
                        // Vanilla step (3 packets) [COULD TRIGGER TOO MANY PACKETS]
                        mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(stepX,
                            stepY + 0.41999998688698, stepZ, false))
                        mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(stepX,
                            stepY + 0.7531999805212, stepZ, false))
                        mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(stepX,
                            stepY + 1.001335979112147, stepZ, false))
                    } else // Force step
                        mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(stepX,
                            stepY + 0.6, stepZ, false))

                    // Spartan allows one unlegit step so just swap between legit and unlegit
                    spartanSwitch = !spartanSwitch

                    // Reset timer
                    timer.reset()
                }

                mode.equals("Rewinside", ignoreCase = true) -> {
                    fakeJump()

                    // Vanilla step (3 packets) [COULD TRIGGER TOO MANY PACKETS]
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(stepX,
                        stepY + 0.41999998688698, stepZ, false))
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(stepX,
                        stepY + 0.7531999805212, stepZ, false))
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(stepX,
                        stepY + 1.001335979112147, stepZ, false))

                    // Reset timer
                    timer.reset()
                }
            }
        }

        isStep = false
        stepX = 0.0
        stepY = 0.0
        stepZ = 0.0
    }

    @EventTarget(ignoreCondition = true)
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if ((packet)is C03PacketPlayer && isStep && modeValue.get().equals("OldNCP", ignoreCase = true)) {
            packet.y += 0.07
            isStep = false
        }
    }

    // There could be some anti cheats which tries to detect step by checking for achievements and stuff
    private fun fakeJump() {
        val thePlayer = mc.thePlayer ?: return

        thePlayer.isAirBorne = true
        thePlayer.triggerAchievement(StatList.jumpStat)
    }

    private fun couldStep(): Boolean {
        val yaw = MovementUtils.direction
        val x = -sin(yaw) * 0.4
        val z = cos(yaw) * 0.4

        return mc.theWorld!!.getCollisionBoxes(mc.thePlayer!!.entityBoundingBox.offset(x, 1.001335979112147, z))
            .isEmpty()
    }

    override val tag: String
        get() = modeValue.get()
}