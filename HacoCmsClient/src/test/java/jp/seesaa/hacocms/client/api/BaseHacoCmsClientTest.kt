package jp.seesaa.hacocms.client.api

import com.google.gson.annotations.SerializedName
import jp.seesaa.hacocms.client.HacoCmsClient
import jp.seesaa.hacocms.client.response.ApiContent
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import java.time.LocalDateTime

open class BaseHacoCmsClientTest {
    protected lateinit var server: MockWebServer
        private set

    @BeforeEach
    fun setUp() {
        server = MockWebServer()
        server.start()
    }

    @AfterEach
    fun tearDown() {
        server.shutdown()
    }

    protected fun createHacoCmsClient(
        isIncludeDraftToken: Boolean = false
    ): HacoCmsClient {
        val baseUrl = server.url("/").toString()

        return HacoCmsClient.create(
            baseUrl = baseUrl,
            accessToken = DUMMY_ACCESS_TOKEN,
            projectDraftToken = if (isIncludeDraftToken) DUMMY_PROJECT_DRAFT_TOKEN else null
        )
    }

    companion object {
        const val DUMMY_ACCESS_TOKEN = "DUMMY_ACCESS_TOKEN"
        const val DUMMY_PROJECT_DRAFT_TOKEN = "DUMMY_PROJECT_DRAFT_TOKEN"
        const val DUMMY_ENDPOINT = "/dummy"
    }
}

data class DummyApiContent(
    @SerializedName("id") override val id: String,
    @SerializedName("createdAt") override val createdAt: LocalDateTime,
    @SerializedName("updatedAt") override val updatedAt: LocalDateTime,
    @SerializedName("publishedAt") override val publishedAt: LocalDateTime?,
    @SerializedName("closedAt") override val closedAt: LocalDateTime?,
) : ApiContent
