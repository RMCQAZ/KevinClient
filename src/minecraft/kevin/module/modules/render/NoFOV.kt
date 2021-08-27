package kevin.module.modules.render

import kevin.module.FloatValue
import kevin.module.Module
import kevin.module.ModuleCategory

class NoFOV : Module("NoFOV", "Disables FOV changes caused by speed effect, etc.", category = ModuleCategory.RENDER) {
    val fovValue = FloatValue("FOV", 1f, 0f, 1.5f)
}