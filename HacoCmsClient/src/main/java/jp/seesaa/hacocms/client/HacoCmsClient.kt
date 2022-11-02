package jp.seesaa.hacocms.client

import jp.seesaa.hacocms.client.response.ApiContent
import jp.seesaa.hacocms.client.response.ListApiResponse

interface HacoCmsClient {
    suspend fun <T : ApiContent> getList(
        endpoint: String,
        query: QueryParameters = QueryParameters.EMPTY,
        clazz: Class<T>
    ): ListApiResponse<T>

    suspend fun <T : ApiContent> getListIncludingDraft(
        endpoint: String,
        query: QueryParameters = QueryParameters.EMPTY,
        clazz: Class<T>
    ): ListApiResponse<T>

    suspend fun <T : ApiContent> getSingle(endpoint: String, clazz: Class<T>): T

    suspend fun <T : ApiContent> getContent(
        endpoint: String,
        id: String,
        draftToken: String? = null,
        clazz: Class<T>
    ): T

    companion object {
        fun create(
            baseUrl: String,
            accessToken: String,
            projectDraftToken: String? = null,
            converter: Converter = GsonConverter.createDefault()
        ): HacoCmsClient {
            return HacoCmsClientImpl(
                baseUrl = baseUrl,
                accessToken = accessToken,
                projectDraftToken = projectDraftToken,
                converter = converter
            )
        }
    }
}
