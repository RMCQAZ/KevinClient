package kevin.module.modules.world

import kevin.event.EventTarget
import kevin.event.UpdateEvent
import kevin.main.KevinClient
import kevin.module.FloatValue
import kevin.module.Module
import kevin.module.ModuleCategory

class FastBreak : Module("FastBreak", "Allows you to break blocks faster.", category = ModuleCategory.WORLD) {

    private val breakDamage = FloatValue("BreakDamage", 0.8F, 0.1F, 1F)

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        mc.playerController.blockHitDelay = 0

        if (mc.playerController.curBlockDamageMP > breakDamage.get())
            mc.playerController.curBlockDamageMP = 1F

        val breaker = KevinClient.moduleManager.getModule("Breaker") as Breaker
        val nuker = KevinClient.moduleManager.getModule("Nuker") as Nuker

        if (breaker.currentDamage > breakDamage.get())
            breaker.currentDamage = 1F

        if (nuker.currentDamage > breakDamage.get())
            nuker.currentDamage = 1F
    }
}