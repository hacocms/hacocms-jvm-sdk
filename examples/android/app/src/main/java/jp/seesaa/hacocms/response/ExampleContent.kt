package jp.seesaa.hacocms.response

import com.google.gson.annotations.SerializedName
import com.hacocms.sdk.response.ApiContent
import java.time.LocalDateTime

data class ExampleContent(
    @SerializedName("id") override val id: String,
    @SerializedName("createdAt") override val createdAt: LocalDateTime,
    @SerializedName("updatedAt") override val updatedAt: LocalDateTime,
    @SerializedName("publishedAt") override val publishedAt: LocalDateTime?,
    @SerializedName("closedAt") override val closedAt: LocalDateTime?,
    @SerializedName("title") val title: String?,
    @SerializedName("body") val body: String?,
) : ApiContent
