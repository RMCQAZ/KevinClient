package kevin.module.modules.render

import kevin.event.*
import kevin.module.*
import kevin.utils.ColorUtils
import kevin.utils.RenderUtils
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.Entity
import net.minecraft.entity.projectile.EntityArrow
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import net.minecraft.util.Vec3
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.floor

class Trajectories : Module("Trajectories", description = "Shows the trajectory of the flying arrows.", category = ModuleCategory.RENDER) {

    private val colorMode = ListValue("ColorMode", arrayOf("Custom","Rainbow"),"Rainbow")
    private val cColorR = IntegerValue("R",255,0,255)
    private val cColorG = IntegerValue("G",0,0,255)
    private val cColorB = IntegerValue("B",0,0,255)
    private val cColorA = IntegerValue("A",0,0,255)
    private val lineWidth = FloatValue("LineWidth",2F,0.5F,3F)

    @EventTarget
    fun onRender3D(event: Render3DEvent){
        for (e in mc.theWorld?.loadedEntityList ?: return) {
            if (e !is EntityArrow) continue
            val thePlayer = mc.thePlayer ?: return
            val theWorld = mc.theWorld ?: return
            val renderManager = mc.renderManager
            val motionSlowdown = 0.99F
            val gravity = 0.05F
            val size = 0.3F

            var motionX = e.motionX
            var motionY = e.motionY
            var motionZ = e.motionZ

            var posX = e.posX
            var posY = e.posY
            var posZ = e.posZ

            val tessellator = Tessellator.getInstance()
            val worldRenderer = tessellator.worldRenderer
            GL11.glDepthMask(false)
            RenderUtils.enableGlCap(GL11.GL_BLEND, GL11.GL_LINE_SMOOTH)
            RenderUtils.disableGlCap(GL11.GL_DEPTH_TEST, GL11.GL_ALPHA_TEST, GL11.GL_TEXTURE_2D)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST)
            if (colorMode.get() == "Rainbow"){
                RenderUtils.glColor(ColorUtils.rainbow())
            }else{
                RenderUtils.glColor(Color(cColorR.get(), cColorG.get(), cColorB.get(), cColorA.get()))
            }
            GL11.glLineWidth(lineWidth.get())
            worldRenderer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION)
            var hasLanded = false
            while (!hasLanded && posY > 0.0) {
                val posBefore = Vec3(posX, posY, posZ)
                val posAfter = Vec3(posX + motionX, posY + motionY, posZ + motionZ)

                val landingPosition = theWorld.rayTraceBlocks(posBefore, posAfter,false, true,false)

                if (landingPosition != null) {
                    hasLanded = true
                }

                val arrowBox = AxisAlignedBB(posX - size, posY - size, posZ - size, posX + size,
                    posY + size, posZ + size).addCoord(motionX, motionY, motionZ).expand(1.0, 1.0, 1.0)

                val chunkMinX = floor((arrowBox.minX - 2.0) / 16.0).toInt()
                val chunkMaxX = floor((arrowBox.maxX + 2.0) / 16.0).toInt()
                val chunkMinZ = floor((arrowBox.minZ - 2.0) / 16.0).toInt()
                val chunkMaxZ = floor((arrowBox.maxZ + 2.0) / 16.0).toInt()

                val collidedEntities = mutableListOf<Entity>()

                for (x in chunkMinX..chunkMaxX)
                    for (z in chunkMinZ..chunkMaxZ)
                        theWorld.getChunkFromChunkCoords(x, z)
                            .getEntitiesWithinAABBForEntity(thePlayer, arrowBox, collidedEntities, null)

                for (possibleEntity in collidedEntities) {
                    if (possibleEntity.canBeCollidedWith()) {
                        hasLanded = true
                    }
                }

                posX += motionX
                posY += motionY
                posZ += motionZ

                val blockState = theWorld.getBlockState(BlockPos(posX, posY, posZ))

                if (blockState.block.material == Material.water) {
                    motionX *= 0.6
                    motionY *= 0.6
                    motionZ *= 0.6
                } else {
                    motionX *= motionSlowdown.toDouble()
                    motionY *= motionSlowdown.toDouble()
                    motionZ *= motionSlowdown.toDouble()
                }

                motionY -= gravity.toDouble()

                worldRenderer.pos(posX - renderManager.renderPosX, posY - renderManager.renderPosY,
                    posZ - renderManager.renderPosZ).endVertex()
            }
            tessellator.draw()
            GL11.glPushMatrix()
            GL11.glTranslated(posX - renderManager.renderPosX, posY - renderManager.renderPosY,
                posZ - renderManager.renderPosZ)
            GL11.glPopMatrix()
            GL11.glDepthMask(true)
            RenderUtils.resetCaps()
            GL11.glColor4f(1F, 1F, 1F, 1F)
        }
    }
}