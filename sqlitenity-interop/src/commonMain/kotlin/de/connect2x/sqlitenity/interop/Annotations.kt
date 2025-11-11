@file:OptIn(ExperimentalMultiplatform::class)
@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package de.connect2x.sqlitenity.interop

@OptionalExpectation @Target(AnnotationTarget.FUNCTION) expect annotation class CriticalNative()

@OptionalExpectation @Target(AnnotationTarget.FUNCTION) expect annotation class FastNative()

@OptionalExpectation
@Target(AnnotationTarget.FUNCTION)
expect annotation class ModuleImport(val module: String, val name: String)

@OptionalExpectation
@Target(AnnotationTarget.FUNCTION)
expect annotation class ExternalSymbolName(val name: String)
