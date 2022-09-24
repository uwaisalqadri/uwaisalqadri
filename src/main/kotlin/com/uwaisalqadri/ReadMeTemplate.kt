package com.uwaisalqadri

fun createReadMe(
    githubActivity: List<ActivityItem>
): String {
    return """
        
    <table>
    <tr>
    
    <td valign="top" width="60%">
    
    ## GitHub Activity
    <!-- githubActivity starts -->
    ${githubActivity.joinToString("\n\n") { "    $it" }}
    <!-- githubActivity ends -->
    
    </td>
    
    </tr>
    </table>
    
    <sub><a href="https://github.com/ZacSweers/ZacSweers/">Inspired by Zac Sweeners's auto-updating profile README with Kotlin Implementation.</a></sub>
  """.trimIndent()
}