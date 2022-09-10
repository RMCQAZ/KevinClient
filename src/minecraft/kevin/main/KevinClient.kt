package kevin.main

import kevin.cape.CapeManager
import kevin.command.CommandManager
import kevin.command.bind.BindCommandManager
import kevin.event.ClientShutdownEvent
import kevin.event.EventManager
import kevin.file.ConfigManager
import kevin.file.FileManager
import kevin.file.ImageManager
import kevin.file.ResourceManager
import kevin.font.FontGC
import kevin.hud.HUD
import kevin.hud.HUD.Companion.createDefault
import kevin.module.ModuleManager
import kevin.module.modules.render.ClickGui.ClickGUI
import kevin.module.modules.render.ClickGui.NewClickGui
import kevin.module.modules.render.Renderer
import kevin.script.ScriptManager
import kevin.skin.SkinManager
import kevin.utils.CombatManager
import kevin.font.FontManager
import kevin.via.ViaVersion
import org.lwjgl.opengl.Display

object KevinClient {
    var name = "Kevin"
    var version = "b2.3"

    var isStarting = true

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
        Display.setTitle("$name $version | Minecraft 1.8.9")
        moduleManager = ModuleManager()
        fileManager = FileManager()
        fileManager.load()
        commandManager = CommandManager()
        eventManager = EventManager()
        fontManager = FontManager()
        fontManager.loadFonts()
        eventManager.registerListener(FontGC)
        Renderer.load()
        moduleManager.load()
        ScriptManager.load()
        fileManager.loadConfig(fileManager.modulesConfig)
        fileManager.loadConfig(fileManager.bindCommandConfig)
        eventManager.registerListener(BindCommandManager)
        hud = createDefault()
        fileManager.loadConfig(fileManager.hudConfig)
        commandManager.load()
        clickGUI = ClickGUI()
        newClickGui = NewClickGui()
        capeManager = CapeManager()
        capeManager.load()
        SkinManager.load()
        ImageManager.load()
        ResourceManager.init()
        ConfigManager.load()
        combatManager = CombatManager()
        ViaVersion.start()
        isStarting = false
    }

    fun stop() {
        eventManager.callEvent(ClientShutdownEvent())
        fileManager.saveAllConfigs()
        capeManager.save()
        SkinManager.save()
        ImageManager.save()
    }
}