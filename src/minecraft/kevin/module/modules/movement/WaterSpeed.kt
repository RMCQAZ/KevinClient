package kevin.module.modules.movement

import kevin.event.EventTarget
import kevin.event.UpdateEvent
import kevin.module.FloatValue
import kevin.module.Module
import kevin.module.ModuleCategory
import kevin.utils.BlockUtils.getBlock
import net.minecraft.block.BlockLiquid

class WaterSpeed : Module("WaterSpeed", "Allows you to swim faster.", category = ModuleCategory.MOVEMENT) {
    private val speedValue = FloatValue("Speed", 1.2f, 1.1f, 1.5f)

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        val thePlayer = mc.thePlayer ?: return

        if (thePlayer.isInWater && (getBlock(thePlayer.position))is BlockLiquid) {
            val speed = speedValue.get()

            thePlayer.motionX *= speed
            thePlayer.motionZ *= speed
        }
    }
}