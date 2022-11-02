package jp.seesaa.hacocms.client.response

import com.google.gson.annotations.SerializedName

data class ListApiResponse<ApiSchema : ApiContent>(
    @SerializedName("meta") val meta: ApiMetaResponse,
    @SerializedName("data") val data: List<ApiSchema>
)
