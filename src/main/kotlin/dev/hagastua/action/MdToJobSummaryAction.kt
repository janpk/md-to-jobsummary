package dev.hagastua.action

import io.quarkiverse.githubaction.Action
import io.quarkiverse.githubaction.Commands
import io.quarkiverse.githubaction.Inputs
import io.quarkiverse.githubapp.event.PullRequest
import java.io.File
import java.nio.charset.Charset
import org.kohsuke.github.GHEventPayload

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
    pullRequest.pullRequest.comment(markdownFile.readText(Charset.defaultCharset()))
  }
}
