package jp.seesaa.hacocms.client.api

import jp.seesaa.hacocms.client.getSingle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetSingleTest : BaseHacoCmsClientTest() {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun get_single_content() = runTest {
        val dateStr = "2022-03-08T12:00:00.000+09:00"
        val expectedTime = OffsetDateTime.parse(dateStr).toLocalDateTime()
        val mockResponse = MockResponse().setBody(
            """
            {
              "id": "abcdef",
              "createdAt": "$dateStr",
              "updatedAt": "$dateStr",
              "publishedAt": "$dateStr",
              "closedAt": null
            }
        """.trimIndent()
        )

        server.enqueue(mockResponse)

        val hacoCmsClient = createHacoCmsClient()

        val gotData = hacoCmsClient.getSingle<DummyApiContent>(endpoint = DUMMY_ENDPOINT)
        assertEquals("abcdef", gotData.id)
        assertEquals(expectedTime, gotData.createdAt)
        assertEquals(expectedTime, gotData.updatedAt)
        assertEquals(expectedTime, gotData.publishedAt)
        assertNull(gotData.closedAt)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun get_single_with_draft_token() = runTest {
        val hacoCmsClient = createHacoCmsClient(isIncludeDraftToken = true)

        server.enqueue(MockResponse())
        runCatching {
            hacoCmsClient.getSingle<DummyApiContent>(endpoint = DUMMY_ENDPOINT)
        }

        val headers = server.takeRequest().headers
        assertEquals(DUMMY_PROJECT_DRAFT_TOKEN, headers["Haco-Project-Draft-Token"])
    }

}
