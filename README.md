# HiddenPlace — Telegram-like Channel App

A free, self-hosted channel app for Android with image/video uploads to **Firebase** (no credit card required), thumbnails, and a Telegram-like messaging feed.

## Overview

- **Frontend**: Android app (Kotlin + Jetpack Compose)
- **Backend**: Node.js + Express server
- **Storage**: Firebase Storage (free, 1 GB/month)
- **Database**: Firestore (free tier, no credit card needed)
- **Deploy**: Render.com (free hosting, no credit card)

## ⚡ Quick Start (No Credit Card Required)

### Prerequisites
1. GitHub account (free)
2. Render account (free, via GitHub)
3. Firebase account (free, **no credit card needed**)

### 3-Step Setup

**1. Create Firebase Project (2 min)**
- Go to [Firebase Console](https://console.firebase.google.com)
- Create project → Enable Firestore + Storage → Download service account key

**2. Deploy Backend (5 min)**
- Go to [Render.com](https://render.com)
- Deploy `server/` folder
- Set Firebase env vars (key, bucket, database URL)
- Get Render URL

**3. Build & Install (2 min)**
- Update `Config.kt` with Render URL
- Push to GitHub → GitHub Actions auto-builds APK
- Download APK → Install on phone

**Total: ~10 minutes** ⏱️

---

## 📖 Full Documentation

| Document | Purpose |
|----------|---------|
| **[FIREBASE_SETUP.md](./FIREBASE_SETUP.md)** | ⭐ **START HERE** — Step-by-step Firebase + Render setup (no credit card) |
| [DEPLOYMENT.md](./DEPLOYMENT.md) | Google Drive + Render setup (requires credit card for Google Cloud) |
| [android/README.md](./android/README.md) | Android app build & troubleshooting |
| [server/README.md](./server/README.md) | Backend API reference |

---

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
│   │   │   ├── ApiService.kt        # Retrofit client
│   │   │   └── Config.kt            # Backend URL config
│   │   └── res/                     # Resources
│   ├── build.gradle
│   ├── settings.gradle
│   └── README.md
│
├── server/                     # Node.js + Firebase backend
│   ├── index.js               # Express server + /upload endpoint
│   ├── package.json
│   └── README.md
│
├── .github/workflows/
│   └── build.yml              # GitHub Actions auto-build APK
│
├── FIREBASE_SETUP.md          # ⭐ Firebase setup (no credit card)
├── DEPLOYMENT.md              # Google Drive setup (alternative)
└── README.md                  # This file
```

## Features

### Android App
- **Telegram UI**: Clean message feed with card layout
- **Image Upload**: Pick → upload → show thumbnail
- **Video Upload**: Pick → upload → auto-thumbnail
- **Media Viewer**: Tap thumbnail → full-screen image or video
- **Message Composer**: Text + media in one interface
- **Real-time Feed**: Auto-refresh messages

### Backend
- **Firebase Storage**: Free 1 GB/month media storage
- **Firestore DB**: Free database for messages
- **Multipart Upload**: Accept images and videos
- **REST API**: `/upload`, `/messages` endpoints
- **No ffmpeg needed**: Works on free Render plan

---

## Which Setup?

### ✅ **Firebase (Recommended)**
- ✨ **No credit card required**
- 1 GB free storage/month
- Free Firestore database
- Super easy setup
- **→ Use [FIREBASE_SETUP.md](./FIREBASE_SETUP.md)**

### ⚠️ Google Drive
- Requires Google Cloud credit card
- 15 GB free per account
- More storage but complex setup
- **→ Use [DEPLOYMENT.md](./DEPLOYMENT.md) if you have credit card**

---

## Usage

1. **Install APK** on Android phone
2. **Type message** → Tap Send
3. **Tap 📸 Image** or **🎥 Video** to upload
4. **See thumbnail** in feed
5. **Tap thumbnail** to view full media

---

## Cost Breakdown

| Service | Free Tier | Cost |
|---------|-----------|------|
| **Firebase Storage** | 1 GB/month | $0.18/GB after |
| **Firestore Database** | 1 GB + 50K reads/day | $0.06/100K reads after |
| **Render Backend** | Free tier included | Pay-as-you-go after |
| **GitHub Actions** | 2,000 min/month free | Usually free |
| **Total** | **Completely Free** | ✅ |

For a small channel (< 100 users), **you'll stay free forever**.

---

## Troubleshooting

### Setup Issues
1. **Can't access Google Cloud**: Use Firebase instead (see FIREBASE_SETUP.md)
2. **APK download fails**: Check GitHub Actions tab for build logs
3. **App won't connect**: Verify Render URL in Config.kt

### Runtime Issues
1. **Upload fails**: Check Render backend logs on Render.com dashboard
2. **No thumbnail**: Firebase displays full URL; client can extract later
3. **Firestore quota**: Free tier resets daily; upgrade if needed

See [FIREBASE_SETUP.md](./FIREBASE_SETUP.md) for more help.

---

## Next Steps (Optional)

1. **User Auth**: Add sign-in with Google
2. **Search**: Full-text search messages
3. **Reactions**: Emoji reactions to messages
4. **Offline**: Cache messages locally
5. **Compression**: Client-side image/video compression
6. **CDN**: Add Cloudflare in front

---

## Tech Stack

- **Android**: Kotlin, Jetpack Compose, Retrofit, ExoPlayer, Coil
- **Backend**: Node.js, Express, Firebase Admin SDK
- **Database**: Firestore (NoSQL)
- **Storage**: Firebase Storage (Google Cloud)
- **Hosting**: Render.com (free tier)
- **CI/CD**: GitHub Actions

---

## Support

1. **Firebase not working?** → Check [FIREBASE_SETUP.md](./FIREBASE_SETUP.md)
2. **Android app issues?** → Check [android/README.md](./android/README.md)
3. **Backend problems?** → Check [server/README.md](./server/README.md)
4. **Still stuck?** → Check Render/Firebase logs

---

## License

MIT — Use freely for personal or commercial projects

---

**Ready?** → [**→ Start with FIREBASE_SETUP.md**](./FIREBASE_SETUP.md) ⭐ 
