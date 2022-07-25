package kevin.module.modules.movement.flys.ncp

import kevin.event.UpdateEvent
import kevin.module.modules.movement.flys.FlyMode

object NCPNew : FlyMode("NCPNew") {
    override fun onEnable() {
        if (mc.thePlayer.onGround && mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer,mc.thePlayer.entityBoundingBox.offset(.0, .2, .0).expand(.0, .0, .0)).isEmpty()) {
            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + .2, mc.thePlayer.posZ)
        }
        mc.thePlayer.motionX = .0
        mc.thePlayer.motionY = .0
        mc.thePlayer.motionZ = .0
        mc.thePlayer.speedInAir = 0F
    }

    override fun onDisable() {
        mc.thePlayer.speedInAir = .02F
    }
    override fun onUpdate(event: UpdateEvent) {
        mc.thePlayer.motionX = .0
        mc.thePlayer.motionY = .0
        mc.thePlayer.motionZ = .0
    }
}