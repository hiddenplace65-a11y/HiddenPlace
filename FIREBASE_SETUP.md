# Firebase Setup for HiddenPlace (No Credit Card Required)

Complete guide to set up HiddenPlace with Firebase (free tier).

## What You'll Use

- **Firebase Storage**: Free 1 GB for images/videos (pay-as-you-go, free tier never charges)
- **Firestore Database**: Free tier includes 1 GB storage, 50K reads/day
- **Firebase Hosting**: Free to deploy backend (optional)
- **NO credit card required** for free tier

---

## Step 1: Create Firebase Project (2 min)

### 1.1: Go to Firebase Console

1. Go to [Firebase Console](https://console.firebase.google.com)
2. Click **Create a project**

### 1.2: Create Project

- **Project name**: `HiddenPlace`
- Click **Create project**
- Click **Continue**

### 1.3: Disable Google Analytics (Optional)

- Uncheck **Enable Google Analytics for this project**
- Click **Create project**
- Wait for creation (~30 sec)

---

## Step 2: Set Up Firebase Services

### 2.1: Enable Firestore Database

1. In Firebase Console, click **Firestore Database** (left menu)
2. Click **Create database**
3. **Location**: Select closest region (e.g., `us-central1`)
4. **Security rules**: Select **Start in test mode** (allows reads/writes for testing)
5. Click **Create**

### 2.2: Enable Firebase Storage

1. Click **Storage** (left menu)
2. Click **Get started**
3. Location: Select closest region
4. Click **Done**

### 2.3: Create Web App (for backend authentication)

1. Click **Project Settings** (gear icon, top right)
2. Click **Service Accounts** tab
3. Click **Generate New Private Key**
4. A JSON file downloads — **Save this file**
5. Click **Generate Key** again

---

## Step 3: Deploy Backend to Render (5 min)

### 3.1: Update Backend Code

The backend now uses Firebase instead of Google Drive API.

Replace `server/index.js`:

```javascript
const express = require('express');
const multer = require('multer');
const path = require('path');
const fs = require('fs');
const admin = require('firebase-admin');

// Initialize Firebase with service account
const serviceAccount = JSON.parse(process.env.FIREBASE_KEY || '{}');
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  storageBucket: process.env.FIREBASE_STORAGE_BUCKET,
  databaseURL: process.env.FIREBASE_DATABASE_URL
});

const db = admin.firestore();
const storage = admin.storage().bucket();
const upload = multer({ dest: 'uploads/' });

const app = express();
app.use(express.json());

// Upload endpoint
app.post('/upload', upload.single('media'), async (req, res) => {
  try {
    const file = req.file;
    if (!file) return res.status(400).json({ error: 'No file' });

    const timestamp = Date.now();
    const filename = `${timestamp}-${file.originalname}`;
    const storagePath = `media/${filename}`;

    // Upload to Firebase Storage
    await storage.upload(file.path, {
      destination: storagePath,
      public: true,
      metadata: {
        contentType: file.mimetype
      }
    });

    // Get public URL
    const fileUrl = `https://storage.googleapis.com/${process.env.FIREBASE_STORAGE_BUCKET}/${storagePath}`;

    // For videos, use first frame as thumbnail (optional, client-side is easier)
    let thumbnail = fileUrl;
    if (file.mimetype.startsWith('video/')) {
      thumbnail = fileUrl; // Client can handle thumbnail
    }

    fs.unlinkSync(file.path);

    res.json({ fileUrl, thumbnail });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: String(err) });
  }
});

// Get messages
app.get('/messages', async (req, res) => {
  try {
    const snapshot = await db.collection('messages')
      .orderBy('timestamp', 'desc')
      .limit(50)
      .get();
    const messages = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
    res.json(messages);
  } catch (err) {
    res.status(500).json({ error: String(err) });
  }
});

// Post message
app.post('/messages', async (req, res) => {
  try {
    const { text, fileUrl, thumbnail, mediaType } = req.body;
    const message = {
      text: text || '',
      fileUrl: fileUrl || null,
      thumbnail: thumbnail || null,
      mediaType: mediaType || null,
      timestamp: admin.firestore.FieldValue.serverTimestamp()
    };
    const docRef = await db.collection('messages').add(message);
    res.json({ id: docRef.id, ...message });
  } catch (err) {
    res.status(500).json({ error: String(err) });
  }
});

const PORT = process.env.PORT || 4000;
app.listen(PORT, () => console.log(`Server listening on ${PORT}`));
```

Replace `server/package.json`:

```json
{
  "name": "hiddenplace-firebase-backend",
  "version": "0.1.0",
  "main": "index.js",
  "scripts": {
    "start": "node index.js"
  },
  "dependencies": {
    "express": "^4.18.2",
    "multer": "^1.4.5-lts.1",
    "firebase-admin": "^11.10.0",
    "body-parser": "^1.20.2"
  }
}
```

### 3.2: Get Firebase Credentials

1. Go to Firebase Console → **Project Settings** (gear icon)
2. Click **Service Accounts** tab
3. Click **Generate New Private Key** → JSON file downloads
4. Open the file with a text editor and **copy the entire contents**

### 3.3: Deploy on Render

1. Go to [Render.com](https://render.com)
2. Sign up (free, use GitHub)
3. Click **New** → **Web Service**
4. Select your GitHub repo with the updated code
5. Fill in:
   - **Name**: `hiddenplace-firebase`
   - **Root Directory**: `server`
   - **Environment**: `Node`
   - **Build**: `npm install`
   - **Start**: `npm start`
6. Click **Advanced**
7. Add environment variables:
   - **FIREBASE_KEY**: Paste entire JSON key content
   - **FIREBASE_STORAGE_BUCKET**: Your bucket ID (from Firebase Console → Storage)
   - **FIREBASE_DATABASE_URL**: Your database URL (from Firebase Console → Firestore)
   - **PORT**: `4000`

8. Click **Create Web Service**
9. Wait for deployment (2-3 min)
10. Copy the Render URL (e.g., `https://hiddenplace-firebase.onrender.com`)

---

## Step 4: Update Android App

Update `android/app/src/main/java/com/hiddenplace/Config.kt`:

```kotlin
package com.hiddenplace

object Config {
    const val BACKEND_BASE_URL = "https://YOUR_RENDER_URL.onrender.com/"
}
```

---

## Step 5: Push & Build

```bash
cd /workspaces/HiddenPlace
git add .
git commit -m "Add Firebase backend"
git push origin main
```

GitHub Actions auto-builds APK → Download from **Actions** tab → Install on phone

---

## Your Firebase Credentials

After setup, you'll have:

| Item | Where to Find |
|------|---------------|
| **Storage Bucket** | Firebase Console → Storage |
| **Database URL** | Firebase Console → Firestore → Settings |
| **Service Account Key** | Firebase Console → Project Settings → Service Accounts |

---

## Free Tier Limits

- **Storage**: 1 GB free per month (resets), then pay $0.18/GB
- **Firestore**: 1 GB storage, 50K reads/day free
- **Bandwidth**: First 1 GB/month free egress

For a small channel (< 100 users), you'll stay in free tier forever.

---

## Troubleshooting

**"FIREBASE_KEY is invalid"**
- Make sure you copied the ENTIRE JSON file contents, not just a key name

**"Bucket not found"**
- Go to Firebase Console → Storage, copy the bucket ID (e.g., `hiddenplace-abc123.appspot.com`)

**"Firestore connection error"**
- Make sure Firestore is created in Firebase Console

---

**You're ready! Push to GitHub and download the APK from GitHub Actions.** 🚀
