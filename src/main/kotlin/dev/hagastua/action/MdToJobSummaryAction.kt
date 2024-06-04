package dev.hagastua.action

import io.quarkiverse.githubaction.Action
import io.quarkiverse.githubaction.Commands
import io.quarkiverse.githubaction.Inputs
import io.quarkiverse.githubapp.event.Issue
import io.quarkiverse.githubapp.event.PullRequest
import io.smallrye.graphql.client.Response
import org.kohsuke.github.GHEventPayload
import java.io.File
import java.nio.charset.Charset

open class MdToJobSummaryAction {

  @Action
  fun action(inputs: Inputs, commands: Commands, @PullRequest pullRequest: GHEventPayload.PullRequest?) {
    val markdownFile = File(inputs.getRequired("file"))
    if (!markdownFile.exists()) {
      commands.error("Specified file does not exist ${markdownFile.absolutePath}")
    }
    commands.appendJobSummary(markdownFile.readText(Charset.defaultCharset()))
    if (pullRequest != null) {
      commands.notice(pullRequest.pullRequest.toString())
      pullRequest.pullRequest.comment(markdownFile.readText(Charset.defaultCharset()))
    } else {
      commands.notice("not pull request")
    }
  }
}
