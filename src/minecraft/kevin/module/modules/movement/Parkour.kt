package kevin.module.modules.movement

import kevin.event.EventTarget
import kevin.event.UpdateEvent
import kevin.module.Module
import kevin.module.ModuleCategory
import kevin.utils.MovementUtils

class Parkour: Module("Parkour", "Automatically jumps when reaching the edge of a block.", category = ModuleCategory.MOVEMENT) {
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val thePlayer = mc.thePlayer ?: return

        if (MovementUtils.isMoving && thePlayer.onGround && !thePlayer.isSneaking && !mc.gameSettings.keyBindSneak.isKeyDown && !mc.gameSettings.keyBindJump.isKeyDown &&
            mc.theWorld!!.getCollidingBoundingBoxes(thePlayer, thePlayer.entityBoundingBox
                .offset(0.0, -0.5, 0.0).expand(-0.001, 0.0, -0.001)).isEmpty())
            thePlayer.jump()
    }
}