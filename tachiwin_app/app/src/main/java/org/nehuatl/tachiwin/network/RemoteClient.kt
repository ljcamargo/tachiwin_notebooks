package org.nehuatl.tachiwin.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.Json
import org.nehuatl.tachiwin.Constants
import org.nehuatl.tachiwin.models.TextCompletion

class RemoteClient {

    private val ktorClient by lazy {
        HttpClient(CIO) {
            install(HttpTimeout) {
                requestTimeoutMillis = 90000
                connectTimeoutMillis = 15000
                socketTimeoutMillis = 60000
            }
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }
        }
    }

    suspend fun sendStream(prompt: String, state: MutableStateFlow<String>) {
        val channel: ByteReadChannel = ktorClient
            .post(Constants.TRANSLATE_REMOTE_ENDPOINT) {
                contentType(ContentType.Application.Json)
                setBody(mapOf(
                    "prompt" to prompt,
                    "api_key" to Constants.TRANSLATE_REMOTE_APIKEY,
                ))
            }.body()

        val buffer = ByteArray(1024)
        while (!channel.isClosedForRead) {
            val bytesRead = channel.readAvailable(buffer, 0, buffer.size)
            if (bytesRead < 0) break
            if (bytesRead > 0) {
                val content = buffer.copyOfRange(0, bytesRead).decodeToString()
                state.value += content
            }
        }
    }

    suspend fun send(prompt: String, state: MutableStateFlow<String>) {
        val response: String = ktorClient
            .post(Constants.TRANSLATE_REMOTE_ENDPOINT) {
                contentType(ContentType.Application.Json)
                setBody(mapOf(
                    "prompt" to prompt,
                    "api_key" to Constants.TRANSLATE_REMOTE_APIKEY,
                ))
            }.body()

        val textCompletion = Json.decodeFromString<TextCompletion>(response)
        state.value = textCompletion.text()!!
    }
}