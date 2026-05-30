# HiddenPlace Backend Server

Node.js + Express server for uploading media to Google Drive and generating thumbnails.

## Features

- **File Upload**: Accept multipart file uploads from Android app.
- **Google Drive Integration**: Store files in shared Google Drive folder using service account.
- **Thumbnail Generation**: Generate video thumbnails with ffmpeg (1 sec frame, 360p).
- **Public URLs**: Make files publicly accessible and return URLs to client.
- **Message Feed**: Store and retrieve messages with metadata.

## Architecture

- **Framework**: Express.js
- **Storage**: Google Drive (via googleapis SDK + service account)
- **Thumbnails**: ffmpeg
- **DB**: SQLite (simple in-memory or disk-based for messages)

## Prerequisites

1. **Node.js** 14+ and npm
2. **ffmpeg** installed on server machine:
   ```bash
   # Ubuntu/Debian
   sudo apt-get install ffmpeg
   
   # macOS
   brew install ffmpeg
   ```
3. **Google Cloud Project** with Drive API enabled
4. **Service Account** JSON key with Drive API access
5. **Google Drive Folder** (shared with service account email)

## Setup

### 1. Create Google Cloud Project & Service Account

1. Go to [Google Cloud Console](https://console.cloud.google.com/).
2. Create a new project.
3. Enable **Google Drive API** (APIs & Services → Library → Drive API → Enable).
4. Create a **Service Account** (APIs & Services → Credentials → Create Credentials → Service Account).
5. Click the service account, go to **Keys** tab, create a new JSON key.
6. Save the JSON file as `service-account.json` in the `server/` directory.

### 2. Create Google Drive Folder

1. Go to [Google Drive](https://drive.google.com/).
2. Create a new folder, e.g., "HiddenPlace Channel".
3. Copy the folder ID from the URL: `https://drive.google.com/drive/folders/<FOLDER_ID>`
4. Right-click the folder → Share → add the service account email (found in JSON key under `client_email`) with **Editor** role.

### 3. Install Dependencies

```bash
cd server
npm install
```

### 4. Configure Environment

Create a `.env` file in the `server/` directory (or set env vars):

```
GOOGLE_APPLICATION_CREDENTIALS=./service-account.json
GDRIVE_FOLDER_ID=<your-google-drive-folder-id>
PORT=4000
```

Or pass them as env vars when running:

```bash
export GOOGLE_APPLICATION_CREDENTIALS=./service-account.json
export GDRIVE_FOLDER_ID=<your-folder-id>
export PORT=4000
npm start
```

### 5. Install ffmpeg

Ensure ffmpeg is available on PATH:

```bash
which ffmpeg  # should print path to ffmpeg
```

If not found, install:
- **Ubuntu/Debian**: `sudo apt-get install ffmpeg`
- **macOS**: `brew install ffmpeg`
- **Windows**: Download from [ffmpeg.org](https://ffmpeg.org/download.html)

## Running

```bash
cd server
npm start
```

Server will listen on `http://localhost:4000`.

## API Endpoints

### POST /upload

Upload a media file (image or video).

**Request**:
- Method: `POST`
- Content-Type: `multipart/form-data`
- Body: `media` field containing the file

**Response**:
```json
{
  "fileUrl": "https://drive.google.com/file/d/...",
  "thumbnail": "https://drive.google.com/file/d/..." // null for images on first run
}
```

**Example (curl)**:
```bash
curl -F "media=@/path/to/video.mp4" http://localhost:4000/upload
```

### GET /messages

Fetch all messages (optional; depends on DB implementation).

**Response**:
```json
[
  {
    "id": "123",
    "text": "Hello",
    "fileUrl": "https://...",
    "thumbnail": "https://...",
    "mediaType": "image",
    "timestamp": 1234567890
  }
]
```

### POST /messages

Post a new message (optional).

**Request Body**:
```json
{
  "text": "Message text",
  "fileUrl": "https://...",
  "thumbnail": "https://...",
  "mediaType": "image"
}
```

**Response**: Created message object.

## Project Structure

```
server/
├── package.json
├── index.js             # Express server + /upload endpoint
├── drive.js             # Google Drive upload & permission helpers
├── service-account.json # Service account key (add to .gitignore!)
├── .env                 # Environment variables (add to .gitignore!)
├── uploads/             # Temp upload dir (auto-created)
└── README.md
```

## Key Files

### `index.js`

Main Express server:
- POST `/upload` endpoint to accept file uploads.
- Generates ffmpeg thumbnail for videos.
- Calls Drive helpers to upload and make public.
- Returns file URL and thumbnail URL to client.

### `drive.js`

Google Drive integration:
- `uploadFileToDrive(filePath, filename, mimeType)`: Uploads file to shared Drive folder.
- `createPublicPermission(fileId)`: Makes file publicly readable.

## Performance & Limits

- **Google Drive Quotas**: Free tier ~15 GB per account; quota resets daily.
- **ffmpeg Processing**: ~1-5 sec per video depending on size; runs on single thread.
- **Upload Size**: Default multer limit is ~50 MB; adjust in `index.js` if needed.
- **Concurrent Uploads**: Node.js can handle ~1000 concurrent requests; ffmpeg is single-threaded (queue videos).

## Deployment

### Local Development

```bash
npm start
```

### Production (Heroku, AWS, etc.)

1. Ensure ffmpeg is available on the hosting platform (most platforms have it).
2. Set env vars via platform settings (not `.env` file).
3. Use a process manager like `pm2`:
   ```bash
   npm install -g pm2
   pm2 start index.js --name "hiddenplace"
   ```

### Docker (Optional)

```dockerfile
FROM node:18
RUN apt-get update && apt-get install -y ffmpeg
WORKDIR /app
COPY . .
RUN npm install
EXPOSE 4000
CMD ["npm", "start"]
```

## Troubleshooting

**"GOOGLE_APPLICATION_CREDENTIALS not found"**:
- Ensure `service-account.json` exists in `server/` directory or path is set correctly.

**"ffmpeg not found"**:
- Install ffmpeg and add to PATH.

**"Permission denied: service account email not shared to Drive folder"**:
- Get service account email from JSON key (`client_email`).
- Share the Drive folder with that email (Editor role).

**"Videos upload but no thumbnail is generated"**:
- Check backend logs for ffmpeg errors.
- Ensure ffmpeg is in PATH.
- Test manually: `ffmpeg -i <video.mp4> -ss 00:00:01 -vframes 1 -s 360x-1 thumb.png`

**Upload timeout**:
- Increase request timeout in Express:
  ```javascript
  app.use(express.json({ limit: '100mb' }));
  ```

## Next Steps

- Add database (SQLite or PostgreSQL) for persistent message storage.
- Implement user authentication and message ownership.
- Add message deletion/editing.
- Optimize ffmpeg: use workers or queue for parallel processing.
- Add CDN (Cloudflare, CloudFront) in front of Google Drive for faster delivery.
- Implement rate limiting and abuse prevention.

## Security Notes

- **Service Account Key**: Keep `service-account.json` secret. Add to `.gitignore`.
- **Public Files**: All uploaded files are public (accessible without authentication). Use authentication endpoint if private storage needed.
- **Input Validation**: Add checks for file type, size, and metadata.

## License

MIT
