package kevin.module.modules.render

import kevin.event.ClientShutdownEvent
import kevin.event.EventTarget
import kevin.event.UpdateEvent
import kevin.module.ListValue
import kevin.module.Module
import kevin.module.ModuleCategory
import net.minecraft.potion.Potion
import net.minecraft.potion.PotionEffect

class FullBright : Module("FullBright", "Brightens up the world around you.", category = ModuleCategory.RENDER) {
    private val modeValue = ListValue("Mode", arrayOf("Gamma", "NightVision"), "Gamma")
    private var prevGamma = -1f

    override fun onEnable() {
        prevGamma = mc.gameSettings.gammaSetting
    }

    override fun onDisable() {
        if (prevGamma == -1f)
            return

        mc.gameSettings.gammaSetting = prevGamma
        prevGamma = -1f

        mc.thePlayer?.removePotionEffectClient(Potion.nightVision.id)
    }

    @EventTarget(ignoreCondition = true)
    fun onUpdate(event: UpdateEvent?) {
        if (getToggle() /**|| LiquidBounce.moduleManager.getModule(XRay::class.java)!!.state**/) {
            when (modeValue.get().toLowerCase()) {
                "gamma" -> when {
                    mc.gameSettings.gammaSetting <= 100f -> mc.gameSettings.gammaSetting++
                }
                "nightvision" -> mc.thePlayer?.addPotionEffect(PotionEffect(Potion.nightVision.id, 1337, 1))
            }
        } else if (prevGamma != -1f) {
            mc.gameSettings.gammaSetting = prevGamma
            prevGamma = -1f
        }
    }

    @EventTarget(ignoreCondition = true)
    fun onShutdown(event: ClientShutdownEvent?) {
        onDisable()
    }
}