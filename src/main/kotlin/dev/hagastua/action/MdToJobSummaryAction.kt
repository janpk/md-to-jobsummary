package dev.hagastua.action

import io.quarkiverse.githubaction.Action
import io.quarkiverse.githubaction.Commands
import io.quarkiverse.githubaction.Inputs
import java.io.File
import java.nio.charset.Charset

open class MdToJobSummaryAction {

  @Action
  fun action(inputs: Inputs, commands: Commands) {
    val markdownFile = File(inputs.getRequired("file"))
    if (!markdownFile.exists()) {
      commands.error("Specified file does not exist ${markdownFile.absolutePath}")
    }
    commands.appendJobSummary(markdownFile.readText(Charset.defaultCharset()))
  }
}
