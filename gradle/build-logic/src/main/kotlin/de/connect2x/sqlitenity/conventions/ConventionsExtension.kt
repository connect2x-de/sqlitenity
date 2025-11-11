package de.connect2x.sqlitenity.conventions

import javax.inject.Inject
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.property

open class ConventionsExtension @Inject constructor(objects: ObjectFactory) {

    val group: Property<String> = objects.property()
    val version: Property<String> = objects.property()

    val java: Java = objects.newInstance(Java::class.java)
    val android: Android = objects.newInstance(Android::class.java)
    val publishing: Publishing = objects.newInstance(Publishing::class.java)

    fun java(configure: Java.() -> Unit) = java.configure()

    fun android(configure: Android.() -> Unit) = android.configure()

    fun publishing(configure: Publishing.() -> Unit) = publishing.configure()

    open class Java @Inject constructor(objects: ObjectFactory) {
        val languageVersion: Property<Int> = objects.property()
    }

    open class Android @Inject constructor(objects: ObjectFactory) {
        val compileSdk: Property<Int> = objects.property()
        val minSdk: Property<Int> = objects.property()
    }

    open class Publishing @Inject constructor(objects: ObjectFactory) {
        val registry: Property<String> = objects.property()
    }
}
