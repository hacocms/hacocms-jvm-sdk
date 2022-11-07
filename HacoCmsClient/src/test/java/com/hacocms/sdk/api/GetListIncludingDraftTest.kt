package com.hacocms.sdk.api

import com.hacocms.sdk.getListIncludingDraft
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetListIncludingDraftTest : BaseHacoCmsClientTest() {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun get_list_with_draft_token() = runTest {
        val hacoCmsClient = createHacoCmsClient(isIncludeDraftToken = true)

        server.enqueue(MockResponse())
        runCatching {
            hacoCmsClient.getListIncludingDraft<DummyApiContent>(endpoint = DUMMY_ENDPOINT)
        }

        val headers = server.takeRequest().headers
        assertEquals(DUMMY_PROJECT_DRAFT_TOKEN, headers["Haco-Project-Draft-Token"])
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun get_list_with_draft_token_throws_error_if_draft_token_is_not_given() = runTest {
        val hacoCmsClient = createHacoCmsClient(isIncludeDraftToken = false)

        server.enqueue(MockResponse())

        val response = runCatching {
            hacoCmsClient.getListIncludingDraft<DummyApiContent>(endpoint = DUMMY_ENDPOINT)
        }

        assertTrue { response.isFailure }
        assertTrue { response.exceptionOrNull()!!.message!!.contains("Project-Draft-Token") }
    }
}
