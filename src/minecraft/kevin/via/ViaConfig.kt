package kevin.via

import com.viaversion.viaversion.configuration.AbstractViaConfig
import java.io.File

class ViaConfig(configFile: File?) : AbstractViaConfig(configFile) {
    override fun getDefaultConfigURL() = javaClass.classLoader.getResource("assets/viaversion/config.yml")!!
    override fun handleConfig(config: Map<String, Any>) {}
    override fun getUnsupportedOptions() = UNSUPPORTED
    override fun isAntiXRay() = false
    override fun isNMSPlayerTicking() = false
    override fun is1_12QuickMoveActionFix() = false
    override fun getBlockConnectionMethod() = "packet"
    override fun is1_9HitboxFix() = false
    override fun is1_14HitboxFix() = false
    companion object {
        private val UNSUPPORTED = listOf(
            "anti-xray-patch", "bungee-ping-interval",
            "bungee-ping-save", "bungee-servers", "quick-move-action-fix", "nms-player-ticking",
            "velocity-ping-interval", "velocity-ping-save", "velocity-servers",
            "blockconnection-method", "change-1_9-hitbox", "change-1_14-hitbox"
        )
    }
    init {
        reloadConfig()
    }
}