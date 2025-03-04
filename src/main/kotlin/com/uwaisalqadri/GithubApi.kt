@file:OptIn(ExperimentalSerializationApi::class)

package com.uwaisalqadri

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.*
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

class GithubApi(private val httpClient: HttpClient) {

    companion object {
        const val BASE_URL = "https://api.github.com"
    }

    suspend fun getUserActivity(login: String): List<GithubActivityEvent> {
        return httpClient.get("$BASE_URL/users/$login/events").body()
    }
}

@Serializable
data class GithubActivityEvent(
    @SerialName("id")
    val id: String,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("payload")
    val payload: GitHubActivityEventPayload,
    @SerialName("public")
    val public: Boolean,
    @SerialName("type")
    val type: String,
    @SerialName("repo")
    val repo: Repo?
)

@Serializable(with = EventPayloadSerializer::class)
sealed class GitHubActivityEventPayload

@Serializable
object UnknownPayload : GitHubActivityEventPayload()

@Serializable
data class IssuesEventPayload(
    val action: String,
    val issue: Issue
): GitHubActivityEventPayload()

@Serializable
data class IssueCommentEventPayload(
    val action: String,
    val comment: Comment,
    val issue: Issue
): GitHubActivityEventPayload()

@Serializable
data class PullRequestPayload(
    val action: String,
    val number: Int,
    @SerialName("pull_request")
    val pullRequest: PullRequest
): GitHubActivityEventPayload()

@Serializable
data class CreateEvent(
    val ref: String?,
    @SerialName("ref_type")
    val refType: String
): GitHubActivityEventPayload()

@Serializable
data class DeleteEvent(
    val ref: String?,
    @SerialName("ref_type")
    val refType: String
): GitHubActivityEventPayload()

@Serializable
data class PushEventPayload(
    val ref: String?,
    val repositoryId: Long = 0L,
    val commits: List<Commits> = listOf(),
    val head: String
): GitHubActivityEventPayload()

@Serializable
data class ForkEventPayload(
    @SerialName("svn_url")
    val svnUrl: String?,
    @SerialName("languages_url")
    val languagesUrl: String?,
    @SerialName("html_url")
    val htmlUrl: String?,
    @SerialName("archive_url")
    val archiveUrl: String?,
    @SerialName("has_wiki")
    val hasWiki: Boolean?,
    @SerialName("has_discussions")
    val hasDiscussions: Boolean?,
    @SerialName("size")
    val size: Long?,
    @SerialName("compare_url")
    val compareUrl: String?,
    @SerialName("blobs_url")
    val blobsUrl: String?,
    @SerialName("hooks_url")
    val hooksUrl: String?,
    @SerialName("comments_url")
    val commentsUrl: String?,
    @SerialName("milestones_url")
    val milestonesUrl: String?,
    @SerialName("clone_url")
    val cloneUrl: String?,
    @SerialName("open_issues_count")
    val openIssuesCount: Int?,
    @SerialName("releases_url")
    val releasesUrl: String?,
    @SerialName("has_downloads")
    val hasDownloads: Boolean?,
    @SerialName("teams_url")
    val teamsUrl: String?,
    @SerialName("notifications_url")
    val notificationsUrl: String?,
    @SerialName("is_template")
    val isTemplate: Boolean?,
    @SerialName("pulls_url")
    val pullsUrl: String?,
    @SerialName("allow_forking")
    val allowForking: Boolean?,
    @SerialName("pushed_at")
    val pushedAt: String?,
    @SerialName("created_at")
    val createdAt: String?,
    @SerialName("git_tags_url")
    val gitTagsUrl: String?,
    @SerialName("open_issues")
    val openIssues: Int?,
    @SerialName("subscribers_url")
    val subscribersUrl: String?,
    @SerialName("forks")
    val forks: Int?,
    @SerialName("subscription_url")
    val subscriptionUrl: String?,
    @SerialName("node_id")
    val nodeId: String?,
    @SerialName("web_commit_signoff_required")
    val webCommitSignoffRequired: Boolean?,
    @SerialName("commits_url")
    val commitsUrl: String?,
    @SerialName("issue_events_url")
    val issueEventsUrl: String?,
    @SerialName("git_refs_url")
    val gitRefsUrl: String?,
    @SerialName("public")
    val public: Boolean?,
    @SerialName("url")
    val url: String?,
    @SerialName("git_url")
    val gitUrl: String?,
    @SerialName("forks_url")
    val forksUrl: String?,
    @SerialName("branches_url")
    val branchesUrl: String?,
    @SerialName("labels_url")
    val labelsUrl: String?,
    @SerialName("private")
    val private: Boolean?,
    @SerialName("merges_url")
    val mergesUrl: String?,
    @SerialName("homepage")
    val homepage: String?,
    @SerialName("trees_url")
    val treesUrl: String?,
    @SerialName("collaborators_url")
    val collaboratorsUrl: String?,
    @SerialName("id")
    val id: Long?,
    @SerialName("default_branch")
    val defaultBranch: String?,
    @SerialName("issues_url")
    val issuesUrl: String?,
    @SerialName("has_issues")
    val hasIssues: Boolean?,
    @SerialName("deployments_url")
    val deploymentsUrl: String?,
    @SerialName("archived")
    val archived: Boolean?,
    @SerialName("contributors_url")
    val contributorsUrl: String?,
    @SerialName("visibility")
    val visibility: String?,
    @SerialName("has_pages")
    val hasPages: Boolean?,
    @SerialName("statuses_url")
    val statusesUrl: String?,
    @SerialName("name")
    val name: String?,
    @SerialName("full_name")
    val fullName: String?,
    @SerialName("git_commits_url")
    val gitCommitsUrl: String?,
    @SerialName("stargazers_url")
    val stargazersUrl: String?,
    @SerialName("forks_count")
    val forksCount: Int?,
    @SerialName("issue_comment_url")
    val issueCommentUrl: String?,
    @SerialName("has_projects")
    val hasProjects: Boolean?,
    @SerialName("downloads_url")
    val downloadsUrl: String?,
    @SerialName("contents_url")
    val contentsUrl: String?,
    @SerialName("updated_at")
    val updatedAt: String?,
    @SerialName("disabled")
    val disabled: Boolean?,
    @SerialName("tags_url")
    val tagsUrl: String?,
    @SerialName("watchers")
    val watchers: Int?,
    @SerialName("description")
    val description: String?,
    @SerialName("fork")
    val fork: Boolean?,
    @SerialName("keys_url")
    val keysUrl: String?,
    @SerialName("assignees_url")
    val assigneesUrl: String?,
    @SerialName("events_url")
    val eventsUrl: String?,
    @SerialName("stargazers_count")
    val stargazersCount: Int?,
    @SerialName("ssh_url")
    val sshUrl: String?,
    @SerialName("watchers_count")
    val watchersCount: Int?
): GitHubActivityEventPayload()


