package kevin.module.modules.player.nofalls.verus

import kevin.event.BlockBBEvent
import kevin.main.KevinClient
import kevin.module.modules.movement.Fly
import kevin.module.modules.player.nofalls.NoFallMode
import kevin.module.modules.render.FreeCam
import kevin.utils.BlockUtils
import net.minecraft.block.BlockLiquid
import net.minecraft.init.Blocks
import net.minecraft.util.AxisAlignedBB

object VerusNoFall : NoFallMode("Verus") {
    override fun onBlockBB(event: BlockBBEvent) {
        if (mc.thePlayer.fallDistance>2.6&&
            !KevinClient.moduleManager.getModule(FreeCam::class.java).state&&
            !KevinClient.moduleManager.getModule(Fly::class.java).state&&
            !(BlockUtils.collideBlock(
                mc.thePlayer!!.entityBoundingBox,
                fun(block: Any?) = block is BlockLiquid) || BlockUtils.collideBlock(
                AxisAlignedBB(
                    mc.thePlayer!!.entityBoundingBox.maxX,
                    mc.thePlayer!!.entityBoundingBox.maxY,
                    mc.thePlayer!!.entityBoundingBox.maxZ,
                    mc.thePlayer!!.entityBoundingBox.minX,
                    mc.thePlayer!!.entityBoundingBox.minY - 0.01,
                    mc.thePlayer!!.entityBoundingBox.minZ
                ), fun(block: Any?) = block is BlockLiquid))){
            if (event.block== Blocks.air&&
                event.y < mc.thePlayer!!.posY&&
                mc.thePlayer.getDistance(event.x.toDouble(),event.y.toDouble(),event.z.toDouble()) < 1.5) event.boundingBox =
                AxisAlignedBB(
                    event.x.toDouble(),
                    event.y.toDouble(),
                    event.z.toDouble(),
                    event.x + 1.0,
                    (mc.thePlayer.posY/0.125).toInt()*0.125,
                    event.z + 1.0
                )
        }
    }
}