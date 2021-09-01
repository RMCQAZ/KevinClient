package kevin.module.modules.combat

import kevin.event.EventTarget
import kevin.event.JumpEvent
import kevin.event.PacketEvent
import kevin.event.UpdateEvent
import kevin.main.Kevin
import kevin.module.*
import kevin.utils.MSTimer
import kevin.utils.MovementUtils
import kevin.utils.PacketUtils
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.network.play.server.S27PacketExplosion
import kotlin.math.cos
import kotlin.math.sin

class AntiKnockback : Module("AntiKnockback","Allows you to modify the amount of knockback you take.", category = ModuleCategory.COMBAT) {
    private val horizontalValue = FloatValue("Horizontal", 0F, 0F, 1F)
    private val verticalValue = FloatValue("Vertical", 0F, 0F, 1F)
    private val modeValue = ListValue("Mode", arrayOf("Simple", "AAC", "AACPush", "AACZero", "AACv4",
        "Reverse", "SmoothReverse", "Jump", "Glitch", "AAC5"), "Simple")

    // Reverse
    private val reverseStrengthValue = FloatValue("ReverseStrength", 1F, 0.1F, 1F)
    private val reverse2StrengthValue = FloatValue("SmoothReverseStrength", 0.05F, 0.02F, 0.1F)

    // AAC Push
    private val aacPushXZReducerValue = FloatValue("AACPushXZReducer", 2F, 1F, 3F)
    private val aacPushYReducerValue = BooleanValue("AACPushYReducer", true)

    // AAc v4
    private val aacv4MotionReducerValue = FloatValue("AACv4MotionReducer", 0.62F,0F,1F)

    private val cancelExplosionPacket = BooleanValue("CancelExplosionPacket",false)
    private val explosionCheck = BooleanValue("ExplosionCheck",true)

    private var velocityTimer = MSTimer()
    private var velocityInput = false

    private var explosion = false

    // SmoothReverse
    private var reverseHurt = false

    // AACPush
    private var jump = false

    override val tag: String
        get() = if (modeValue.get() == "Simple") "H:${horizontalValue.get()*100}% V:${verticalValue.get()*100}%" else modeValue.get()

    override fun onDisable() {
        mc.thePlayer?.speedInAir = 0.02F
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val thePlayer = mc.thePlayer ?: return

        if (thePlayer.isInWater || thePlayer.isInLava || thePlayer.isInWeb)
            return

        when (modeValue.get().toLowerCase()) {
            "jump" -> if (thePlayer.hurtTime > 0 && thePlayer.onGround) {
                thePlayer.motionY = 0.42

                val yaw = thePlayer.rotationYaw * 0.017453292F

                thePlayer.motionX -= sin(yaw) * 0.2
                thePlayer.motionZ += cos(yaw) * 0.2
            }

            "glitch" -> {
                thePlayer.noClip = velocityInput

                if (thePlayer.hurtTime == 7)
                    thePlayer.motionY = 0.4

                velocityInput = false
            }

            "reverse" -> {
                if (!velocityInput)
                    return

                if (!thePlayer.onGround) {
                    MovementUtils.strafe(MovementUtils.speed * reverseStrengthValue.get())
                } else if (velocityTimer.hasTimePassed(80L))
                    velocityInput = false
            }

            "smoothreverse" -> {
                if (!velocityInput) {
                    thePlayer.speedInAir = 0.02F
                    return
                }

                if (thePlayer.hurtTime > 0)
                    reverseHurt = true

                if (!thePlayer.onGround) {
                    if (reverseHurt)
                        thePlayer.speedInAir = reverse2StrengthValue.get()
                } else if (velocityTimer.hasTimePassed(80L)) {
                    velocityInput = false
                    reverseHurt = false
                }
            }

            "aac" -> if (velocityInput && velocityTimer.hasTimePassed(80L)) {
                thePlayer.motionX *= horizontalValue.get()
                thePlayer.motionZ *= horizontalValue.get()
                //mc.thePlayer.motionY *= verticalValue.get() ?
                velocityInput = false
            }

            "aacv4" -> {
                if (thePlayer.hurtTime>0 && !thePlayer.onGround){
                    val reduce=aacv4MotionReducerValue.get();
                    thePlayer.motionX *= reduce
                    thePlayer.motionZ *= reduce
                }
            }

            "aacpush" -> {
                if (jump) {
                    if (thePlayer.onGround)
                        jump = false
                } else {
                    // Strafe
                    if (thePlayer.hurtTime > 0 && thePlayer.motionX != 0.0 && thePlayer.motionZ != 0.0)
                        thePlayer.onGround = true

                    // Reduce Y
                    if (thePlayer.hurtResistantTime > 0 && aacPushYReducerValue.get()
                        && !Kevin.getInstance.moduleManager.getModule("Speed")!!.getToggle())
                        thePlayer.motionY -= 0.014999993
                }

                // Reduce XZ
                if (thePlayer.hurtResistantTime >= 19) {
                    val reduce = aacPushXZReducerValue.get()

                    thePlayer.motionX /= reduce
                    thePlayer.motionZ /= reduce
                }
            }

            "aaczero" -> if (thePlayer.hurtTime > 0) {
                if (!velocityInput || thePlayer.onGround || thePlayer.fallDistance > 2F)
                    return

                thePlayer.motionY -= 1.0
                thePlayer.isAirBorne = true
                thePlayer.onGround = true
            } else
                velocityInput = false
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val thePlayer = mc.thePlayer ?: return

        val packet = event.packet

        if (packet is S12PacketEntityVelocity) {
            val packetEntityVelocity = packet as S12PacketEntityVelocity


            if ((mc.theWorld?.getEntityByID(packetEntityVelocity.entityID) ?: return) != thePlayer)
                return

            velocityTimer.reset()

            when (modeValue.get().toLowerCase()) {
                "simple" -> {
                    if (explosion && explosionCheck.get()) {explosion=false;return}

                    val horizontal = horizontalValue.get()
                    val vertical = verticalValue.get()

                    if (horizontal == 0F && vertical == 0F)
                        event.cancelEvent()

                    packetEntityVelocity.motionX = (packetEntityVelocity.motionX * horizontal).toInt()
                    packetEntityVelocity.motionY = (packetEntityVelocity.motionY * vertical).toInt()
                    packetEntityVelocity.motionZ = (packetEntityVelocity.motionZ * horizontal).toInt()
                }

                "aac", "reverse", "smoothreverse", "aaczero" -> velocityInput = true

                "aac5" -> {
                    PacketUtils.sendPacketNoEvent(C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, Double.MAX_VALUE, mc.thePlayer.posZ, mc.thePlayer.onGround))
                }

                "glitch" -> {
                    if (!thePlayer.onGround)
                        return

                    velocityInput = true
                    event.cancelEvent()
                }
            }
        } else if (packet is S27PacketExplosion) {
            if (packet.func_149149_c() != 0F ||
                packet.func_149144_d() != 0F ||
                packet.func_149147_e() != 0F) explosion = true
            if (cancelExplosionPacket.get()) event.cancelEvent()
        }
    }

    @EventTarget
    fun onJump(event: JumpEvent) {
        val thePlayer = mc.thePlayer

        if (thePlayer == null || thePlayer.isInWater || thePlayer.isInLava || thePlayer.isInWeb)
            return

        when (modeValue.get().toLowerCase()) {
            "aacpush" -> {
                jump = true

                if (!thePlayer.isCollidedVertically)
                    event.cancelEvent()
            }
            "aaczero" -> if (thePlayer.hurtTime > 0)
                event.cancelEvent()
        }
    }
}