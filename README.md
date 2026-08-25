This is a Kotlin Multiplatform project targeting Android, iOS.

* [/iosApp](./iosApp/iosApp) contains an iOS application. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

* [/shared](./shared/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
    - [commonMain](./shared/src/commonMain/kotlin) is for code that’s common for all targets.
    - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
      For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
      the [iosMain](./shared/src/iosMain/kotlin) folder would be the right place for such calls.
      Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./shared/src/jvmMain/kotlin)
      folder is the appropriate location.

### Running the apps

Use the run configurations provided by the run widget in your IDE's toolbar. You can also use these commands and
options:

- Android app: `./gradlew :androidApp:assembleDebug`
- iOS app: open the [/iosApp](./iosApp) directory in Xcode and run it from there.

---

### Release signing

Release builds are signed with a single app signing key shared between the Play and
RuStore builds. The keystore itself and its passwords never live in this repository or
in `local.properties` — they are read from Gradle properties in the developer's own
`~/.gradle/gradle.properties`:

```properties
LESHY_STORE_FILE=/absolute/path/to/leshy-release.jks
LESHY_STORE_PASSWORD=<store password>
LESHY_KEY_ALIAS=<key alias>
LESHY_KEY_PASSWORD=<key password>
```

If these properties are absent, `assembleRelease` still succeeds and produces an
**unsigned** artifact — this is intentional so CI/dev machines without the key can still
build.

To generate the key (once, by a human — not by an agent):

```bash
keytool -genkeypair -v \
  -keystore leshy-release.jks \
  -alias <key alias> \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -storepass <store password> \
  -keypass <key password>
```

Store the resulting `.jks` file and the passwords outside the repo (password manager +
encrypted backup). This key must be uploaded to Play Console via PEPK when the app is
first created there — Google-generated signing keys cannot be swapped in later, and
Play/RuStore builds must share this same key to stay compatible.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…