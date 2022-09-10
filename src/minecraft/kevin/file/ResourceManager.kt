package kevin.file

import kevin.utils.MinecraftInstance
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.util.ResourceLocation
import javax.imageio.ImageIO

object ResourceManager : MinecraftInstance() {
    val resourceShadow1 = ResourceLocation("kevin/shadows/PanelBottom.png")
    val resourceShadow2 = ResourceLocation("kevin/shadows/PanelBottomLeft.png")
    val resourceShadow3 = ResourceLocation("kevin/shadows/PanelBottomRight.png")
    val resourceShadow4 = ResourceLocation("kevin/shadows/PanelLeft.png")
    val resourceShadow5 = ResourceLocation("kevin/shadows/PanelRight.png")
    val resourceShadow6 = ResourceLocation("kevin/shadows/PanelTop.png")
    val resourceShadow7 = ResourceLocation("kevin/shadows/PanelTopLeft.png")
    val resourceShadow8 = ResourceLocation("kevin/shadows/PanelTopRight.png")

    val resourceConnectedLeft = ResourceLocation("kevin/icons/ConnectedLeft.png")
    val resourceConnectedRight = ResourceLocation("kevin/icons/ConnectedRight.png")
    val resourceDisconnectedLeft = ResourceLocation("kevin/icons/DisconnectedLeft.png")
    val resourceDisconnectedRight = ResourceLocation("kevin/icons/DisconnectedRight.png")

    val resourceInfo= ResourceLocation("kevin/icons/Info.png")
    val resourceDone = ResourceLocation("kevin/icons/Done.png")
    val resourceWarn = ResourceLocation("kevin/icons/Warn.png")
    val resourceError = ResourceLocation("kevin/icons/Error.png")

    fun init() {
        mc.addScheduledTask{
            mc.textureManager.loadTexture(resourceShadow1, DynamicTexture(ImageIO.read(javaClass.getResourceAsStream("/resources/shadows/PanelBottom.png"))))
            mc.textureManager.loadTexture(resourceShadow2, DynamicTexture(ImageIO.read(javaClass.getResourceAsStream("/resources/shadows/PanelBottomLeft.png"))))
            mc.textureManager.loadTexture(resourceShadow3, DynamicTexture(ImageIO.read(javaClass.getResourceAsStream("/resources/shadows/PanelBottomRight.png"))))
            mc.textureManager.loadTexture(resourceShadow4, DynamicTexture(ImageIO.read(javaClass.getResourceAsStream("/resources/shadows/PanelLeft.png"))))
            mc.textureManager.loadTexture(resourceShadow5, DynamicTexture(ImageIO.read(javaClass.getResourceAsStream("/resources/shadows/PanelRight.png"))))
            mc.textureManager.loadTexture(resourceShadow6, DynamicTexture(ImageIO.read(javaClass.getResourceAsStream("/resources/shadows/PanelTop.png"))))
            mc.textureManager.loadTexture(resourceShadow7, DynamicTexture(ImageIO.read(javaClass.getResourceAsStream("/resources/shadows/PanelTopLeft.png"))))
            mc.textureManager.loadTexture(resourceShadow8, DynamicTexture(ImageIO.read(javaClass.getResourceAsStream("/resources/shadows/PanelTopRight.png"))))

            mc.textureManager.loadTexture(resourceConnectedLeft, DynamicTexture(ImageIO.read(javaClass.getResourceAsStream("/resources/icons/ConnectedLeft.png"))))
            mc.textureManager.loadTexture(resourceConnectedRight, DynamicTexture(ImageIO.read(javaClass.getResourceAsStream("/resources/icons/ConnectedRight.png"))))
            mc.textureManager.loadTexture(resourceDisconnectedLeft, DynamicTexture(ImageIO.read(javaClass.getResourceAsStream("/resources/icons/DisconnectedLeft.png"))))
            mc.textureManager.loadTexture(resourceDisconnectedRight, DynamicTexture(ImageIO.read(javaClass.getResourceAsStream("/resources/icons/DisconnectedRight.png"))))

            mc.textureManager.loadTexture(resourceInfo, DynamicTexture(ImageIO.read(javaClass.getResourceAsStream("/resources/icons/info.png"))))
            mc.textureManager.loadTexture(resourceDone, DynamicTexture(ImageIO.read(javaClass.getResourceAsStream("/resources/icons/done.png"))))
            mc.textureManager.loadTexture(resourceWarn, DynamicTexture(ImageIO.read(javaClass.getResourceAsStream("/resources/icons/warn.png"))))
            mc.textureManager.loadTexture(resourceError, DynamicTexture(ImageIO.read(javaClass.getResourceAsStream("/resources/icons/error.png"))))
        }
    }
}