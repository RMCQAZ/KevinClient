package kevin.via

import com.viaversion.viaversion.api.platform.ViaInjector
import com.viaversion.viaversion.libs.gson.JsonObject


class Injector : ViaInjector {
    override fun inject() {}
    override fun uninject() {}
    override fun getServerProtocolVersion(): Int {
        return ViaVersion.CLIENT_VERSION
    }
    override fun getEncoderName(): String {
        return CommonTransformer.HANDLER_ENCODER_NAME
    }
    override fun getDecoderName(): String {
        return CommonTransformer.HANDLER_DECODER_NAME
    }
    override fun getDump(): JsonObject {
        return JsonObject()
    }
}