package kevin.module.modules.render

import kevin.event.*
import kevin.main.Kevin
import kevin.module.*
import kevin.utils.*
import kevin.utils.FontManager.AWTFontRenderer.Companion.assumeNonVolatile
import net.minecraft.block.material.Material
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.resources.I18n
import net.minecraft.potion.Potion
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import java.awt.Color
import java.io.Closeable
import java.text.DecimalFormat
import java.util.*

class HUD : Module("HUD","Toggles visibility of the HUD.",category = ModuleCategory.RENDER) {

    private val arrayList = BooleanValue("ArrayList",true)
    private val notification = BooleanValue("Notification",true)
    private val armor = BooleanValue("Armor",true)
    private val effects = BooleanValue("Effects",true)
    private val clientName = BooleanValue("ClientName",true)
    private val packetCounter = BooleanValue("PacketCounter",true)

    private val notificationSize = FloatValue("NotificationSize",1F,0.1F,1F)
    private val notificationMode = ListValue("NotificationMode", arrayOf("LiquidBounce-Kevin","Kevin"),"LiquidBounce-Kevin")

    private val arrayListSize = FloatValue("ArrayListSize",1F,0.1F,1F)
    private val tagMode = ListValue("ArrayList-TagMode", arrayOf("<>","[]","Custom","None"),"<>")
    private val tagCustomLeft = TextValue("ArrayList-TagCustomLeft","<")
    private val tagCustomRight = TextValue("ArrayList-TagCustomRight",">")
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

    private val clientNameSize = FloatValue("ClientNameSize",1F,0.1F,2F)
    private val clientNameColorMode = ListValue("ClientNameColorMode", arrayOf("Custom","Rainbow"),"Rainbow")
    private val clientNameCustomRed = IntegerValue("ClientName-CustomRed",0,0,255)
    private val clientNameCustomGreen = IntegerValue("ClientName-CustomGreen",0,0,255)
    private val clientNameCustomBlue = IntegerValue("ClientName-CustomBlue",0,0,255)
    private val clientNameRainbowX = FloatValue("ClientName-Rainbow-X",-1000F, -2000F, 2000F)
    private val clientNameRainbowY = FloatValue("ClientName-Rainbow-Y",-1000F, -2000F, 2000F)
    private val clientNameTag = BooleanValue("ClientName-Tag",true)
    private val clientNameTagColorMode = ListValue("ClientNameTagColorMode", arrayOf("Custom","Rainbow"),"Rainbow")
    private val clientNameTagCustomRed = IntegerValue("ClientName-TagCustomRed",0,0,255)
    private val clientNameTagCustomGreen = IntegerValue("ClientName-TagCustomGreen",0,0,255)
    private val clientNameTagCustomBlue = IntegerValue("ClientName-TagCustomBlue",0,0,255)
    private val clientNameTagRainbowX = FloatValue("ClientName-Tag-Rainbow-X",-1000F, -2000F, 2000F)
    private val clientNameTagRainbowY = FloatValue("ClientName-Tag-Rainbow-Y",-1000F, -2000F, 2000F)

    private val armorMode = ListValue("ArmorMode", arrayOf("Horizontal", "Vertical"), "Horizontal")
    private val armorShowDamageMode = ListValue("ArmorShowDamage", arrayOf("None","Percentage","Value","All"),"All")
    private val armorDamageColor = ListValue("ArmorShowDamageColor", arrayOf("Custom","Rainbow","Damage"),"Damage")
    private val armorDamageCustomRed = IntegerValue("ArmorDamageCustomRed",255,0,255)
    private val armorDamageCustomGreen = IntegerValue("ArmorDamageCustomGreen",255,0,255)
    private val armorDamageCustomBlue = IntegerValue("ArmorDamageCustomBlue",255,0,255)
    private val armorDamageRainbowX = FloatValue("ArmorDamageRainbowX",-1000F, -2000F, 2000F)
    private val armorDamageRainbowY = FloatValue("ArmorDamageRainbowY",-1000F, -2000F, 2000F)

    private val packetCounterHeight = IntegerValue("PacketCounterHeight", 50, 30, 150)
    private val packetCounterWidth = IntegerValue("PacketCounterWidth", 100, 100, 300)
    private val packetCounterUpdateDelay = IntegerValue("PacketCounterUpdateDelay",500,0,2000)
    private val packetCounterMessage = ListValue("PacketCounterMessageMode", arrayOf("None","Right","Up"),"Right")


    private val notifications = mutableListOf<Notification>()
    private var ArrayList:Arraylist? = null
    private var NOtification:Notifications? = null
    private var clientname:ClientName? = null
    private var aRmor:Armor? = null
    private var eFfects:Effects? = null
    private var packetcounter:PacketCounter? = null

    @EventTarget
    fun onRender2D(event: Render2DEvent?) {
        val array = arrayListOf<Element>()
        if (ArrayList != null) array.add(ArrayList!!)
        if (NOtification != null) array.add(NOtification!!)
        if (clientname != null) array.add(clientname!!)
        if (aRmor != null) array.add(aRmor!!)
        if (eFfects != null) array.add(eFfects!!)
        if (packetcounter!=null) array.add(packetcounter!!)
        array.forEach {
                GL11.glPushMatrix()

                if (!it.info.disableScale)
                    GL11.glScalef(it.scale, it.scale, it.scale)

                GL11.glTranslated(it.renderX / it.scale, it.renderY / it.scale, 0.0)

                try {
                    it.border = it.drawElement()
                } catch (ex: Exception) {
                    println("Something went wrong while drawing ${it.name} element in HUD. $ex")
                }
                GL11.glColor4f(1F,1F,1F,1F)
                GL11.glPopMatrix()
            }
    }

