package kevin.module.modules.player.nofalls

import kevin.event.*
import kevin.main.KevinClient
import kevin.module.Value
import kevin.module.modules.player.NoFall
import kevin.utils.ClassUtils
import kevin.utils.MinecraftInstance

abstract class NoFallMode(val modeName: String) : MinecraftInstance() {
    protected val valuePrefix = "$modeName-"
    protected val noFall by lazy { KevinClient.moduleManager.getModule(NoFall::class.java) }
    open val values: List<Value<*>>
        get() = ClassUtils.getValues(this.javaClass, this)
    open fun onEnable() {}
    open fun onDisable() {}

    open fun onUpdate(event: UpdateEvent) {}
    open fun onNoFall(event: UpdateEvent) {}
    open fun onMotion(event: MotionEvent) {}
    open fun onPacket(event: PacketEvent) {}
    open fun onMove(event: MoveEvent) {}
    open fun onBlockBB(event: BlockBBEvent) {}
    open fun onJump(event: JumpEvent) {}
    open fun onStep(event: StepEvent) {}
}