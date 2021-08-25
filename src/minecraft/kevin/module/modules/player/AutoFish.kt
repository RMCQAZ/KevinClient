package kevin.module.modules.player

import kevin.event.EventTarget
import kevin.event.UpdateEvent
import kevin.module.Module
import kevin.module.ModuleCategory
import kevin.utils.MSTimer
import net.minecraft.item.ItemFishingRod

class AutoFish : Module("AutoFish", "Automatically catches fish when using a rod.", category = ModuleCategory.PLAYER) {
    private val rodOutTimer = MSTimer()

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val thePlayer = mc.thePlayer

        if (thePlayer?.heldItem == null || (thePlayer.heldItem!!.item)!is ItemFishingRod)
            return

        val fishEntity = thePlayer.fishEntity

        if (rodOutTimer.hasTimePassed(500L) && fishEntity == null || (fishEntity != null && fishEntity.motionX == 0.0 && fishEntity.motionZ == 0.0 && fishEntity.motionY != 0.0)) {
            mc.rightClickMouse()
            rodOutTimer.reset()
        }
    }
}