import de.connect2x.sqlitenity.conventions.ConventionsExtension
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.time.ExperimentalTime
import org.gradle.api.Project
import org.gradle.api.internal.GradleInternal
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType

inline val Project.conventions: ConventionsExtension
    get() = (gradle as GradleInternal).settings.extensions.getByType<ConventionsExtension>()

enum class EnvironmentKind {
    LOCAL,
    DEV,
    RELEASE,
}

inline val Project.isCI: Provider<Boolean>
    get() = providers.environmentVariable("CI").map(String::toBoolean).orElse(false)

inline val Project.isRelease: Provider<Boolean>
    get() = providers.environmentVariable("CI_COMMIT_TAG").map(String::isNotEmpty).orElse(false)

inline val Project.environment: Provider<EnvironmentKind>
    get() =
        isCI.zip(isRelease) { isCI, isRelease ->
            when {
                !isCI -> EnvironmentKind.LOCAL
                isRelease -> EnvironmentKind.RELEASE
                else -> EnvironmentKind.DEV
            }
        }

@OptIn(ExperimentalTime::class)
inline val Project.commitTimestamp: Provider<Instant>
    get() = providers.environmentVariable("CI_COMMIT_TIMESTAMP").map(Instant::parse)

@PublishedApi
internal val formatter = DateTimeFormatter.ofPattern("yyyyMMdd.HHmmss").withZone(ZoneOffset.UTC)

@OptIn(ExperimentalTime::class)
inline val Project.versionTimestamp: Provider<String>
    get() = commitTimestamp.map(formatter::format)

inline val Project.formattedVersion: Provider<String>
    get() =
        providers.provider {
            val version = conventions.version.get()

            when (environment.get()) {
                EnvironmentKind.LOCAL -> "$version-LOCAL"
                EnvironmentKind.DEV -> "$version-SNAPSHOT-${versionTimestamp.get()}"
                EnvironmentKind.RELEASE -> version
            }
        }
