package kevin.module.modules.render

import kevin.event.EventTarget
import kevin.event.PacketEvent
import kevin.main.Kevin
import kevin.module.*
import kevin.module.modules.Targets
import kevin.utils.BlockUtils
import kevin.utils.FontManager
import kevin.utils.RenderUtils
import kevin.utils.RenderUtils.glColor
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.network.play.server.S2EPacketCloseWindow
import net.minecraft.util.MathHelper
import net.minecraft.util.ResourceLocation
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11.*
import java.awt.Color
import java.util.*

class ClickGui : Module("ClickGui","Opens the ClickGUI.", category = ModuleCategory.RENDER, keyBind = Keyboard.KEY_RSHIFT) {
    @EventTarget(ignoreCondition = true)
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is S2EPacketCloseWindow && mc.currentScreen is ClickGUI) {
            event.cancelEvent()
        }
    }

    override fun onEnable() {
        mc.displayGuiScreen(Kevin.getInstance.clickGUI)
        this.toggle(false)
    }

    class ClickGUI : GuiScreen(){
        private val category = arrayListOf("Combat","Exploit","Misc","Movement","Player","Render","World","Targets")
        private var categoryOpen = 0
        private var categoryStart:HashMap<String,Int>? = null
        private var moduleOpen:HashMap<String,Int>? = null
        private var canModuleRoll = false
        private var canSettingRoll = false
        private var canSettingRoll2 = false
        private var settingStart:HashMap<String,Int>? = null

        override fun initGui() {
            if (categoryStart == null){
                categoryStart = HashMap()
                categoryStart!!["Combat"] = 0
                categoryStart!!["Exploit"] = 0
                categoryStart!!["Misc"] = 0
                categoryStart!!["Movement"] = 0
                categoryStart!!["Player"] = 0
                categoryStart!!["Render"] = 0
                categoryStart!!["World"] = 0
            }
            if (moduleOpen == null){
                moduleOpen = HashMap()
                moduleOpen!!["Combat"] = -1
                moduleOpen!!["Exploit"] = -1
                moduleOpen!!["Misc"] = -1
                moduleOpen!!["Movement"] = -1
                moduleOpen!!["Player"] = -1
                moduleOpen!!["Render"] = -1
                moduleOpen!!["World"] = -1
            }
            if (settingStart == null){
                settingStart = HashMap()
                settingStart!!["Combat"] = 0
                settingStart!!["Exploit"] = 0
                settingStart!!["Misc"] = 0
                settingStart!!["Movement"] = 0
                settingStart!!["Player"] = 0
                settingStart!!["Render"] = 0
                settingStart!!["World"] = 0
            }
        }

        override fun doesGuiPauseGame(): Boolean = false
        override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
            RenderUtils.drawRect(mc.currentScreen.width/4F,mc.currentScreen.height/4F,mc.currentScreen.width/4F*3,mc.currentScreen.height/4F*3,Color(80,80,80,255).rgb)
            Kevin.getInstance.fontManager.fontBold180!!.drawStringWithShadow("K",mc.currentScreen.width/4F + 3,mc.currentScreen.height/4F - 4,Color(0,114,255).rgb)

            canModuleRoll = isClick(mc.currentScreen.width/8F*3,mc.currentScreen.height / 4F,mc.currentScreen.width / 16F * 9,mc.currentScreen.height/4F*3,mouseX.toFloat(),mouseY.toFloat())
            canSettingRoll = isClick(mc.currentScreen.width / 16F * 9,mc.currentScreen.height / 4F,mc.currentScreen.width/4F*3,mc.currentScreen.height/4F*3,mouseX.toFloat(),mouseY.toFloat())

            glEnable(GL_BLEND)
            glDisable(GL_TEXTURE_2D)
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
            glEnable(GL_LINE_SMOOTH)
            glColor(Color(255,255,255,150))
            glLineWidth(2F)
            glBegin(GL_LINES)

            glVertex2f(mc.currentScreen.width/4F, mc.currentScreen.height/4F)
            glVertex2f(mc.currentScreen.width/4F, mc.currentScreen.height/4F - 4 + Kevin.getInstance.fontManager.fontBold180!!.fontHeight)

            glVertex2f(mc.currentScreen.width/4F, mc.currentScreen.height/4F)
            glVertex2f(mc.currentScreen.width/4F*3, mc.currentScreen.height/4F)

            glVertex2f(mc.currentScreen.width/4F*3,mc.currentScreen.height/4F)
            glVertex2f(mc.currentScreen.width/4F*3, mc.currentScreen.height/4F*3)

            glVertex2f(mc.currentScreen.width/8F*3,mc.currentScreen.height/4F)
            glVertex2f(mc.currentScreen.width/8F*3, mc.currentScreen.height/4F - 4 + Kevin.getInstance.fontManager.fontBold180!!.fontHeight)

            if (categoryOpen != 0 && categoryOpen != 8) {
                glVertex2f(mc.currentScreen.width / 16F * 9, mc.currentScreen.height / 4F)
                glVertex2f(mc.currentScreen.width / 16F * 9, mc.currentScreen.height / 4F * 3)
            }

            glVertex2f(mc.currentScreen.width/8F*3,mc.currentScreen.height/4F*3)
            glVertex2f(mc.currentScreen.width/4F*3, mc.currentScreen.height/4F*3)

            glEnd()
            glEnable(GL_TEXTURE_2D)
            glDisable(GL_BLEND)
            glDisable(GL_LINE_SMOOTH)

            var y = mc.currentScreen.height/4F - 4 + Kevin.getInstance.fontManager.fontBold180!!.fontHeight
            val high = ((mc.currentScreen.height/4F*3) - (mc.currentScreen.height/4F - 4 + Kevin.getInstance.fontManager.fontBold180!!.fontHeight)) / 8
            for (c in category){
                val fontRainbow = category.indexOf(c) + 1 == categoryOpen
                val borderRainbow = isClick(mc.currentScreen.width/4F,y,mc.currentScreen.width/8F*3,y+high,mouseX.toFloat(),mouseY.toFloat())
                val noHead = isClick(mc.currentScreen.width/4F,y-high,mc.currentScreen.width/8F*3,y,mouseX.toFloat(),mouseY.toFloat())
                RenderUtils.drawRect(mc.currentScreen.width/4F,y,mc.currentScreen.width/8F*3,y+high,Color(60,60,60).rgb)
                HUD.RainbowShader.begin(borderRainbow,-0.001F,-0.001F,System.currentTimeMillis() % 10000 / 10000F).use {
                    if (c != category.first() && noHead) drawNoHeadBorder(mc.currentScreen.width/4F,y,mc.currentScreen.width/8F*3,y+high,2F,Color(255,255,255,150))
                    else RenderUtils.drawBorder(mc.currentScreen.width/4F,y,mc.currentScreen.width/8F*3,y+high,2F,Color(255,255,255,150).rgb)
                }
                if (c != category.first() && noHead) HUD.RainbowShader.begin(true,-0.001F,-0.001F,System.currentTimeMillis() % 10000 / 10000F).use {
                    glEnable(GL_BLEND)
                    glDisable(GL_TEXTURE_2D)
                    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
                    glEnable(GL_LINE_SMOOTH)
                    glColor(Color.white)
                    glLineWidth(2F)
                    glBegin(GL_LINES)
                    glVertex2f(mc.currentScreen.width/4F,y)
                    glVertex2f(mc.currentScreen.width/8F*3,y)
                    glEnd()
                    glEnable(GL_TEXTURE_2D)
                    glDisable(GL_BLEND)
                    glDisable(GL_LINE_SMOOTH)
                }
                FontManager.RainbowFontShader.begin(fontRainbow,-0.001F,-0.001F,System.currentTimeMillis() % 10000 / 10000F).use {
                    Kevin.getInstance.fontManager.font35!!.drawString(c,mc.currentScreen.width/4F+(mc.currentScreen.width/8F*3-mc.currentScreen.width/4F)/2 - Kevin.getInstance.fontManager.font35!!.getStringWidth(c)/2,y+high/2-Kevin.getInstance.fontManager.font35!!.fontHeight/3,Color(0,111,255).rgb)
                }

                y += high
            }
            val moduleY = mc.currentScreen.height/4F
            val moduleX = mc.currentScreen.width/8F*3
            when(categoryOpen){
                1 -> {
                    val start = categoryStart!!["Combat"]!!
                    drawModule(ModuleCategory.COMBAT,start,mouseX,mouseY)
                }
                2 -> {
                    val start = categoryStart!!["Exploit"]!!
                    drawModule(ModuleCategory.EXPLOIT,start,mouseX,mouseY)
                }
                3 -> {
                    val start = categoryStart!!["Misc"]!!
                    drawModule(ModuleCategory.MISC,start,mouseX,mouseY)
                }
                4 -> {
                    val start = categoryStart!!["Movement"]!!
                    drawModule(ModuleCategory.MOVEMENT,start,mouseX,mouseY)
                }
                5 -> {
                    val start = categoryStart!!["Player"]!!
                    drawModule(ModuleCategory.PLAYER,start,mouseX,mouseY)
                }
                6 -> {
                    val start = categoryStart!!["Render"]!!
                    drawModule(ModuleCategory.RENDER,start,mouseX,mouseY)
                }
                7 -> {
                    val start = categoryStart!!["World"]!!
                    drawModule(ModuleCategory.WORLD,start,mouseX,mouseY)
                }
                8 -> {
                    var setY = moduleY
                    for (s in (Kevin.getInstance.moduleManager.getModule("Targets") as Targets).values){
                        if (s !is BooleanValue) continue
                        glPushMatrix()
                        glScalef(0.8F,0.8F,0.8F)
                        Kevin.getInstance.fontManager.font40!!.drawString(s.name,(moduleX + 3)/0.8F,(setY + 3)/0.8F,Color(0,0,0).rgb)
                        glPopMatrix()
                        drawButton1(mc.currentScreen.width/4F*3 - 13,setY + 3.5F,s.get(),isClick(mc.currentScreen.width/4F*3 - 13,setY + 3.5F,mc.currentScreen.width/4F*3 - 4,setY + 8.5F,mouseX.toFloat(),mouseY.toFloat()),1F)
                        setY += Kevin.getInstance.fontManager.font40!!.fontHeight + 2
                    }
                }
            }

            super.drawScreen(mouseX, mouseY, partialTicks)
        }

        override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
            var y = mc.currentScreen.height/4F - 4 + Kevin.getInstance.fontManager.fontBold180!!.fontHeight
            val high = ((mc.currentScreen.height/4F*3) - (mc.currentScreen.height/4F - 4 + Kevin.getInstance.fontManager.fontBold180!!.fontHeight)) / 8
            for (c in category){
                if (isClick(mc.currentScreen.width/4F,y,mc.currentScreen.width/8F*3,y+high,mouseX.toFloat(),mouseY.toFloat())) {if (mouseButton == 0) {categoryOpen = category.indexOf(c)+1;mc.soundHandler.playSound(PositionedSoundRecord.create(ResourceLocation("gui.button.press"), 1f))} else {if (categoryOpen == category.indexOf(c)+1) {categoryOpen = 0;mc.soundHandler.playSound(PositionedSoundRecord.create(ResourceLocation("gui.button.press"), 0.6114514191981f));break} else {categoryOpen = category.indexOf(c)+1;mc.soundHandler.playSound(PositionedSoundRecord.create(ResourceLocation("gui.button.press"), 1f))}}}
                y += high
            }
            val moduleY = mc.currentScreen.height/4F
            when(categoryOpen){
                1 -> {
                    val start = categoryStart!!["Combat"]!!
                    moduleClick(ModuleCategory.COMBAT,start,mouseX,mouseY,mouseButton)
                }
                2 -> {
                    val start = categoryStart!!["Exploit"]!!
                    moduleClick(ModuleCategory.EXPLOIT,start,mouseX,mouseY,mouseButton)
                }
                3 -> {
                    val start = categoryStart!!["Misc"]!!
                    moduleClick(ModuleCategory.MISC,start,mouseX,mouseY,mouseButton)
                }
                4 -> {
                    val start = categoryStart!!["Movement"]!!
                    moduleClick(ModuleCategory.MOVEMENT,start,mouseX,mouseY,mouseButton)
                }
                5 -> {
                    val start = categoryStart!!["Player"]!!
                    moduleClick(ModuleCategory.PLAYER,start,mouseX,mouseY,mouseButton)
                }
                6 -> {
                    val start = categoryStart!!["Render"]!!
                    moduleClick(ModuleCategory.RENDER,start,mouseX,mouseY,mouseButton)
                }
                7 -> {
                    val start = categoryStart!!["World"]!!
                    moduleClick(ModuleCategory.WORLD,start,mouseX,mouseY,mouseButton)
                }
                8 -> {
                    var setY = moduleY
                    for (s in (Kevin.getInstance.moduleManager.getModule("Targets") as Targets).values){
                        if (s !is BooleanValue) continue
                        if (isClick(mc.currentScreen.width/4F*3 - 13,setY + 3.5F,mc.currentScreen.width/4F*3 - 4,setY + 8.5F,mouseX.toFloat(),mouseY.toFloat())) {s.set(!s.get())}
                        setY += Kevin.getInstance.fontManager.font40!!.fontHeight + 2
                    }
                }
            }
            super.mouseClicked(mouseX, mouseY, mouseButton)
        }

        private fun isClick(x:Float,y:Float,x1:Float,y1:Float,mouseX:Float,mouseY:Float):Boolean = mouseX in x..x1 && mouseY in y..y1

        private fun drawNoHeadBorder(x: Float,y: Float,x1: Float,y1: Float,width: Float,color: Color){
            glEnable(GL_BLEND)
            glDisable(GL_TEXTURE_2D)
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
            glEnable(GL_LINE_SMOOTH)
            glColor(color)
            glLineWidth(width)
            glBegin(GL_LINES)

            glVertex2f(x, y)
            glVertex2f(x, y1)

            glVertex2f(x, y1)
            glVertex2f(x1, y1)

            glVertex2f(x1, y)
            glVertex2f(x1, y1)
            glEnd()
            glEnable(GL_TEXTURE_2D)
            glDisable(GL_BLEND)
            glDisable(GL_LINE_SMOOTH)
        }
        private fun drawButton1(x: Float,y: Float,enable: Boolean,rainbow: Boolean,scale: Float){
            val x1 = (if (enable) x + 5 else x + 1)/scale
            val y1 = (y + 1)/scale
            glPushMatrix()
            glScalef(scale,scale,scale)
            RenderUtils.drawRect(x1,y1,x1+3/scale,y1+3/scale,if (enable) Color.green.rgb else Color.red.rgb)
            HUD.RainbowShader.begin(rainbow,-0.001F,-0.001F,System.currentTimeMillis() % 10000 / 10000F).use {
                RenderUtils.drawBorder(x/scale,y/scale,x+9/scale,y1+4/scale,1F,Color(255,255,255).rgb)
            }
            glPopMatrix()
        }
        private fun drawModule(category: ModuleCategory,start: Int,mouseX: Int,mouseY: Int){
            var jumpOver = 0
            var y = mc.currentScreen.height / 4F
            val moduleHigh = mc.currentScreen.height/16F
            for (m in Kevin.getInstance.moduleManager.getModules()){
                if (y > mc.currentScreen.height / 4F+moduleHigh*7)break
                if (m.getCategory() != category) continue
                if (jumpOver != start){jumpOver+=1;continue}
                val rainbow = isClick(mc.currentScreen.width/8F*3,y,mc.currentScreen.width / 16F * 9,y+moduleHigh,mouseX.toFloat(),mouseY.toFloat())
                val noHead = y != mc.currentScreen.height / 4F && isClick(mc.currentScreen.width/8F*3,y-moduleHigh,mc.currentScreen.width / 16F * 9,y,mouseX.toFloat(),mouseY.toFloat())
                HUD.RainbowShader.begin(rainbow,-0.001F,-0.001F,System.currentTimeMillis() % 10000 / 10000F).use {
                    if (noHead) drawNoHeadBorder(mc.currentScreen.width/8F*3,y,mc.currentScreen.width / 16F * 9,y+moduleHigh,2F,Color(255,255,255,150))
                    else RenderUtils.drawBorder(mc.currentScreen.width/8F*3,y,mc.currentScreen.width / 16F * 9,y+moduleHigh,2F,Color(255,255,255,150).rgb)
                }
                val fontRainbow = ((y - mc.currentScreen.height / 4F) / moduleHigh).toInt() == when(category){
                    ModuleCategory.COMBAT -> {moduleOpen!!["Combat"]!!}
                    ModuleCategory.EXPLOIT -> {moduleOpen!!["Exploit"]!!}
                    ModuleCategory.MISC -> {moduleOpen!!["Misc"]!!}
                    ModuleCategory.MOVEMENT -> {moduleOpen!!["Movement"]!!}
                    ModuleCategory.PLAYER -> {moduleOpen!!["Player"]!!}
                    ModuleCategory.RENDER -> {moduleOpen!!["Render"]!!}
                    ModuleCategory.WORLD -> {moduleOpen!!["World"]!!}
                }
                FontManager.RainbowFontShader.begin(fontRainbow,-0.001F,-0.001F,System.currentTimeMillis() % 10000 / 10000F).use {
                    Kevin.getInstance.fontManager.font35!!.drawString(
                        m.getName(),
                        mc.currentScreen.width / 8F * 3 + 5,
                        y - Kevin.getInstance.fontManager.font35!!.fontHeight / 3 + moduleHigh / 2,
                        Color(0, 111, 255).rgb
                    )
                }
                val moduleCategoryString = when(category){
                    ModuleCategory.COMBAT -> "Combat"
                    ModuleCategory.EXPLOIT -> "Exploit"
                    ModuleCategory.MISC -> "Misc"
                    ModuleCategory.MOVEMENT -> "Movement"
                    ModuleCategory.PLAYER -> "Player"
                    ModuleCategory.RENDER -> "Render"
                    ModuleCategory.WORLD -> "World"
                }
                if (fontRainbow) drawSettings(m,settingStart!![moduleCategoryString]!!,mouseX,mouseY)
                drawButton1(mc.currentScreen.width / 16F * 9 -13,y+moduleHigh/2-2.5F,m.getToggle(),isClick(mc.currentScreen.width / 16F * 9 -13,y+moduleHigh/2-2.5F,mc.currentScreen.width / 16F * 9 - 4,y+moduleHigh/2-2.5F + 5,mouseX.toFloat(),mouseY.toFloat()),1F)
                if (rainbow){glPushMatrix();drawHoveringText(listOf(m.getDescription()),mouseX,mouseY);RenderHelper.disableStandardItemLighting();glPopMatrix()}
                y += moduleHigh
            }
        }
        private fun moduleClick(category: ModuleCategory,start: Int,mouseX: Int,mouseY: Int,mouseButton: Int){
            var jumpOver = 0
            var y = mc.currentScreen.height / 4F
            val moduleHigh = mc.currentScreen.height/16F
            for (m in Kevin.getInstance.moduleManager.getModules()){
                if (y > mc.currentScreen.height / 4F+moduleHigh*7)break
                if (m.getCategory() != category) continue
                if (jumpOver != start){jumpOver+=1;continue}
                if (isClick(mc.currentScreen.width / 16F * 9 -13,y+moduleHigh/2-2.5F,mc.currentScreen.width / 16F * 9 - 4,y+moduleHigh/2-2.5F + 5,mouseX.toFloat(),mouseY.toFloat())){ m.toggle() }
                if (isClick(mc.currentScreen.width/8F*3,y,mc.currentScreen.width / 16F * 9,y+moduleHigh,mouseX.toFloat(),mouseY.toFloat())&&!isClick(mc.currentScreen.width / 16F * 9 -13,y+moduleHigh/2-2.5F,mc.currentScreen.width / 16F * 9 - 4,y+moduleHigh/2-2.5F + 5,mouseX.toFloat(),mouseY.toFloat())){
                    when(category){
                        ModuleCategory.COMBAT -> setModuleOpen(y,moduleHigh,"Combat",mouseButton)
                        ModuleCategory.EXPLOIT -> setModuleOpen(y,moduleHigh,"Exploit",mouseButton)
                        ModuleCategory.MISC -> setModuleOpen(y,moduleHigh,"Misc",mouseButton)
                        ModuleCategory.MOVEMENT -> setModuleOpen(y,moduleHigh,"Movement",mouseButton)
                        ModuleCategory.PLAYER -> setModuleOpen(y,moduleHigh,"Player",mouseButton)
                        ModuleCategory.RENDER -> setModuleOpen(y,moduleHigh,"Render",mouseButton)
                        ModuleCategory.WORLD -> setModuleOpen(y,moduleHigh,"World",mouseButton)
                    }
                }
                val choose = ((y - mc.currentScreen.height / 4F) / moduleHigh).toInt() == when(category){
                    ModuleCategory.COMBAT -> {moduleOpen!!["Combat"]!!}
                    ModuleCategory.EXPLOIT -> {moduleOpen!!["Exploit"]!!}
                    ModuleCategory.MISC -> {moduleOpen!!["Misc"]!!}
                    ModuleCategory.MOVEMENT -> {moduleOpen!!["Movement"]!!}
                    ModuleCategory.PLAYER -> {moduleOpen!!["Player"]!!}
                    ModuleCategory.RENDER -> {moduleOpen!!["Render"]!!}
                    ModuleCategory.WORLD -> {moduleOpen!!["World"]!!}
                }
                val moduleCategoryString = when(category){
                    ModuleCategory.COMBAT -> "Combat"
                    ModuleCategory.EXPLOIT -> "Exploit"
                    ModuleCategory.MISC -> "Misc"
                    ModuleCategory.MOVEMENT -> "Movement"
                    ModuleCategory.PLAYER -> "Player"
                    ModuleCategory.RENDER -> "Render"
                    ModuleCategory.WORLD -> "World"
                }
                if (choose) settingsClick(m,settingStart!![moduleCategoryString]!!,mouseX,mouseY)
                y += moduleHigh
            }
        }
        private fun setModuleOpen(y: Float,moduleHigh: Float,moduleCategory: String,mouseButton: Int){
            if (mouseButton == 0) {moduleOpen!![moduleCategory] = ((y - mc.currentScreen.height / 4F) / moduleHigh).toInt();mc.soundHandler.playSound(PositionedSoundRecord.create(ResourceLocation("gui.button.press"), 1f));settingStart!![moduleCategory] = 0} else {
                if (moduleOpen!![moduleCategory] == ((y - mc.currentScreen.height / 4F) / moduleHigh).toInt()) {moduleOpen!![moduleCategory] = -114514;mc.soundHandler.playSound(PositionedSoundRecord.create(ResourceLocation("gui.button.press"), 0.6114514191981f));settingStart!![moduleCategory] = 0} else {
                    moduleOpen!![moduleCategory] = ((y - mc.currentScreen.height / 4F) / moduleHigh).toInt()
                    settingStart!![moduleCategory] = 0
                    mc.soundHandler.playSound(PositionedSoundRecord.create(ResourceLocation("gui.button.press"), 1f))
                }
            }
        }
        override fun handleMouseInput() {
            super.handleMouseInput()
            if (categoryOpen == 0 || categoryOpen == 8 || (!canModuleRoll && !canSettingRoll)) return
            val moduleCategoryString = when(categoryOpen){
                1 -> "Combat"
                2 -> "Exploit"
                3 -> "Misc"
                4 -> "Movement"
                5 -> "Player"
                6 -> "Render"
                7 -> "World"
                else -> ""
            }
            val moduleCategory = when(categoryOpen){
                1 -> ModuleCategory.COMBAT
                2 -> ModuleCategory.EXPLOIT
                3 -> ModuleCategory.MISC
                4 -> ModuleCategory.MOVEMENT
                5 -> ModuleCategory.PLAYER
                6 -> ModuleCategory.RENDER
                7 -> ModuleCategory.WORLD
                else -> ModuleCategory.MISC
            }
            var modulesNumber = 0
            for (m in Kevin.getInstance.moduleManager.getModules()){
                if (m.getCategory() == moduleCategory) modulesNumber += 1
            }
            when{
                Mouse.getEventDWheel() > 0 ->{
                    if (canModuleRoll) {
                        if (categoryStart!![moduleCategoryString]!! != 0) {
                            categoryStart!![moduleCategoryString] = categoryStart!![moduleCategoryString]!! - 1
                            moduleOpen!![moduleCategoryString] = moduleOpen!![moduleCategoryString]!! + 1
                        }
                    }
                    if (canSettingRoll && settingStart!![moduleCategoryString]!!>=5){
                        settingStart!![moduleCategoryString] = settingStart!![moduleCategoryString]!! - 5
                    }
                }
                Mouse.getEventDWheel() < 0 ->{
                    if (canModuleRoll) {
                        if (categoryStart!![moduleCategoryString]!! + 8 < modulesNumber) {
                            categoryStart!![moduleCategoryString] = categoryStart!![moduleCategoryString]!! + 1
                            moduleOpen!![moduleCategoryString] = moduleOpen!![moduleCategoryString]!! - 1
                        }
                    }
                    if (canSettingRoll && canSettingRoll2){
                        settingStart!![moduleCategoryString] = settingStart!![moduleCategoryString]!! + 5
                    }
                }
            }
        }
        private fun drawSettings(module: Module,start: Int,mouseX: Int,mouseY: Int){
            val values = module.values
            var y = mc.currentScreen.height/4F + 5 - start
            canSettingRoll2 = false
            for (v in values){
                when(v){
                    is BooleanValue ->{
                        if (y + Kevin.getInstance.fontManager.font35!!.fontHeight >mc.currentScreen.height/4F*3) {canSettingRoll2 = true;break}
                        if (y < mc.currentScreen.height/4F + 5) {y += Kevin.getInstance.fontManager.font35!!.fontHeight;continue}
                        glPushMatrix()
                        glScaled(0.6,0.6,0.6)
                        Kevin.getInstance.fontManager.font35!!.drawString(v.name,(mc.currentScreen.width / 16F * 9 + 5)/0.6F,y/0.6F,Color(0,0,0).rgb)
                        glPopMatrix()
                        drawButton1(mc.currentScreen.width / 4F * 3 - 13,y,v.get(),isClick(mc.currentScreen.width / 4F * 3 - 13,y,mc.currentScreen.width / 4F * 3 - 4,y + 5,mouseX.toFloat(),mouseY.toFloat()),1F)
                        y += Kevin.getInstance.fontManager.font35!!.fontHeight
                    }
                    is ListValue -> {
                        if (y + Kevin.getInstance.fontManager.font35!!.fontHeight * 0.6F > mc.currentScreen.height/4F*3) {canSettingRoll2 = true;break}
                        if (y < mc.currentScreen.height/4F + 5) {y += Kevin.getInstance.fontManager.font35!!.fontHeight * 0.6F;continue}
                        var x = 0
                        val maxWidth = (mc.currentScreen.width / 4F * 3) - (mc.currentScreen.width / 16F * 9 + 5)
                        glPushMatrix()
                        glScaled(0.6,0.6,0.6)
                        Kevin.getInstance.fontManager.font35!!.drawString(v.name,(mc.currentScreen.width / 16F * 9 + 5)/0.6F,y/0.6F,Color(0,0,0).rgb)
                        y += Kevin.getInstance.fontManager.font35!!.fontHeight * 0.6F
                        for (i in v.values){
                            if (y + Kevin.getInstance.fontManager.font35!!.fontHeight * 0.6F > mc.currentScreen.height/4F*3) {canSettingRoll2 = true;break}
                            if (y < mc.currentScreen.height/4F + 5) {y += Kevin.getInstance.fontManager.font35!!.fontHeight * 0.6F;continue}
                            if (x + Kevin.getInstance.fontManager.font35!!.getStringWidth(i) > maxWidth) {y += Kevin.getInstance.fontManager.font35!!.fontHeight * 0.6F;x = 0}
                            val color = if (v.get() == i) Color(0,111,255).rgb else Color(150,150,150).rgb
                            Kevin.getInstance.fontManager.font35!!.drawString(i,(mc.currentScreen.width / 16F * 9 + 5 + x)/0.6F,y/0.6F,color)
                            x += Kevin.getInstance.fontManager.font35!!.getStringWidth(i)
                        }
                        y += Kevin.getInstance.fontManager.font35!!.fontHeight
                        glPopMatrix()
                    }
                    is TextValue -> {
                        if (y + Kevin.getInstance.fontManager.font35!!.fontHeight * 1.6F > mc.currentScreen.height/4F*3) {canSettingRoll2 = true;break}
                        if (y < mc.currentScreen.height/4F + 5) {y += Kevin.getInstance.fontManager.font35!!.fontHeight * 1.6F;continue}
                        glPushMatrix()
                        glScaled(0.6,0.6,0.6)
                        Kevin.getInstance.fontManager.font35!!.drawString(v.name,(mc.currentScreen.width / 16F * 9 + 5)/0.6F,y/0.6F,Color(0,0,0).rgb)
                        y += Kevin.getInstance.fontManager.font35!!.fontHeight * 0.6F
                        Kevin.getInstance.fontManager.font35!!.drawString(v.get(),(mc.currentScreen.width / 16F * 9 + 5)/0.6F,y/0.6F,Color(0,111,255).rgb)
                        glPopMatrix()
                        y += Kevin.getInstance.fontManager.font35!!.fontHeight
                    }
                    is IntegerValue -> {
                        if (y + Kevin.getInstance.fontManager.font35!!.fontHeight * 0.8F + 8 > mc.currentScreen.height/4F*3) {canSettingRoll2 = true;break}
                        if (y < mc.currentScreen.height/4F + 5) {y += Kevin.getInstance.fontManager.font35!!.fontHeight * 0.8F + 8;continue}
                        glPushMatrix()
                        glScaled(0.6,0.6,0.6)
                        Kevin.getInstance.fontManager.font35!!.drawString(v.name,(mc.currentScreen.width / 16F * 9 + 5)/0.6F,y/0.6F,Color(0,0,0).rgb)
                        Kevin.getInstance.fontManager.font35!!.drawString(v.get().toString(),(mc.currentScreen.width / 4F * 3 - Kevin.getInstance.fontManager.font35!!.getStringWidth(v.get().toString())/2 - 10)/0.6F,y/0.6F,Color(0,0,0).rgb)
                        y += Kevin.getInstance.fontManager.font35!!.fontHeight*0.8F
                        glPopMatrix()
                        drawButton2(mc.currentScreen.width / 16F * 9 + 5,y,mc.currentScreen.width / 4F * 3 - 5,v.minimum.toFloat(),v.maximum.toFloat(),v.get().toFloat())
                        if (isClick(mc.currentScreen.width / 16F * 9 + 5,y - 3,mc.currentScreen.width / 4F * 3 - 5,y + 3,mouseX.toFloat(),mouseY.toFloat())){
                            if (Mouse.isButtonDown(0)){
                                val i = MathHelper.clamp_double((mouseX - (mc.currentScreen.width / 16F * 9 + 5)) / ((mc.currentScreen.width / 4F * 3 - 5) - (mc.currentScreen.width / 16F * 9 + 5)).toDouble(),0.0,1.0)
                                v.set((v.minimum + (v.maximum - v.minimum) * i).toInt())
                            }
                        }
                        y += 8
                    }
                    is FloatValue -> {
                        if (y + Kevin.getInstance.fontManager.font35!!.fontHeight * 0.8F + 8 > mc.currentScreen.height/4F*3) {canSettingRoll2 = true;break}
                        if (y < mc.currentScreen.height/4F + 5) {y += Kevin.getInstance.fontManager.font35!!.fontHeight * 0.8F + 8;continue}
                        glPushMatrix()
                        glScaled(0.6,0.6,0.6)
                        Kevin.getInstance.fontManager.font35!!.drawString(v.name,(mc.currentScreen.width / 16F * 9 + 5)/0.6F,y/0.6F,Color(0,0,0).rgb)
                        Kevin.getInstance.fontManager.font35!!.drawString(v.get().toString(),(mc.currentScreen.width / 4F * 3 - Kevin.getInstance.fontManager.font35!!.getStringWidth(v.get().toString())/2 - 10)/0.6F,y/0.6F,Color(0,0,0).rgb)
                        y += Kevin.getInstance.fontManager.font35!!.fontHeight*0.8F
                        glPopMatrix()
                        drawButton2(mc.currentScreen.width / 16F * 9 + 5,y,mc.currentScreen.width / 4F * 3 - 5,v.minimum,v.maximum,v.get())
                        if (isClick(mc.currentScreen.width / 16F * 9 + 5,y - 3,mc.currentScreen.width / 4F * 3 - 5,y + 3,mouseX.toFloat(),mouseY.toFloat())){
                            if (Mouse.isButtonDown(0)){
                                val i = MathHelper.clamp_double((mouseX - (mc.currentScreen.width / 16F * 9 + 5)) / ((mc.currentScreen.width / 4F * 3 - 5) - (mc.currentScreen.width / 16F * 9 + 5)).toDouble(),0.0,1.0)
                                v.set(Formatter().format("%.2f",v.minimum + (v.maximum - v.minimum) * i).toString().toFloat())
                            }
                        }
                        y += 8
                    }
                    is BlockValue -> {
                        if (y + Kevin.getInstance.fontManager.font35!!.fontHeight > mc.currentScreen.height/4F*3) {canSettingRoll2 = true;break}
                        if (y < mc.currentScreen.height/4F + 5) {y += Kevin.getInstance.fontManager.font35!!.fontHeight;continue}
                        glPushMatrix()
                        glScaled(0.6,0.6,0.6)
                        Kevin.getInstance.fontManager.font35!!.drawString(v.name,(mc.currentScreen.width / 16F * 9 + 5)/0.6F,y/0.6F,Color(0,0,0).rgb)
                        Kevin.getInstance.fontManager.font35!!.drawString(BlockUtils.getBlockName(v.get()),(mc.currentScreen.width / 4F * 3 - 5 - Kevin.getInstance.fontManager.font35!!.getStringWidth(BlockUtils.getBlockName(v.get())))/0.6F,y/0.6F,Color(60,60,60).rgb)
                        glPopMatrix()
                        y += Kevin.getInstance.fontManager.font35!!.fontHeight
                    }
                }
                if (y>mc.currentScreen.height/4F*3) {canSettingRoll2 = true;break}
            }
        }
        private fun drawButton2(x: Float,y: Float,x1: Float,minValue: Float,maxValue: Float,value: Float){
            glEnable(GL_BLEND)
            glDisable(GL_TEXTURE_2D)
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
            glEnable(GL_LINE_SMOOTH)
            glColor(Color(0,111,255))
            glLineWidth(3F)
            glBegin(GL_LINES)
            glVertex2f(x, y)
            glVertex2f(x1, y)
            glEnd()
            glEnable(GL_TEXTURE_2D)
            glDisable(GL_BLEND)
            glDisable(GL_LINE_SMOOTH)
            val d = x1 - x
            val dv = (value - minValue)/(maxValue - minValue)
            val dl = d * dv
            RenderUtils.drawRect( x + dl - 1,y + 3,x + dl + 1,y - 3,Color(0,111,255).rgb)
        }
        private fun settingsClick(module: Module,start: Int,mouseX: Int,mouseY: Int){
            val values = module.values
            var y = mc.currentScreen.height/4F + 5 - start
            for (v in values){
                when(v){
                    is BooleanValue ->{
                        if (y + Kevin.getInstance.fontManager.font35!!.fontHeight >mc.currentScreen.height/4F*3)break
                        if (y < mc.currentScreen.height/4F + 5) {y += Kevin.getInstance.fontManager.font35!!.fontHeight;continue}
                        if (isClick(mc.currentScreen.width / 4F * 3 - 13,y,mc.currentScreen.width / 4F * 3 - 4,y + 5,mouseX.toFloat(),mouseY.toFloat())) {v.set(!v.get());if(v.get())mc.soundHandler.playSound(PositionedSoundRecord.create(ResourceLocation("gui.button.press"), 1f))else mc.soundHandler.playSound(PositionedSoundRecord.create(ResourceLocation("gui.button.press"), 0.6114514191981f))}
                        y += Kevin.getInstance.fontManager.font35!!.fontHeight
                    }

                    is ListValue -> {
                        if (y + Kevin.getInstance.fontManager.font35!!.fontHeight * 0.6F > mc.currentScreen.height/4F*3)break
                        if (y < mc.currentScreen.height/4F + 5) {y += Kevin.getInstance.fontManager.font35!!.fontHeight * 0.6F;continue}
                        var x = 0
                        val maxWidth = (mc.currentScreen.width / 4F * 3) - (mc.currentScreen.width / 16F * 9 + 5)
                        y += Kevin.getInstance.fontManager.font35!!.fontHeight * 0.6F
                        for (i in v.values){
                            if (y + Kevin.getInstance.fontManager.font35!!.fontHeight * 0.6F > mc.currentScreen.height/4F*3)break
                            if (y < mc.currentScreen.height/4F + 5) {y += Kevin.getInstance.fontManager.font35!!.fontHeight * 0.6F;continue}
                            if (x + Kevin.getInstance.fontManager.font35!!.getStringWidth(i) > maxWidth) {y += Kevin.getInstance.fontManager.font35!!.fontHeight * 0.6F;x = 0}
                            val click = isClick(mc.currentScreen.width/16F*9+5+x,y,mc.currentScreen.width/16F*9+5+x+Kevin.getInstance.fontManager.font35!!.getStringWidth(i),y+Kevin.getInstance.fontManager.font35!!.fontHeight*0.6F,mouseX.toFloat(),mouseY.toFloat())
                            if (click) {v.set(i);mc.soundHandler.playSound(PositionedSoundRecord.create(ResourceLocation("gui.button.press"), 1f))}
                            x += Kevin.getInstance.fontManager.font35!!.getStringWidth(i)
                        }
                        y += Kevin.getInstance.fontManager.font35!!.fontHeight
                    }
                    is TextValue -> {
                        if (y + Kevin.getInstance.fontManager.font35!!.fontHeight * 1.6F > mc.currentScreen.height/4F*3)break
                        if (y < mc.currentScreen.height/4F + 5) {y += Kevin.getInstance.fontManager.font35!!.fontHeight * 1.6F;continue}
                        y += Kevin.getInstance.fontManager.font35!!.fontHeight * 0.6F
                        y += Kevin.getInstance.fontManager.font35!!.fontHeight
                    }
                    is IntegerValue -> {
                        if (y + Kevin.getInstance.fontManager.font35!!.fontHeight * 0.8F + 8 > mc.currentScreen.height/4F*3)break
                        if (y < mc.currentScreen.height/4F + 5) {y += Kevin.getInstance.fontManager.font35!!.fontHeight * 0.8F + 8;continue}
                        y += Kevin.getInstance.fontManager.font35!!.fontHeight*0.8F
                        y += 8
                    }
                    is FloatValue -> {
                        if (y + Kevin.getInstance.fontManager.font35!!.fontHeight * 0.8F + 8 > mc.currentScreen.height/4F*3) break
                        if (y < mc.currentScreen.height/4F + 5) {y += Kevin.getInstance.fontManager.font35!!.fontHeight * 0.8F + 8;continue}
                        y += Kevin.getInstance.fontManager.font35!!.fontHeight*0.8F
                        y += 8
                    }
                    is BlockValue -> {
                        if (y + Kevin.getInstance.fontManager.font35!!.fontHeight > mc.currentScreen.height/4F*3)break
                        if (y < mc.currentScreen.height/4F + 5) {y += Kevin.getInstance.fontManager.font35!!.fontHeight;continue}
                        y += Kevin.getInstance.fontManager.font35!!.fontHeight
                    }
                }
                if (y>mc.currentScreen.height/4F*3) break
            }
        }
    }
}