@Serializable
data class WatchEventPayload(
    val action: String
): GitHubActivityEventPayload()

@Serializable
data class Commits(
    val url: String,
    val message: String,
    val author: Author,
    val distinct: Boolean,
    val sha: String
) {
    fun adjustedUrl(): String {
        return url.replaceFirst("api.", "")
            .replaceFirst("repos/", "")
    }
    fun markdownUrl(): String = "[${sha.take(7)}](${adjustedUrl()})"
}

@Serializable
data class Author(
    val email: String,
    val name: String
)

@Serializable
data class Issue(
    val title: String,
    val body: String? = null,
    @SerialName("html_url")
    val htmlUrl: String,
    val number: Int
)

@Serializable
data class Comment(
    @SerialName("html_url")
    val htmlUrl: String,
    val body: String
)

@Serializable
data class PullRequest(
    @SerialName("html_url")
    val htmlUrl: String,
    val title: String,
    val body: String?,
    val merged: Boolean? = false
)

@Serializable
data class Repo(
    val name: String,
    val url: String
) {
    fun adjustedUrl(): String {
        return url.replaceFirst("api.", "")
            .replaceFirst("repos/", "")
    }
    fun markdownUrl(): String = "[$name](${adjustedUrl()})"
}

object EventPayloadSerializer : JsonContentPolymorphicSerializer<GitHubActivityEventPayload>(GitHubActivityEventPayload::class) {
    override fun selectDeserializer(element: JsonElement) = when {
        "IssuesEvent" in element.jsonObject -> IssuesEventPayload.serializer()
        "commits" in element.jsonObject -> PushEventPayload.serializer()
        "fork" in element.jsonObject -> ForkEventPayload.serializer()
        "WatchEvent" in element.jsonObject -> WatchEventPayload.serializer()
        "comment" in element.jsonObject -> IssueCommentEventPayload.serializer()
        "pull_request" in element.jsonObject -> PullRequestPayload.serializer()
        "ref_type" in element.jsonObject -> CreateEvent.serializer()
        "ref_type" in element.jsonObject -> DeleteEvent.serializer()
        else -> UnknownPayload.serializer()
    }
}