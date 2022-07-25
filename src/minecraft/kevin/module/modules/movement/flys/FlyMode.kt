package kevin.module.modules.movement.flys

import kevin.event.*
import kevin.main.KevinClient
import kevin.module.Value
import kevin.module.modules.movement.Fly
import kevin.utils.ClassUtils
import kevin.utils.MinecraftInstance

abstract class FlyMode(val modeName: String): MinecraftInstance() {
    protected val valuePrefix = "$modeName-"
    protected val fly by lazy { KevinClient.moduleManager.getModule("Fly") as Fly }
    open val values: List<Value<*>>
        get() = ClassUtils.getValues(this.javaClass,this)
    open fun onEnable() {}
    open fun onDisable() {}

    open fun onMotion(event: MotionEvent) {}
    open fun onRender3D(event: Render3DEvent) {}
    open fun onWorld(event: WorldEvent) {}
    open fun onBB(event: BlockBBEvent) {}
    open fun onStep(event: StepEvent) {}
    open fun onJump(event: JumpEvent) {}
    open fun onUpdate(event: UpdateEvent) {}
    open fun onPacket(event: PacketEvent) {}
    open fun onMove(event: MoveEvent) {}
    open val tagV: String? = null
}