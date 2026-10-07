package codes.yousef.summon.test

import codes.yousef.summon.components.display.Text
import codes.yousef.summon.modifier.Modifier
import java.nio.file.Files
import kotlin.test.Test

fun componentHarnessSnapshotSample() {
    val golden = Files.createTempFile("summon-profile-", ".snap").toFile()
    try {
        withComponentHarness(
            mountJvmComponentHarness {
                Text("Profile", Modifier().testTag("title"))
            }
        ) { harness ->
            harness.onNodeWithTag("title").assertTextEquals("Profile")
            harness.assertSemanticSnapshot(harness.semanticSnapshot())

            // Baseline changes are explicit; normal verification is read-only.
            harness.updateSemanticGolden(golden)
            harness.verifySemanticGolden(golden)
        }
    } finally {
        golden.delete()
    }
}

class DocumentationSamplesTest {
    @Test
    fun componentHarnessSnapshot() {
        componentHarnessSnapshotSample()
    }
}
