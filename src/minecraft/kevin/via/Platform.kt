package kevin.via

import com.viaversion.viaversion.api.command.ViaCommandSender
import com.viaversion.viaversion.api.platform.PlatformTask
import com.viaversion.viaversion.api.platform.ViaPlatform
import com.viaversion.viaversion.libs.gson.JsonObject
import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import io.netty.util.concurrent.Future
import io.netty.util.concurrent.GenericFutureListener
import org.apache.logging.log4j.LogManager
import java.io.File
import java.util.*
import java.util.concurrent.CancellationException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

class Platform(dataFolder: File): ViaPlatform<UUID> {
    private val logger = JLoggerToLog4j(LogManager.getLogger("ViaVersion"))
    private var config: ViaConfig
    private val dataFolder: File
    init {
        val configDir = dataFolder.toPath().resolve("ViaVersion")
        config = ViaConfig(configDir.resolve("viaversion.yml").toFile())
        this.dataFolder = configDir.toFile()
    }
    private var api = ViaAPI()
    fun legacyToJson(legacy: String?) =
        GsonComponentSerializer.gson().serialize(LegacyComponentSerializer.legacySection().deserialize(legacy!!))
    override fun getLogger() = logger
    override fun getPlatformName() = "KevinClient"
    override fun getPlatformVersion() = ViaVersion.CLIENT_VERSION.toString()
    override fun getPluginVersion() = "4.0.0"
    override fun runAsync(runnable: Runnable?): FutureTaskId {
        return FutureTaskId(CompletableFuture
            .runAsync(runnable, ViaVersion.asyncExecutor)
            .exceptionally { throwable: Throwable ->
                if (throwable !is CancellationException) {
                    throwable.printStackTrace()
                }
                null
            }
        )
    }
    override fun runSync(runnable: Runnable): FutureTaskId {
        return FutureTaskId(
            ViaVersion.eventLoop!!.submit(runnable).addListener(errorLogger<Future<Any>>())
        )
    }
    override fun runSync(runnable: Runnable, ticks: Long): PlatformTask<*> {
        return FutureTaskId(
            ViaVersion.eventLoop!!.schedule(
                { runSync(runnable) }, ticks *
                        50, TimeUnit.MILLISECONDS
            ).addListener(errorLogger<Future<Any>>())
        )
    }
    override fun runRepeatingSync(runnable: Runnable, ticks: Long): PlatformTask<*> {
        return FutureTaskId(
            ViaVersion.eventLoop!!.scheduleAtFixedRate(
                { runSync(runnable) },
                0, ticks * 50, TimeUnit.MILLISECONDS
            ).addListener(errorLogger<Future<Any>>())
        )
    }
    private fun <T : Future<*>?> errorLogger(): GenericFutureListener<T> {
        return GenericFutureListener<T> { future ->
            if (!future!!.isCancelled && future.cause() != null) {
                future.cause().printStackTrace()
            }
        }
    }
    override fun getOnlinePlayers(): Array<ViaCommandSender?> = arrayOfNulls(1145)
    private fun getServerPlayers(): Array<ViaCommandSender?> = arrayOfNulls(1145)
    override fun sendMessage(uuid: UUID?, s: String?) {}
    override fun kickPlayer(uuid: UUID?, s: String?) = false
    override fun isPluginEnabled() = true
    override fun getApi() = api
    override fun getConf() = config
    override fun getConfigurationProvider() = config
    override fun getDataFolder() = dataFolder
    override fun onReload() {}
    override fun getDump() = JsonObject()
    override fun isOldClientsAllowed() = true
}