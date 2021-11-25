package kevin.via

import com.viaversion.viaversion.util.PipelineUtil
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import io.netty.handler.codec.MessageToByteEncoder
import io.netty.handler.codec.MessageToMessageDecoder
import java.lang.reflect.InvocationTargetException

object CommonTransformer {
    const val HANDLER_DECODER_NAME = "via-decoder"
    const val HANDLER_ENCODER_NAME = "via-encoder"
    @Throws(InvocationTargetException::class)
    fun decompress(ctx: ChannelHandlerContext, buf: ByteBuf) {
        val handler: ChannelHandler = ctx.pipeline().get("decompress")
        val decompressed: ByteBuf = if (handler is MessageToMessageDecoder<*>) PipelineUtil.callDecode(handler, ctx, buf)[0] as ByteBuf
        else PipelineUtil.callDecode(handler as ByteToMessageDecoder, ctx, buf)[0] as ByteBuf
        try {
            buf.clear().writeBytes(decompressed)
        } finally {
            decompressed.release()
        }
    }
    @Throws(Exception::class)
    fun compress(ctx: ChannelHandlerContext, buf: ByteBuf) {
        val compressed: ByteBuf = ctx.alloc().buffer()
        try {
            PipelineUtil.callEncode(ctx.pipeline().get("compress") as MessageToByteEncoder<*>, ctx, buf, compressed)
            buf.clear().writeBytes(compressed)
        } finally {
            compressed.release()
        }
    }
}