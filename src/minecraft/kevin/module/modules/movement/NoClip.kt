package kevin.module.modules.movement

import kevin.event.EventTarget
import kevin.event.UpdateEvent
import kevin.module.FloatValue
import kevin.module.Module
import kevin.module.ModuleCategory

class NoClip : Module("NoClip", "Allows you to freely move through walls.", category = ModuleCategory.MOVEMENT) {

    private val speedValue = FloatValue("Speed",0.25F,0.01F,1F)

    override fun onDisable() {
        mc.thePlayer?.noClip = false
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val thePlayer = mc.thePlayer ?: return

        thePlayer.noClip = true
        thePlayer.fallDistance = 0f
        thePlayer.onGround = false

        thePlayer.capabilities.isFlying = false
        thePlayer.motionX = 0.0
        thePlayer.motionY = 0.0
        thePlayer.motionZ = 0.0

        val speed = speedValue.get()

        thePlayer.jumpMovementFactor = speed

        if (mc.gameSettings.keyBindJump.isKeyDown)
            thePlayer.motionY += speed.toDouble()

        if (mc.gameSettings.keyBindSneak.isKeyDown)
            thePlayer.motionY -= speed.toDouble()
    }
}