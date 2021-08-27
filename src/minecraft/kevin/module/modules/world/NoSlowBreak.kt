package kevin.module.modules.world

import kevin.module.BooleanValue
import kevin.module.Module
import kevin.module.ModuleCategory

class NoSlowBreak : Module("NoSlowBreak", "Automatically adjusts breaking speed when using modules that influence it.", category = ModuleCategory.WORLD) {
    val airValue = BooleanValue("Air", true)
    val waterValue = BooleanValue("Water", false)
}