package kevin.module.modules.player

import kevin.event.EventTarget
import kevin.event.UpdateEvent
import kevin.module.Module
import kevin.module.ModuleCategory
import net.minecraft.client.settings.GameSettings
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos

class AutoSneak : Module("AutoSneak", description = "Automatically sneak at the edge of the block.", category = ModuleCategory.PLAYER) {
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val thePlayer = mc.thePlayer ?: return
        if (mc.thePlayer.onGround)
            mc.gameSettings.keyBindSneak.pressed = mc.theWorld!!.getBlockState(BlockPos(thePlayer.posX, thePlayer.posY - 1.0, thePlayer.posZ)).block == Blocks.air
    }

    override fun onDisable() {
        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindSneak))
            mc.gameSettings.keyBindSneak.pressed = false
    }
}