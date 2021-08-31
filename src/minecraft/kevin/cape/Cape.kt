package kevin.cape

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.util.ResourceLocation
import java.awt.image.BufferedImage

class Cape(val name: String, val image: BufferedImage) {
    val resource = ResourceLocation("kevin/capes/${name.toLowerCase().replace(" ","_")}")
    init {
        val mc = Minecraft.getMinecraft()
        mc.addScheduledTask{mc.textureManager.loadTexture(resource, DynamicTexture(image))}
    }
}