package com.uwaisalqadri

fun createReadMe(
    githubActivities: List<ActivityItem>,
    mediumArticles: List<ActivityItem>,
): String {
    return """
<table>
<tr>
<td valign="top" width="50%">
        
## GitHub Activity
           
${githubActivities.joinToString("\n") { "- $it" }}
            
</td>
        
<td valign="top" width="50%">
        
## Medium Articles
            
${mediumArticles.joinToString("\n") { "- $it" }}
            
</td>
</tr>
</table>
        
<sub><a href="https://github.com/ZacSweers/ZacSweers/">Inspired by Zac Sweers's auto-updating profile README with Kotlin Implementation.</a></sub>
    """.trimIndent()
}