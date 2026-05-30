# HiddenPlace — Telegram-like Channel App

A free, self-hosted channel app for Android with image/video uploads to Google Drive, thumbnails, and a Telegram-like messaging feed.

## Overview

- **Frontend**: Android app (Kotlin + Jetpack Compose)
- **Backend**: Node.js + Express server
- **Storage**: Google Drive (free, 15 GB per account)
- **Thumbnails**: Auto-generated for videos using ffmpeg

## Quick Start

### Prerequisites

1. **Android Studio** (Jellyfish+) and Android SDK 33
2. **Node.js** 14+ and npm
3. **ffmpeg** installed
4. **Google Cloud Account** (free tier works)

### 1. Setup Backend

```bash
# Navigate to server directory
cd server

# Install dependencies
npm install

# Create Google Cloud project and service account (see server/README.md)
# Download service account JSON key and place as service-account.json

# Create Google Drive folder and note the folder ID
# Share folder with service account email

# Set environment variables
export GOOGLE_APPLICATION_CREDENTIALS=./service-account.json
export GDRIVE_FOLDER_ID=<your-folder-id>
export PORT=4000

# Start backend
npm start
# Server runs on http://localhost:4000
```

### 2. Setup Android App

```bash
# Navigate to Android directory
cd android

# Build APK
./gradlew build

# Run on emulator or device
./gradlew installDebug

# Or open in Android Studio and click Run
```

### 3. Use the App

1. Open app on Android device/emulator.
2. Type a message → tap Send.
3. Tap **📸 Image** to upload image or **🎥 Video** to upload video.
4. See thumbnails in feed.
5. Tap thumbnail to view full image or play video.

## File Structure

```
HiddenPlace/
├── android/                    # Android app (Kotlin + Jetpack Compose)
│   ├── app/build.gradle
│   ├── app/src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/hiddenplace/
│   │   │   ├── MainActivity.kt      # Compose UI
│   │   │   ├── Message.kt           # Data models
│   │   │   └── ApiService.kt        # Retrofit client
│   │   └── res/                     # Resources (colors, themes, etc.)
│   ├── build.gradle
│   ├── settings.gradle
│   └── README.md
│
├── server/                     # Node.js backend
│   ├── index.js               # Express server + /upload endpoint
│   ├── drive.js               # Google Drive API helpers
│   ├── package.json
│   ├── service-account.json   # (create after setting up Google Cloud)
│   ├── .env                   # (optional, for env vars)
│   └── README.md
│
└── README.md                  # This file
```

## Features

### Android App
- **Telegram-like UI**: Clean message feed with card-based layout.
- **Image Upload**: Pick from gallery, upload, show thumbnail.
- **Video Upload**: Pick from gallery, upload, auto-generate thumbnail on backend.
- **Media Viewer**: Tap thumbnail → full-screen image or video player.
- **Message Composer**: Type text, attach media, send.
- **Auto-Refresh**: Messages load on startup and after each upload.

### Backend
- **Multipart Upload**: Accept image and video files.
- **Google Drive Storage**: Files stored in shared Drive folder.
- **Thumbnail Generation**: ffmpeg extracts 1-sec frame from video at 360p.
- **Public URLs**: Files made publicly readable; URLs returned to app.
- **RESTful API**: `/upload`, `/messages` (extensible).

## Configuration

### Backend Configuration

Set environment variables in `server/`:

```bash
# Google service account key location
export GOOGLE_APPLICATION_CREDENTIALS=./service-account.json

# Google Drive folder ID where files are stored
export GDRIVE_FOLDER_ID=<your-folder-id>

# Server port (default 4000)
export PORT=4000
```

### Android Configuration

**For Emulator** (default):
- Backend URL: `http://10.0.2.2:4000/`

**For Physical Device**:
- Update `MainActivity.kt` line ~100:
  ```kotlin
  private val apiService = ApiService.create("http://<YOUR_MACHINE_IP>:4000/")
  ```
  Replace `<YOUR_MACHINE_IP>` with your machine's IP (e.g., `192.168.1.100`).

## Usage Examples

### Upload Image via curl

```bash
curl -F "media=@/path/to/image.jpg" http://localhost:4000/upload
```

### Response

```json
{
  "fileUrl": "https://drive.google.com/file/d/1Abc2Def3gHi4jKl5mNoPqRs6tUvWxYz7/view",
  "thumbnail": "https://drive.google.com/file/d/1Abc2Def3gHi4jKl5mNoPqRs6tUvWxYz7/view"
}
```

## Notes & Recommendations

### Storage Limits
- **Google Drive Free**: 15 GB per account (quota resets daily for some operations).
- **For scale**: Consider S3, GCS, or Backblaze B2 for larger deployments.

### Performance
- **Video Thumbnails**: ffmpeg is single-threaded; queue long videos or use workers for production.
- **Image Compression**: Client-side compression before upload saves bandwidth and Drive space.
- **CDN**: Add Cloudflare or CloudFront in front of Drive for faster delivery.

### Security
- **Public Files**: All uploaded files are public (no auth required to view).
- **Add Authentication**: Implement user login and role-based access for production.
- **Validate Input**: Add file type and size checks on backend.

### Scalability Roadmap
1. Add user authentication (JWT, OAuth).
2. Move to S3 or GCS for unlimited storage.
3. Add message DB (SQLite → PostgreSQL).
4. Implement image/video compression and transcoding.
5. Add CDN caching layer.
6. Deploy on cloud (Heroku, AWS Lambda, GCP).

## Troubleshooting

### Backend Connection Fails
- **Emulator**: Use `http://10.0.2.2:4000` (not `localhost`).
- **Device**: Use machine IP, e.g., `192.168.1.100:4000`.
- **Check backend is running**: `curl http://localhost:4000/upload` should respond.

### Video Upload Succeeds, No Thumbnail
- **ffmpeg not installed**: `apt-get install ffmpeg` or `brew install ffmpeg`.
- **Check logs**: Backend logs should show ffmpeg errors.

### Permission Denied on Files
- **Android 6.0+**: App requests runtime permissions on first use.
- Grant "Photos", "Videos", "Camera" when prompted.

### Drive API Errors
- **Service account not shared to folder**: Share the Drive folder with service account email (Editor role).
- **API not enabled**: Enable Google Drive API in Google Cloud Console.

## Next Steps

- **User Authentication**: Add Firebase Auth or JWT-based login.
- **Message Search**: Implement full-text search on messages.
- **Reactions**: Add emoji reactions to messages.
- **Media Compression**: Client-side image/video compression before upload.
- **Offline Mode**: Cache messages locally with Room or SQLite.
- **Notifications**: Push notifications for new messages.
- **Admin Panel**: Backend dashboard to manage messages and storage.

## License

MIT

## Support

For issues:
1. Check the Android [README.md](./android/README.md) for app-specific troubleshooting.
2. Check the server [README.md](./server/README.md) for backend setup and API details.
3. Verify Google Cloud project and Drive folder permissions.
4. Ensure ffmpeg and Node.js versions are correct.

---

**Happy messaging!** 🚀 
