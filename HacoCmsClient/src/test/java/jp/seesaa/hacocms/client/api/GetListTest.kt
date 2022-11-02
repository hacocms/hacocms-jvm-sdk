package jp.seesaa.hacocms.client.api

import jp.seesaa.hacocms.client.QueryParameters
import jp.seesaa.hacocms.client.SortQuery.Companion.sq
import jp.seesaa.hacocms.client.getList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.OffsetDateTime
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GetListTest : BaseHacoCmsClientTest() {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun get_list_success() = runTest {
        val dateStr = "2022-03-08T12:00:00.000+09:00"
        val expectedTime = OffsetDateTime.parse(dateStr).toLocalDateTime()
        val mockResponse = MockResponse().setBody(
            """
            {
              "meta": {
                "total": 1,
                "offset": 0,
                "limit": 10
              },
              "data": [
                {
                  "id": "abcdef",
                  "createdAt": "$dateStr",
                  "updatedAt": "$dateStr",
                  "publishedAt": "$dateStr",
                  "closedAt": null
                }
              ]
            }
        """.trimIndent()
        )

        server.enqueue(mockResponse)

        val hacoCmsClient = createHacoCmsClient()

        val response = hacoCmsClient.getList<DummyApiContent>(endpoint = DUMMY_ENDPOINT)

        assertEquals(1, response.meta.total)
        assertEquals(0, response.meta.offset)
        assertEquals(10, response.meta.limit)

        val gotData = response.data.first()
        assertEquals("abcdef", gotData.id)
        assertEquals(expectedTime, gotData.createdAt)
        assertEquals(expectedTime, gotData.updatedAt)
        assertEquals(expectedTime, gotData.publishedAt)
        assertNull(gotData.closedAt)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @ParameterizedTest
    @MethodSource("providerQueryParametersTestCases")
    fun query_parameters_are_appended_to_query_string(
        expect: Any?,
        query: QueryParameters,
        extractor: (HttpUrl) -> Any?
    ) = runTest {
        val hacoCmsClient = createHacoCmsClient()

        server.enqueue(MockResponse())
        runCatching {
            hacoCmsClient.getList<DummyApiContent>(
                endpoint = DUMMY_ENDPOINT,
                query = query
            )
        }
        val requestUrl = server.takeRequest().requestUrl!!
        assertEquals(expect, extractor(requestUrl))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun throw_an_error_if_api_returns_401() = runTest {
        val hacoCmsClient = createHacoCmsClient()

        server.enqueue(
            MockResponse()
                .setBody("Unauthorized")
                .setResponseCode(401)
        )

        val result =
            runCatching { hacoCmsClient.getList<DummyApiContent>(endpoint = DUMMY_ENDPOINT) }

        assertTrue(result.isFailure)
    }

    companion object {
        @JvmStatic
        fun providerQueryParametersTestCases(): Stream<Arguments> {
            class TestCase(
                val expect: Any?,
                val query: QueryParameters,
                val extractor: (HttpUrl) -> Any?
            )

            return listOf(
                TestCase(expect = 50, query = QueryParameters(limit = 50)) {
                    it.queryParameter("limit")?.toInt()
                },
                TestCase(expect = 100, query = QueryParameters(offset = 100)) {
                    it.queryParameter("offset")?.toInt()
                },
                TestCase(
                    expect = "createdAt",
                    query = QueryParameters(s = listOf("createdAt".sq()))
                ) { it.queryParameter("s") },
                TestCase(
                    expect = "-publishedAt,id",
                    query = QueryParameters(s = listOf("publishedAt".sq().desc(), "id".sq()))
                ) { it.queryParameter("s") },
            ).stream()
                .map { Arguments.of(it.expect, it.query, it.extractor) }
        }
    }
}
