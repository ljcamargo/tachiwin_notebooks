package org.nehuatl.tachiwin.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TextCompletion(
    val id: String,
    @SerialName("object")
    val objectType: String,
    val created: Long,
    val model: String,
    val choices: List<Choice>,
    val usage: Usage
) {
    fun text(): String? = choices.firstOrNull()?.text

    @Serializable
    data class Choice(
        val text: String,
        val index: Int,
        val logprobs: String? = null,
        @SerialName("finish_reason")
        val finishReason: String
    )

    @Serializable
    data class Usage(
        @SerialName("prompt_tokens")
        val promptTokens: Int,
        @SerialName("completion_tokens")
        val completionTokens: Int,
        @SerialName("total_tokens")
        val totalTokens: Int
    )
}
