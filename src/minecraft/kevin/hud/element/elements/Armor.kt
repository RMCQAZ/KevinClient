package kevin.hud.element.elements

import kevin.hud.element.Border
import kevin.hud.element.Element
import kevin.hud.element.ElementInfo
import kevin.hud.element.Side
import kevin.main.Kevin
import kevin.module.FloatValue
import kevin.module.IntegerValue
import kevin.module.ListValue
import kevin.utils.FontManager
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.text.DecimalFormat

@ElementInfo(name = "Armor")
class Armor(x: Double = -8.0, y: Double = 57.0, scale: Float = 1F,
            side: Side = Side(Side.Horizontal.MIDDLE, Side.Vertical.DOWN)) : Element(x, y, scale, side) {

    private val modeValue = ListValue("Alignment", arrayOf("Horizontal", "Vertical"), "Horizontal")
    private val armorShowDamageMode = ListValue("ArmorShowDamage", arrayOf("None","Percentage","Value","All"),"All")
    private val armorDamageColor = ListValue("ArmorShowDamageColor", arrayOf("Custom","Rainbow","Damage"),"Damage")
    private val armorDamageCustomRed = IntegerValue("ArmorDamageCustomRed",255,0,255)
    private val armorDamageCustomGreen = IntegerValue("ArmorDamageCustomGreen",255,0,255)
    private val armorDamageCustomBlue = IntegerValue("ArmorDamageCustomBlue",255,0,255)
    private val armorDamageRainbowX = FloatValue("ArmorDamageRainbowX",-1000F, -2000F, 2000F)
    private val armorDamageRainbowY = FloatValue("ArmorDamageRainbowY",-1000F, -2000F, 2000F)

    override fun drawElement(): Border {
        val showDamageMode = armorShowDamageMode
        val showDamageColorMode = armorDamageColor
        val showDamageRainbow = showDamageColorMode.get().equals("rainbow",true)
        val rainbowX = armorDamageRainbowX
        val rainbowY = armorDamageRainbowY

        GL11.glPushMatrix()

        val isCreative = !mc.playerController.isNotCreative
        val renderItem = mc.renderItem
        val isInsideWater = mc.thePlayer!!.isInsideOfMaterial(Material.water)

        val mode = modeValue.get()
        var x = if(mode.equals("Horizontal", true)) 27 else 87
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

        val bx1 = if(mode.equals("Horizontal", true)) 27f else 87f
        var by1 = 55f
        var bx2 = 104f
        val by2 = 55f

        var damageTextMaxLong = 0
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

            if (Kevin.getInstance.fontManager.font35!!.getStringWidth(damageText) > damageTextMaxLong) damageTextMaxLong = Kevin.getInstance.fontManager.font35!!.getStringWidth(damageText)

            val color = if (showDamageRainbow) 0
            else if (showDamageColorMode.get().equals("custom",true)) Color(armorDamageCustomRed.get(),armorDamageCustomGreen.get(),armorDamageCustomBlue.get()).rgb else {
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
            by1 -= 18
        }
        GL11.glPopMatrix()

        if (damageTextMaxLong!=0) bx2 += damageTextMaxLong + 5
        return Border(bx1, by1, bx2, by2)
    }
}
