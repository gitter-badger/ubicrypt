# UbiCrypt

## Keep your digital life in your control. Privately and easily

Cloud storage is pervasive and cheap, but it raises concerns on privacy, security, resiliantness and portability.

*UbiCrypt* allows you to keep your data secrect and safely stored in multiple locations.

### Ubicrypt encrypts your files and keeps them to any cloud storage
#### Key features:
  - Asymetric encryption with [Elliptic Curves 32bit](https://en.wikipedia.org/wiki/Elliptic_curve).
  - Symmetric encryption with [GCM-AES-256](https://en.wikipedia.org/wiki/Galois/Counter_Mode).
  - File replication on multiple [Cloud Storage](https://en.wikipedia.org/wiki/Cloud_storage) system.
  - Sharing between multiple devices (Computer at home, at work, etc)
  - File Versioning and synchronization with [Vector Clocks](https://en.wikipedia.org/wiki/Vector_clock)

#### Software Stack:
  - [BouncyCastle](https://www.bouncycastle.org/). PGP/AES Encryption.
  - [Spring Boot](https://projects.spring.io/spring-boot/). Application container.
  - [RxJava](https://github.com/ReactiveX/RxJava). Asyncronous and event-based framework.
  - [JavaFX](http://docs.oracle.com/javase/8/javase-clienttechnologies.htm). Java UI library

#### Run UbiCrypt 

`./gradlew run`
