@file:OptIn(ExperimentalSerializationApi::class)

package com.uwaisalqadri

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant

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
    @SerialName("createdAt")
    @Serializable(with = InstantSerializer::class)
    val createdAt: Instant,
    @SerialName("payload")
    @Contextual
    val payload: GitHubActivityEventPayload,
    @SerialName("public")
    val public: Boolean,
    @SerialName("repo")
    val repo: Repo?
)

@Serializable
sealed class GitHubActivityEventPayload {
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
}

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

@Serializer(forClass = Instant::class)
object InstantSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Instant", PrimitiveKind.LONG)
    override fun serialize(encoder: Encoder, value: Instant) = encoder.encodeLong(value.toEpochMilli())
    override fun deserialize(decoder: Decoder) = Instant.ofEpochMilli(decoder.decodeLong())
}