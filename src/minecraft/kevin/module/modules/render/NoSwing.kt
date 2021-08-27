package kevin.module.modules.render

import kevin.module.BooleanValue
import kevin.module.Module
import kevin.module.ModuleCategory

class NoSwing : Module("NoSwing", "Disabled swing effect when hitting an entity/mining a block.", category = ModuleCategory.RENDER) {
    val serverSideValue = BooleanValue("ServerSide", true)
}