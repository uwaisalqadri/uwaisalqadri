package com.uwaisalqadri

fun createReadMe(
    githubActivities: List<ActivityItem>,
    mediumArticles: List<ActivityItem>
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
        
<sub>This is an automation written in Kotlin by <a href="https://uwais.framer.website/">Uwais Alqadri</a></sub>
    """.trimIndent()
}