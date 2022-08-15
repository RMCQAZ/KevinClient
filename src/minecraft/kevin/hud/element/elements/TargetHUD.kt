package kevin.hud.element.elements

import kevin.hud.element.Border
import kevin.hud.element.Element
import kevin.hud.element.ElementInfo
import kevin.main.KevinClient
import kevin.module.FloatValue
import kevin.module.ListValue
import kevin.module.modules.combat.KillAura
import kevin.utils.RenderUtils
import kevin.utils.getDistanceToEntityBox
import kevin.utils.getPing
import net.minecraft.client.gui.Gui.drawScaledCustomSizeModalRect
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.pow

@ElementInfo(name = "TargetHUD")
class TargetHUD : Element() {

    private val decimalFormat = DecimalFormat("##0.00", DecimalFormatSymbols(Locale.ENGLISH))
    private val fadeSpeed = FloatValue("FadeSpeed", 2F, 1F, 9F)
    private val mode = ListValue("Mode", arrayOf("Liquid","Kevin"),"Kevin")

    private var easingHealth: Float = 0F
    private var lastTarget: Entity? = null

    override fun drawElement(): Border? {
        val target = (KevinClient.moduleManager.getModule("KillAura") as KillAura).target ?: (KevinClient.moduleManager.getModule("KillAura") as KillAura).sTarget
        when(mode.get()){
            "Liquid" -> {
                if ((target) is EntityPlayer) {
                    if (target != lastTarget || easingHealth < 0 || easingHealth > target.maxHealth ||
                        abs(easingHealth - target.health) < 0.01) {
                        easingHealth = target.health
                    }
                    val width = (38 + (target.name?.let(KevinClient.fontManager.font40!!::getStringWidth) ?: 0))
                        .coerceAtLeast(118)
                        .toFloat()
                    // Draw rect box
                    RenderUtils.drawBorderedRect(0F, 0F, width, 36F, 3F, Color.BLACK.rgb, Color.BLACK.rgb)
                    // Damage animation
                    if (easingHealth > target.health)
                        RenderUtils.drawRect(0F, 34F, (easingHealth / target.maxHealth) * width,
                            36F, Color(252, 185, 65).rgb)
                    // Health bar
                    RenderUtils.drawRect(0F, 34F, (target.health / target.maxHealth) * width,
                        36F, Color(252, 96, 66).rgb)
                    // Heal animation
                    if (easingHealth < target.health)
                        RenderUtils.drawRect((easingHealth / target.maxHealth) * width, 34F,
                            (target.health / target.maxHealth) * width, 36F, Color(44, 201, 144).rgb)
                    easingHealth += ((target.health - easingHealth) / 2.0F.pow(10.0F - fadeSpeed.get())) * RenderUtils.deltaTime
                    target.name?.let { KevinClient.fontManager.font40!!.drawString(it, 36f, 3f, 0xffffff) }
                    KevinClient.fontManager.font35!!.drawString("Distance: ${decimalFormat.format(mc.thePlayer!!.getDistanceToEntityBox(target))}", 36f, 15f, 0xffffff)
                    // Draw info
                    val playerInfo = mc.netHandler.getPlayerInfo(target.uniqueID)
                    if (playerInfo != null) {
                        KevinClient.fontManager.font35!!.drawString("Ping: ${playerInfo.responseTime.coerceAtLeast(0)}",
                            36f, 24f, 0xffffff)
                    // Draw head
                        val locationSkin = playerInfo.locationSkin
                        drawHead(locationSkin, 30, 30)
                    }
                }
                lastTarget = target
                return Border(0F, 0F, 120F, 36F)
            }
            "Kevin" -> {
                if (target!=null){
                    val health = if (target is EntityLivingBase) String.format("%.2f",target.health).toFloat() else 1F
                    val maxHealth = if (target is EntityLivingBase) String.format("%.2f",target.maxHealth).toFloat() else 1F
                    val healthPercent = (health/maxHealth)*100f

                    val hurtTime = if (target is EntityLivingBase) target.hurtTime else 0
                    val ping = if (target is EntityPlayer) target.getPing() else 0
                    val yaw = String.format("%.2f",target.rotationYaw).toFloat()
                    val pitch = String.format("%.2f",target.rotationPitch).toFloat()
                    val distance = String.format("%.2f",mc.thePlayer.getDistanceToEntityBox(target)).toFloat()
                    val onGround = target.onGround

                    val nameText = "Name: ${target.name}"
                    val IDText = "ID: ${target.uniqueID}"
                    val healthText = "Health: $health/$maxHealth  $healthPercent%"
                    val hurtTimeText = "HurtTime: $hurtTime"
                    val pingText = "Ping: $ping"
                    val rotationText = "Yaw: $yaw | Pitch: $pitch"
                    val distanceOnGroundText = "Distance: $distance | OnGround: $onGround"

                    val itemInHand = if (target is EntityLivingBase) target.heldItem else null
                    val armor1 = if (target is EntityLivingBase) target.getCurrentArmor(0) else null
                    val armor2 = if (target is EntityLivingBase) target.getCurrentArmor(1) else null
                    val armor3 = if (target is EntityLivingBase) target.getCurrentArmor(2) else null
                    val armor4 = if (target is EntityLivingBase) target.getCurrentArmor(3) else null

                    val textList = arrayListOf(nameText,healthText,hurtTimeText,pingText,rotationText,distanceOnGroundText)
                    val textListSorted = textList.toMutableList()
                    textListSorted.sortBy{KevinClient.fontManager.font40!!.getStringWidth(it)}
                    val width = KevinClient.fontManager.font35!!.getStringWidth(textListSorted.last())
                    val x2 = if (0.25F+width/2+3F>18*5)0.25F+width/2+3F else 18*5F
                    val text = if (target is EntityLivingBase) "A:${target.totalArmorValue} ${(target.totalArmorValue/20F)*100}%" else "A:? ?%"

                    RenderUtils.drawBorderedRect(-8.5F,-12.5F,14.75F+x2,54.5F,1F,Color.white.rgb,Color(0,0,0,150).rgb)
                    drawEntityOnScreen(3.0,20.0,15F,target)

                    linesStart(1F,Color.white)
                    GL11.glVertex2d(14.75, -12.5)
                    GL11.glVertex2d(14.75,46.5)

                    GL11.glVertex2d(-8.5,23.0)
                    GL11.glVertex2d(14.75+x2,23.0)

                    GL11.glVertex2d(-8.5,46.5)
                    GL11.glVertex2d(14.75+x2,46.5)

                    GL11.glVertex2d(14.75,47-19.0)
                    GL11.glVertex2d(14.75+x2,47-19.0)

                    var xO = 18
                    repeat(4) {
                        GL11.glVertex2d(14.75 + xO, 47 - 19.0)
                        GL11.glVertex2d(14.75 + xO, 46.5)
                        xO += 18
                    }

                    GL11.glVertex2d((14.75+x2+6.5-(KevinClient.fontManager.font35!!.getStringWidth(text))*0.8),46.5)
                    GL11.glVertex2d((14.75+x2+6.5-(KevinClient.fontManager.font35!!.getStringWidth(text))*0.8),54.5)
                    linesEnd()
                    linesStart(5F,Color(255,0,0))

                    val h = x2 * (hurtTime / 10F)
                    val hel = (14.0+x2+6.5-(KevinClient.fontManager.font35!!.getStringWidth(text))*0.8)*(healthPercent/100)

                    GL11.glVertex2d(14.75,25.5)
                    GL11.glVertex2d(14.75+h,25.5)

                    GL11.glVertex2d(-7.75,48.5)
                    GL11.glVertex2d(-7.75+hel,48.5)
                    linesEnd()
                    linesStart(5F,Color(0,111,255))

                    val arv = if (target is EntityLivingBase) (14.0+x2+6.5-(KevinClient.fontManager.font35!!.getStringWidth(text))*0.8)*(target.totalArmorValue/20F) else .0

                    GL11.glVertex2d(-7.75,52.5)
                    GL11.glVertex2d(-7.75+arv,52.5)
                    linesEnd()

                    GL11.glPushMatrix()
                    GL11.glScaled(0.5,0.5,0.5)
                    KevinClient.fontManager.font35!!.drawString(text,(14.75F+x2-2F)/0.5F-KevinClient.fontManager.font35!!.getStringWidth(text),48.5F/0.5F,Color(0,111,255).rgb)
                    GL11.glPopMatrix()

                    if (!(mc.netHandler.getPlayerInfo(target.uniqueID) == null || mc.netHandler.getPlayerInfo(target.uniqueID).locationSkin == null)) {
                        GL11.glPushMatrix()
                        GL11.glTranslated(-10.36, 21.25, 0.0)
                        drawHead(mc.netHandler.getPlayerInfo(target.uniqueID).locationSkin, 23, 23)
                        GL11.glPopMatrix()
                    }

                    var yO = 0.0
                    textList.forEach{
                        yO += if (it == textList.first()){
                            GL11.glPushMatrix()
                            GL11.glScaled(0.6,0.6,0.6)
                            KevinClient.fontManager.font40!!.drawString(it,16F/0.6F,(-11F+yO.toFloat())/0.6F,Color.white.rgb)
                            GL11.glPopMatrix()
                            KevinClient.fontManager.font40!!.fontHeight * 0.6
                        }else{
                            GL11.glPushMatrix()
                            GL11.glScaled(0.5,0.5,0.5)
                            KevinClient.fontManager.font35!!.drawString(it,16F/0.5F,(-11F+yO.toFloat())/0.5F,Color.white.rgb)
                            GL11.glPopMatrix()
                            KevinClient.fontManager.font35!!.fontHeight * 0.5
                        }
                    }
                    val itemList = arrayListOf(itemInHand,armor1,armor2,armor3,armor4)
                    var x = 1
                    GL11.glPushMatrix()
                    RenderHelper.enableGUIStandardItemLighting()
                    itemList.forEach {
                        x += if (it != null){
                            mc.renderItem.renderItemAndEffectIntoGUI(it, 15+x, 47-18)
                            mc.renderItem.renderItemOverlays(mc.fontRendererObj, it, 15+x, 47-18)
                            18
                        }else{
                            18
                        }
                    }
                    RenderHelper.disableStandardItemLighting()
                    GlStateManager.enableAlpha()
                    GlStateManager.disableBlend()
                    GlStateManager.disableLighting()
                    GlStateManager.disableCull()
                    GL11.glPopMatrix()
                }
                lastTarget = target
                return Border(-8.5F,-12.5F,14.75F+(18*5F),54.5F)
            }
            else -> return null
        }
    }

