package com.uwaisalqadri

import com.prof18.rssparser.RssParser
import com.prof18.rssparser.model.RssChannel
import com.prof18.rssparser.model.RssItem

interface MediumRssApi {
    suspend fun fetchArticles(atUsername: String): List<RssItem>

    companion object {
        const val BASE_URL = "https://medium.com/feed/"

        fun create(rssParser: RssParser): MediumRssApi {
            return object : MediumRssApi {
                override suspend fun fetchArticles(atUsername: String): List<RssItem> {
                    val response: RssChannel = rssParser.getRssChannel("$BASE_URL/$atUsername")
                    return response.items
                }
            }
        }
    }
}