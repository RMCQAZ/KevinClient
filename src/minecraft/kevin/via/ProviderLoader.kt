package kevin.via

import com.viaversion.viaversion.api.Via
import com.viaversion.viaversion.api.connection.UserConnection
import com.viaversion.viaversion.api.platform.ViaPlatformLoader
import com.viaversion.viaversion.api.protocol.version.VersionProvider
import com.viaversion.viaversion.bungee.providers.BungeeMovementTransmitter
import com.viaversion.viaversion.protocols.base.BaseVersionProvider
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.MovementTransmitterProvider


class ProviderLoader : ViaPlatformLoader {
    override fun load() {
        Via.getManager().providers.use(MovementTransmitterProvider::class.java, BungeeMovementTransmitter())
        Via.getManager().providers.use(VersionProvider::class.java, object : BaseVersionProvider() {
            @Throws(Exception::class)
            override fun getClosestServerProtocol(connection: UserConnection): Int {
                return if (connection.isClientSide) ViaVersion.nowVersion
                else super.getClosestServerProtocol(connection)
            }
        })
    }
    override fun unload() {}
}
