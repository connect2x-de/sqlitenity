# SQLitenity

A KMP wrapper around SQLite for:

- Kotlin/Native (all supported Platforms)
- Kotlin/JVM
- Kotlin/Android

> [!NOTE]
> There is no support for Kotlin/Web yet, but this is just due to time constraints.
> In theory it should be possible to support that.

Provided packages:

- sqlitenity-binaries: native binaries
- sqlitenity-interop: low level interop between binaries and kotlin
- sqlitenity-bindings: type safe bindings via the interop layer
- sqlitenity-api: api interfaces
- sqlitenity-bundled: api implementation via bindings and binaries
- sqlitenity-compat: compatibility layer for room

# Room Compatibilty

SQLitenity can be used as SQLite implementation for Room via `sqlitenity-compat`.
The provided compatibility wrappers can be used to adapt SQLitenity's interfaces.

