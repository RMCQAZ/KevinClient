package kevin.file

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kevin.hud.HUD
import kevin.hud.HUD.Companion.createDefault
import kevin.hud.HUD.Companion.elements
import kevin.hud.element.ElementInfo
import kevin.hud.element.Side

class Config {

    private var jsonArray = JsonArray()

    constructor(config: String) {
        jsonArray = Gson().fromJson(config, JsonArray::class.java)
    }

    constructor(hud: HUD) {
        for (element in hud.elements) {
            val elementObject = JsonObject()
            elementObject.addProperty("Type", element.name)
            elementObject.addProperty("X", element.x)
            elementObject.addProperty("Y", element.y)
            elementObject.addProperty("Scale", element.scale)
            elementObject.addProperty("HorizontalFacing", element.side.horizontal.sideName)
            elementObject.addProperty("VerticalFacing", element.side.vertical.sideName)

            for (value in element.values)
                elementObject.add(value.name, value.toJson())

            jsonArray.add(elementObject)
        }
    }

    fun toJson(): String = GsonBuilder().setPrettyPrinting().create().toJson(jsonArray)

    fun toHUD(): HUD {
        val hud = HUD()

        try {
            for (jsonObject in jsonArray) {
                try {
                    if (jsonObject !is JsonObject)
                        continue

                    if (!jsonObject.has("Type"))
                        continue

                    val type = jsonObject["Type"].asString

                    for (elementClass in elements) {
                        val classType = elementClass.getAnnotation(ElementInfo::class.java).name

                        if (classType == type) {
                            val element = elementClass.newInstance()

                            element.x = jsonObject["X"].asInt.toDouble()
                            element.y = jsonObject["Y"].asInt.toDouble()
                            element.scale = jsonObject["Scale"].asFloat
                            element.side = Side(
                                Side.Horizontal.getByName(jsonObject["HorizontalFacing"].asString)!!,
                                Side.Vertical.getByName(jsonObject["VerticalFacing"].asString)!!
                            )

                            for (value in element.values) {
                                if (jsonObject.has(value.name))
                                    value.fromJson(jsonObject[value.name])
                            }

                            hud.addElement(element)
                            break
                        }
                    }
                } catch (e: Exception) {
                    println("Error while loading custom hud element from config. $e")
                }
            }

            // Add forced elements when missing
            for (elementClass in elements) {
                if (elementClass.getAnnotation(ElementInfo::class.java).force
                    && hud.elements.none { it.javaClass == elementClass }) {
                    hud.addElement(elementClass.newInstance())
                }
            }
        } catch (e: Exception) {
            println("Error while loading custom hud config. $e")
            return createDefault()
        }

        return hud
    }
}