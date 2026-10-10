package pe.kuntur.tv

import android.content.Context
import org.json.JSONObject

data class Channel(
    val id: String,
    val name: String,
    val category: String,
    /** URLs directas de la señal (HLS .m3u8). Se prueban en orden si una falla. */
    val streamUrls: List<String>
)

object ChannelRepository {
    fun load(context: Context): List<Channel> {
        return try {
            val json = context.assets.open("channels.json").bufferedReader().use { it.readText() }
            val arr = JSONObject(json).getJSONArray("channels")
            List(arr.length()) { i ->
                val o = arr.getJSONObject(i)
                val urlsArr = o.optJSONArray("streamUrls")
                val urls = if (urlsArr == null) emptyList() else List(urlsArr.length()) { j -> urlsArr.getString(j) }
                Channel(
                    id = o.getString("id"),
                    name = o.getString("name"),
                    category = o.optString("category", "Nacional"),
                    streamUrls = urls.filter { it.isNotBlank() }
                )
            }.filter { it.streamUrls.isNotEmpty() }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
