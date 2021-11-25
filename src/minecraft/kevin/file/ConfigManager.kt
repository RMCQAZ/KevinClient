package kevin.file

import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kevin.main.KevinClient
import kevin.module.Value
import org.apache.commons.io.FileUtils
import java.io.*
import java.util.function.Consumer

object ConfigManager {
    val configList = arrayListOf<File>()
    private val configDir = KevinClient.fileManager.configsDir
    fun load(){
        configList.clear()
        val configs = configDir.listFiles()
        if (configs.isNullOrEmpty()) return
        for (c in configs){
            if (!c.isFile||!c.name.endsWith(".json")||c.name.endsWith("-HUD.json")) continue
            configList.add(c)
        }
    }
    fun saveConfig(name: String){
        val file = File(configDir,"$name.json")
        val hudFile = File(configDir,"$name-HUD.json")
        if (!file.exists()||!file.isFile) file.createNewFile()
        if (!hudFile.exists()||!hudFile.isFile) hudFile.createNewFile()
        //Modules
        val modulesConfig = JsonObject()
        KevinClient.moduleManager.getModules().forEach {
            val jsonMod = JsonObject()
            jsonMod.addProperty("State", it.state)
            jsonMod.addProperty("KeyBind", it.keyBind)
            it.values.forEach(Consumer { value: Value<*> -> jsonMod.add(value.name, value.toJson())})
            modulesConfig.add(it.name, jsonMod)
        }
        val printWriter = PrintWriter(FileWriter(file))
        printWriter.println(FileManager.PRETTY_GSON.toJson(modulesConfig))
        printWriter.close()
        //HUD
        val hudPrintWriter = PrintWriter(FileWriter(hudFile))
        hudPrintWriter.println(Config(KevinClient.hud).toJson())
        hudPrintWriter.close()
        //Add to config list
        configList.add(file)
    }
    fun loadConfig(name: String):Int{
        val config = configList.filter { it.name=="$name.json" }
        if (config.isEmpty()) return 3
        val modulesConfig = config[0]
        val hudConfig = File(configDir,"$name-HUD.json")
        var returnValue = 0
        //LoadModules
        if (modulesConfig.exists()&&modulesConfig.isFile){
            val jsonElement = JsonParser().parse(BufferedReader(FileReader(modulesConfig)))
            if (jsonElement !is JsonNull) {
                val entryIterator: Iterator<Map.Entry<String, JsonElement>> =
                    jsonElement.asJsonObject.entrySet().iterator()
                while (entryIterator.hasNext()) {
                    val (key, value) = entryIterator.next()
                    val module = KevinClient.moduleManager.getModule(key)
                    if (module != null) {
                        val jsonModule = value as JsonObject
                        module.state = jsonModule["State"].asBoolean
                        module.keyBind = jsonModule["KeyBind"].asInt
                        for (moduleValue in module.values) {
                            val element = jsonModule[moduleValue.name]
                            if (element != null) moduleValue.fromJson(element)
                        }
                    }
                }
            }
        } else returnValue = 1
        //LoadHUD
        if (hudConfig.exists()&&modulesConfig.isFile){
            KevinClient.hud.clearElements()
            KevinClient.hud = Config(FileUtils.readFileToString(hudConfig)).toHUD()
        } else if (returnValue==1) returnValue = 3 else returnValue = 2
        return returnValue
    }
}