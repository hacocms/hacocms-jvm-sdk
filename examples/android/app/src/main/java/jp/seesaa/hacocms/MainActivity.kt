package jp.seesaa.hacocms

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.hacocms.sdk.*
import com.hacocms.sdk.SortQuery.Companion.sq
import jp.seesaa.hacocms.response.ExampleContent
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val showCases = listOf(
        Showcase(name = "Gets list content.") { client ->
            client.getList<ExampleContent>(endpoint = "/example")
        },
        Showcase(name = "Gets list content with custom query.") { client ->
            client.getList<ExampleContent>(
                endpoint = "/example",
                query = QueryParameters(limit = LIMIT, offset = OFFSET)
            )
        },
        Showcase(name = "Gets content by content id.") { client ->
            client.getContent<ExampleContent>(endpoint = "/example", id = "CONTENT_ID")
        },
        Showcase(name = "Gets single object.") { client ->
            client.getSingle<ExampleContent>(endpoint = "/single")
        },
        Showcase(name = "Gets including draft.") { client ->
            client.getListIncludingDraft<ExampleContent>(
                endpoint = "/example",
                query = QueryParameters(search = "abc", s = listOf("createdAt".sq().desc())),
            )
        },
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val client = HacoCmsClient.create(
            baseUrl = BASE_URL,
            accessToken = ACCESS_TOKEN,
            projectDraftToken = PROJECT_DRAFT_TOKEN
        )

        lifecycleScope.launch {
            for (showcase in showCases) {
                val result = runCatching { showcase.action(client) }
                println("=============================")
                println("Show case: ${showcase.name}")
                println(result)
                println("=============================")
            }
        }
    }

    data class Showcase(
        val name: String,
        val action: suspend (client: HacoCmsClient) -> Any
    )

    companion object {
        private const val BASE_URL = "PROJECT_SUBDOMAIN"
        private const val ACCESS_TOKEN = "ACCESS_TOKEN"
        private const val PROJECT_DRAFT_TOKEN = "PROJECT_DRAFT_TOKEN"
        private const val LIMIT = 100
        private const val OFFSET = 10
    }

}
