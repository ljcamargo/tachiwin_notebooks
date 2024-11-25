package org.nehuatl.tachiwin.models

import kotlinx.datetime.Instant

@kotlinx.serialization.Serializable
data class Report(
    val date: Instant,
    val version: String,
    val comment: String,
)

