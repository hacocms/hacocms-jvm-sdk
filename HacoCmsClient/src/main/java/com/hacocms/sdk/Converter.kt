package com.hacocms.sdk

import com.hacocms.sdk.response.ApiContent
import com.hacocms.sdk.response.ListApiResponse
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
