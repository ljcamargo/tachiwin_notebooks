package org.nehuatl.tachiwin.models

import java.io.File

@kotlinx.serialization.Serializable
data class ModelFile(
    val name: String,
    val source: String,
    val checksum: String,
    val size: Long,
    val version: String,
) {
    companion object {
        fun from(file: File, checksum: String? = null, version: String? = null) = ModelFile(
            name = file.name,
            source = file.path,
            checksum = checksum ?: "",
            size = file.length(),
            version = version ?: "",
        )
    }
}

