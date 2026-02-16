@file:Suppress("UnstableApiUsage", "MissingPackageDeclaration")

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.NamedDomainObjectSet
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

fun KotlinMultiplatformExtension.withAll() {
    withAndroidxCompatible()

    mingwX64()

    androidNativeX86()
    androidNativeX64()
    androidNativeArm32()
    androidNativeArm64()
}

fun KotlinMultiplatformExtension.withAndroidxCompatible() {
    jvm()

    linuxX64()
    linuxArm64()

    macosX64()
    macosArm64()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    extensions.configure<KotlinMultiplatformAndroidLibraryTarget>("androidLibrary") {}
}

val NamedDomainObjectContainer<KotlinSourceSet>.native: NamedDomainObjectSet<KotlinSourceSet>
    get() = matching(KotlinSourceSet::isNative)

val NamedDomainObjectContainer<KotlinSourceSet>.androidHostTest:
    NamedDomainObjectProvider<KotlinSourceSet>
    get() = named("androidHostTest")

val NamedDomainObjectContainer<KotlinSourceSet>.androidDeviceTest:
    NamedDomainObjectProvider<KotlinSourceSet>
    get() = named("androidDeviceTest")

private fun KotlinSourceSet.isNative(): Boolean =
    when {
        name == "nativeMain" || name == "nativeTest" -> true
        dependsOn.any { it.isNative() } -> true
        else -> false
    }
