const express = require('express');
const multer = require('multer');
const path = require('path');
const fs = require('fs');
const admin = require('firebase-admin');

// Initialize Firebase with service account from environment variable
const serviceAccountKey = process.env.FIREBASE_KEY;
if (!serviceAccountKey) {
  console.error('ERROR: FIREBASE_KEY environment variable not set');
  process.exit(1);
}

let serviceAccount;
try {
  serviceAccount = JSON.parse(serviceAccountKey);
} catch (e) {
  console.error('ERROR: Invalid FIREBASE_KEY JSON:', e.message);
  process.exit(1);
}

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

// Health check endpoint
app.get('/health', (req, res) => {
  res.json({ status: 'ok' });
});

// Upload endpoint
app.post('/upload', upload.single('media'), async (req, res) => {
  try {
    const file = req.file;
    if (!file) {
      return res.status(400).json({ error: 'No file provided' });
    }

    const timestamp = Date.now();
    const filename = `${timestamp}-${file.originalname}`;
    const storagePath = `media/${filename}`;

    console.log(`Uploading file: ${filename} (${file.mimetype})`);

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
    let thumbnail = fileUrl;

    // For videos, use the same URL (client can handle thumbnail extraction if needed)
    if (file.mimetype.startsWith('video/')) {
      console.log(`Video uploaded: ${fileUrl}`);
    }

    // Clean up temp file
    fs.unlinkSync(file.path);

    console.log(`Upload successful: ${fileUrl}`);
    res.json({ fileUrl, thumbnail });
  } catch (err) {
    console.error('Upload error:', err);
    res.status(500).json({ error: err.message });
  }
});

// Get messages
app.get('/messages', async (req, res) => {
  try {
    const snapshot = await db.collection('messages')
      .orderBy('timestamp', 'desc')
      .limit(100)
      .get();
    
    const messages = snapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data(),
      timestamp: doc.data().timestamp?.toMillis?.() || Date.now()
    }));
    
    res.json(messages);
  } catch (err) {
    console.error('Get messages error:', err);
    res.status(500).json({ error: err.message });
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
    
    res.json({ 
      id: docRef.id, 
      ...message,
      timestamp: Date.now()
    });
  } catch (err) {
    console.error('Post message error:', err);
    res.status(500).json({ error: err.message });
  }
});

// Delete message (optional)
app.delete('/messages/:id', async (req, res) => {
  try {
    await db.collection('messages').doc(req.params.id).delete();
    res.json({ success: true });
  } catch (err) {
    console.error('Delete message error:', err);
    res.status(500).json({ error: err.message });
  }
});

const PORT = process.env.PORT || 4000;
app.listen(PORT, () => {
  console.log(`HiddenPlace Firebase Backend running on port ${PORT}`);
  console.log(`Storage Bucket: ${process.env.FIREBASE_STORAGE_BUCKET}`);
});