    @EventTarget
    fun onPacket(event: PacketEvent){
        if (packetcounter != null){
            val packet = event.packet
            if (packet.javaClass.name.contains("net.minecraft.network.play.client.",ignoreCase = true)){
                packetcounter!!.sentPackets += 1
            }
            if (packet.javaClass.name.contains("net.minecraft.network.play.server.",ignoreCase = true)){
                packetcounter!!.receivedPackets += 1
            }
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        if (arrayList.get()){
            if (ArrayList == null){
                ArrayList = Arraylist()
            }
        }else{
            if (ArrayList != null){
                ArrayList = null
            }
        }
        if (notification.get()){
            if (NOtification == null){
                NOtification = Notifications()
            }
        }else{
            if (NOtification != null){
                NOtification = null
            }
        }
        if (clientName.get()){
            if (clientname == null){
                clientname = ClientName()
            }
        }else{
            if (clientname != null){
                clientname = null
            }
        }
        if (armor.get()){
            if (aRmor == null){
                aRmor = Armor()
            }
        }else{
            if (aRmor != null){
                aRmor = null
            }
        }
        if (effects.get()){
            if (eFfects == null){
                eFfects = Effects()
            }
        }else{
            if (eFfects != null){
                eFfects = null
            }
        }
        if (packetCounter.get()){
            if (packetcounter == null){
                packetcounter = PacketCounter()
            }
        }else{
            if (packetcounter != null){
                packetcounter = null
            }
        }
        if (NOtification != null && NOtification!!.scale != notificationSize.get()) NOtification!!.scale = notificationSize.get()
        if (ArrayList != null && ArrayList!!.scale != arrayListSize.get()) ArrayList!!.scale = arrayListSize.get()
        if (clientname != null && clientname!!.scale != clientNameSize.get()) clientname!!.scale = clientNameSize.get()
        val array = arrayListOf<Element>()
        if (ArrayList != null) array.add(ArrayList!!)
        if (NOtification != null) array.add(NOtification!!)
        if (clientname != null) array.add(clientname!!)
        if (aRmor != null) array.add(aRmor!!)
        if (packetcounter != null) array.add(packetcounter!!)
        array.forEach {
            it.updateElement()
        }
    }
    fun addNotification(notification: Notification,text:String = ""): Boolean {
        notification.text1 = text
        return this.NOtification != null && notifications.add(notification)
    }

    fun removeNotification(notification: Notification) = notifications.remove(notification)

    abstract class Element(var x: Double = 2.0, var y: Double = 2.0, scale: Float = 1F,
                           var side: Side = Side.default()) : MinecraftInstance(){
        val info = javaClass.getAnnotation(ElementInfo::class.java)
            ?: throw IllegalArgumentException("Passed element with missing element info")

        var scale: Float = 1F
            set(value) {
                if (info.disableScale)
                    return

                field = value
            }
            get() {
                if (info.disableScale)
                    return 1.0f
                return field
            }

        init {
            this.scale = scale
        }

        val name: String
            get() = info.name

        var renderX: Double
            get() = when (side.horizontal) {
                Side.Horizontal.LEFT -> x
                Side.Horizontal.MIDDLE -> (ScaledResolution(mc).scaledWidth / 2) - x
                Side.Horizontal.RIGHT -> ScaledResolution(mc).scaledWidth - x
            }
            set(value) = when (side.horizontal) {
                Side.Horizontal.LEFT -> {
                    x += value
                }
                Side.Horizontal.MIDDLE, Side.Horizontal.RIGHT -> {
                    x -= value
                }
            }

        var renderY: Double
            get() = when (side.vertical) {
                Side.Vertical.UP -> y
                Side.Vertical.MIDDLE -> (ScaledResolution(mc).scaledHeight / 2) - y
                Side.Vertical.DOWN -> ScaledResolution(mc).scaledHeight - y
            }
            set(value) = when (side.vertical) {
                Side.Vertical.UP -> {
                    y += value
                }
                Side.Vertical.MIDDLE, Side.Vertical.DOWN -> {
                    y -= value
                }
            }

        var border: Border? = null

        open val values: List<Value<*>>
            get() = javaClass.declaredFields.map { valueField ->
                valueField.isAccessible = true
                valueField[this]
            }.filterIsInstance<Value<*>>()

        abstract fun drawElement(): Border?

        open fun updateElement() {}

    }
    class Side(var horizontal: Horizontal, var vertical: Vertical) {

        companion object {

            fun default() = Side(Horizontal.LEFT, Vertical.UP)

        }

        enum class Horizontal(val sideName: String) {

            LEFT("Left"),
            MIDDLE("Middle"),
            RIGHT("Right");

            companion object {

                @JvmStatic
                fun getByName(name: String) = values().find { it.sideName == name }

            }

        }

        enum class Vertical(val sideName: String) {

            UP("Up"),
            MIDDLE("Middle"),
            DOWN("Down");

            companion object {

                @JvmStatic
                fun getByName(name: String) = values().find { it.sideName == name }

            }
        }
    }

    data class Border(val x: Float, val y: Float, val x2: Float, val y2: Float) {
        fun draw() = RenderUtils.drawBorderedRect(x, y, x2, y2, 3F, Int.MIN_VALUE, 0)
    }
    @kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
    annotation class ElementInfo(val name: String, val single: Boolean = false, val force: Boolean = false, val disableScale: Boolean = false, val priority: Int = 0)

    @ElementInfo(name = "Arraylist", single = true)
    class Arraylist(x: Double = 1.0, y: Double = 2.0, scale: Float = 1F,
                    side: Side = Side(Side.Horizontal.RIGHT, Side.Vertical.UP)) : Element(x, y, scale, side) {

        private val fontValue = Kevin.getInstance.fontManager.font40!!
        private var modules = emptyList<Module>()

        override fun drawElement(): Border? {
            val fontRenderer = fontValue
            val hud = Kevin.getInstance.moduleManager.getModule("HUD") as HUD
            val tagMode = hud.tagMode.get()
            val tagLeft = if (tagMode == "<>") "<" else if (tagMode == "[]") "[" else if (tagMode == "Custom") hud.tagCustomLeft.get() else ""
            val tagRight = if (tagMode == "<>") ">" else if (tagMode == "[]") "]" else if (tagMode == "Custom") hud.tagCustomRight.get() else ""
            val rainbowX = hud.arrayListRainbowX
            val rainbowY = hud.arrayListRainbowY
            val tags = hud.arrayListTags
            val upperCaseValue = hud.arrayListUpperCaseValue
            val tagsArrayColor = hud.arrayListTagsArrayColor
            val rectWidth = hud.arrayListRectWidth

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
            val colorMode = hud.arrayListTextColorMode.get()
            val rectColorMode = hud.arrayListRectColorModeValue.get()
            val backgroundColorMode = hud.arrayListBackgroundColorModeValue.get()
            val customColor = Color(hud.arraylistTextCustomRed.get(), hud.arraylistTextCustomGreen.get(), hud.arraylistTextCustomBlue.get(), 1).rgb
            val rectCustomColor = Color(hud.arrayListRectColorRedValue.get(), hud.arrayListRectColorGreenValue.get(), hud.arrayListRectColorBlueValue.get(),
                hud.arrayListRectColorBlueAlpha.get()).rgb
            val space = hud.arrayListSpaceValue.get()
            val textHeight = hud.arrayListTextHeightValue.get()
            val textY = hud.arrayListTextYValue.get()
            val rectMode = hud.arrayListRectValue.get()
            val backgroundCustomColor = Color(hud.arrayListBackgroundColorRedValue.get(), hud.arrayListBackgroundColorGreenValue.get(),
                hud.arrayListBackgroundColorBlueValue.get(), hud.arrayListBackgroundColorAlphaValue.get()).rgb
            val textShadow = hud.arrayListTagsShadow.get()
            val textSpacer = textHeight + space
            val saturation = hud.arrayListSaturationValue.get()
            val brightness = hud.arrayListBrightnessValue.get()

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

            FontManager.AWTFontRenderer.assumeNonVolatile = false
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
            return null
        }

        override fun updateElement() {
            val hud = Kevin.getInstance.moduleManager.getModule("HUD") as HUD
            val tagMode = hud.tagMode.get()
            val tagLeft = if (tagMode == "<>") "<" else if (tagMode == "[]") "[" else if (tagMode == "Custom") hud.tagCustomLeft.get() else ""
            val tagRight = if (tagMode == "<>") ">" else if (tagMode == "[]") "]" else if (tagMode == "Custom") hud.tagCustomRight.get() else ""
            val tags = hud.arrayListTags
            val upperCaseValue = hud.arrayListUpperCaseValue
            val tagsArrayColor = hud.arrayListTagsArrayColor
            modules = Kevin.getInstance.moduleManager.getModules()
                .filter { it.array && it.slide > 0 }
                .sortedBy { -fontValue.getStringWidth(if (upperCaseValue.get()) (if (!tags.get()) it.getName() else if (tagsArrayColor.get()) it.getColorlessTagName(tagLeft,tagRight) else it.getTagName(tagLeft,tagRight)).toUpperCase() else if (!tags.get()) it.getName() else if (tagsArrayColor.get()) it.getColorlessTagName(tagLeft,tagRight) else it.getTagName(tagLeft,tagRight)) }
        }
    }

    @ElementInfo(name = "Notifications", single = true)
    class Notifications(x: Double = 0.0, y: Double = 30.0, scale: Float = 1F,
                        side: Side = Side(Side.Horizontal.RIGHT, Side.Vertical.DOWN)) : Element(x, y, scale, side) {

        override fun drawElement(): Border? {
            var animationY = 30F
            val notifications = mutableListOf<Notification>()
            val hud = Kevin.getInstance.moduleManager.getModule("HUD") as HUD
            for(i in hud.notifications)
                notifications.add(i)
            for(i in notifications){
                if (hud.notificationMode.get().equals("liquidbounce-kevin",true))
                    i.drawNotification(animationY).also { animationY += 20 }

                else if (hud.notificationMode.get().equals("kevin",true))
                    i.drawNotificationKevinNew(animationY,i.text1).also { animationY += 40 }
            }
            return null
        }
    }

    class Notification(private val message: String) {
        var text1 = ""
        var x = 0F
        var textLength = 0

        private var stay = 0F
        private var fadeStep = 0F
        var fadeState = FadeState.IN

        private var stayTimer = MSTimer()
        private var firstY = 0f
        private var animeTime: Long = 0

        enum class FadeState { IN, STAY, OUT, END }

        init {
            stayTimer.reset()
            firstY = 1919F
            textLength = Kevin.getInstance.fontManager.font35!!.getStringWidth(message)
        }

        fun drawNotification(animationY: Float) {
            var y = animationY
            if (firstY == 1919.0F) {
                firstY = y
            }
            if (firstY > y) {
                val cacheY = firstY - (firstY - y) * ((System.currentTimeMillis() - animeTime).toFloat() / 300.0f)
                if (cacheY <= y) {
                    firstY = cacheY
                }
                y = cacheY
            } else {
                firstY = y
                animeTime = System.currentTimeMillis()
            }
            // Draw notification
            RenderUtils.drawRect(-x + 8 + textLength, -y, -x, -20F-y, Color(0,0,0,155).rgb)
            if (message.contains("Enabled")) {
                RenderUtils.drawRect(-x, -y, -x - 5, -20F-y, Color(0, 255, 160,225).rgb)
                RenderUtils.drawRect(-x + 8 + textLength, -19F-y, -x, -20F-y, Color(0, 255, 160,225).rgb)
                RenderUtils.drawRect(-x + 8 + textLength, -y, -x + 7 + textLength, -19F-y, Color(0, 255, 160,225).rgb)
                Kevin.getInstance.fontManager.font35!!.drawString(message, -x + 4, -14F-y, Color(0, 255, 160).rgb)
            }else if (message.contains("Disabled")) {
                RenderUtils.drawRect(-x, -y, -x - 5, -20F-y, Color(255, 0, 80,225).rgb)
                RenderUtils.drawRect(-x + 8 + textLength, -19F-y, -x, -20F-y, Color(255, 0, 80,225).rgb)
                RenderUtils.drawRect(-x + 8 + textLength, -y, -x + 7 + textLength, -19F-y, Color(255, 0, 80,225).rgb)
                Kevin.getInstance.fontManager.font35!!.drawString(message, -x + 4, -14F-y, Color(255, 0, 80).rgb)
            }else {
                RenderUtils.drawRect(-x, -y, -x - 5, -20F-y, Color(0, 160, 255,225).rgb)
                RenderUtils.drawRect(-x + 8 + textLength, -19F-y, -x, -20F-y, Color(0, 160, 255,225).rgb)
                RenderUtils.drawRect(-x + 8 + textLength, -y, -x + 7 + textLength, -19F-y, Color(0, 160, 255,225).rgb)
                Kevin.getInstance.fontManager.font35!!.drawString(message, -x + 4, -14F-y, Color(0, 160, 255).rgb)
            }
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

            // Animation
            val delta = RenderUtils.deltaTime - 4
            val width = textLength + 8F
            when (fadeState) {
                FadeState.IN -> {
                    if (x < width) {
                        x = AnimationUtils.easeOut(fadeStep, width) * width
                        fadeStep += delta / 4F
                    }
                    if (x >= width) {
                        fadeState = FadeState.STAY
                        x = width
                        fadeStep = width
                    }

                    stay = 60F
                }

                FadeState.STAY -> {
                    if (stay > 0) {
                        stay = 0F
                        stayTimer.reset()
                    }
                    if (stayTimer.hasTimePassed(500L)) {
                        if (message.contains("Enabled")) {
                            RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6, -2F - y, Color(0, 255, 160, 225).rgb)
                        } else if (message.contains("Disabled")) {
                            RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6, -2F - y, Color(255, 0, 80,225).rgb)
                        } else {
                            RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6, -2F - y, Color(0, 160, 255,225).rgb)
                        }
                    }
                    if (stayTimer.hasTimePassed(1000L)) {
                        if (message.contains("Enabled")) {
                            RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*2, -2F - y, Color(0, 255, 160, 225).rgb)
                        } else if (message.contains("Disabled")) {
                            RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*2, -2F - y, Color(255, 0, 80,225).rgb)
                        } else {
                            RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*2, -2F - y, Color(0, 160, 255,225).rgb)
                        }
                    }
                    if (stayTimer.hasTimePassed(1500L)) {
                        if (message.contains("Enabled")) {
                            RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*3, -2F - y, Color(0, 255, 160, 225).rgb)
                        } else if (message.contains("Disabled")) {
                            RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*3, -2F - y, Color(255, 0, 80,225).rgb)
                        } else {
                            RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*3, -2F - y, Color(0, 160, 255,225).rgb)
                        }
                    }
                    if (stayTimer.hasTimePassed(2000L)) {
                        if (message.contains("Enabled")) {
                            RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*4, -2F - y, Color(0, 255, 160, 225).rgb)
                        } else if (message.contains("Disabled")) {
                            RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*4, -2F - y, Color(255, 0, 80,225).rgb)
                        } else {
                            RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*4, -2F - y, Color(0, 160, 255,225).rgb)
                        }
                    }
                    if (stayTimer.hasTimePassed(2500L)) {
                        if (message.contains("Enabled")) {
                            RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*5, -2F - y, Color(0, 255, 160, 225).rgb)
                        } else if (message.contains("Disabled")) {
                            RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*5, -2F - y, Color(255, 0, 80,225).rgb)
                        } else {
                            RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*5, -2F - y, Color(0, 160, 255,225).rgb)
                        }
                    }
                    if (stayTimer.hasTimePassed(2850L)) {
                        if (message.contains("Enabled")) {
                            RenderUtils.drawRect(-x + 8 + textLength, -y, -x, -2F - y, Color(0, 255, 160, 225).rgb)
                        } else if (message.contains("Disabled")) {
                            RenderUtils.drawRect(-x + 8 + textLength, -y, -x, -2F - y, Color(255, 0, 80,225).rgb)
                        } else {
                            RenderUtils.drawRect(-x + 8 + textLength, -y, -x, -2F - y, Color(0, 160, 255,225).rgb)
                        }
                    }
                    if (stayTimer.hasTimePassed(3000L))
                        fadeState = FadeState.OUT
                }

                FadeState.OUT -> if (x > 0) {
                    x = AnimationUtils.easeOut(fadeStep, width) * width
                    fadeStep -= delta / 4F
                } else
                    fadeState = FadeState.END

                FadeState.END -> {
                    val hud = Kevin.getInstance.moduleManager.getModule("HUD") as HUD
                    hud.removeNotification(this)
                }

            }
        }

        fun drawNotificationKevinNew(animationY: Float,text2:String = "") {
            var y = animationY
            if (firstY == 1919.0F) {
                firstY = y
            }
            if (firstY > y) {
                val cacheY = firstY - (firstY - y) * ((System.currentTimeMillis() - animeTime).toFloat() / 300.0f)
                if (cacheY <= y) {
                    firstY = cacheY
                }
                y = cacheY
            } else {
                firstY = y
                animeTime = System.currentTimeMillis()
            }
            // Draw notification
            val color = if (message.contains("Enabled")) Color(0, 255, 160).rgb else if (message.contains("Disabled")) Color(255, 0, 80).rgb else Color(0, 160, 255).rgb
            textLength = if (textLength > 100) textLength else 100
            val text = if (message.contains("Enabled")||message.contains("Disabled")) "ModuleManager" else text2

            RenderUtils.drawRect(-x + 8 + textLength, -y, -x, -40F-y, Color(0,0,0,100).rgb)

            RenderUtils.drawRect(-x, -y, -x - 1, -40F-y, color)
            RenderUtils.drawRect(-x + 8 + textLength, -39F-y, -x, -40F-y, color)
            RenderUtils.drawRect(-x + 8 + textLength, -y, -x + 7 + textLength, -39F-y, color)
            Kevin.getInstance.fontManager.font35!!.drawString(message, -x + (8F + textLength)/2 - Kevin.getInstance.fontManager.font35!!.getStringWidth(message)/2, -14F-y, color)
            Kevin.getInstance.fontManager.font35!!.drawString(text, -x + (8F + textLength)/2 - Kevin.getInstance.fontManager.font35!!.getStringWidth(text)/2, -30F-y, color)
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

            // Animation
            val delta = RenderUtils.deltaTime - 4
            val width = textLength + 8F
            when (fadeState) {
                FadeState.IN -> {
                    if (x < width) {
                        x = AnimationUtils.easeOut(fadeStep, width) * width
                        fadeStep += delta / 4F
                    }
                    if (x >= width) {
                        fadeState = FadeState.STAY
                        x = width
                        fadeStep = width
                    }

                    stay = 60F
                }

                FadeState.STAY -> {
                    if (stay > 0) {
                        stay = 0F
                        stayTimer.reset()
                    }
                    if (stayTimer.hasTimePassed(500L)) {
                        if (message.contains("Enabled")) {
                            RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6, -2F - y, Color(0, 255, 160, 225).rgb)
                        } else if (message.contains("Disabled")) {
                            RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6, -2F - y, Color(255, 0, 80,225).rgb)
                        } else {
                            RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6, -2F - y, Color(0, 160, 255,225).rgb)
                        }
                    }
                    if (stayTimer.hasTimePassed(1000L)) {
                        if (message.contains("Enabled")) {
                            RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*2, -2F - y, Color(0, 255, 160, 225).rgb)
                        } else if (message.contains("Disabled")) {
                            RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*2, -2F - y, Color(255, 0, 80,225).rgb)
                        } else {
                            RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*2, -2F - y, Color(0, 160, 255,225).rgb)
                        }
                    }
                    if (stayTimer.hasTimePassed(1500L)) {
                        if (message.contains("Enabled")) {
                            RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*3, -2F - y, Color(0, 255, 160, 225).rgb)
                        } else if (message.contains("Disabled")) {
                            RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*3, -2F - y, Color(255, 0, 80,225).rgb)
                        } else {
                            RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*3, -2F - y, Color(0, 160, 255,225).rgb)
                        }
                    }
                    if (stayTimer.hasTimePassed(2000L)) {
                        if (message.contains("Enabled")) {
                            RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*4, -2F - y, Color(0, 255, 160, 225).rgb)
                        } else if (message.contains("Disabled")) {
                            RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*4, -2F - y, Color(255, 0, 80,225).rgb)
                        } else {
                            RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*4, -2F - y, Color(0, 160, 255,225).rgb)
                        }
                    }
                    if (stayTimer.hasTimePassed(2500L)) {
                        if (message.contains("Enabled")) {
                            RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*5, -2F - y, Color(0, 255, 160, 225).rgb)
                        } else if (message.contains("Disabled")) {
                            RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*5, -2F - y, Color(255, 0, 80,225).rgb)
                        } else {
                            RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*5, -2F - y, Color(0, 160, 255,225).rgb)
                        }
                    }
                    if (stayTimer.hasTimePassed(2850L)) {
                        if (message.contains("Enabled")) {
                            RenderUtils.drawRect(-x + 8 + textLength, -y, -x, -2F - y, Color(0, 255, 160, 225).rgb)
                        } else if (message.contains("Disabled")) {
                            RenderUtils.drawRect(-x + 8 + textLength, -y, -x, -2F - y, Color(255, 0, 80,225).rgb)
                        } else {
                            RenderUtils.drawRect(-x + 8 + textLength, -y, -x, -2F - y, Color(0, 160, 255,225).rgb)
                        }
                    }
                    if (stayTimer.hasTimePassed(3000L))
                        fadeState = FadeState.OUT
                }

                FadeState.OUT -> if (x > 0) {
                    x = AnimationUtils.easeOut(fadeStep, width) * width
                    fadeStep -= delta / 4F
                } else
                    fadeState = FadeState.END

                FadeState.END -> {
                    val hud = Kevin.getInstance.moduleManager.getModule("HUD") as HUD
                    hud.removeNotification(this)
                }

            }
        }
    }

    class RainbowShader : FontManager.Shader("rainbow_shader.frag"), Closeable {
        var isInUse = false
            private set

        var strengthX = 0f
        var strengthY = 0f
        var offset = 0f

        override fun setupUniforms() {
            setupUniform("offset")
            setupUniform("strength")
        }

        override fun updateUniforms() {
            GL20.glUniform2f(getUniform("strength"), strengthX, strengthY)
            GL20.glUniform1f(getUniform("offset"), offset)
        }

        override fun startShader() {
            super.startShader()

            isInUse = true
        }

        override fun stopShader() {
            super.stopShader()

            isInUse = false
        }

        override fun close() {
            if (isInUse)
                stopShader()
        }

        companion object {
            @JvmField
            val INSTANCE = RainbowShader()

            @Suppress("NOTHING_TO_INLINE")
            inline fun begin(enable: Boolean, x: Float, y: Float, offset: Float): RainbowShader {
                val instance = INSTANCE

                if (enable) {
                    instance.strengthX = x
                    instance.strengthY = y
                    instance.offset = offset

                    instance.startShader()
                }

                return instance
            }
        }
    }

    @ElementInfo(name="ClientName",single = true)
    class ClientName(x: Double = 5.0, y: Double = 10.0, scale: Float = 1F,
                     side: Side = Side(Side.Horizontal.LEFT, Side.Vertical.UP)) : Element(x, y, scale, side){
        override fun drawElement(): Border? {
            val hud = Kevin.getInstance.moduleManager.getModule("HUD") as HUD
            val rainbow = hud.clientNameColorMode.get().equals("rainbow",true)
            val color = if (rainbow) 0 else Color(hud.clientNameCustomRed.get(),hud.clientNameCustomGreen.get(),hud.clientNameCustomBlue.get()).rgb
            val rainbowX = hud.clientNameRainbowX
            val rainbowY = hud.clientNameRainbowY
            val tag = hud.clientNameTag.get()
            val tagRainbow = hud.clientNameTagColorMode.get().equals("rainbow",true)
            val tagColor = if (tagRainbow) 0 else Color(hud.clientNameTagCustomRed.get(),hud.clientNameTagCustomGreen.get(),hud.clientNameTagCustomBlue.get()).rgb
            val tagRainbowX = hud.clientNameTagRainbowX
            val tagRainbowY = hud.clientNameTagRainbowY
            FontManager.RainbowFontShader.begin(rainbow,if (rainbowX.get() == 0.0F) 0.0F else 1.0F / rainbowX.get(), if (rainbowY.get() == 0.0F) 0.0F else 1.0F / rainbowY.get(),System.currentTimeMillis() % 10000 / 10000F).use {
                if (mc.gameSettings.ofShowFps) {
                    Kevin.getInstance.fontManager.font40!!.drawStringWithShadow(Kevin.getInstance.name, 0F, 5F, color)
                }else{
                    Kevin.getInstance.fontManager.font40!!.drawStringWithShadow(Kevin.getInstance.name, 0F, -5F, color)
                }
            }
            if (tag){
                GL11.glPushMatrix()
                GL11.glScaled(0.6,0.6,0.6)
                FontManager.RainbowFontShader.begin(tagRainbow,if (tagRainbowX.get() == 0.0F) 0.0F else 1.0F / tagRainbowX.get(), if (tagRainbowY.get() == 0.0F) 0.0F else 1.0F / tagRainbowY.get(),System.currentTimeMillis() % 10000 / 10000F).use {
                    if (mc.gameSettings.ofShowFps){
                        Kevin.getInstance.fontManager.font35!!.drawStringWithShadow(Kevin.getInstance.version,Kevin.getInstance.fontManager.font40!!.getStringWidth(Kevin.getInstance.name)/0.6F,(3.0F)/0.6F,tagColor)
                    }else{
                        Kevin.getInstance.fontManager.font35!!.drawStringWithShadow(Kevin.getInstance.version,Kevin.getInstance.fontManager.font40!!.getStringWidth(Kevin.getInstance.name)/0.6F,(-7.0F)/0.6F,tagColor)
                    }
                }
                GL11.glPopMatrix()
            }
            return null
        }
    }

    @ElementInfo(name = "Armor")
    class Armor(x: Double = -8.0, y: Double = 57.0, scale: Float = 1F,
                side: Side = Side(Side.Horizontal.MIDDLE, Side.Vertical.DOWN)) : Element(x, y, scale, side) {

        override fun drawElement(): Border {
            val hud = Kevin.getInstance.moduleManager.getModule("HUD") as HUD
            val modeValue = hud.armorMode
            val showDamageMode = hud.armorShowDamageMode
            val showDamageColorMode = hud.armorDamageColor
            val showDamageRainbow = showDamageColorMode.get().equals("rainbow",true)
            val rainbowX = hud.armorDamageRainbowX
            val rainbowY = hud.armorDamageRainbowY

            GL11.glPushMatrix()

            val isCreative = !mc.playerController.isNotCreative
            val renderItem = mc.renderItem
            val isInsideWater = mc.thePlayer!!.isInsideOfMaterial(Material.water)

            val mode = modeValue.get()
            var x = if(mode.equals("Horizontal", true)) 7 else 87
            var y = if(mode.equals("Horizontal", true)) if (isCreative) 10 else if (isInsideWater) -10 else 0 else 38


            for (index in if (mode.equals("Horizontal", true)) 3 downTo 0 else 0..3) {
                val stack = mc.thePlayer!!.inventory.armorInventory[index] ?: continue
                renderItem.renderItemIntoGUI(stack, x, y)
                renderItem.renderItemOverlays(mc.fontRendererObj, stack, x, y)
                if (mode.equals("Horizontal", true))
                    x += 18
                else if (mode.equals("Vertical", true))
                    y -= 18
            }

            GlStateManager.enableAlpha()
            GlStateManager.disableBlend()
            GlStateManager.disableLighting()
            GlStateManager.disableCull()
            GL11.glPopMatrix()

            x = 87
            y = 38

            GL11.glPushMatrix()
            for (index in 0..3){
                val stack = mc.thePlayer!!.inventory.armorInventory[index] ?: continue
                val maxDamage = stack.maxDamage
                val itemDamage = stack.itemDamage
                val df = DecimalFormat("###0.00")
                val damagePercentage = df.format((maxDamage-itemDamage).toFloat()/maxDamage.toFloat()*100F)

                val damageText = if (showDamageMode.get().equals("value",true)) "${maxDamage-itemDamage}/$maxDamage"
                else if (showDamageMode.get().equals("percentage",true)) "$damagePercentage%"
                else if (showDamageMode.get().equals("all",true)) "${maxDamage-itemDamage}/$maxDamage $damagePercentage%" else ""

                val color = if (showDamageRainbow) 0
                else if (showDamageColorMode.get().equals("custom",true)) Color(hud.armorDamageCustomRed.get(),hud.armorDamageCustomGreen.get(),hud.armorDamageCustomBlue.get()).rgb else {
                    when{
                        damagePercentage.toFloat()>75 -> Color.green.rgb
                        damagePercentage.toFloat()>50 -> Color.orange.rgb
                        damagePercentage.toFloat()>25 -> Color.yellow.rgb
                        else -> Color.red.rgb
                    }
                }

                FontManager.RainbowFontShader.begin(showDamageRainbow,if (rainbowX.get() == 0.0F) 0.0F else 1.0F / rainbowX.get(), if (rainbowY.get() == 0.0F) 0.0F else 1.0F / rainbowY.get(),System.currentTimeMillis() % 10000 / 10000F).use {
                    Kevin.getInstance.fontManager.font35!!.drawStringWithShadow(damageText,x+20F,y+6F,color)
                }

                y -= 18
            }
            GL11.glPopMatrix()

            return Border(0F, 0F, 0F, 0F)
        }
    }

    @ElementInfo(name = "Effects")
    class Effects(x: Double = 5.0, y: Double = 50.0, scale: Float = 1F,
                  side: Side = Side(Side.Horizontal.LEFT, Side.Vertical.MIDDLE)) : Element(x, y, scale, side) {

        override fun drawElement(): Border {
            var y = 0F
            var width = 0F

            val fontRenderer = mc.fontRendererObj

            assumeNonVolatile = true

            for (effect in mc.thePlayer!!.activePotionEffects) {
                val potion = Potion.potionTypes[effect.potionID]

                val number = when {
                    effect.amplifier == 1 -> "II"
                    effect.amplifier == 2 -> "III"
                    effect.amplifier == 3 -> "IV"
                    effect.amplifier == 4 -> "V"
                    effect.amplifier == 5 -> "VI"
                    effect.amplifier == 6 -> "VII"
                    effect.amplifier == 7 -> "VIII"
                    effect.amplifier == 8 -> "IX"
                    effect.amplifier == 9 -> "X"
                    effect.amplifier > 10 -> "X+"
                    else -> "I"
                }

                val color = if (Potion.getDurationString(effect) == "**:**") "§9"
                else if (Potion.getDurationString(effect).split(":")[1].toInt()<10 && Potion.getDurationString(effect).split(":")[0].toInt() == 0) "§c"
                else if (Potion.getDurationString(effect).split(":")[0].toInt() == 0) "§e"
                else "§a"

                val name = "${I18n.format(potion.name)} $number §f: $color${Potion.getDurationString(effect)}"

                fontRenderer.drawString(name, width, y, potion.liquidColor, true)
                y += fontRenderer.FONT_HEIGHT
            }

            assumeNonVolatile = false

            if (width == 0F)
                width = 40F

            if (y == 0F)
                y = -10F

            return Border(2F, fontRenderer.FONT_HEIGHT.toFloat(), -width - 2F, y + fontRenderer.FONT_HEIGHT - 2F)
        }
    }

    @ElementInfo(name = "PacketCounter")
    class PacketCounter(x: Double = 100.0, y: Double = 30.0, scale: Float = 1F, side: Side = Side(Side.Horizontal.LEFT, Side.Vertical.UP)) : Element(x, y, scale, side) {

        var sentPackets = 0
        var receivedPackets = 0
        private var sentPacketsList = ArrayList<Int>()
        private var receivedPacketsList = ArrayList<Int>()
        private var Timer = MSTimer()

        override fun drawElement(): Border {
            val hud = Kevin.getInstance.moduleManager.getModule("HUD") as HUD

            val height = hud.packetCounterHeight
            val width = hud.packetCounterWidth.get()
            val delay = hud.packetCounterUpdateDelay.get()
            val tickdelay = delay/50
            val messageMode = hud.packetCounterMessage.get()
            if (Timer.hasTimePassed(delay.toLong())) {
                Timer.reset()
                sentPacketsList.add(sentPackets)
                receivedPacketsList.add(receivedPackets)
                sentPackets = 0
                receivedPackets = 0
                while (sentPacketsList.size > width) {
                    sentPacketsList.removeAt(0)
                }
                while (receivedPacketsList.size > width) {
                    receivedPacketsList.removeAt(0)
                }
            }
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            GL11.glLineWidth(2F)
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glDisable(GL11.GL_DEPTH_TEST)
            GL11.glDepthMask(false)

            GL11.glBegin(GL11.GL_LINES)

            val sentSize = sentPacketsList.size
            val receivedSize = receivedPacketsList.size
            val sentStart = (if (sentSize > width) sentSize - width else 0)
            val receivedStart = (if (receivedSize > width) receivedSize - width else 0)
            for (i in sentStart until sentSize - 1) {
                val y = sentPacketsList[i] * 10 * 0.25F / tickdelay
                val y1 = sentPacketsList[i + 1] * 10 * 0.25F / tickdelay

                RenderUtils.glColor(Color(255, 0, 0, 255))
                GL11.glVertex2d(i.toDouble() - sentStart, height.get() + 1 - y.coerceAtMost(height.get().toFloat()).toDouble())
                GL11.glVertex2d(i + 1.0 - sentStart, height.get() + 1 - y1.coerceAtMost(height.get().toFloat()).toDouble())
            }
            for (i in receivedStart until receivedSize - 1) {
                val y = receivedPacketsList[i] * 10 * 0.03F / tickdelay
                val y1 = receivedPacketsList[i + 1] * 10 * 0.03F / tickdelay

                RenderUtils.glColor(Color(0, 255, 0, 255))
                GL11.glVertex2d(i.toDouble() - receivedStart, height.get()*2 + 1 - y.coerceAtMost(height.get().toFloat()).toDouble() + if (messageMode.equals("Up",true)) Kevin.getInstance.fontManager.font35!!.fontHeight else 0)
                GL11.glVertex2d(i + 1.0 - receivedStart, height.get()*2 + 1 - y1.coerceAtMost(height.get().toFloat()).toDouble() + if (messageMode.equals("Up",true)) Kevin.getInstance.fontManager.font35!!.fontHeight else 0)
            }

            GL11.glEnd()

            GL11.glEnable(GL11.GL_TEXTURE_2D)
            GL11.glDisable(GL11.GL_LINE_SMOOTH)
            GL11.glEnable(GL11.GL_DEPTH_TEST)
            GL11.glDepthMask(true)
            GL11.glDisable(GL11.GL_BLEND)
            GlStateManager.resetColor()


            if (!messageMode.equals("None",true)) {
                GL11.glPushMatrix()
                GL11.glScaled(0.6, 0.6, 0.6)
                if (messageMode.equals("Right",true)) {
                    val y1 = sentPacketsList.last() * 10 * 0.25F / tickdelay
                    val y12 = receivedPacketsList.last() * 10 * 0.03F / tickdelay
                    Kevin.getInstance.fontManager.font35!!.drawString(
                        "Sent ${sentPacketsList.last()} packets in the past $delay MS.",
                        (sentPacketsList.lastIndex + 4F - sentStart) / 0.6F,
                        (height.get() + 1 - y1.coerceAtMost(height.get().toFloat())) / 0.6F,
                        Color(255, 0, 0, 255).rgb
                    )
                    Kevin.getInstance.fontManager.font35!!.drawString(
                        "Received ${receivedPacketsList.last()} packets in the past $delay MS.",
                        (receivedPacketsList.lastIndex + 4F - receivedStart) / 0.6F,
                        (height.get() * 2 + 1 - y12.coerceAtMost(height.get().toFloat())) / 0.6F,
                        Color(0, 255, 0, 255).rgb
                    )
                }else if (messageMode.equals("Up",true)){
                    Kevin.getInstance.fontManager.font35!!.drawString(
                        "Sent ${sentPacketsList.last()} packets in the past $delay MS.",
                        0F,
                        (-Kevin.getInstance.fontManager.font35!!.fontHeight/2) / 0.6F,
                        Color(255, 0, 0, 255).rgb
                    )
                    Kevin.getInstance.fontManager.font35!!.drawString(
                        "Received ${receivedPacketsList.last()} packets in the past $delay MS.",
                        0F,
                        (height.get() + Kevin.getInstance.fontManager.font35!!.fontHeight/2) / 0.6F,
                        Color(0, 255, 0, 255).rgb
                    )
                }
                GL11.glPopMatrix()
            }

            return Border(0F,0F,0F,0F)
        }
    }
}