package com.uwaisalqadri

import com.prof18.rssparser.RssParser
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class ReadMeUpdater {

    val parser = RssParser()

    val client = HttpClient {
        install(HttpRequestRetry) {
            retryOnExceptionOrServerErrors(maxRetries = 2)
            exponentialDelay()
        }
        install(ContentNegotiation) {
            json(json = Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun generateReadme(): String {
        return withContext(Dispatchers.Default) {
            val githubActivity = async { fetchGithubActivity() }
            val blogActivity = async { fetchMediumArticle() }

            // Fetch in parallel
            createReadMe(githubActivity.await(), blogActivity.await())
        }
    }

    private suspend fun fetchMediumArticle(): List<ActivityItem> {
        val mediumRssApi = MediumRssApi.create(parser)
        val articles = mediumRssApi.fetchArticles(atUsername = "@uwaisalqadri")
        return articles
            .map {
                ActivityItem(
                    text = "[Read Article](${it.link ?: "-"})",
                    head = it.title ?: "-",
                    isTimestamp = false
                )
            }
            .take(10)
        }

    private suspend fun fetchGithubActivity(): List<ActivityItem> {
        val githubApi = GitHubApi.create(client)
        val events =
            try {
                githubApi.getUserActivity(login = "uwaisalqadri")
            } catch (e: Exception) {
                println("Could not load GitHub activity.")
                e.printStackTrace()
                return listOf(
                    ActivityItem(
                        "Could not load GitHub activity. Please check back later.",
                        head = LocalDateTime.now().toString(),
                        isTimestamp = false,
                    )
                )
            }
        return events
            .filter { it.public }
            .mapNotNull { event ->
                println("GITHUB ${event.repo} | ${event.payload}")
                when (val payload = event.payload) {
                    UnknownPayload,
                    null -> return@mapNotNull null
                    is IssuesEventPayload -> {
                        ActivityItem(
                            "${payload.action} issue [#${payload.issue.number}](${payload.issue.htmlUrl}) on ${event.repo?.markdownUrl()}: \"${payload.issue.title}\"",
                            event.createdAt,
                        )
                    }
                    is IssueCommentEventPayload -> {
                        ActivityItem(
                            "commented on [#${payload.issue.number}](${payload.comment.htmlUrl}) in ${event.repo?.markdownUrl()}",
                            event.createdAt,
                        )
                    }
                    is PullRequestPayload -> {
                        val action = if (payload.pullRequest.merged == true) "merged" else payload.action
                        ActivityItem(
                            "$action PR [#${payload.number}](${payload.pullRequest.htmlUrl}) to ${event.repo?.markdownUrl()}: \"${payload.pullRequest.title}\"",
                            event.createdAt,
                        )
                    }
                    is CreateEvent -> {
                        ActivityItem(
                            "created ${payload.refType}${payload.ref?.let { " `$it`" } ?: ""} on ${event.repo?.markdownUrl()}",
                            event.createdAt,
                        )
                    }
                    is WatchEventPayload -> {
                        ActivityItem(
                            "watching ${event.repo?.markdownUrl()}",
                            event.createdAt,
                        )
                    }
                    is DeleteEvent -> {
                        if (payload.refType == "branch") {
                            // Filter out branch deletions
                            // https://github.com/ZacSweers/ZacSweers/issues/65
                            return@mapNotNull null
                        }
                        ActivityItem(
                            "deleted ${payload.refType}${payload.ref?.let { " `$it`" } ?: ""} on ${event.repo?.markdownUrl()}",
                            event.createdAt,
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
            } else "**$head** - $text"
        }
    }
}