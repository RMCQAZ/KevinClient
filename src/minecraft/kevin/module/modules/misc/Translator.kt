package kevin.module.modules.misc

import com.google.gson.JsonParser
import kevin.event.EventTarget
import kevin.event.PacketEvent
import kevin.module.ListValue
import kevin.module.Module
import net.minecraft.network.play.server.S02PacketChat
import net.minecraft.util.ChatComponentText
import net.minecraft.util.IChatComponent
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

object Translator : Module("Translator","Translate chat messages from server.") {
    private val languageValue = ListValue("Language", arrayOf("Chinese", "English"), "Chinese")
    private val apiValue = ListValue("API", arrayOf("Google","YouDao"), "Google")
    private val client = HttpClients.createDefault()

    @EventTarget
    fun onPacket(event: PacketEvent){
        if(event.packet is S02PacketChat){
            doTranslate(event.packet.chatComponent)
            event.cancelEvent()
        }
    }

    private fun getLink(msg: String):String{
        val message=msg.replace(" ","%20")
        return when(apiValue.get().lowercase()){
            "google" -> "http://translate.google.cn/translate_a/single?client=gtx&dt=t&dj=1&ie=UTF-8&sl=auto&tl=" + (if (languageValue.equals("chinese")) "zh_cn" else "en_us") + "&q=$message"
            "youdao" -> "http://fanyi.youdao.com/translate?&doctype=json&type=AUTO&i=$message"
            else -> ""
        }
    }

    private fun getResult(data:String):String{
        when(apiValue.get().lowercase()){
            "google" -> {
                val json = JsonParser().parse(data).asJsonObject
                return json.get("sentences").asJsonArray.get(0).asJsonObject.get("trans").asString
            }
            "youdao" -> {
                val json = JsonParser().parse(data).asJsonObject
                return json.get("translateResult").asJsonArray.get(0).asJsonArray.get(0).asJsonObject.get("tgt").asString
            }
        }
        return "WRONG VALUE"
    }

    private fun doTranslate(msg:IChatComponent){
        Thread {
            var chatMessage = msg
            try {
                val request = HttpGet(getLink(msg.unformattedTextForChat))
                val response = client.execute(request)

                if (response.statusLine.statusCode != 200) {
                    throw IllegalStateException("resp code: "+response.statusLine.statusCode+" != 200")
                }
                chatMessage = ChatComponentText(getResult(EntityUtils.toString(response.entity)))
                chatMessage.chatStyle = msg.chatStyle
            }catch (e:Exception){
                e.printStackTrace()
            }finally {
                mc.ingameGUI.chatGUI.printChatMessage(chatMessage)
            }
        }.start()
    }
}