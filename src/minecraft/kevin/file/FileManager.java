package kevin.file;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kevin.main.Kevin;
import kevin.utils.ChatUtils;
import kevin.utils.MinecraftInstance;

import java.io.File;
import java.lang.reflect.Field;

public class FileManager extends MinecraftInstance {
    public static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();

    public final File dir = new File(mc.mcDataDir, Kevin.getInstance.name);
    public final FileConfig modulesConfig = new ModulesConfig(new File(dir, "modules.json"));
    public final File fontsDir = new File(dir, "Fonts");
    public final File spammerDir = new File(dir,"SpammerMessages");

    public void load(){
        if (!dir.exists()) dir.mkdir();
        if (!fontsDir.exists()) fontsDir.mkdir();
        if (!spammerDir.exists()) spammerDir.mkdir();
        loadConfigs(modulesConfig);
    }
    public void saveConfig(final FileConfig config) {
        saveConfig(config, false);
    }
    private void saveConfig(final FileConfig config, final boolean ignoreStarting) {
        if (!ignoreStarting && Kevin.getInstance.fileManager == null)
            return;

        try {
            if(!config.hasConfig())
                config.createConfig();
            config.saveConfig();
        }catch(final Throwable t) {
            new ChatUtils().messageWithStart("§cSaveConfig Error: " + t);
        }
    }
    public void loadConfigs(final FileConfig... configs) {
        for(final FileConfig fileConfig : configs)
            loadConfig(fileConfig);
    }
    public void loadConfig(final FileConfig config) {
        if(!config.hasConfig()) {
            saveConfig(config, true);
            return;
        }

        try {
            config.loadConfig();
        }catch(final Throwable t) {
        }
    }
    public void saveAllConfigs() {
        for(final Field field : getClass().getDeclaredFields()) {
            if(field.getType() == FileConfig.class) {
                try {
                    if(!field.isAccessible())
                        field.setAccessible(true);

                    final FileConfig fileConfig = (FileConfig) field.get(this);
                    saveConfig(fileConfig);
                }catch(final IllegalAccessException e) {

                }
            }
        }
    }
}
