package kevin.module.modules.movement

import kevin.event.*
import kevin.module.*
import kevin.utils.MSTimer
import kevin.utils.MovementUtils
import net.minecraft.network.play.server.S27PacketExplosion
import net.minecraft.util.EnumFacing
import kotlin.math.cos
import kotlin.math.sin

class LongJump : Module("LongJump", "Allows you to jump further.", category = ModuleCategory.MOVEMENT) {
    private val modeValue = ListValue("Mode", arrayOf("NCP", "AACv1", "AACv2", "AACv3", "Mineplex", "Mineplex2", "Mineplex3", "Redesky", "ExplosionBoost", "Timer"), "NCP")
    private val ncpBoostValue = FloatValue("NCPBoost", 4.25f, 1f, 10f)
    private val autoJumpValue = BooleanValue("AutoJump", false)
    private val explosionBoostHigh = FloatValue("ExplosionBoostHigh",0.00F,0.01F,1F)
    private val explosionBoostLong = FloatValue("ExplosionBoostLong",0.25F,0.01F,1F)
    private val timer = FloatValue("Timer",0.1F,0.1F,1F)
    private val speed = FloatValue("Speed",3F,0.5F,5F)
    private val delay = IntegerValue("Delay",0,0,500)
    private var jumped = false
    private var canBoost = false
    private var teleported = false
    private var canMineplexBoost = false
    private var explosion = false
    private val msTimer = MSTimer()
    private var timerState = 0

    override fun onEnable() {
        if (modeValue equal "Timer"){
            mc.timer.timerSpeed = timer.get()
            msTimer.reset()
        }
    }
    override fun onDisable() {
        if (modeValue equal "Timer") {
            mc.timer.timerSpeed = 1F
            timerState = 0
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        val thePlayer = mc.thePlayer ?: return

        if ((modeValue equal "Timer")&&msTimer.hasTimePassed(delay.get().toLong())){
            when(timerState){
                0 -> {
                    val radiansYaw = mc.thePlayer!!.rotationYaw * Math.PI / 180
                    mc.thePlayer!!.motionX = speed.get() * -sin(radiansYaw)
                    mc.thePlayer!!.motionZ = speed.get() * cos(radiansYaw)
                    if (mc.thePlayer.onGround) mc.thePlayer.jump()
                    timerState = 1
                    return
                }
                1 -> {
                    mc.timer.timerSpeed = 1F
                    timerState = 2
                    msTimer.reset()
                    return
                }
                2 -> {
                    if (msTimer.hasTimePassed(300)){
                        mc.thePlayer!!.motionX = .0
                        mc.thePlayer!!.motionZ = .0
                        state = false
                    }
                }
            }
        }

        if (jumped) {
            val mode = modeValue.get()

            if (thePlayer.onGround || thePlayer.capabilities.isFlying) {
                jumped = false
                canMineplexBoost = false

                if (mode.equals("NCP", ignoreCase = true)) {
                    thePlayer.motionX = 0.0
                    thePlayer.motionZ = 0.0
                }
                return
            }
            run {
                when (mode.toLowerCase()) {
                    "ncp" -> {
                        MovementUtils.strafe(MovementUtils.speed * if (canBoost) ncpBoostValue.get() else 1f)
                        canBoost = false
                    }
                    "aacv1" -> {
                        thePlayer.motionY += 0.05999
                        MovementUtils.strafe(MovementUtils.speed * 1.08f)
                    }
                    "aacv2", "mineplex3" -> {
                        thePlayer.jumpMovementFactor = 0.09f
                        thePlayer.motionY += 0.0132099999999999999999999999999
                        thePlayer.jumpMovementFactor = 0.08f
                        MovementUtils.strafe()
                    }
                    "aacv3" -> {
                        if (thePlayer.fallDistance > 0.5f && !teleported) {
                            val value = 3.0
                            val horizontalFacing = thePlayer.horizontalFacing
                            var x = 0.0
                            var z = 0.0

                            when {
                                horizontalFacing==EnumFacing.NORTH -> z = -value
                                horizontalFacing==EnumFacing.EAST -> x = +value
                                horizontalFacing==EnumFacing.SOUTH -> z = +value
                                horizontalFacing==EnumFacing.WEST -> x = -value
                                else -> {
                                }
                            }

                            thePlayer.setPosition(thePlayer.posX + x, thePlayer.posY, thePlayer.posZ + z)
                            teleported = true
                        }
                    }
                    "mineplex" -> {
                        thePlayer.motionY += 0.0132099999999999999999999999999
                        thePlayer.jumpMovementFactor = 0.08f
                        MovementUtils.strafe()
                    }
                    "mineplex2" -> {
                        if (!canMineplexBoost)
                            return@run

                        thePlayer.jumpMovementFactor = 0.1f
                        if (thePlayer.fallDistance > 1.5f) {
                            thePlayer.jumpMovementFactor = 0f
                            thePlayer.motionY = (-10f).toDouble()
                        }

                        MovementUtils.strafe()
                    }
                    "redesky" -> {
                        thePlayer.jumpMovementFactor = 0.15f
                        thePlayer.motionY += 0.05f
                    }
                }
            }
        }
        if (autoJumpValue.get() && thePlayer.onGround && MovementUtils.isMoving) {
            jumped = true
            thePlayer.jump()
        }
        if (modeValue.get().equals("ExplosionBoost",true)){
            if (explosion){
                mc.thePlayer.motionX *= 1F + explosionBoostLong.get()
                mc.thePlayer.motionY *= 1F + explosionBoostHigh.get()
                mc.thePlayer.motionZ *= 1F + explosionBoostLong.get()
                explosion = false
            }
        }
    }

    @EventTarget
    fun onMove(event: MoveEvent) {
        val thePlayer = mc.thePlayer ?: return
        val mode = modeValue.get()

        if (mode.equals("mineplex3", ignoreCase = true)) {
            if (thePlayer.fallDistance != 0.0f)
                thePlayer.motionY += 0.037
        } else if (mode.equals("ncp", ignoreCase = true) && !MovementUtils.isMoving && jumped) {
            thePlayer.motionX = 0.0
            thePlayer.motionZ = 0.0
            event.zeroXZ()
        }
    }

    @EventTarget(ignoreCondition = true)
    fun onJump(event: JumpEvent) {
        jumped = true
        canBoost = true
        teleported = false

        if (state) {
            when (modeValue.get().toLowerCase()) {
                "mineplex" -> event.motion = event.motion * 4.08f
                "mineplex2" -> {
                    if (mc.thePlayer!!.isCollidedHorizontally) {
                        event.motion = 2.31f
                        canMineplexBoost = true
                        mc.thePlayer!!.onGround = false
                    }
                }
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent){
        if (event.packet is S27PacketExplosion)
            if (event.packet.func_149149_c() != 0F ||
                event.packet.func_149144_d() != 0F ||
                event.packet.func_149147_e() != 0F) explosion = true
    }

    override val tag: String
        get() = modeValue.get()
}