package kevin.hud.element.elements

import kevin.hud.designer.GuiHudDesigner
import kevin.hud.element.Border
import kevin.hud.element.Element
import kevin.hud.element.ElementInfo
import kevin.hud.element.Side
import kevin.main.Kevin
import kevin.module.*
import kevin.utils.AnimationUtils
import kevin.utils.FontManager
import kevin.utils.RainbowShader
import kevin.utils.RenderUtils
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11
import java.awt.Color

@ElementInfo(name = "Arraylist", single = true)
class Arraylist(x: Double = 1.0, y: Double = 2.0, scale: Float = 1F,
                side: Side = Side(Side.Horizontal.RIGHT, Side.Vertical.UP)) : Element(x, y, scale, side) {

    private val fontValue = Kevin.getInstance.fontManager.font40!!
    private var modules = emptyList<Module>()

    private val tagMode = ListValue("ArrayList-TagMode", arrayOf("<>","[]","None"),"<>")
    private val arrayListRainbowX = FloatValue("ArrayList-Rainbow-X",-1000F, -2000F, 2000F)
    private val arrayListRainbowY = FloatValue("ArrayList-Rainbow-Y",-1000F, -2000F, 2000F)
    private val arrayListTextColorMode = ListValue("ArrayList-TextColor-Mode", arrayOf("Custom","Random","Rainbow"),"Rainbow")
    private val arraylistTextCustomRed = IntegerValue("ArrayList-TextColor-CustomRed",0,0,255)
    private val arraylistTextCustomGreen = IntegerValue("ArrayList-TextColor-CustomGreen",0,0,255)
    private val arraylistTextCustomBlue = IntegerValue("ArrayList-TextColor-CustomBlue",0,0,255)
    private val arrayListTags = BooleanValue("ArrayList-Tags", true)
    private val arrayListTagsShadow = BooleanValue("ArrayList-ShadowText", true)
    private val arrayListBackgroundColorModeValue = ListValue("ArrayList-Background-ColorMode", arrayOf("Custom", "Random", "Rainbow"), "Custom")
    private val arrayListBackgroundColorRedValue = IntegerValue("ArrayList-Background-R", 0, 0, 255)
    private val arrayListBackgroundColorGreenValue = IntegerValue("ArrayList-Background-G", 0, 0, 255)
    private val arrayListBackgroundColorBlueValue = IntegerValue("ArrayList-Background-B", 0, 0, 255)
    private val arrayListBackgroundColorAlphaValue = IntegerValue("ArrayList-Background-Alpha", 0, 0, 255)
    private val arrayListUpperCaseValue = BooleanValue("ArrayList-UpperCase", false)
    private val arrayListSpaceValue = FloatValue("ArrayList-Space", 0F, 0F, 5F)
    private val arrayListTextHeightValue = FloatValue("ArrayList-TextHeight", 11F, 1F, 20F)
    private val arrayListTextYValue = FloatValue("ArrayList-TextY", 1F, 0F, 20F)
    private val arrayListTagsArrayColor = BooleanValue("ArrayList-TagsArrayColor", false)
    private val arrayListSaturationValue = FloatValue("ArrayList-Random-Saturation", 0.9f, 0f, 1f)
    private val arrayListBrightnessValue = FloatValue("ArrayList-Random-Brightness", 1f, 0f, 1f)
    private val arrayListRectColorModeValue = ListValue("ArrayList-Rect-Color", arrayOf("Custom", "Random", "Rainbow"), "Rainbow")
    private val arrayListRectColorRedValue = IntegerValue("ArrayList-Rect-R", 255, 0, 255)
    private val arrayListRectColorGreenValue = IntegerValue("ArrayList-Rect-G", 255, 0, 255)
    private val arrayListRectColorBlueValue = IntegerValue("ArrayList-Rect-B", 255, 0, 255)
    private val arrayListRectColorBlueAlpha = IntegerValue("ArrayList-Rect-Alpha", 255, 0, 255)
    private val arrayListRectValue = ListValue("ArrayList-Rect", arrayOf("None", "Left", "Right", "All"), "All")
    private val arrayListRectWidth = FloatValue("ArrayList-RectWidth",2F,1F,3F)

    override fun drawElement(): Border? {
        val fontRenderer = fontValue
        val tagMode = tagMode.get()
        val tagLeft = if (tagMode == "<>") "<" else if (tagMode == "[]") "[" else ""
        val tagRight = if (tagMode == "<>") ">" else if (tagMode == "[]") "]" else ""
        val rainbowX = arrayListRainbowX
        val rainbowY = arrayListRainbowY
        val tags = arrayListTags
        val upperCaseValue = arrayListUpperCaseValue
        val tagsArrayColor = arrayListTagsArrayColor
        val rectWidth = arrayListRectWidth

        FontManager.AWTFontRenderer.assumeNonVolatile = true

        // Slide animation - update every render
        val delta = RenderUtils.deltaTime

        for (module in Kevin.getInstance.moduleManager.getModules()) {
            if (!module.array || (!module.getToggle() && module.slide == 0F)) continue

            var displayString = if (!tags.get())
                module.getName()
            else if (tagsArrayColor.get())
                module.getColorlessTagName(tagLeft,tagRight)
            else module.getTagName(tagLeft,tagRight)

            if (upperCaseValue.get())
                displayString = displayString.toUpperCase()

            val width = fontRenderer.getStringWidth(displayString)

            if (module.getToggle()) {
                if (module.slide < width) {
                    module.slide = AnimationUtils.easeOut(module.slideStep, width.toFloat()) * width
                    module.slideStep += delta / 4F
                }
            } else if (module.slide > 0) {
                module.slide = AnimationUtils.easeOut(module.slideStep, width.toFloat()) * width
                module.slideStep -= delta / 4F
            }

            module.slide = module.slide.coerceIn(0F, width.toFloat())
            module.slideStep = module.slideStep.coerceIn(0F, width.toFloat())
        }

        // Draw arraylist
        val colorMode = arrayListTextColorMode.get()
        val rectColorMode = arrayListRectColorModeValue.get()
        val backgroundColorMode = arrayListBackgroundColorModeValue.get()
        val customColor = Color(arraylistTextCustomRed.get(), arraylistTextCustomGreen.get(), arraylistTextCustomBlue.get(), 1).rgb
        val rectCustomColor = Color(arrayListRectColorRedValue.get(), arrayListRectColorGreenValue.get(), arrayListRectColorBlueValue.get(),
            arrayListRectColorBlueAlpha.get()).rgb
        val space = arrayListSpaceValue.get()
        val textHeight = arrayListTextHeightValue.get()
        val textY = arrayListTextYValue.get()
        val rectMode = arrayListRectValue.get()
        val backgroundCustomColor = Color(arrayListBackgroundColorRedValue.get(), arrayListBackgroundColorGreenValue.get(),
            arrayListBackgroundColorBlueValue.get(), arrayListBackgroundColorAlphaValue.get()).rgb
        val textShadow = arrayListTagsShadow.get()
        val textSpacer = textHeight + space
        val saturation = arrayListSaturationValue.get()
        val brightness = arrayListBrightnessValue.get()

        modules.forEachIndexed { index, module ->
            var displayString = if (!tags.get())
                module.getName()
            else if (tagsArrayColor.get())
                module.getColorlessTagName(tagLeft,tagRight)
            else module.getTagName(tagLeft,tagRight)

            if (upperCaseValue.get())
                displayString = displayString.toUpperCase()

            val xPos = -module.slide - 2
            val yPos = (if (side.vertical == Side.Vertical.DOWN) -textSpacer else textSpacer) *
                    if (side.vertical == Side.Vertical.DOWN) index + 1 else index
            val moduleColor = Color.getHSBColor(module.hue, saturation, brightness).rgb

            val backgroundRectRainbow = backgroundColorMode.equals("Rainbow", ignoreCase = true)

            RainbowShader.begin(backgroundRectRainbow, if (rainbowX.get() == 0.0F) 0.0F else 1.0F / rainbowX.get(), if (rainbowY.get() == 0.0F) 0.0F else 1.0F / rainbowY.get(), System.currentTimeMillis() % 10000 / 10000F).use {
                RenderUtils.drawRect(
                    xPos - if (rectMode.equals("right", true)||rectMode.equals("all",true)) rectWidth.get() + 2F else 2F,
                    yPos,
                    if (rectMode.equals("right", true)||rectMode.equals("all",true)) -rectWidth.get() else 0F,
                    yPos + textHeight, when {
                        backgroundRectRainbow -> 0xFF shl 24
                        backgroundColorMode.equals("Random", ignoreCase = true) -> moduleColor
                        else -> backgroundCustomColor
                    }
                )
            }

            val rainbow = colorMode.equals("Rainbow", ignoreCase = true)

            GlStateManager.resetColor()
            FontManager.RainbowFontShader.begin(rainbow, if (rainbowX.get() == 0.0F) 0.0F else 1.0F / rainbowX.get(), if (rainbowY.get() == 0.0F) 0.0F else 1.0F / rainbowY.get(), System.currentTimeMillis() % 10000 / 10000F).use {
                fontRenderer.drawString(displayString, xPos - if (rectMode.equals("right", true)||rectMode.equals("all",true)) rectWidth.get() else 0F, yPos + textY, when {
                    rainbow -> 0
                    colorMode.equals("Random", ignoreCase = true) -> moduleColor
                    else -> customColor }, textShadow)
            }

            if (!rectMode.equals("none", true)) {
                val rectRainbow = rectColorMode.equals("Rainbow", ignoreCase = true)

                RainbowShader.begin(rectRainbow, if (rainbowX.get() == 0.0F) 0.0F else 1.0F / rainbowX.get(), if (rainbowY.get() == 0.0F) 0.0F else 1.0F / rainbowY.get(), System.currentTimeMillis() % 10000 / 10000F).use {
                    val rectColor = when {
                        rectRainbow -> 0
                        rectColorMode.equals("Random", ignoreCase = true) -> moduleColor
                        else -> rectCustomColor
                    }

                    when {
                        rectMode.equals("left", true) -> RenderUtils.drawRect(xPos - rectWidth.get() - 2, yPos, xPos - 2, yPos + textHeight,
                            rectColor)
                        rectMode.equals("right", true) -> RenderUtils.drawRect(-rectWidth.get()+1.1F, yPos, 1.1F,
                            yPos + textHeight, rectColor)
                        rectMode.equals("all",true) -> {
                            RenderUtils.drawRect(xPos - rectWidth.get()*2 - 3, yPos, xPos - 3 - rectWidth.get(), yPos + textHeight, rectColor)
                            RenderUtils.drawRect(-rectWidth.get()+1.1F, yPos, 1.1F, yPos + textHeight, rectColor)
                            if (index + 1 <= modules.size - 1){
                                val m = modules[index+1]
                                val mPosX = -m.slide - 2
                                val x1 = xPos - rectWidth.get()*2 - 3
                                val x2 = mPosX - rectWidth.get()*2 - 3
                                if (x1<x2)
                                    RenderUtils.drawRect(x1, yPos + textHeight, x2, yPos + textHeight + 1, rectColor)
                                else
                                    RenderUtils.drawRect(x2, yPos + textHeight - 1, x1, yPos + textHeight, rectColor)
                            }else{
                                RenderUtils.drawRect(xPos - rectWidth.get()*2 - 3, yPos + textHeight, 1.1F, yPos + textHeight + 1, rectColor)
                            }
                        }
                    }
                }
            }
        }

        if ((mc.currentScreen) is GuiHudDesigner) {
            var x2 = Int.MIN_VALUE

            if (modules.isEmpty()) {
                return if (side.horizontal == Side.Horizontal.LEFT)
                    Border(0F, -1F, 20F, 20F)
                else
                    Border(0F, -1F, -20F, 20F)
            }

            for (module in modules) {
                when (side.horizontal) {
                    Side.Horizontal.RIGHT, Side.Horizontal.MIDDLE -> {
                        val xPos = -module.slide.toInt() - 2
                        if (x2 == Int.MIN_VALUE || xPos < x2) x2 = xPos
                    }
                    Side.Horizontal.LEFT -> {
                        val xPos = module.slide.toInt() + 14
                        if (x2 == Int.MIN_VALUE || xPos > x2) x2 = xPos
                    }
                }
            }
            val y2 = (if (side.vertical == Side.Vertical.DOWN) -textSpacer else textSpacer) * modules.size

            return Border(0F, 0F, x2 - 7F, y2 - if (side.vertical == Side.Vertical.DOWN) 1F else 0F)
        }

        FontManager.AWTFontRenderer.assumeNonVolatile = false
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        return null
    }

    override fun updateElement() {
        val tagMode = tagMode.get()
        val tagLeft = if (tagMode == "<>") "<" else if (tagMode == "[]") "[" else ""
        val tagRight = if (tagMode == "<>") ">" else if (tagMode == "[]") "]" else ""
        val tags = arrayListTags
        val upperCaseValue = arrayListUpperCaseValue
        val tagsArrayColor = arrayListTagsArrayColor
        modules = Kevin.getInstance.moduleManager.getModules()
            .filter { it.array && it.slide > 0 }
            .sortedBy { -fontValue.getStringWidth(if (upperCaseValue.get()) (if (!tags.get()) it.getName() else if (tagsArrayColor.get()) it.getColorlessTagName(tagLeft,tagRight) else it.getTagName(tagLeft,tagRight)).toUpperCase() else if (!tags.get()) it.getName() else if (tagsArrayColor.get()) it.getColorlessTagName(tagLeft,tagRight) else it.getTagName(tagLeft,tagRight)) }
    }
}