    private fun linesStart(width: Float,color: Color){
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        RenderUtils.glColor(color)
        GL11.glLineWidth(width)
        GL11.glBegin(GL11.GL_LINES)
    }
    private fun linesEnd(){
        GL11.glEnd()
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
    }

    private fun drawHead(skin: ResourceLocation, width: Int, height: Int) {
        GL11.glColor4f(1F, 1F, 1F, 1F)
        mc.textureManager.bindTexture(skin)
        drawScaledCustomSizeModalRect(2, 2, 8F, 8F, 8, 8, width, height,
            64F, 64F)
    }

    private fun drawEntityOnScreen(X: Double, Y: Double, S: Float, entity: Entity){
        GlStateManager.enableColorMaterial()
        GlStateManager.pushMatrix()
        GlStateManager.translate(X, Y, 50.0)
        GlStateManager.scale((-S), S, S)
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F)
        val renderYawOffset = if (entity is EntityLivingBase) entity.renderYawOffset else 0F
        val rotationYaw = entity.rotationYaw
        val rotationPitch = entity.rotationPitch
        val prevRotationYawHead = if (entity is EntityLivingBase) entity.prevRotationYawHead else 0F
        val rotationYawHead = entity.rotationYawHead
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F)
        RenderHelper.enableStandardItemLighting()
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F)

        if (entity is EntityLivingBase) entity.renderYawOffset = atan(entity.rotationYaw / 40F) * 20F
        entity.rotationYaw = atan(entity.rotationYaw / 40F) * 40F
        entity.rotationPitch = -atan((if (entity.rotationPitch > 0) -entity.rotationPitch else abs(entity.rotationPitch)) / 40F) * 20F
        entity.rotationYawHead = entity.rotationYaw
        if (entity is EntityLivingBase) entity.prevRotationYawHead = entity.rotationYaw

        GlStateManager.translate(0.0, 0.0, 0.0)

        val renderManager = mc.renderManager
        renderManager.playerViewY = 180.0F
        renderManager.isRenderShadow = false
        renderManager.renderEntityWithPosYaw(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F)
        renderManager.isRenderShadow = true

        if (entity is EntityLivingBase) entity.renderYawOffset = renderYawOffset
        entity.rotationYaw = rotationYaw
        entity.rotationPitch = rotationPitch
        if (entity is EntityLivingBase) entity.prevRotationYawHead = prevRotationYawHead
        entity.rotationYawHead = rotationYawHead

        GlStateManager.popMatrix()
        RenderHelper.disableStandardItemLighting()
        GlStateManager.disableRescaleNormal()
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit)
        GlStateManager.disableTexture2D()
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit)
    }
}