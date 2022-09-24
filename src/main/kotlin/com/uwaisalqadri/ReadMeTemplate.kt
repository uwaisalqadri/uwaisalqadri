package com.uwaisalqadri

import java.text.SimpleDateFormat
import java.util.*

fun createReadMe(
    githubActivity: List<ActivityItem>
): String {
    return """
    <table><tr><td valign="top" width="100%">    

    ## GitHub Activity

    ${githubActivity.joinToString("\n") { "**${it.timestamp.formatDate("dd MMM, yy")}** - ${it.text}" }}
                
    <sub><a href="https://github.com/ZacSweers/ZacSweers/">Inspired by Zac Sweeners's auto-updating profile README with Kotlin Implementation.</a></sub>
        
  """.trimIndent()
}

fun String.formatDate(format: String): String {
    val date = if (this.isNotEmpty()) SimpleDateFormat("yyyy-MM-ddThh:mm:ssZ").parse(this) else Date()
    val dateFormatter = SimpleDateFormat(format, Locale.getDefault())
    return dateFormatter.format(date ?: Date())
}