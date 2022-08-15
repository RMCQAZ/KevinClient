package kevin.file

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kevin.main.KevinClient
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.util.ResourceLocation
import org.apache.commons.io.IOUtils
import java.awt.image.BufferedImage
import java.io.*
import java.nio.charset.StandardCharsets
import javax.imageio.ImageIO

object ImageManager {
    var saveServerIcon = false
    private val jsonFile = File(KevinClient.fileManager.serverIconsDir,"config.json")
    //load some textures
    val resourceShadow1 = ResourceLocation("kevin/shadows/PanelBottom.png")
    val resourceShadow2 = ResourceLocation("kevin/shadows/PanelBottomLeft.png")
    val resourceShadow3 = ResourceLocation("kevin/shadows/PanelBottomRight.png")
    val resourceShadow4 = ResourceLocation("kevin/shadows/PanelLeft.png")
    val resourceShadow5 = ResourceLocation("kevin/shadows/PanelRight.png")
    val resourceShadow6 = ResourceLocation("kevin/shadows/PanelTop.png")
    val resourceShadow7 = ResourceLocation("kevin/shadows/PanelTopLeft.png")
    val resourceShadow8 = ResourceLocation("kevin/shadows/PanelTopRight.png")

    fun load(){
        val mc = Minecraft.getMinecraft()
        mc.addScheduledTask{
            mc.textureManager.loadTexture(resourceShadow1, DynamicTexture(ImageIO.read(javaClass.getResourceAsStream("/resources/shadows/PanelBottom.png"))))
            mc.textureManager.loadTexture(resourceShadow2, DynamicTexture(ImageIO.read(javaClass.getResourceAsStream("/resources/shadows/PanelBottomLeft.png"))))
            mc.textureManager.loadTexture(resourceShadow3, DynamicTexture(ImageIO.read(javaClass.getResourceAsStream("/resources/shadows/PanelBottomRight.png"))))
            mc.textureManager.loadTexture(resourceShadow4, DynamicTexture(ImageIO.read(javaClass.getResourceAsStream("/resources/shadows/PanelLeft.png"))))
            mc.textureManager.loadTexture(resourceShadow5, DynamicTexture(ImageIO.read(javaClass.getResourceAsStream("/resources/shadows/PanelRight.png"))))
            mc.textureManager.loadTexture(resourceShadow6, DynamicTexture(ImageIO.read(javaClass.getResourceAsStream("/resources/shadows/PanelTop.png"))))
            mc.textureManager.loadTexture(resourceShadow7, DynamicTexture(ImageIO.read(javaClass.getResourceAsStream("/resources/shadows/PanelTopLeft.png"))))
            mc.textureManager.loadTexture(resourceShadow8, DynamicTexture(ImageIO.read(javaClass.getResourceAsStream("/resources/shadows/PanelTopRight.png"))))
        }
        if (!jsonFile.exists()) return
        val json = JsonParser().parse(IOUtils.toString(FileInputStream(jsonFile),"utf-8")).asJsonObject
        if (json.has("state")){
            val state = json.get("state")
            saveServerIcon = state.asBoolean
        }
    }
    fun save(){
        val json = JsonObject()
        json.addProperty("state", saveServerIcon)
        val writer = BufferedWriter(OutputStreamWriter(FileOutputStream(jsonFile), StandardCharsets.UTF_8))
        writer.write(FileManager.PRETTY_GSON.toJson(json))
        writer.close()
    }
    fun saveIcon(icon: BufferedImage,serverIP: String){
        try {
            val files = KevinClient.fileManager.serverIconsDir.listFiles()
            val ip = if (serverIP.contains(":")) serverIP.replace(":","_") else serverIP
            val name = "${files?.size} IP-$ip "
            val file = File(KevinClient.fileManager.serverIconsDir,"$name.png")
            var save = true
            if (!files.isNullOrEmpty()) for (f in files){
                if (!f.name.endsWith(".png")) continue
                val image = ImageIO.read(f)
                var same = true
                lx@ for (x in 0..63){
                    for (y in 0..63){
                        if (image.getRGB(x,y)!=icon.getRGB(x,y)) {
                            same = false
                            break@lx
                        }
                    }
                }
                if (same) {
                    save = false
                    break
                }
            }
            if (!file.exists()&&save){
                Minecraft.logger.info("Got the server icon.")
                ImageIO.write(icon,"png",file)
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }
}