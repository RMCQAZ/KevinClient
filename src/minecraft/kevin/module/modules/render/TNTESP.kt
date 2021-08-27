package kevin.module.modules.render

import kevin.event.EventTarget
import kevin.event.Render3DEvent
import kevin.module.Module
import kevin.module.ModuleCategory
import kevin.utils.RenderUtils
import net.minecraft.entity.item.EntityTNTPrimed
import java.awt.Color

class TNTESP : Module("TNTESP","Allows you to see ignited TNT blocks through walls.", category = ModuleCategory.RENDER) {
    @EventTarget
    fun onRender3D(event : Render3DEvent) {
        mc.theWorld!!.loadedEntityList.filter{it is EntityTNTPrimed}.forEach { RenderUtils.drawEntityBox(it, Color.RED, false) }
    }
}