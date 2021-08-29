package kevin.module

import kevin.event.Listenable
import kevin.hud.element.elements.Notification
import kevin.main.Kevin
import kevin.module.modules.render.HUD
import kevin.utils.ColorUtils.stripColor
import kevin.utils.MinecraftInstance
import net.minecraft.client.Minecraft
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.util.ResourceLocation
import org.lwjgl.input.Keyboard

open class Module(name: String, description: String = "", keyBind: Int = Keyboard.KEY_NONE, category: ModuleCategory = ModuleCategory.MISC) : MinecraftInstance(), Listenable {
    private var name: String? = null
    private var description: String? = null
    private var keyBind = 0
    private var Category: ModuleCategory? = null
    private var enable: Boolean? = null
    val hue = Math.random().toFloat()
    var slide = 0F
    fun getTagName(tagleft:String,tagright:String):String{
        return "$name${if (tag == null) "" else " §7$tagleft$tag$tagright"}"
    }
    fun getColorlessTagName(tagleft:String,tagright:String):String{
        return "$name${if (tag == null) "" else " $tagleft${stripColor(tag)}$tagright"}"
    }
    var slideStep = 0F
    var array = true
    open val tag: String?
        get() = null
    init {
        this.name = name
        this.description = description
        this.keyBind = keyBind
        Category = category
        enable = false
    }

    open fun getName(): String {
        return name!!
    }

    open fun getDescription(): String {
        return description!!
    }

    open fun getKeyBind(): Int {
        return keyBind
    }

    open fun setKeyBind(keyBind: Int) {
        this.keyBind = keyBind
        Kevin.getInstance.fileManager.saveConfig(Kevin.getInstance.fileManager.modulesConfig)
    }

    open fun getCategory(): ModuleCategory {
        return Category!!
    }

    open fun getToggle(): Boolean {
        return enable!!
    }

    open fun toggle() {
        enable = !enable!!
        val hud = Kevin.getInstance.hud
        if (enable!!) {
            Minecraft.getMinecraft().soundHandler.playSound(
                PositionedSoundRecord.create(
                    ResourceLocation("gui.button.press"),
                    1f
                )
            )
            hud.addNotification(Notification("Enabled $name"))
            Kevin.getInstance.fileManager.saveConfig(Kevin.getInstance.fileManager.modulesConfig)
            onEnable()
        } else {
            Minecraft.getMinecraft().soundHandler.playSound(
                PositionedSoundRecord.create(
                    ResourceLocation("gui.button.press"),
                    0.6114514191981f
                )
            )
            hud.addNotification(Notification("Disabled $name"))
            Kevin.getInstance.fileManager.saveConfig(Kevin.getInstance.fileManager.modulesConfig)
            onDisable()
        }
    }

    open fun toggle(state: Boolean) {
        val oldEnable = enable
        enable = state
        if (enable !== oldEnable) {
            if (enable!!) {
                Kevin.getInstance.fileManager.saveConfig(Kevin.getInstance.fileManager.modulesConfig)
                onEnable()
            } else {
                Kevin.getInstance.fileManager.saveConfig(Kevin.getInstance.fileManager.modulesConfig)
                onDisable()
            }
        }
    }

    open fun onEnable() {}

    open fun onDisable() {}

    override fun handleEvents(): Boolean {
        return enable!!
    }

    open fun getValue(valueName: String) = values.find { it.name.equals(valueName, ignoreCase = true) }

    open val values: List<Value<*>>
        get() = javaClass.declaredFields.map { valueField ->
            valueField.isAccessible = true
            valueField[this]
        }.filterIsInstance<Value<*>>().filter { it.isSupported }
}