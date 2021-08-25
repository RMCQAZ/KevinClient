package kevin.module.modules.movement

import kevin.event.EventTarget
import kevin.event.UpdateEvent
import kevin.module.FloatValue
import kevin.module.ListValue
import kevin.module.Module
import kevin.module.ModuleCategory
import kevin.utils.MovementUtils
import org.lwjgl.input.Keyboard

class Fly : Module("Fly","Allow you fly", Keyboard.KEY_F,ModuleCategory.MOVEMENT) {
    private val speed = FloatValue("Speed",2F,0.5F,5F)
    val mode = ListValue("Mode", arrayOf("Vanilla"),"Vanilla")
    @EventTarget
    fun onUpdate(event: UpdateEvent){
        when(mode.get()){
            "Vanilla" -> {
                mc.thePlayer.motionY = 0.0
                mc.thePlayer.motionX = 0.0
                mc.thePlayer.motionZ = 0.0
                mc.thePlayer.capabilities.isFlying = false
                if (mc.gameSettings.keyBindJump.isKeyDown) mc.thePlayer.motionY += speed.get()
                if (mc.gameSettings.keyBindSneak.isKeyDown) mc.thePlayer.motionY -= speed.get()
                MovementUtils.strafe(speed.get())
            }
        }
    }

    override val tag: String
        get() = mode.get()
}