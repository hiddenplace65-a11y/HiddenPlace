# Deployment Guide — HiddenPlace Mobile App (Option 2)

Complete step-by-step guide to deploy backend on the cloud and build the Android app without a PC.

## Overview

1. **Backend**: Deploy to Render.com (free, runs 24/7)
2. **APK Build**: GitHub Actions auto-builds when you push code
3. **App**: Download APK from GitHub, install on phone

Total setup time: ~15 minutes

---

## Part 1: Google Cloud Setup (5 min)

### Step 1.1: Create Google Cloud Project

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Click **Create Project**
   - Name: `HiddenPlace`
   - Click **Create**
3. Wait for project to be created (1-2 min)

### Step 1.2: Enable Google Drive API

1. In the left menu, click **APIs & Services** → **Library**
2. Search for `Google Drive API`
3. Click the result, then click **Enable**

### Step 1.3: Create Service Account

1. Go to **APIs & Services** → **Credentials** (left menu)
2. Click **Create Credentials** → **Service Account**
3. Fill in:
   - Service account name: `hiddenplace-uploader`
   - Click **Create and Continue**
4. Click **Continue** (skip optional steps)
5. Click **Create Key** → **JSON** → A file downloads automatically

**Save this JSON file** — you'll need it in the next step.

---

## Part 2: Google Drive Setup (3 min)

### Step 2.1: Create Shared Folder

