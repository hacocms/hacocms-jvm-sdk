package jp.seesaa.hacocms.client

import jp.seesaa.hacocms.client.response.ApiContent
import jp.seesaa.hacocms.client.response.ListApiResponse
import okhttp3.ResponseBody

/**
 * Convert objects from the response body HTTP.
 */
interface Converter {
    fun <T : ApiContent> convertSingle(
        responseBody: ResponseBody,
        clazz: Class<T>
    ): T

    fun <T : ApiContent> convertList(
        responseBody: ResponseBody,
        clazz: Class<T>
    ): ListApiResponse<T>
}
