package org.nehuatl.tachiwin.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.nehuatl.tachiwin.Constants
import org.nehuatl.tachiwin.models.Report
import org.nehuatl.tachiwin.v
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object ReportClient {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

    suspend fun issueReport(report: Report, scope: CoroutineScope) = suspendCoroutine { co ->
        scope.launch {
            val response: HttpResponse = client.post(Constants.REPORT_URL) {
                headers {
                    append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    append(HttpHeaders.Accept, "json/application")
                }
                setBody(report)
            }
            if (response.status.isSuccess()) {
                "success $response".v()
                co.resume(true)
            } else {
                "report failed $response".v()
                co.resume(false)
            }
        }
    }
}