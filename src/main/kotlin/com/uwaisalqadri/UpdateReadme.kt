package com.uwaisalqadri

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.file
import com.prof18.rssparser.RssParser
import io.ktor.client.HttpClient
import kotlinx.coroutines.runBlocking
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
        val rssParser = createRssParser()

        val githubActivities = fetchGithubActivity(ktorHttpClient)
        val mediumArticles = fetchMediumArticle(rssParser)

        val newReadMe = createReadMe(githubActivities, mediumArticles)
        outputFile.writeText(newReadMe)

        // TODO why do I need to do this
        exitProcess(0)
    }
}

private fun fetchMediumArticle(
    parser: RssParser
): List<ActivityItem> {
    val mediumRssApi = MediumRssApi(parser)
    val article = runBlocking {
        mediumRssApi.fetchArticles(atUsername = "@uwaisalqadri")
    }
    return article.map {
            ActivityItem(
                text = "[Read Article](${it.link ?: "-"})",
                head = it.title ?: "-",
                isTimestamp = false
            )
        }
}

private fun fetchGithubActivity(
    client: HttpClient
): List<ActivityItem> {
    val githubApi = GithubApi(client)
    val activity = runBlocking {
        githubApi.getUserActivity(login = "uwaisalqadri")
    }
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
                        head = event.createdAt
                    )
                }

                is CreateEvent -> {
                    ActivityItem(
                        text = "created ${payload.refType}${payload.ref?.let { " `$it`" } ?: ""} on ${event.repo?.markdownUrl()}",
                        head = event.createdAt
                    )
                }

                is DeleteEvent -> {
                    ActivityItem(
                        text = "deleted ${payload.refType}${payload.ref?.let { " `$it`" } ?: ""} on ${event.repo?.markdownUrl()}",
                        head = event.createdAt
                    )
                }

                is ForkEventPayload -> {
                    ActivityItem(
                        text = "forked repository [#${payload.name}](${event.repo?.markdownUrl()}) to ${payload.htmlUrl}",
                        head = event.createdAt
                    )
                }

                is PushEventPayload -> {
                    ActivityItem(
                        text = "pushed ${payload.commits.first().markdownUrl()} to ${event.repo?.markdownUrl()}: \"${payload.commits.first().message}\"",
                        head = event.createdAt
                    )
                }

                is WatchEventPayload -> {
                    ActivityItem(
                        text = "watched repository ${event.repo?.markdownUrl()}",
                        head = event.createdAt
                    )
                }
            }
        }
        .take(10)
}

data class ActivityItem(
    val text: String,
    val head: String,
    val isTimestamp: Boolean = true
) {
    override fun toString(): String {
        return if (isTimestamp) {
            val timestamp = Instant.parse(head)
            "**${timestamp.atZone(ZoneId.of("America/New_York")).toLocalDate()}** — $text"
        } else "**$head** — $text"
    }
}

fun main(argv: Array<String>) {
    UpdateReadmeCommand().main(argv)
}