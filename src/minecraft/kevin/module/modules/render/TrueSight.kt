package kevin.module.modules.render

import kevin.module.BooleanValue
import kevin.module.Module
import kevin.module.ModuleCategory

class TrueSight: Module("TrueSight", "Allows you to see invisible entities and barriers.", category = ModuleCategory.RENDER) {
    val barriersValue = BooleanValue("Barriers", true)
    val entitiesValue = BooleanValue("Entities", true)
}