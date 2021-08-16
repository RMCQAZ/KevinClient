package kevin.module.modules.render

import kevin.event.EventTarget
import kevin.event.Render3DEvent
import kevin.module.*
import kevin.utils.BlockUtils.canBeClicked
import kevin.utils.ColorUtils.rainbow
import kevin.utils.RenderUtils
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.BlockPos
import org.lwjgl.opengl.GL11
import java.awt.Color

class BlockOverlay : Module("BlockOverlay", "Allows you to change the design of the block overlay.", category = ModuleCategory.RENDER) {
    private val colorRedValue = IntegerValue("R", 68, 0, 255)
    private val colorGreenValue = IntegerValue("G", 117, 0, 255)
    private val colorBlueValue = IntegerValue("B", 255, 0, 255)
    private val lineWidth = FloatValue("LineWidth",5F,2F,10F)
    private val colorRainbow = BooleanValue("Rainbow", false)
    private val onlyOutlineValue = BooleanValue("OnlyOutline", false)

    val currentBlock: BlockPos?
        get() {
            val blockPos = mc.objectMouseOver?.blockPos ?: return null

            if (canBeClicked(blockPos) && mc.theWorld!!.worldBorder.contains(blockPos))
                return blockPos

            return null
        }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        val blockPos = currentBlock ?: return

        val block = mc.theWorld!!.getBlockState(blockPos).block
        val partialTicks = event.partialTicks

        val color = if (colorRainbow.get()) rainbow(0.4F) else Color(colorRedValue.get(),
            colorGreenValue.get(), colorBlueValue.get(), (0.4F * 255).toInt())

        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO)
        RenderUtils.glColor(color)
        GL11.glLineWidth(2F)
        GlStateManager.disableTexture2D()
        GL11.glDepthMask(false)

        block.setBlockBoundsBasedOnState(mc.theWorld!!, blockPos)

        val thePlayer = mc.thePlayer ?: return

        val x = thePlayer.lastTickPosX + (thePlayer.posX - thePlayer.lastTickPosX) * partialTicks
        val y = thePlayer.lastTickPosY + (thePlayer.posY - thePlayer.lastTickPosY) * partialTicks
        val z = thePlayer.lastTickPosZ + (thePlayer.posZ - thePlayer.lastTickPosZ) * partialTicks

        val axisAlignedBB = block.getSelectedBoundingBox(mc.theWorld!!, blockPos)
            .expand(0.0020000000949949026, 0.0020000000949949026, 0.0020000000949949026)
            .offset(-x, -y, -z)

        GL11.glLineWidth(lineWidth.get())
        RenderUtils.drawSelectionBoundingBox(axisAlignedBB)
        if (!onlyOutlineValue.get()) RenderUtils.drawFilledBox(axisAlignedBB)
        GL11.glDepthMask(true)
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
        GlStateManager.resetColor()
    }
}