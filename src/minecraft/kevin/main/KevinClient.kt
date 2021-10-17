package kevin.main

import kevin.cape.CapeManager
import kevin.command.CommandManager
import kevin.event.ClientShutdownEvent
import kevin.event.EventManager
import kevin.file.ConfigManager
import kevin.file.FileManager
import kevin.file.ImageManager
import kevin.hud.HUD
import kevin.hud.HUD.Companion.createDefault
import kevin.module.ModuleManager
import kevin.module.modules.render.ClickGui.ClickGUI
import kevin.module.modules.render.ClickGui.NewClickGui
import kevin.module.modules.render.Renderer
import kevin.script.ScriptManager
import kevin.skin.SkinManager
import kevin.utils.CombatManager
import kevin.utils.FontManager
import org.lwjgl.opengl.Display

object KevinClient {
    var name = "Kevin"
    var version = "b1.6"

    lateinit var moduleManager: ModuleManager
    lateinit var fileManager: FileManager
    lateinit var eventManager: EventManager
    lateinit var commandManager: CommandManager
    lateinit var fontManager: FontManager
    lateinit var clickGUI: ClickGUI
    lateinit var newClickGui: NewClickGui
    lateinit var hud: HUD
    lateinit var capeManager: CapeManager
    lateinit var combatManager: CombatManager

    var cStart = "§l§7[§l§9Kevin§l§7]"

    fun run() {
        moduleManager = ModuleManager()
        fileManager = FileManager()
        commandManager = CommandManager()
        eventManager = EventManager()
        fontManager = FontManager()
        fontManager.loadFonts()
        Display.setTitle("$name $version | Minecraft 1.8.9")
        fileManager.load()
        Renderer.load()
        moduleManager.load()
        ScriptManager.load()
        fileManager.loadConfig(fileManager.modulesConfig)
        hud = createDefault()
        fileManager.loadConfig(fileManager.hudConfig)
        commandManager.load()
        clickGUI = ClickGUI()
        newClickGui = NewClickGui()
        capeManager = CapeManager()
        capeManager.load()
        SkinManager.load()
        ImageManager.load()
        ConfigManager.load()
        combatManager = CombatManager()
    }

    fun stop() {
        eventManager.callEvent(ClientShutdownEvent())
        fileManager.saveAllConfigs()
        capeManager.save()
        SkinManager.save()
        ImageManager.save()
    }
}