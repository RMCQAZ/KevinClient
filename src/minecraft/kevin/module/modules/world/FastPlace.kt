package kevin.module.modules.world

import kevin.module.IntegerValue
import kevin.module.Module
import kevin.module.ModuleCategory

class FastPlace : Module("FastPlace", "Allows you to place blocks faster.", category = ModuleCategory.WORLD) {
    val speedValue = IntegerValue("Speed", 0, 0, 4)
}