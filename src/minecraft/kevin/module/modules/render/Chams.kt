package kevin.module.modules.render

import kevin.module.BooleanValue
import kevin.module.Module
import kevin.module.ModuleCategory

class Chams : Module("Chams", "Allows you to see targets through blocks.", category = ModuleCategory.RENDER) {
    val targetsValue = BooleanValue("Targets", true)
    val chestsValue = BooleanValue("Chests", true)
    val itemsValue = BooleanValue("Items", true)
}