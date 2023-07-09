# Kebab 1.20.1
**A fast blazingly fast Minecraft Java Edition backend server.**

Kebab is an open source project supported by [**Uroria Network**](https://github.com/Uroria?view_as=public).

In order to run Kebab you need an Minecraft reverse proxy like [**Velocity**](https://papermc.io/software/velocity).
Other proxies like [**BungeeCord**](https://github.com/SpigotMC/BungeeCord) should work fine but are not officially supported.

## How Kebab works

The Kebab server itself is written in Rust. This means that the Kebab-server itself isn't running in a [*JVM*](https://de.wikipedia.org/wiki/Java_Virtual_Machine).
This is a huge benefit because the JVMs [*Just-In-Time-Compiler (JIT)*](https://de.wikipedia.org/wiki/Just-in-time-Kompilierung) doesn't need to compile
Java byte-code to machine code.

Rust itself is also way better in Multi-Threading than Java. This increases the overall performance of the server.

Garbage-Collection is a big deal, but not in Rust. Rust relies on its [*Borrow-checker*](https://doc.rust-lang.org/1.8.0/book/references-and-borrowing.html)
which is surprisingly faster than a Java GC like G1.

**So what does the Java part of Kebab do?**

Kebab has a Java API used in plugins because most of the Minecraft's server developers are way better in Java. Rust is not an easy language to learn
and is just to low-level for Minecraft applications.

The Java API of Kebab loads the Kebab-Server library compiled as a binary into the JVM, so both run at the same time. This is possible due to the built-in
Java library [*JNI*](https://de.wikipedia.org/wiki/Java_Native_Interface).

The Kebab-Server part is more than **10 times faster** than the Kebab-API running in Java.*

This means the only bottleneck left is the Kebab-API. To get maximum performance out of Kebab we suggest using
[*GraalVM*](https://www.graalvm.org/) as JRE.

**We measured this with simple math operations. You could get a different result depending on your application.*

## Current API plans

 - [ ] Basic implementations
 - [ ] Event handling
 - [ ] Scheduler implementation
 - [ ] Built-in packet library
 - [ ] World generation
 - [ ] Rust plugin api

