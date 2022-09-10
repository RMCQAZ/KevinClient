package kevin.module.modules.player.nofalls.aac

import kevin.event.JumpEvent
import kevin.event.MoveEvent
import kevin.event.UpdateEvent
import kevin.module.modules.player.nofalls.NoFallMode
import kevin.utils.BlockUtils
import net.minecraft.block.BlockLiquid
import net.minecraft.util.AxisAlignedBB

object LAACNoFall : NoFallMode("LAAC") {
    private var jumped = false
    override fun onUpdate(event: UpdateEvent) {
        if (mc.thePlayer!!.onGround)
            jumped = false
        if (mc.thePlayer!!.motionY > 0)
            jumped = true
    }
    override fun onJump(event: JumpEvent) {
        jumped = true
    }
    override fun onNoFall(event: UpdateEvent) {
        if (!jumped && mc.thePlayer!!.onGround && !mc.thePlayer!!.isOnLadder && !mc.thePlayer!!.isInWater
            && !mc.thePlayer!!.isInWeb) mc.thePlayer!!.motionY = (-6).toDouble()
    }
    override fun onMove(event: MoveEvent) {
        if (BlockUtils.collideBlock(
                mc.thePlayer!!.entityBoundingBox,
                fun(block: Any?) = block is BlockLiquid) || BlockUtils.collideBlock(
                AxisAlignedBB(
                    mc.thePlayer!!.entityBoundingBox.maxX,
                    mc.thePlayer!!.entityBoundingBox.maxY,
                    mc.thePlayer!!.entityBoundingBox.maxZ,
                    mc.thePlayer!!.entityBoundingBox.minX,
                    mc.thePlayer!!.entityBoundingBox.minY - 0.01,
                    mc.thePlayer!!.entityBoundingBox.minZ
                ), fun(block: Any?) = block is BlockLiquid)
        )
            return
        if (!jumped && !mc.thePlayer!!.onGround && !mc.thePlayer!!.isOnLadder && !mc.thePlayer!!.isInWater && !mc.thePlayer!!.isInWeb && mc.thePlayer!!.motionY < 0.0) {
            event.x = 0.0
            event.z = 0.0
        }
    }
}