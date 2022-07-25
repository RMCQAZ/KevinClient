package kevin.module.modules.movement.flys.ncp

import kevin.event.PacketEvent
import kevin.event.UpdateEvent
import kevin.module.FloatValue
import kevin.module.modules.movement.flys.FlyMode
import kevin.utils.MovementUtils
import net.minecraft.network.play.client.C03PacketPlayer

object NCPFly : FlyMode("NCPFly") {
    private val motionValue = FloatValue("${valuePrefix}Motion", 0f, 0f, 1f)

    override fun onEnable() {
        if (!mc.thePlayer.onGround) {
            return
        }

        repeat(65) {
            mc.netHandler.addToSendQueue(
                C03PacketPlayer.C04PacketPlayerPosition(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY + 0.049,
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
        mc.netHandler.addToSendQueue(
            C03PacketPlayer.C04PacketPlayerPosition(
                mc.thePlayer.posX,
                mc.thePlayer.posY + 0.1,
                mc.thePlayer.posZ,
                true
            )
        )

        mc.thePlayer.motionX *= 0.1
        mc.thePlayer.motionZ *= 0.1
        mc.thePlayer.swingItem()
    }

    override fun onUpdate(event: UpdateEvent) {
        mc.thePlayer.motionY = (-motionValue.get()).toDouble()

        if (mc.gameSettings.keyBindSneak.isKeyDown) mc.thePlayer.motionY = -0.5
        MovementUtils.strafe()
    }

    override fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (packet is C03PacketPlayer) {
            packet.onGround = true
        }
    }
}