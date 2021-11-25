package kevin.via

import com.viaversion.viabackwards.api.ViaBackwardsPlatform
import java.io.File
import java.util.logging.Logger

class BackwardsLoader(var fileIn: File?) : ViaBackwardsPlatform {
    private val file = File(fileIn, "ViaBackwards")
    init {
        init(file)
    }
    override fun getLogger(): Logger {
        return ViaVersion.jLogger
    }
    override fun disable() {}
    override fun isOutdated() = false
    override fun getDataFolder() = File(file, "config.yml")
}