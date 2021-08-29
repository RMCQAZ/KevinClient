package kevin.file;

import kevin.main.Kevin;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class HudConfig extends FileConfig {

    public HudConfig(final File file) {
        super(file);
    }

    @Override
    protected void loadConfig() throws IOException {
        Kevin.getInstance.hud.clearElements();
        Kevin.getInstance.hud = new Config(FileUtils.readFileToString(getFile())).toHUD();
    }

    @Override
    protected void saveConfig() throws IOException {
        final PrintWriter printWriter = new PrintWriter(new FileWriter(getFile()));
        printWriter.println(new Config(Kevin.getInstance.hud).toJson());
        printWriter.close();
    }
}
