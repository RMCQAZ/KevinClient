package kevin.utils

import com.google.gson.JsonParser
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

object UserUtils {
    fun getUUID(username : String) : String {
        try {

            val httpConnection = URL("https://api.mojang.com/users/profiles/minecraft/$username").openConnection() as HttpsURLConnection
            httpConnection.connectTimeout = 2000
            httpConnection.readTimeout = 2000
            httpConnection.requestMethod = "GET"
            httpConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0")
            HttpURLConnection.setFollowRedirects(true)
            httpConnection.doOutput = true

            if(httpConnection.responseCode != 200)
                return ""


            InputStreamReader(httpConnection.inputStream).use {
                val jsonElement = JsonParser().parse(it)

                if(jsonElement.isJsonObject) {
                    return jsonElement.asJsonObject.get("id").asString
                }
            }
        } catch(ignored : Throwable) {
        }

        return ""
    }
}