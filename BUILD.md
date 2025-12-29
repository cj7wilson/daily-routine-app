# Building and Running the Daily Routine App

This guide explains how to build and run the Daily Routine App.

## Prerequisites

### Required Software
- **Android Studio**: Hedgehog (2023.1.1) or later
- **JDK**: OpenJDK 17 or later
- **Android SDK**: 
  - compileSdk: 34
  - minSdk: 24
  - targetSdk: 34

### Android SDK Components
Install these via Android Studio SDK Manager:
- Android SDK Platform 34
- Android SDK Build-Tools 34.0.0
- Android SDK Platform-Tools
- Android Emulator (for testing)

## Setup Steps

### 1. Clone the Repository
```bash
git clone https://github.com/cj7wilson/daily-routine-app.git
cd daily-routine-app
```

### 2. Open in Android Studio
1. Launch Android Studio
2. Select "Open an Existing Project"
3. Navigate to the cloned repository
4. Click "OK"

### 3. Sync Gradle
Android Studio should automatically start Gradle sync. If not:
1. Click "File" → "Sync Project with Gradle Files"
2. Wait for dependencies to download

## Building

### Command Line Build

#### Debug Build
```bash
./gradlew assembleDebug
```
Output: `app/build/outputs/apk/debug/app-debug.apk`

#### Release Build
```bash
./gradlew assembleRelease
```
Output: `app/build/outputs/apk/release/app-release-unsigned.apk`

### Android Studio Build
1. Click "Build" → "Build Bundle(s) / APK(s)" → "Build APK(s)"
2. Wait for build to complete
3. Click "locate" in the notification to find the APK

## Running

### On Emulator
1. Create an emulator in AVD Manager (Tools → Device Manager)
2. Start the emulator
3. Click the green "Run" button in Android Studio
4. Select the running emulator

### On Physical Device
1. Enable Developer Options on your Android device:
   - Go to Settings → About Phone
   - Tap "Build Number" 7 times
2. Enable USB Debugging in Developer Options
3. Connect device via USB
4. Click "Run" and select your device

### Command Line Install
```bash
# Install debug APK
./gradlew installDebug

# Launch the app
adb shell am start -n com.dailyroutine.app/.MainActivity
```

## Testing

### Unit Tests
```bash
# Run all unit tests
./gradlew test

# Run tests with coverage
./gradlew testDebugUnitTest jacocoTestReport
```

### Instrumented Tests
```bash
# Run on connected device/emulator
./gradlew connectedAndroidTest
```

### Lint Checks
```bash
# Run lint analysis
./gradlew lint

# View results
open app/build/reports/lint-results.html
```

## Troubleshooting

### Gradle Sync Fails
- Ensure you have JDK 17 installed
- Check internet connection (for dependency download)
- Try "File" → "Invalidate Caches / Restart"

### Build Fails
- Check Android SDK is properly installed
- Ensure ANDROID_HOME environment variable is set
- Clean and rebuild: `./gradlew clean build`

### Emulator Issues
- Ensure Intel HAXM or AMD Virtualization is enabled
- Try creating a new emulator with different API level
- Check available RAM (emulator needs ~2GB)

### Out of Memory
Add to `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx2048m -XX:MaxMetaspaceSize=512m
```

## Building for Production

### 1. Create Keystore
```bash
keytool -genkey -v -keystore my-release-key.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias my-key-alias
```

### 2. Configure Signing
Create `keystore.properties` in project root:
```properties
storePassword=YOUR_STORE_PASSWORD
keyPassword=YOUR_KEY_PASSWORD
keyAlias=my-key-alias
storeFile=my-release-key.jks
```

### 3. Build Release APK
```bash
./gradlew assembleRelease
```

### 4. Build App Bundle (for Play Store)
```bash
./gradlew bundleRelease
```
Output: `app/build/outputs/bundle/release/app-release.aab`

## Continuous Integration

### GitHub Actions Example
```yaml
name: Android CI

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      - name: Build with Gradle
        run: ./gradlew build
      - name: Run tests
        run: ./gradlew test
```

## Performance Optimization

### Build Speed
- Enable Gradle daemon (default)
- Use parallel builds: `org.gradle.parallel=true`
- Enable configuration cache: `org.gradle.configuration-cache=true`
- Use build cache: `org.gradle.caching=true`

### APK Size Reduction
- Enable ProGuard/R8 (already configured in release builds)
- Use APK splits for different architectures
- Enable resource shrinking

## IDE Setup

### Recommended Android Studio Plugins
- Kotlin
- Android Jetpack Compose
- Material Theme UI
- Database Inspector (built-in)

### Code Style
- Use default Kotlin code style
- Enable "Optimize imports on the fly"
- Format on save recommended

## Additional Resources
- [Android Developer Documentation](https://developer.android.com)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Room Database Guide](https://developer.android.com/training/data-storage/room)
- [Google Play Billing](https://developer.android.com/google/play/billing)

## Support
For issues or questions, please open an issue on the GitHub repository.
