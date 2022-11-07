package com.hacocms.sdk

import com.hacocms.sdk.response.ApiContent
import com.hacocms.sdk.response.ListApiResponse

suspend inline fun <reified T : ApiContent> HacoCmsClient.getList(
    endpoint: String, query: QueryParameters = QueryParameters.EMPTY
): ListApiResponse<T> = getList(endpoint = endpoint, query = query, clazz = T::class.java)

suspend inline fun <reified T : ApiContent> HacoCmsClient.getListIncludingDraft(
    endpoint: String, query: QueryParameters = QueryParameters.EMPTY
): ListApiResponse<T> =
    getListIncludingDraft(endpoint = endpoint, query = query, clazz = T::class.java)


suspend inline fun <reified T : ApiContent> HacoCmsClient.getSingle(
    endpoint: String
): T = getSingle(endpoint = endpoint, clazz = T::class.java)

suspend inline fun <reified T : ApiContent> HacoCmsClient.getContent(
    endpoint: String,
    id: String,
    draftToken: String? = null,
): T = getContent(endpoint = endpoint, id = id, draftToken = draftToken, clazz = T::class.java)
