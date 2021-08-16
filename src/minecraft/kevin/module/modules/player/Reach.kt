package kevin.module.modules.player

import kevin.module.FloatValue
import kevin.module.Module
import kevin.module.ModuleCategory

class Reach : Module("Reach","Increases your reach.", category = ModuleCategory.PLAYER) {
    val combatReachValue = FloatValue("CombatReach", 3.5f, 3f, 7f)
    val buildReachValue = FloatValue("BuildReach", 5f, 4.5f, 7f)

    val maxRange: Float
        get() {
            val combatRange = combatReachValue.get()
            val buildRange = buildReachValue.get()

            return if (combatRange > buildRange) combatRange else buildRange
        }

    override val tag: String
        get() = combatReachValue.get().toString()
}