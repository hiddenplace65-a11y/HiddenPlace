# Firebase Backend Setup (No Credit Card!) — Quick Summary

You chose **Firebase** ✅ — perfect for no credit card!

---

## Your Next Steps

### 1️⃣ **Firebase Setup** (2 min)

Go to [Firebase Console](https://console.firebase.google.com):

1. **Create Project**: `HiddenPlace`
2. **Enable Firestore**: Database → Create
3. **Enable Storage**: Storage → Get Started
4. **Get Service Key**: Project Settings → Service Accounts → Generate Key (JSON file downloads)

**Save the JSON file — you'll paste it into Render next.**

---

### 2️⃣ **Deploy Backend** (5 min)

Go to [Render.com](https://render.com) (free account):

1. Click **New** → **Web Service**
2. Select your `HiddenPlace` GitHub repo
3. **Name**: `hiddenplace-firebase`
4. **Root Directory**: `server`
5. **Environment**: Node
6. **Build Command**: `npm install`
7. **Start Command**: `npm start`
8. Click **Advanced** → Add Environment Variables:
   - `FIREBASE_KEY`: Paste **entire JSON file contents** (not just a key name!)
   - `FIREBASE_STORAGE_BUCKET`: Your bucket (from Firebase Console → Storage)
   - `FIREBASE_DATABASE_URL`: Your database URL (from Firebase Console → Firestore → Settings)
9. Click **Create**
10. Wait for deployment (2-3 min) → You get a URL like `https://hiddenplace-firebase-xyz.onrender.com`

**Copy this URL — you need it next.**

---

### 3️⃣ **Update Android App** (1 min)

Edit this file on GitHub:
- `android/app/src/main/java/com/hiddenplace/Config.kt`

Change:
```kotlin
const val BACKEND_BASE_URL = "https://YOUR_BACKEND_URL.onrender.com/"
```

To (paste your Render URL):
```kotlin
const val BACKEND_BASE_URL = "https://hiddenplace-firebase-xyz.onrender.com/"
```

Click **Commit changes** → Done!

---

### 4️⃣ **Auto-Build APK** (2 min)

After you commit:

1. Go to GitHub → Your repo
2. Click **Actions** tab
3. Wait for green checkmark (1-2 min)
4. Click the workflow → Scroll down to **Artifacts**
5. Click **app-debug-apk** → APK downloads

---

### 5️⃣ **Install on Phone** (2 min)

1. Download APK to your phone
2. Open file → Click **Install**
3. Done! 🎉

---

## Quick Links

- 📘 **Full Guide**: [FIREBASE_SETUP.md](./FIREBASE_SETUP.md) ← Read if you get stuck
- 🔐 **Firebase Console**: https://console.firebase.google.com
- 🚀 **Render Dashboard**: https://render.com
- 📱 **GitHub Actions**: Your repo → Actions tab

---

## Free Tier (You get this)

✅ 1 GB storage/month (resets)  
✅ 1 GB Firestore database  
✅ 50K reads/day  
✅ Render free tier  
✅ **NO credit card ever charged**

---

**You're ready! Commit and push your code now.** 🚀
