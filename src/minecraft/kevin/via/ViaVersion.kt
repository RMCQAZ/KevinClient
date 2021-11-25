package kevin.via

import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.viaversion.viaversion.ViaManagerImpl
import com.viaversion.viaversion.api.Via
import com.viaversion.viaversion.api.data.MappingDataLoader
import io.netty.channel.EventLoop
import io.netty.channel.local.LocalEventLoopGroup
import kevin.main.KevinClient
import org.apache.logging.log4j.LogManager
import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.logging.Logger

object ViaVersion {
    @JvmStatic
    val CLIENT_VERSION = 47
    var nowVersion = CLIENT_VERSION
    val jLogger: Logger = JLoggerToLog4j(LogManager.getLogger("Via"))
    val initFuture = CompletableFuture<Void>()
    var asyncExecutor: ExecutorService? = null
    var eventLoop: EventLoop? = null
    val versions: Array<ProtocolCollection>
    init {
        val value = ProtocolCollection.values()
        value.sortBy { it.protocolVersion.version }
        versions = value
    }

    fun start() {
        val factory = ThreadFactoryBuilder().setDaemon(true).setNameFormat("Via-%d").build()
        asyncExecutor = Executors.newFixedThreadPool(8, factory)
        eventLoop = LocalEventLoopGroup(1, factory).next()
        eventLoop!!.submit(Callable { initFuture.join() })
        Via.init(
            ViaManagerImpl.builder()
                .injector(Injector())
                .loader(ProviderLoader())
                .platform(Platform(KevinClient.fileManager.via))
                .build()
        )
        MappingDataLoader.enableMappingsCache()
        (Via.getManager() as ViaManagerImpl).init()
        BackwardsLoader(KevinClient.fileManager.via)
        RewindLoader(KevinClient.fileManager.via)
        initFuture.complete(null)
    }
}