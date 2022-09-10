package kevin.module.modules.movement.speeds

import kevin.event.MoveEvent
import kevin.event.UpdateEvent
import kevin.main.KevinClient
import kevin.module.Value
import kevin.module.modules.movement.Speed
import kevin.utils.ClassUtils
import kevin.utils.MinecraftInstance

abstract class SpeedMode(val modeName: String): MinecraftInstance() {
    protected val valuePrefix = "$modeName-"
    protected val speed by lazy { KevinClient.moduleManager.getModule(Speed::class.java) }
    open val values: List<Value<*>>
        get() = ClassUtils.getValues(this.javaClass,this)
    open fun onEnable() {}
    open fun onDisable() {}

    open fun onMove(event: MoveEvent) {}
    open fun onUpdate(event: UpdateEvent) {}
    open fun onPreMotion() {}
}