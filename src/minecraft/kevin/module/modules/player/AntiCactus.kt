package kevin.module.modules.player

import kevin.event.BlockBBEvent
import kevin.event.EventTarget
import kevin.module.Module
import kevin.module.ModuleCategory
import net.minecraft.block.BlockCactus
import net.minecraft.util.AxisAlignedBB

class AntiCactus : Module(name = "AntiCactus", description = "Prevents cactuses from damaging you.", category = ModuleCategory.PLAYER) {
    @EventTarget
    fun onBlockBB(event: BlockBBEvent) {
        if (event.block is BlockCactus)
            event.boundingBox = AxisAlignedBB(event.x.toDouble(), event.y.toDouble(), event.z.toDouble(),
                event.x + 1.0, event.y + 1.0, event.z + 1.0)
    }
}