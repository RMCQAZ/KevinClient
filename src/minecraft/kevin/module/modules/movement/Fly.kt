package kevin.module.modules.movement

import kevin.event.EventTarget
import kevin.event.UpdateEvent
import kevin.module.*
import kevin.utils.MovementUtils
import net.minecraft.network.play.client.C00PacketKeepAlive
import org.lwjgl.input.Keyboard

class Fly : Module("Fly","Allow you fly", Keyboard.KEY_F,ModuleCategory.MOVEMENT) {
    private val speed = FloatValue("Speed",2F,0.5F,5F)
    val mode = ListValue("Mode", arrayOf("Vanilla","Creative"),"Vanilla")
    private val resetMotion = BooleanValue("ResetMotion",false)
    private val keepAlive = BooleanValue("KeepAlive",false)

    private var isFlying = false

    override fun onEnable() {
        isFlying = mc.thePlayer.capabilities.isFlying
        when(mode.get()){
            "Creative" -> mc.thePlayer.capabilities.allowFlying = true
        }
    }

    override fun onDisable() {
        mc.thePlayer.capabilities.isFlying = isFlying
        when(mode.get()){
            "Creative" -> mc.thePlayer.capabilities.allowFlying = mc.playerController.isInCreativeMode || mc.playerController.isSpectatorMode
            "Vanilla" -> {
                if (resetMotion.get()) {
                    mc.thePlayer.motionY = 0.0
                    mc.thePlayer.motionX = 0.0
                    mc.thePlayer.motionZ = 0.0
                }
            }
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent){
        when(mode.get()){
            "Vanilla" -> {
                if (keepAlive.get()) mc.netHandler.addToSendQueue(C00PacketKeepAlive())
                mc.thePlayer.motionY = 0.0
                mc.thePlayer.motionX = 0.0
                mc.thePlayer.motionZ = 0.0
                mc.thePlayer.capabilities.isFlying = false
                if (mc.gameSettings.keyBindJump.isKeyDown) mc.thePlayer.motionY += speed.get()
                if (mc.gameSettings.keyBindSneak.isKeyDown) mc.thePlayer.motionY -= speed.get()
                MovementUtils.strafe(speed.get())
            }
            "Creative" -> if (!mc.thePlayer.capabilities.allowFlying) mc.thePlayer.capabilities.allowFlying = true
        }
    }

    override val tag: String
        get() = mode.get()
}