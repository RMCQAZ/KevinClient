package kevin.module.modules.movement

import kevin.event.*
import kevin.main.Kevin
import kevin.module.*
import kevin.utils.MovementUtils
import net.minecraft.network.play.server.S12PacketEntityVelocity
import kotlin.math.cos
import kotlin.math.sin

class Speed : Module("Speed","Allows you to move faster.", category = ModuleCategory.MOVEMENT) {
    private val mode = ListValue("Mode", arrayOf("AAC5Long","AAC5Fast","YPort","AutoJump"),"AAC5Long")
    private val keepSprint = BooleanValue("KeepSprint",false)
    private val antiKnockback = BooleanValue("AntiKnockBack",false)
    private val antiKnockbackLong = FloatValue("AntiKnockBackLong",0F,0.00F,1.00F)
    private val antiKnockbackHigh = FloatValue("AntiKnockBackHigh",1F,0.00F,1.00F)

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

        //if (event.eventState == UpdateState.OnUpdate) return

        when(mode.get()){
            "AAC5Long" -> {
                if (!MovementUtils.isMoving) return
                if (mc.thePlayer.isInWater || mc.thePlayer.isInLava || mc.thePlayer.isOnLadder || mc.thePlayer.isInWeb) return

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
                if (!MovementUtils.isMoving) return
                if (mc.thePlayer.isInWater || mc.thePlayer.isInLava || mc.thePlayer.isOnLadder || mc.thePlayer.isInWeb) return
                if (mc.thePlayer.onGround) {
                    val strafe = Kevin.getInstance.moduleManager.getModule("Strafe") as Strafe
                    if (strafe.getToggle() && strafe.allDirectionsJumpValue.get()) {
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

    @EventTarget
    fun onPacket(event: PacketEvent){
        if (event.packet is S12PacketEntityVelocity && antiKnockback.get()){
            val thePlayer = mc.thePlayer ?: return
            val packet = event.packet
            val packetEntityVelocity = packet as S12PacketEntityVelocity
            if ((mc.theWorld?.getEntityByID(packetEntityVelocity.entityID) ?: return) != thePlayer) return
            val horizontal = antiKnockbackLong.get()
            val vertical = antiKnockbackHigh.get()
            if (horizontal == 0F && vertical == 0F) event.cancelEvent()
            packetEntityVelocity.motionX = (packetEntityVelocity.motionX * horizontal).toInt()
            packetEntityVelocity.motionY = (packetEntityVelocity.motionY * vertical).toInt()
            packetEntityVelocity.motionZ = (packetEntityVelocity.motionZ * horizontal).toInt()
        }
    }
}