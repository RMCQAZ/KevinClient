package kevin.module.modules

import kevin.event.EventTarget
import kevin.event.UpdateEvent
import kevin.module.BooleanValue
import kevin.module.Module
import kevin.utils.EntityUtils

class Targets : Module("Targets") {
    private val players = BooleanValue("Players",true)
    private val mobs = BooleanValue("Mobs",true)
    private val animals = BooleanValue("Animals",false)
    private val invisible = BooleanValue("Invisible",true)
    private val death = BooleanValue("Death",false)
    @EventTarget(true)
    fun onUpdate(event: UpdateEvent){
        EntityUtils.targetPlayer = players.get()
        EntityUtils.targetMobs = mobs.get()
        EntityUtils.targetAnimals = animals.get()
        EntityUtils.targetInvisible = invisible.get()
        EntityUtils.targetDeath = death.get()
    }
}