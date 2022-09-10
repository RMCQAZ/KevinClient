package kevin.font

import com.google.gson.*
import kevin.main.KevinClient
import kevin.utils.MinecraftInstance
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import java.awt.Font
import java.io.*

class FontManager : MinecraftInstance(){
    private val CUSTOM_FONT_RENDERERS: HashMap<FontInfo, GameFontRenderer> = HashMap()

    @FontDetails(fontName = "Minecraft Font")
    val minecraftFont: FontRenderer = mc.fontRendererObj

    @FontDetails(fontName = "JetBrainsMono Medium", fontSize = 35)
    lateinit var font35: GameFontRenderer

    @FontDetails(fontName = "JetBrainsMono Medium", fontSize = 40)
    lateinit var font40: GameFontRenderer

    @FontDetails(fontName = "JetBrainsMono Bold", fontSize = 180)
    lateinit var fontBold180: GameFontRenderer

    @FontDetails(fontName = "Misans", fontSize = 32)
    lateinit var fontMisans32: GameFontRenderer

    @FontDetails(fontName = "UniFont-14.0.04", fontSize = 32)
    lateinit var fontUniFont32: GameFontRenderer

    fun loadFonts(){
        font35 = GameFontRenderer(getFont("JetBrainsMono-Medium.ttf",35))
        font40 = GameFontRenderer(getFont("JetBrainsMono-Medium.ttf",40))
        fontBold180 = GameFontRenderer(getFont("JetBrainsMono-Bold.ttf",180))
        fontMisans32 = GameFontRenderer(getFont("misans.ttf",32))
        fontUniFont32  = GameFontRenderer(getFont("unifont-14.0.04.ttf",32))
        loadCustomFonts()
    }

    fun reloadFonts() {
        loadCustomFonts()
    }

    private fun loadCustomFonts() {
        val l = System.currentTimeMillis()
        try {
            CUSTOM_FONT_RENDERERS.clear()
            val fontsFile = File(KevinClient.fileManager.fontsDir, "fonts.json")
            if (fontsFile.exists()) {
                val jsonElement = JsonParser().parse(BufferedReader(FileReader(fontsFile)))
                if (jsonElement is JsonNull) return
                val jsonArray = jsonElement as JsonArray
                for (element in jsonArray) {
                    if (element is JsonNull) return
                    val fontObject = element as JsonObject
                    val font = this.getCustomFont(
                        fontObject["fontFile"].asString,
                        fontObject["fontSize"].asInt
                    )
                    CUSTOM_FONT_RENDERERS[FontInfo(font)] = GameFontRenderer(font)
                }
            } else {
                fontsFile.createNewFile()
                val printWriter = PrintWriter(FileWriter(fontsFile))
                printWriter.println(GsonBuilder().setPrettyPrinting().create().toJson(JsonArray()))
                printWriter.close()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        Minecraft.logger.info("[FontManager] Loaded ${CUSTOM_FONT_RENDERERS.size} font(s)," + (System.currentTimeMillis() - l) + "ms.")
    }

    fun getFontRenderer(name: String?, size: Int): FontRenderer {
        for (field in this::class.java.declaredFields) {
            try {
                field.isAccessible = true
                val o = field[this]
                if (o is FontRenderer) {
                    val fontDetails = field.getAnnotation(FontDetails::class.java)
                    if (fontDetails.fontName == name && fontDetails.fontSize == size) return o
                }
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
        return CUSTOM_FONT_RENDERERS.getOrDefault(
            FontInfo(
                name,
                size
            ), font40
        )
    }

    fun getFontDetails(fontRenderer: FontRenderer): FontInfo? {
        for (field in this::class.java.declaredFields) {
            try {
                field.isAccessible = true
                val o = field[this]
                if (o == fontRenderer) {
                    val fontDetails = field.getAnnotation(FontDetails::class.java)
                    return FontInfo(
                        fontDetails.fontName,
                        fontDetails.fontSize
                    )
                }
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
        for ((key, value) in CUSTOM_FONT_RENDERERS.entries) {
            if (value === fontRenderer) return key
        }
        return null
    }

    fun getFonts(): List<FontRenderer> {
        val fonts: MutableList<FontRenderer> = ArrayList()
        for (fontField in this::class.java.declaredFields) {
            try {
                fontField.isAccessible = true
                val fontObj = fontField[this]
                if (fontObj is FontRenderer) fonts.add(fontObj)
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
        fonts.addAll(CUSTOM_FONT_RENDERERS.values)
        return fonts
    }

    fun getFont(fontName: String, size: Int): Font {
        return try {
            val inputStream = FontManager::class.java.getResourceAsStream("/kevin/font/fonts/$fontName")
            var awtClientFont = Font.createFont(Font.TRUETYPE_FONT, inputStream)
            awtClientFont = awtClientFont.deriveFont(Font.PLAIN, size.toFloat())
            inputStream.close()
            awtClientFont
        } catch (e: Exception) {
            e.printStackTrace()
            Font("default", Font.PLAIN, size)
        }
    }
    private fun getCustomFont(fontName: String, size: Int): Font {
        return try {
            val inputStream: InputStream = FileInputStream(File(KevinClient.fileManager.fontsDir, fontName))
            var awtClientFont = Font.createFont(Font.TRUETYPE_FONT, inputStream)
            awtClientFont = awtClientFont.deriveFont(Font.PLAIN, size.toFloat())
            inputStream.close()
            awtClientFont
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Font("default", Font.PLAIN, size)
        }
    }
}