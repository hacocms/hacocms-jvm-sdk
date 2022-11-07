package com.hacocms.sdk

import com.hacocms.sdk.response.ApiContent
import com.hacocms.sdk.response.ListApiResponse
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import ru.gildor.coroutines.okhttp.await

internal class HacoCmsClientImpl(
    baseUrl: String,
    accessToken: String,
    projectDraftToken: String?,
    private val converter: Converter
) : HacoCmsClient {
    @Suppress("JoinDeclarationAndAssignment")
    private val client: OkHttpClient
    private val clientDraft: OkHttpClient?
    private val baseHttpUrl: HttpUrl by lazy {
        baseUrl.toHttpUrl().newBuilder()
            .addPathSegments("api/v1")
            .build()
    }

    init {
        client = OkHttpClient.Builder()
            .addInterceptor(TokenInterceptor(accessToken = accessToken))
            .build()

        clientDraft = if (projectDraftToken != null) {
            val tokenInterceptor = TokenInterceptor(
                accessToken = accessToken, projectDraftToken = projectDraftToken
            )
            OkHttpClient.Builder()
                .addInterceptor(tokenInterceptor)
                .build()
        } else {
            null
        }
    }

    override suspend fun <T : ApiContent> getList(
        endpoint: String,
        query: QueryParameters,
        clazz: Class<T>
    ): ListApiResponse<T> {
        val url = baseHttpUrl.newBuilder()
            .addPathSegments(endpoint)
            .addQueryParameter(query)
            .build()

        val request = url.toRequest()

        val response = client.newCall(request).await()
        val body = requireNotNull(response.body)

        return converter.convertList(body, clazz)
    }

    override suspend fun <T : ApiContent> getListIncludingDraft(
        endpoint: String,
        query: QueryParameters,
        clazz: Class<T>
    ): ListApiResponse<T> {
        val url = baseHttpUrl.newBuilder()
            .addPathSegments(endpoint)
            .addQueryParameter(query)
            .build()

        val request = url.toRequest()

        val response = requireClientDraft().newCall(request).await()
        val body = requireNotNull(response.body)

        return converter.convertList(body, clazz)
    }

    override suspend fun <T : ApiContent> getSingle(endpoint: String, clazz: Class<T>): T {
        val client = clientDraft ?: this.client

        val url = baseHttpUrl.newBuilder()
            .addPathSegments(endpoint)
            .build()

        val request = url.toRequest()

        val response = client.newCall(request).await()
        val body = requireNotNull(response.body)

        return converter.convertSingle(body, clazz)
    }

    override suspend fun <T : ApiContent> getContent(
        endpoint: String,
        id: String,
        draftToken: String?,
        clazz: Class<T>
    ): T {
        val client = clientDraft ?: this.client

        val url = baseHttpUrl.newBuilder()
            .addPathSegments(endpoint)
            .addPathSegment(id)
            .apply { draftToken?.let { addQueryParameter("draft", it) } }
            .build()

        val request = url.toRequest()

        val response = client.newCall(request).await()

        val body = requireNotNull(response.body)

        return converter.convertSingle(body, clazz)
    }

    private fun HttpUrl.Builder.addQueryParameter(query: QueryParameters): HttpUrl.Builder {
        query.toParameters().forEach { (key, value) -> addQueryParameter(key, value) }
        return this
    }

    private fun requireClientDraft(): OkHttpClient {
        if (clientDraft == null) {
            throw IllegalStateException("Need Project-Draft-Token to get draft contents")
        }
        return clientDraft
    }

    private fun HttpUrl.toRequest(): Request = Request.Builder().url(this).build()
}

private class TokenInterceptor(
    private val accessToken: String,
    private val projectDraftToken: String? = null,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeaderIfNotNull("Haco-Project-Draft-Token", projectDraftToken)
            .build()
        return chain.proceed(newRequest)
    }

    private fun Request.Builder.addHeaderIfNotNull(name: String, value: String?): Request.Builder {
        if (value != null) {
            addHeader(name, value)
        }
        return this
    }
}
