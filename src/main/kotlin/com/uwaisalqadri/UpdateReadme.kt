package com.uwaisalqadri

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.file
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.ZoneId
import kotlin.system.exitProcess

class UpdateReadmeCommand: CliktCommand() {

    private val outputFile by option("-o", help = "The README.md file to write")
        .file()
        .required()

    override fun run() {
        val json = createJson()
        val ktorHttpClient = createHttpClient(json)

        val githubActivity = fetchGithubActivity(ktorHttpClient)
        // val blogActivity = fetchBlogActivity(ktorHttpClient) TODO try medium blogs

        val newReadMe = createReadMe(githubActivity)
        outputFile.writeText(newReadMe)

        // TODO why do I need to do this
        exitProcess(0)
    }
}

private fun fetchGithubActivity(
    client: HttpClient
): List<ActivityItem> {
    val githubApi = GithubApi(client)
    val activity = runBlocking { githubApi.getUserActivity("uwaisalqadri") }
    return activity
        .filter { it.public }
        .mapNotNull { event ->
            when (val payload = event.payload) {
                UnknownPayload, null -> return@mapNotNull null
                is IssuesEventPayload -> {
                    ActivityItem(
                        "${payload.action} issue [#${payload.issue.number}](${payload.issue.htmlUrl}) on ${event.repo?.markdownUrl()}: \"${payload.issue.title}\"",
                        event.createdAt
                    )
                }

                is IssueCommentEventPayload -> {
                    ActivityItem(
                        "commented on [#${payload.issue.number}](${payload.comment.htmlUrl}) in ${event.repo?.markdownUrl()}",
                        event.createdAt
                    )
                }

                is PullRequestPayload -> {
                    val action =
                        if (payload.pullRequest.merged == true) "merged" else payload.action
                    ActivityItem(
                        text = "$action PR [#${payload.number}](${payload.pullRequest.htmlUrl}) to ${event.repo?.markdownUrl()}: \"${payload.pullRequest.title}\"",
                        timestamp = event.createdAt
                    )
                }

                is CreateEvent -> {
                    ActivityItem(
                        text = "created ${payload.refType}${payload.ref?.let { " `$it`" } ?: ""} on ${event.repo?.markdownUrl()}",
                        timestamp = event.createdAt
                    )
                }

                is DeleteEvent -> {
                    ActivityItem(
                        text = "deleted ${payload.refType}${payload.ref?.let { " `$it`" } ?: ""} on ${event.repo?.markdownUrl()}",
                        timestamp = event.createdAt
                    )
                }

                is ForkEventPayload -> {
                    ActivityItem(
                        text = "forked repository [#${payload.name}](${event.repo?.markdownUrl()}) to ${payload.htmlUrl}",
                        timestamp = event.createdAt
                    )
                }

                is PushEventPayload -> {
                    ActivityItem(
                        text = "pushed #${payload.commits.first().sha.take(7)} to ${event.repo?.markdownUrl()}: \"${payload.commits.first().message}\"",
                        timestamp = event.createdAt
                    )
                }

                is WatchEventPayload -> {
                    ActivityItem(
                        text = "watched repository ${event.repo?.markdownUrl()}",
                        timestamp = event.createdAt
                    )
                }
            }
        }
        .take(10)
}

fun createJson() = Json {
    isLenient = true
    ignoreUnknownKeys = true
    useAlternativeNames = false
}

fun createHttpClient(json: Json) = HttpClient(OkHttp) {
    install(ContentNegotiation) {
        json(json = json)
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

data class ActivityItem(
    val text: String,
    val timestamp: String
) {
    override fun toString(): String {
        val timestamp = Instant.parse(timestamp)
        return "**${timestamp.atZone(ZoneId.of("America/New_York")).toLocalDate()}** — $text"
    }
}

fun main(argv: Array<String>) {
    UpdateReadmeCommand().main(argv)
}