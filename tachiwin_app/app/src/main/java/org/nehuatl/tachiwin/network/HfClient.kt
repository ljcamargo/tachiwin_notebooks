package org.nehuatl.tachiwin.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.head
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object HfClient {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

    suspend fun fetchMetadata(url: String, scope: CoroutineScope): Map<String, String?> = suspendCoroutine { co ->
        scope.launch {
            val response: HttpResponse = client.head(url) {
                headers {
                    append(HttpHeaders.Accept, "*/*")
                }
            }

            val metadata = mapOf(
                "Content-Length" to response.headers[HttpHeaders.ContentLength],
                "Date" to response.headers[HttpHeaders.Date],
                "Last-Modified" to response.headers[HttpHeaders.LastModified],
                "Commit" to response.headers["Commit"],
                "ETag" to response.headers[HttpHeaders.ETag]
            )

            co.resume(metadata)
        }
    }
}