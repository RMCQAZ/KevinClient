package kevin.module.modules.movement.flys.vanilla

import kevin.event.UpdateEvent
import kevin.module.modules.movement.flys.FlyMode
import kevin.utils.MovementUtils
import net.minecraft.network.play.client.C00PacketKeepAlive

object Vanilla : FlyMode("Vanilla") {
    override fun onUpdate(event: UpdateEvent) {
        if (fly.keepAlive.get()) mc.netHandler.addToSendQueue(C00PacketKeepAlive())
        mc.thePlayer.motionY = 0.0
        mc.thePlayer.motionX = 0.0
        mc.thePlayer.motionZ = 0.0
        mc.thePlayer.capabilities.isFlying = false
        if (mc.gameSettings.keyBindJump.isKeyDown) mc.thePlayer.motionY += fly.speed.get()
        if (mc.gameSettings.keyBindSneak.isKeyDown) mc.thePlayer.motionY -= fly.speed.get()
        MovementUtils.strafe(fly.speed.get())
    }
}