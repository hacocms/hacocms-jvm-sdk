package jp.seesaa.hacocms.client

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import jp.seesaa.hacocms.client.response.ApiContent
import jp.seesaa.hacocms.client.response.ListApiResponse
import okhttp3.ResponseBody
import java.time.LocalDateTime
import java.time.OffsetDateTime


/**
 * An implementation of [Converter] by using gson.
 */
class GsonConverter(
    private val gson: Gson
) : Converter {
    override fun <T : ApiContent> convertSingle(
        responseBody: ResponseBody,
        clazz: Class<T>
    ): T {
        val json = responseBody.string()
        val token = TypeToken.get(clazz)
        return gson.fromJson(json, token.type)
    }

    override fun <T : ApiContent> convertList(
        responseBody: ResponseBody,
        clazz: Class<T>
    ): ListApiResponse<T> {
        val json = responseBody.string()
        val token = TypeToken.getParameterized(ListApiResponse::class.java, clazz)
        return gson.fromJson(json, token.type)
    }

    companion object {
        fun createDefault(): GsonConverter {
            val gson = GsonBuilder()
                .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter().nullSafe())
                .create()
            return GsonConverter(gson = gson)
        }
    }
}

class LocalDateTimeAdapter : TypeAdapter<LocalDateTime>() {
    override fun write(jsonWriter: JsonWriter, localDate: LocalDateTime) {
        jsonWriter.value(OffsetDateTime.from(localDate).toString())
    }

    override fun read(jsonReader: JsonReader): LocalDateTime {
        return OffsetDateTime.parse(jsonReader.nextString()).toLocalDateTime()
    }
}
