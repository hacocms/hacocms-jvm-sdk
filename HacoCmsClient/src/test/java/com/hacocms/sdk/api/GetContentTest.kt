package com.hacocms.sdk.api

import com.hacocms.sdk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetContentTest : BaseHacoCmsClientTest() {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun get_content() = runTest {
        val contentId = "abcdef"
        val dateStr = "2022-03-08T12:00:00.000+09:00"
        val expectedTime = OffsetDateTime.parse(dateStr).toLocalDateTime()
        val mockResponse = MockResponse().setBody(
            """
            {
              "id": "$contentId",
              "createdAt": "$dateStr",
              "updatedAt": "$dateStr",
              "publishedAt": "$dateStr",
              "closedAt": null
            }
        """.trimIndent()
        )

        server.enqueue(mockResponse)

        val hacoCmsClient = createHacoCmsClient()

        val gotData =
            hacoCmsClient.getContent<DummyApiContent>(endpoint = DUMMY_ENDPOINT, id = contentId)
        assertEquals("abcdef", gotData.id)
        assertEquals(expectedTime, gotData.createdAt)
        assertEquals(expectedTime, gotData.updatedAt)
        assertEquals(expectedTime, gotData.publishedAt)
        assertNull(gotData.closedAt)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun get_content_with_draft_token() = runTest {
        val hacoCmsClient = createHacoCmsClient(isIncludeDraftToken = true)

        server.enqueue(MockResponse())
        runCatching {
            hacoCmsClient.getContent<DummyApiContent>(endpoint = DUMMY_ENDPOINT, id = "dummy")
        }

        val headers = server.takeRequest().headers
        assertEquals(DUMMY_PROJECT_DRAFT_TOKEN, headers["Haco-Project-Draft-Token"])
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun get_content_draft_token_is_appended_to_query_string() = runTest {
        val draftToken = "DUMMY_DRAFT_TOKEN"
        val hacoCmsClient = createHacoCmsClient(isIncludeDraftToken = true)

        server.enqueue(MockResponse())
        runCatching {
            hacoCmsClient.getContent<DummyApiContent>(
                endpoint = DUMMY_ENDPOINT,
                id = "dummy",
                draftToken = draftToken
            )
        }

        val requestUrl = server.takeRequest().requestUrl!!
        assertEquals(draftToken, requestUrl.queryParameter("draft"))
    }
}

