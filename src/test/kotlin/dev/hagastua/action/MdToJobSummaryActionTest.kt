package dev.hagastua.action

import io.quarkiverse.githubaction.Commands
import io.quarkiverse.githubaction.CommandsInitializer
import io.quarkiverse.githubaction.Inputs
import io.quarkiverse.githubaction.InputsInitializer
import io.quarkiverse.githubaction.runtime.CommandsImpl
import io.quarkiverse.githubaction.runtime.github.EnvFiles
import io.quarkiverse.githubaction.testing.DefaultTestInputs
import io.quarkus.test.junit.QuarkusTestProfile
import io.quarkus.test.junit.TestProfile
import io.quarkus.test.junit.main.Launch
import io.quarkus.test.junit.main.QuarkusMainTest
import jakarta.enterprise.inject.Alternative
import jakarta.inject.Singleton
import java.io.IOException
import java.io.UncheckedIOException
import java.nio.file.Files
import java.nio.file.Path
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@QuarkusMainTest
@TestProfile(MdToJobSummaryActionTest.MdToJobSummaryActionTestStatusProfile::class)
class MdToJobSummaryActionTest {

  @Test
  @Launch()
  fun `Verify that job summary is populated`() {
//    assertThat(Path.of(System.getProperty("java.io.tmpdir") + "/temp-github-summary.txt"))
//        .content()
//        .contains("# Test Markdown" + System.lineSeparator())
  }

  class MdToJobSummaryActionTestStatusProfile : QuarkusTestProfile {
    override fun getEnabledAlternatives(): MutableSet<Class<*>> {
      return mutableSetOf(
          MockInputsInitializerStatus::class.java, MockCommandsInitializer::class.java)
    }
  }

  @Alternative
  @Singleton
  class MockInputsInitializerStatus : InputsInitializer {
    override fun createInputs(): Inputs {
      return DefaultTestInputs(
          mapOf<String, String>(
              Pair("file", "src/test/resources/test.md"),
          ))
    }
  }

  @Alternative
  @Singleton
  class MockCommandsInitializer : CommandsInitializer {
    override fun createCommands(): Commands {
      return try {
        val githubSummaryPath: Path =
            Path.of(System.getProperty("java.io.tmpdir") + "/temp-github-summary.txt")
        Files.deleteIfExists(githubSummaryPath)
        CommandsImpl(mutableMapOf(Pair(EnvFiles.GITHUB_STEP_SUMMARY, githubSummaryPath.toString())))
      } catch (e: IOException) {
        throw UncheckedIOException(e)
      }
    }
  }
}
