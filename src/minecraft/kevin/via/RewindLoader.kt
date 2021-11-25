package kevin.via

import com.viaversion.viaversion.api.Via
import de.gerrygames.viarewind.api.ViaRewindConfigImpl
import de.gerrygames.viarewind.api.ViaRewindPlatform
import java.io.File
import java.util.logging.Logger

class RewindLoader(file: File) : ViaRewindPlatform {
    override fun getLogger(): Logger {
        return Via.getPlatform().logger
    }
    init {
        val conf = ViaRewindConfigImpl(file.toPath().resolve("ViaRewind").resolve("config.yml").toFile())
        conf.reloadConfig()
        init(conf)
    }
}