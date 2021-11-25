package kevin.script

import kevin.event.*
import kevin.module.Module
import kevin.module.ModuleCategory
import kevin.module.Value
import net.minecraft.client.Minecraft
import org.python.core.*

class ScriptModule(name: String, description: String, category: ModuleCategory, moduleObject: PyObject) : Module(name,description,category = category) {
    private val events = HashMap<String, PyObject>()
    private val _values = LinkedHashMap<String, Value<*>>()
    private var _tag: String? = null

    val settings by lazy { _values }

    init {
        moduleObject as PyDictionary
        if (moduleObject.has_key(PyString("settings"))) {
            val settings = moduleObject["settings"] as PyDictionary

            for (settingName in settings.keys)
                _values[settingName.toString()] = settings[settingName] as Value<*>
        }

        if (moduleObject.has_key(PyString("tag")))
            _tag = moduleObject["tag"] as String
    }

    override val values: List<Value<*>>
        get() {
            return _values.values.toList()
        }

    override var tag: String?
        get() = _tag
        set(value) {
            _tag = value
        }

    fun on(event: String,pyObject: PyObject){
        events[event] = pyObject
    }

    private fun callEvent(eventName: String, payload: Any? = null) {
        try {
            try {
                events[eventName]?.__call__(Py.java2py(payload),Py.java2py(this))
            } catch (e:Throwable){
                if (e.message!=null&&e.message!!.contains("takes exactly 1 argument (2 given)")) events[eventName]?.__call__(Py.java2py(payload))
                else if (e.message!=null&&e.message!!.contains("takes no arguments (2 given)")) events[eventName]?.__call__()
                else throw e
            }
        } catch (throwable: Throwable) {
            Minecraft.logger.error("[ScriptAPI] Exception in module '${name}'!", throwable)
        }
    }

    override fun onEnable() = callEvent("enable")

    override fun onDisable() = callEvent("disable")

    @EventTarget fun onUpdate(updateEvent: UpdateEvent) = callEvent("update")

    @EventTarget fun onMotion(motionEvent: MotionEvent) = callEvent("motion", motionEvent)

    @EventTarget fun onRender2D(render2DEvent: Render2DEvent) = callEvent("render2D", render2DEvent)

    @EventTarget fun onRender3D(render3DEvent: Render3DEvent) = callEvent("render3D", render3DEvent)

    @EventTarget fun onPacket(packetEvent: PacketEvent) = callEvent("packet", packetEvent)

    @EventTarget fun onJump(jumpEvent: JumpEvent) = callEvent("jump", jumpEvent)

    @EventTarget fun onAttack(attackEvent: AttackEvent) = callEvent("attack", attackEvent)

    @EventTarget fun onKey(keyEvent: KeyEvent) = callEvent("key", keyEvent)

    @EventTarget fun onMove(moveEvent: MoveEvent) = callEvent("move", moveEvent)

    @EventTarget fun onStep(stepEvent: StepEvent) = callEvent("step", stepEvent)

    @EventTarget fun onStepConfirm(stepConfirmEvent: StepConfirmEvent) = callEvent("stepConfirm")

    @EventTarget fun onWorld(worldEvent: WorldEvent) = callEvent("world", worldEvent)

    @EventTarget fun onClickBlock(clickBlockEvent: ClickBlockEvent) = callEvent("clickBlock", clickBlockEvent)

    @EventTarget fun onStrafe(strafeEvent: StrafeEvent) = callEvent("strafe", strafeEvent)

    @EventTarget fun onSlowDown(slowDownEvent: SlowDownEvent) = callEvent("slowDown", slowDownEvent)

    @EventTarget fun onShutdown(shutdownEvent: ClientShutdownEvent) = callEvent("shutdown")

    @EventTarget fun onEntityKilled(entityKilledEvent: EntityKilledEvent) = callEvent("entityKilled",entityKilledEvent)
}