package kevin.module.modules.misc

import kevin.event.EventTarget
import kevin.event.TextEvent
import kevin.main.KevinClient
import kevin.module.Module
import kevin.module.ModuleCategory
import kevin.module.TextValue
import kevin.utils.ColorUtils.translateAlternateColorCodes
import kevin.utils.StringUtils

class NameProtect : Module(name = "NameProtect", description = "Changes playernames clientside.", category = ModuleCategory.MISC) {

    private val fakeNameValue = TextValue("FakeName", "&cKevinUser")

    @EventTarget
    fun onText(event: TextEvent) {
        val thePlayer = mc.thePlayer

        if (thePlayer == null || event.text!!.contains(KevinClient.cStart))
            return

        event.text = StringUtils.replace(event.text, thePlayer.name, translateAlternateColorCodes(fakeNameValue.get()) + "§f")
    }
}