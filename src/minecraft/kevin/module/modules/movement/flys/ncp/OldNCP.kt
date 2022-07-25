package kevin.module.modules.movement.flys.ncp

import kevin.event.UpdateEvent
import kevin.module.modules.movement.flys.FlyMode
import kevin.utils.MovementUtils
import net.minecraft.network.play.client.C03PacketPlayer

object OldNCP : FlyMode("OldNCPFly") {
    override fun onEnable() {
        if (!mc.thePlayer.onGround) {
            return
        }

        repeat(3) {
            mc.netHandler.addToSendQueue(
                C03PacketPlayer.C04PacketPlayerPosition(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY + 1.01,
                    mc.thePlayer.posZ,
                    false
                )
            )
            mc.netHandler.addToSendQueue(
                C03PacketPlayer.C04PacketPlayerPosition(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY,
                    mc.thePlayer.posZ,
                    false
                )
            )
        }

        mc.thePlayer.jump()
        mc.thePlayer.swingItem()
    }

    override fun onUpdate(event: UpdateEvent) {
        if (fly.launchY > mc.thePlayer.posY) {
            mc.thePlayer.motionY = -0.000000000000000000000000000000001
        }

        if (mc.gameSettings.keyBindSneak.isKeyDown) {
            mc.thePlayer.motionY = -0.2
        }

        if (mc.gameSettings.keyBindJump.isKeyDown && mc.thePlayer.posY < fly.launchY - 0.1) {
            mc.thePlayer.motionY = 0.2
        }

        MovementUtils.strafe()
    }
}