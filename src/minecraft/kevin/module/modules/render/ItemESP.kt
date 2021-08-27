package kevin.module.modules.render

import kevin.event.EventTarget
import kevin.event.Render2DEvent
import kevin.event.Render3DEvent
import kevin.module.*
import kevin.utils.ColorUtils.rainbow
import kevin.utils.GlowShader
import kevin.utils.OutlineShader
import kevin.utils.RenderUtils
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.projectile.EntityArrow
import java.awt.Color

class ItemESP : Module("ItemESP", "Allows you to see items through walls.", category = ModuleCategory.RENDER) {
    private val modeValue = ListValue("Mode", arrayOf("Box", "OtherBox", "ShaderOutline", "ShaderGlow"), "Box")
    private val shaderRadiusValue = FloatValue("ShaderRadius", 2f, 0.5f, 5f)
    private val colorRedValue = IntegerValue("R", 0, 0, 255)
    private val colorGreenValue = IntegerValue("G", 255, 0, 255)
    private val colorBlueValue = IntegerValue("B", 0, 0, 255)
    private val colorRainbow = BooleanValue("Rainbow", true)

    private fun getColor(): Color {
        return if (colorRainbow.get()) rainbow() else Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get())
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent?) {
        val color=getColor()
        for (entity in mc.theWorld!!.loadedEntityList) {
            if (!((entity)is EntityItem || (entity)is EntityArrow)) continue
            when (modeValue.get().toLowerCase()) {
                "box" -> RenderUtils.drawEntityBox(entity, color, true)
                "otherbox" -> RenderUtils.drawEntityBox(entity, color, false)
            }
        }
    }

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        val shader = (if (modeValue.get().equals("shaderoutline", ignoreCase = true)) OutlineShader.OUTLINE_SHADER else if (modeValue.get().equals("shaderglow", ignoreCase = true)) GlowShader.GLOW_SHADER else null)
            ?: return
        val partialTicks = event.partialTicks

        shader.startDraw(partialTicks)

        try {
            for (entity in mc.theWorld!!.loadedEntityList) {
                if (!((entity)is EntityItem || (entity)is EntityArrow)) continue
                mc.renderManager.renderEntityStatic(entity, event.partialTicks, true)
            }
        } catch (ex: Exception) {
            println("An error occurred while rendering all item entities for shader esp $ex")
        }

        shader.stopDraw(getColor(),shaderRadiusValue.get(),1f)
    }
}