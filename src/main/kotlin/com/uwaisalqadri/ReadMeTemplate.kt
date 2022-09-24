package com.uwaisalqadri

fun createReadMe(
    githubActivity: List<ActivityItem>
): String {
    return """
    <!-- Currently working on [Slack](https://slack.com/). Read [my blog](https://zacsweers.dev/) or [follow me on Twitter](https://twitter.com/ZacSweers). -->
    <table><tr><td valign="top" width="60%">
    ## GitHub Activity
    <!-- githubActivity starts -->
    ${githubActivity.joinToString("\n\n") { "    $it" }}
    <!-- githubActivity ends -->
    </td><td valign="top" width="40%">
 <!--   
    ## On My Blog
    <!-- blog starts -->
    <!-- blog ends -->
    _More on [zacsweers.dev](https://zacsweers.dev/)_
    </td></tr></table>
 -->   
    
    <sub><a href="https://github.com/ZacSweers/ZacSweers/">Inspired by Zac Sweeners's auto-updating profile README with Kotlin Implementation.</a></sub>
  """.trimIndent()
}