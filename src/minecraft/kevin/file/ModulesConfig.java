package kevin.file;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kevin.main.KevinClient;
import kevin.module.Module;
import kevin.module.Value;
import kotlin.Pair;

import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class ModulesConfig extends FileConfig {

    public ModulesConfig(final File file) {
        super(file);
    }

    @Override
    protected void loadConfig() throws IOException {
        final JsonElement jsonElement = new JsonParser().parse(new BufferedReader(new FileReader(getFile())));

        if(jsonElement instanceof JsonNull)
            return;

        for (Map.Entry<String, JsonElement> entry : jsonElement.getAsJsonObject().entrySet()) {
            final Module module = KevinClient.moduleManager.getModuleByName(entry.getKey());

            if (module != null) {
                final JsonObject jsonModule = (JsonObject) entry.getValue();

                module.setState(jsonModule.get("State").getAsBoolean());
                module.setKeyBind(jsonModule.get("KeyBind").getAsInt());
                if (jsonModule.get("Hide")!=null) module.setArray(!jsonModule.get("Hide").getAsBoolean());
                if (jsonModule.get("AutoDisable")!=null) module.setAutoDisable(new Pair<>(!Objects.equals(jsonModule.get("AutoDisable").getAsString(), "Disable"), Objects.equals(jsonModule.get("AutoDisable").getAsString(), "Disable") ? "" : jsonModule.get("AutoDisable").getAsString()));
                for (final Value moduleValue : module.getValues()) {
                    final JsonElement element = jsonModule.get(moduleValue.getName());

                    if (element != null) moduleValue.fromJson(element);
                }
            }
        }
    }

    @Override
    protected void saveConfig() throws IOException {
        final JsonObject jsonObject = new JsonObject();

        for (final Module module : KevinClient.moduleManager.getModules()) {
            final JsonObject jsonMod = new JsonObject();
            jsonMod.addProperty("State", module.getState());
            jsonMod.addProperty("KeyBind", module.getKeyBind());
            jsonMod.addProperty("Hide", !module.getArray());
            jsonMod.addProperty("AutoDisable", module.getAutoDisable().getFirst() ? module.getAutoDisable().getSecond() : "Disable");
            module.getValues().forEach(value -> jsonMod.add(value.getName(), value.toJson()));
            jsonObject.add(module.getName(), jsonMod);
        }

        final PrintWriter printWriter = new PrintWriter(new FileWriter(getFile()));
        printWriter.println(FileManager.PRETTY_GSON.toJson(jsonObject));
        printWriter.close();
    }
}
