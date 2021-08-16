package kevin.file;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kevin.main.Kevin;
import kevin.module.Module;
import kevin.module.Value;

import java.io.*;
import java.util.Iterator;
import java.util.Map;

public class ModulesConfig extends FileConfig {

    public ModulesConfig(final File file) {
        super(file);
    }

    @Override
    protected void loadConfig() throws IOException {
        final JsonElement jsonElement = new JsonParser().parse(new BufferedReader(new FileReader(getFile())));

        if(jsonElement instanceof JsonNull)
            return;

        final Iterator<Map.Entry<String, JsonElement>> entryIterator = jsonElement.getAsJsonObject().entrySet().iterator();
        while(entryIterator.hasNext()) {
            final Map.Entry<String, JsonElement> entry = entryIterator.next();
            final Module module = Kevin.getInstance.moduleManager.getModule(entry.getKey());

            if(module != null) {
                final JsonObject jsonModule = (JsonObject) entry.getValue();

                module.toggle(jsonModule.get("State").getAsBoolean());
                module.setKeyBind(jsonModule.get("KeyBind").getAsInt());
                for(final Value moduleValue : module.getValues()) {
                    final JsonElement element = jsonModule.get(moduleValue.getName());

                    if(element != null) moduleValue.fromJson(element);
                }
            }
        }
    }

    @Override
    protected void saveConfig() throws IOException {
        final JsonObject jsonObject = new JsonObject();

        for (final Module module : Kevin.getInstance.moduleManager.getModules()) {
            final JsonObject jsonMod = new JsonObject();
            jsonMod.addProperty("State", module.getToggle());
            jsonMod.addProperty("KeyBind", module.getKeyBind());
            module.getValues().forEach(value -> jsonMod.add(value.getName(), value.toJson()));
            jsonObject.add(module.getName(), jsonMod);
        }

        final PrintWriter printWriter = new PrintWriter(new FileWriter(getFile()));
        printWriter.println(FileManager.PRETTY_GSON.toJson(jsonObject));
        printWriter.close();
    }
}