1. Go to [Google Drive](https://drive.google.com)
2. Click **New** → **Folder**
3. Name: `HiddenPlace Channel`
4. Copy the folder ID from the URL:
   ```
   https://drive.google.com/drive/folders/FOLDER_ID_HERE
   ```
   (Save this ID)

### Step 2.2: Share Folder with Service Account

1. Open the JSON file you downloaded (use Notepad/text editor)
2. Find the line with `"client_email": "..."` and copy the email
3. Go back to Google Drive, right-click **HiddenPlace Channel** folder
4. Click **Share**
5. Paste the email, select **Editor** role, click **Share**
6. Click **Share** in the popup (ignore warnings)

---

## Part 3: Deploy Backend to Render (5 min)

### Step 3.1: Prepare Server Code

1. Upload the `server/` folder code to GitHub:
   - Push your code: `git add . && git commit -m "Add server code" && git push`
   - Or upload manually on GitHub.com

### Step 3.2: Create Render Account

1. Go to [Render.com](https://render.com)
2. Sign up (free, use GitHub login recommended)
3. Click **New** → **Web Service**

### Step 3.3: Deploy Backend

1. On the **Create Web Service** page:
   - **GitHub Repo**: Connect your GitHub account, select `HiddenPlace` repo
   - **Name**: `hiddenplace-backend`
   - **Root Directory**: `server`
   - **Environment**: `Node`
   - **Build Command**: `npm install`
   - **Start Command**: `npm start`

2. Scroll down, click **Advanced**:
   - **Environment Variables**: Click **Add Environment Variable**
   - Key: `GOOGLE_APPLICATION_CREDENTIALS`
   - Value: Paste the entire JSON content from the JSON key file (open with text editor, copy all)
   - Click **Add Environment Variable** again
   - Key: `GDRIVE_FOLDER_ID`
   - Value: Paste the folder ID you saved
   - Key: `PORT`
   - Value: `4000`

3. Click **Create Web Service**
4. Wait for deployment (2-3 min)
5. You'll get a URL like: `https://hiddenplace-backend.onrender.com`

**Save this URL** — you need it for the Android app.

---

## Part 4: Update Android App (2 min)

### Step 4.1: Update Backend URL

1. Open GitHub on your phone or computer
2. Go to your `HiddenPlace` repository
3. Navigate to `android/app/src/main/java/com/hiddenplace/Config.kt`
4. Click **Edit** (pencil icon)
5. Replace:
   ```kotlin
   const val BACKEND_BASE_URL = "https://YOUR_BACKEND_URL.onrender.com/"
   ```
   with your actual Render URL, e.g.:
   ```kotlin
   const val BACKEND_BASE_URL = "https://hiddenplace-backend.onrender.com/"
   ```
6. Click **Commit changes** → **Commit**

---

## Part 5: Build APK with GitHub Actions (2 min)

### Step 5.1: Trigger Build

After you committed the Config change in Step 4.1, GitHub Actions automatically starts building.

1. Go to your repo on GitHub
2. Click the **Actions** tab (top menu)
3. You'll see a workflow running (green circle with spinning icon)
4. Wait for it to complete (1-2 min) — turns into a green checkmark

### Step 5.2: Download APK

1. Click the completed workflow
2. Scroll down to **Artifacts**
3. Click **app-debug-apk** → APK downloads to your phone

---

## Part 6: Install on Phone (2 min)

### Step 6.1: Allow Installation from Unknown Sources

1. On your phone, go to **Settings** → **Security** (or **Apps & notifications**)
2. Find **Install unknown apps** or **Unknown sources**
3. Enable it for your browser or file manager

### Step 6.2: Install APK

1. Find the downloaded `app-debug-apk.zip` file in your Downloads
2. Extract the `.zip` (if needed)
3. Open `app-debug.apk` file
4. Click **Install**
5. Wait for installation to complete
6. Click **Open** to launch the app

---

## Part 7: Use the App

1. **App Opens**: You see "HiddenPlace Channel"
2. **Type a Message**: Type text in the input field
3. **Send**: Tap the **Send** button (arrow icon)
4. **Upload Image**: Tap **📸 Image**, pick a photo from gallery
5. **Upload Video**: Tap **🎥 Video**, pick a video from gallery
6. **View Thumbnail**: Tap any thumbnail to see full image or play video

---

## Troubleshooting

### APK Download Fails

**Problem**: No artifact in GitHub Actions
- **Solution**: Check that you committed the Config.kt change. If the build failed, click the workflow to see logs.

### App Opens But Can't Upload

**Problem**: Error when tapping upload button
- **Solution**: Check Render URL is correct in Config.kt
  - Go to Render.com dashboard, find `hiddenplace-backend`, copy the URL
  - Update Config.kt again and push

### Video Upload Succeeds, No Thumbnail

**Problem**: ffmpeg not working
- **Solution**: Check Render backend logs
  - Go to Render.com → `hiddenplace-backend` → **Logs** tab
  - Look for ffmpeg errors
  - Note: Render has ffmpeg pre-installed, so it should work

### Drive Folder Not Found

**Problem**: "Permission denied" or "Folder not found"
- **Solution**:
  1. Verify service account email is shared to the folder (Editor role)
  2. Check GDRIVE_FOLDER_ID is correct in Render env vars
  3. Retry the upload

### "Storage quota exceeded"

**Problem**: Google Drive is full
- **Solution**: You have 15 GB free. Delete old files from the folder or use a new folder ID.

---

## Next Steps (Optional Enhancements)

After you get the basic app working, you can add:

1. **User Authentication**: Sign in with Google/email
2. **Message Search**: Find old messages
3. **Emoji Reactions**: React to messages
4. **Offline Mode**: Cache messages locally
5. **Push Notifications**: Get alerts for new messages
6. **Admin Panel**: Web dashboard to manage channel

---

## Summary

| Step | Time | What You Do |
|------|------|-----------|
| 1 | 5 min | Create Google Cloud project + service account |
| 2 | 3 min | Create Google Drive folder, share with service account |
| 3 | 5 min | Deploy backend to Render, set env vars |
| 4 | 2 min | Update Android Config.kt with Render URL, commit |
| 5 | 2 min | GitHub Actions auto-builds, download APK |
| 6 | 2 min | Install APK on phone |
| 7 | 1 min | Open app, start messaging! |
| **Total** | **~20 min** | **Live app on your phone!** |

---

## Support

If stuck:
1. Check the [server README](./server/README.md) for backend issues
2. Check the [Android README](./android/README.md) for app issues
3. Review Render logs: Render.com → App → Logs
4. Review GitHub Actions logs: GitHub → Actions → Workflow → Logs

**Enjoy your Telegram-like channel app!** 🚀
