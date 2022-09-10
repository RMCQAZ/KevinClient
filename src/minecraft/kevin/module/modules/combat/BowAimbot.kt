package kevin.module.modules.combat

import kevin.event.EventTarget
import kevin.event.Render3DEvent
import kevin.event.UpdateEvent
import kevin.module.*
import kevin.utils.EntityUtils
import kevin.utils.RenderUtils
import kevin.utils.RotationUtils
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemBow
import java.awt.Color

class BowAimbot : Module("BowAimbot", "Automatically aims at players when using a bow.", category = ModuleCategory.COMBAT) {
    private val silentValue = BooleanValue("Silent", true)
    private val predictValue = BooleanValue("Predict", true)
    private val throughWallsValue = BooleanValue("ThroughWalls", false)
    private val predictSizeValue = FloatValue("PredictSize", 2F, 0.1F, 5F)
    private val priorityValue = ListValue("Priority", arrayOf("Health", "Distance", "Direction"), "Direction")
    private val markValue = BooleanValue("Mark", true)

    private var target: Entity? = null

    override fun onDisable() {
        target = null
    }
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        target = null
        if (mc.thePlayer.itemInUse?.item is ItemBow) {
            val entity = getTarget(throughWallsValue.get(), priorityValue.get()) ?: return
            target = entity
            RotationUtils.faceBow(target, silentValue.get(), predictValue.get(), predictSizeValue.get())
        }
    }
    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        if (target != null && !priorityValue.get().equals("Multi", ignoreCase = true) && markValue.get())
            RenderUtils.drawPlatform(target, Color(37, 126, 255, 70))
    }
    private fun getTarget(throughWalls: Boolean, priorityMode: String): Entity? {
        val targets = mc.theWorld.loadedEntityList.filter {
            it is EntityLivingBase && EntityUtils.isSelected(it, true) &&
                    (throughWalls || mc.thePlayer.canEntityBeSeen(it))
        }
        return when (priorityMode.toUpperCase()) {
            "DISTANCE" -> targets.minByOrNull { mc.thePlayer.getDistanceToEntity(it) }
            "DIRECTION" -> targets.minByOrNull { RotationUtils.getRotationDifference(it) }
            "HEALTH" -> targets.minByOrNull { (it as EntityLivingBase).health }
            else -> null
        }
    }
    fun hasTarget() = target != null && mc.thePlayer!!.canEntityBeSeen(target!!)
}