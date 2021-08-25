package kevin.module.modules.movement

import kevin.event.EventTarget
import kevin.event.UpdateEvent
import kevin.module.BooleanValue
import kevin.module.Module
import kevin.module.ModuleCategory
import kevin.utils.BlockUtils
import net.minecraft.block.BlockLadder
import net.minecraft.block.BlockVine
import net.minecraft.util.BlockPos

class AirLadder : Module("AirLadder", "Allows you to climb up ladders/vines without touching them.", category = ModuleCategory.MOVEMENT) {
    private val antiCheat = BooleanValue("Bypass",true)
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val thePlayer = mc.thePlayer ?: return
        if(antiCheat.get()){
            if(thePlayer.isOnLadder&&mc.gameSettings.keyBindJump.pressed) thePlayer.motionY = 0.11
        }else{
            if ((BlockUtils.getBlock(BlockPos(thePlayer.posX, thePlayer.posY + 1, thePlayer.posZ)))is BlockLadder
                && thePlayer.isCollidedHorizontally
                || (BlockUtils.getBlock(BlockPos(thePlayer.posX, thePlayer.posY, thePlayer.posZ)))is BlockVine
                || (BlockUtils.getBlock(BlockPos(thePlayer.posX, thePlayer.posY + 1, thePlayer.posZ))) is BlockVine) {
                thePlayer.motionY = 0.15
                thePlayer.motionX = 0.0
                thePlayer.motionZ = 0.0
            }
        }
    }
}