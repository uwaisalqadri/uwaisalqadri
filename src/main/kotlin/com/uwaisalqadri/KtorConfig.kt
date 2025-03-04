package com.uwaisalqadri

import com.prof18.rssparser.RssParser
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import io.ktor.serialization.kotlinx.xml.xml
import kotlinx.serialization.json.Json

fun createJson() = Json {
    isLenient = true
    ignoreUnknownKeys = true
    useAlternativeNames = false
}

fun createRssParser() = RssParser()

fun createHttpClient(json: Json) = HttpClient(OkHttp) {
    install(ContentNegotiation) {
        json(json = json)
        xml()
    }

    install(HttpTimeout) {
        this.requestTimeoutMillis = 60000
        this.connectTimeoutMillis = 60000
        this.socketTimeoutMillis = 60000
    }

    install(Logging) {
        logger = Logger.DEFAULT
        level = LogLevel.ALL
    }
}