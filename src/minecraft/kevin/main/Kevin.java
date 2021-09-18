package kevin.main;

import kevin.cape.CapeManager;
import kevin.command.CommandManager;
import kevin.event.ClientShutdownEvent;
import kevin.event.EventManager;
import kevin.file.ConfigManager;
import kevin.file.FileManager;
import kevin.file.ImageManager;
import kevin.module.ModuleManager;
import kevin.module.modules.render.ClickGui;
import kevin.hud.HUD;
import kevin.skin.SkinManager;
import kevin.utils.FontManager;
import org.lwjgl.opengl.Display;

public enum Kevin {

    getInstance();

    public String name = "Kevin";
    public String version = "b1.2";

    public ModuleManager moduleManager;
    public FileManager fileManager;
    public EventManager eventManager;
    public CommandManager commandManager;
    public FontManager fontManager;
    public ClickGui.ClickGUI clickGUI;
    public ClickGui.NewClickGui newClickGui;
    public HUD hud;
    public CapeManager capeManager;

    public String cStart = "§l§7[§l§9Kevin§l§7]";

    public void run() {
        moduleManager = new ModuleManager();
        fileManager = new FileManager();
        commandManager = new CommandManager();
        eventManager = new EventManager();
        fontManager = new FontManager();
        fontManager.loadFonts();
        Display.setTitle(name + " " +version +" | Minecraft 1.8.9");
        moduleManager.load();
        fileManager.loadConfig(fileManager.modulesConfig);
        hud = HUD.createDefault();
        fileManager.loadConfig(fileManager.hudConfig);
        fileManager.load();
        commandManager.load();
        clickGUI = new ClickGui.ClickGUI();
        newClickGui = new ClickGui.NewClickGui();
        capeManager = new CapeManager();
        capeManager.load();
        SkinManager.INSTANCE.load();
        ImageManager.INSTANCE.load();
        ConfigManager.INSTANCE.load();
    }

    public void stop() {
        this.eventManager.callEvent(new ClientShutdownEvent());
        fileManager.saveAllConfigs();
        capeManager.save();
        SkinManager.INSTANCE.save();
        ImageManager.INSTANCE.save();
    }
}
