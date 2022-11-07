package com.hacocms.sdk

/**
 * A class represents all possibility query parameters to the Haco Cms.
 * For more detail see: [content api](https://hacocms.com/references/content-api)
 */
data class QueryParameters(
    val search: String? = null,
    val q: String? = null,
    val limit: Int? = null,
    val offset: Int? = null,
    val s: List<SortQuery>? = null,
    val status: Int? = null
) {
    fun toParameters(): Map<String, String> {
        return mapOf(
            "search" to search,
            "q" to q,
            "limit" to limit,
            "offset" to offset,
            "s" to s?.build(),
            "status" to status,
        )
            .filter { it.value != null }
            .map { it.key to it.value.toString() }
            .toMap()
    }

    companion object {
        val EMPTY = QueryParameters(
            search = null,
            q = null,
            limit = null,
            offset = null,
            s = null,
            status = null
        )
    }
}
