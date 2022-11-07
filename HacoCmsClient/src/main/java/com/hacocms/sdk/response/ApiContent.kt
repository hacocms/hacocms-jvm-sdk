package com.hacocms.sdk.response

import java.time.LocalDateTime

interface ApiContent {
    val id: String
    val createdAt: LocalDateTime
    val updatedAt: LocalDateTime
    val publishedAt: LocalDateTime?
    val closedAt: LocalDateTime?
}
