# HiddenPlace Android App

A Telegram-like channel app for Android built with Kotlin and Jetpack Compose. Upload images and videos to Google Drive via a backend server, see thumbnails in a feed, and open full media.

## Features

- **Message Feed**: Scrollable list of messages in Telegram-like style.
- **Image Upload**: Pick and upload images; displays thumbnail in feed.
- **Video Upload**: Pick and upload videos; generates thumbnail on backend.
- **Media Viewer**: Tap thumbnail to view full image or play video.
- **Message Composer**: Type text, send text messages or attach media.

## Architecture

- **Frontend**: Android (Kotlin + Jetpack Compose)
- **Backend**: Node.js + Express
- **Storage**: Google Drive (via service account)
- **Thumbnails**: ffmpeg on backend for videos, client-side image display

## Setup

### 1. Backend Prerequisites

Ensure you have the Node.js backend running (see `server/README.md`):
- Backend listening on `http://localhost:4000`
- Or for emulator: `http://10.0.2.2:4000`

### 2. Android Build & Run

#### Requirements
- Android Studio Jellyfish or later
- JDK 11+
- Android SDK 33 (API level 33)
- emulator or physical device with Android 7.0+

#### Build
```bash
cd android
./gradlew build
```

#### Run on Emulator
```bash
./gradlew installDebug
./gradlew runAndroidTests  # optional
```

Or open `android/` in Android Studio and click **Run** → select emulator.

#### Run on Device
```bash
adb devices  # list connected devices
./gradlew installDebug  # installs APK to device
```

### 3. Adjust Backend URL

If testing on a physical device, update the API base URL in [MainActivity.kt](./app/src/main/java/com/hiddenplace/MainActivity.kt):

```kotlin
private val apiService = ApiService.create("http://<YOUR_BACKEND_IP>:4000/")
```

For emulator, keep `http://10.0.2.2:4000/` (special alias for localhost).

## Usage

1. Launch the app.
2. Type a message and tap Send.
3. Tap **📸 Image** to pick an image from gallery → uploads and shows thumbnail.
4. Tap **🎥 Video** to pick a video → backend generates thumbnail, displays in feed.
5. Tap a thumbnail to view the full image or play the video.

## Project Structure

```
android/
├── app/
│   ├── build.gradle
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/hiddenplace/
│   │   │   ├── MainActivity.kt       # Main UI + Compose composables
│   │   │   ├── Message.kt            # Data models
│   │   │   └── ApiService.kt         # Retrofit API client
│   │   └── res/
│   │       ├── values/
│   │       │   ├── colors.xml
│   │       │   ├── strings.xml
│   │       │   └── themes.xml
│   │       └── xml/
│   │           ├── backup_rules.xml
│   │           └── data_extraction_rules.xml
├── build.gradle
├── settings.gradle
└── README.md
```

## Dependencies

- **Compose**: `androidx.compose.ui`, `androidx.compose.material3`
- **Networking**: `retrofit2`, `okhttp3`
- **Image Loading**: `coil-compose`
- **Media Playback**: `androidx.media3` (ExoPlayer)
- **Lifecycle**: `androidx.lifecycle`

## Notes

- Internet and file permissions required. App requests them on first run (Android 6.0+).
- Videos require backend to be running for thumbnail generation.
- Images can be displayed without processing.
- Emulator may have performance limits on large file uploads; test with smaller files initially.

## Next Steps

- Add user authentication.
- Implement message deletion and editing.
- Add emoji reactions to messages.
- Support for voice messages and file sharing.
- Optimize image/video compression before upload.
- Add offline caching with local DB (Room).

## Troubleshooting

**Backend connection fails**: 
- Ensure backend is running on correct URL.
- For emulator: use `10.0.2.2:4000` (not `localhost`).
- For device: use machine IP (e.g., `192.168.1.x:4000`).

**Permission denied on file access**:
- On Android 6.0+, request runtime permissions. App asks on first media pick.
- Grant "Photos", "Videos", "Camera" when prompted.

**Video upload fails**:
- Ensure ffmpeg is installed on backend server.
- Check backend logs for errors.

## License

MIT
