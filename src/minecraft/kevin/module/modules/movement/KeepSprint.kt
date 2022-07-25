package kevin.module.modules.movement

import kevin.event.AttackEvent
import kevin.event.EventTarget
import kevin.event.UpdateEvent
import kevin.module.IntegerValue
import kevin.module.Module
import kevin.module.ModuleCategory
import kevin.utils.MSTimer
import kevin.utils.TimeUtils
import net.minecraft.entity.EntityLivingBase

class KeepSprint : Module("KeepSprint","Keep sprint when you attack entity.",category = ModuleCategory.MOVEMENT) {
    private val maxDelay: IntegerValue = object : IntegerValue("MaxDelay", 200, 0, 500) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = minDelay.get()
            if (i > newValue) set(i)

            delay = TimeUtils.randomDelay(minDelay.get(), this.get())
        }
    }

    private val minDelay: IntegerValue = object : IntegerValue("MinDelay", 50, 0, 500) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = maxDelay.get()
            if (i < newValue) set(i)

            delay = TimeUtils.randomDelay(this.get(), maxDelay.get())
        }
    }

    var delay = 0L
    var stopSprint = false
    val stopTimer = MSTimer()
    private var isHit = false
    private val attackTimer = MSTimer()

    override fun onEnable() {
        isHit = false
    }

    @EventTarget
    fun onAttack(event: AttackEvent) {
        if (event.targetEntity is EntityLivingBase && !isHit) {
            isHit = true
            attackTimer.reset()
            delay = TimeUtils.randomDelay(minDelay.get(), maxDelay.get())
        }
    }
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (isHit && attackTimer.hasTimePassed(delay/2)) {
            isHit = false
            mc.thePlayer.isSprinting = false
            stopSprint = true
            stopTimer.reset()
        }
    }
}