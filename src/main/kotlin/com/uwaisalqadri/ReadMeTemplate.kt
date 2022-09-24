package com.uwaisalqadri

import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

fun createReadMe(
    githubActivity: List<ActivityItem>
): String {
    return """
    <table><tr><td valign="top" width="100%">    

    ## GitHub Activity

    ${githubActivity.joinToString("\n") { it.toString() }}
                
    <sub><a href="https://github.com/ZacSweers/ZacSweers/">Inspired by Zac Sweeners's auto-updating profile README with Kotlin Implementation.</a></sub>
        
  """.trimIndent()
}