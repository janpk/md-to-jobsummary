package dev.hagastua.action

import io.quarkiverse.githubaction.Action
import io.quarkiverse.githubaction.Commands
import io.quarkiverse.githubaction.Inputs
import io.quarkiverse.githubapp.event.PullRequest
import io.quarkus.logging.Log
import java.io.File
import java.nio.charset.Charset
import java.time.OffsetDateTime
import org.kohsuke.github.GHEventPayload
import org.kohsuke.github.GHIssueComment

open class MdToJobSummaryAction {

  @Action("jobsummary")
  fun action(inputs: Inputs, commands: Commands) {
    val markdownFile = File(inputs.getRequired("file"))
    if (!markdownFile.exists()) {
      commands.error("Specified file does not exist ${markdownFile.absolutePath}")
    }
    commands.appendJobSummary(markdownFile.readText(Charset.defaultCharset()))
  }

  @Action("pullrequest")
  fun action(
      inputs: Inputs,
      commands: Commands,
      @PullRequest pullRequest: GHEventPayload.PullRequest
  ) {
    val markdownFile = File(inputs.getRequired("file"))
    if (!markdownFile.exists()) {
      commands.error("Specified file does not exist ${markdownFile.absolutePath}")
    }
    commands.notice(pullRequest.pullRequest.toString())
    
    val marker = "<!-- md-to-jobsummary-sticky -->"
    val body = buildString {
      appendLine(marker)
      appendLine()
      appendLine("```text")
      appendLine(markdownFile.readText(Charset.defaultCharset()))
      appendLine("```")
      appendLine()
    }
    
    val existing: GHIssueComment? = pullRequest.pullRequest.listComments()
      .withPageSize(ISSUE_COUNT)
      .toList()
      .firstOrNull { it.body?.contains(marker) == true }
    
    if (existing != null) {
      existing.update(body)          // PATCH /issues/comments/{id}
      Log.info("Updated existing sticky comment id=${existing.id}")
    } else {
      val created = pullRequest.pullRequest.comment(body)
      Log.info("Created new sticky comment id=${created.id}")
    }
    
    pullRequest.pullRequest.comment(markdownFile.readText(Charset.defaultCharset()))
  }
  
  companion object {
    const val ISSUE_COUNT = 50
  }
  
}
