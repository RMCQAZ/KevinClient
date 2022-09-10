package kevin.module.modules.render

import kevin.main.KevinClient
import kevin.module.FloatValue
import kevin.module.ListValue
import kevin.module.Module
import kevin.module.ModuleCategory

object RenderSettings : Module("RenderSettings", "Some render settings.", category = ModuleCategory.RENDER) {

    private val fontRendererValue = ListValue("FontRenderer", arrayOf("Glyph", "Vector"), "Glyph")
    val fontEpsilonValue = FloatValue("FontVectorEpsilon", 0.5f, 0f, 1.5f)
    private val getFontRender: String
    get() = if (KevinClient.isStarting) "Glyph" else fontRendererValue.get()
    val useGlyphFontRenderer: Boolean
    get() = getFontRender == "Glyph"

    override fun onEnable() {
        this.state = false
    }
}