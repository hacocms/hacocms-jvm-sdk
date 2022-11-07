package com.hacocms.sdk

enum class SortOrder { ASC, DESC }

/**
 * A class represents a single sort query for a given [field].
 * The sort order is controlled by [order], so DO NOT given the sorting prefix in the field name.
 * For example,
 * The wrong sort query:
 * ```kotlin
 * val q = SortQuery(field: "-updatedAt") // WRONG.
 * ```
 * Instead, passing the custom order.
 * The right way:
 * ```kotlin
 * val q = SortQuery(field: "updatedAt", order: SortOrder.DESC)
 * ```
 *
 * By default the sorting order is ASC. To custom, passing [order] value.
 *
 * There is also a convenient extensions to create sort query from string.
 * @see com.hacocms.sdk.SortQuery.Companion.sq
 */
data class SortQuery(
    val field: String,
    val order: SortOrder = SortOrder.ASC
) {
    fun desc(): SortQuery = copy(order = SortOrder.DESC)

    fun asc(): SortQuery = copy(order = SortOrder.ASC)

    fun build(): String = when (order) {
        SortOrder.ASC -> field
        SortOrder.DESC -> "-$field"
    }

    companion object {
        /**
         * A convenient extensions for create sort query from string.
         * This will automatically detect sort order by checking `-` prefix.
         * If the prefix is `-` then the order will be [SortOrder.DESC]. Otherwise, [SortOrder.ASC].
         */
        fun String.sq(): SortQuery {
            return if (startsWith("-")) {
                SortQuery(field = this.substring(1), order = SortOrder.DESC)
            } else {
                SortQuery(field = this, order = SortOrder.ASC)
            }

        }
    }
}


fun List<SortQuery>.build(): String {
    return map { it.build() }.filter { it.isNotEmpty() }.joinToString(",")
}
