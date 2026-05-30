package com.hiddenplace

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import androidx.activity.ComponentActivity
import androidx.compose.ui.window.Dialog
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import android.view.View
import androidx.compose.ui.viewinterop.AndroidView
import java.io.File

class MessageViewModel : ViewModel() {
    private val apiService = ApiService.create(Config.BACKEND_BASE_URL)

    var messages by mutableStateOf<List<Message>>(emptyList())
    var messageText by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var selectedImageUri by mutableStateOf<Uri?>(null)

    init {
        loadMessages()
    }

    fun loadMessages() {
        viewModelScope.launch {
            try {
                messages = apiService.getMessages()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun uploadMedia(context: Context, uri: Uri, mediaType: String) {
        viewModelScope.launch {
            try {
                isLoading = true
                val file = getRealPathFromURI(context, uri)?.let { File(it) } ?: return@launch
                val mimeType = if (mediaType == "video") "video/mp4" else "image/jpeg"
                val requestBody = file.asRequestBody(mimeType.toMediaType())
                val part = MultipartBody.Part.createFormData("media", file.name, requestBody)
                val uploadResponse = apiService.uploadMedia(part)

                // Create message with file URL and thumbnail
                val newMessage = Message(
                    id = System.currentTimeMillis().toString(),
                    text = messageText.ifEmpty { mediaType },
                    fileUrl = uploadResponse.fileUrl,
                    thumbnail = uploadResponse.thumbnail,
                    mediaType = mediaType
                )
                apiService.postMessage(newMessage)
                messageText = ""
                selectedImageUri = null
                loadMessages()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun sendMessage() {
        viewModelScope.launch {
            try {
                if (messageText.isBlank()) return@launch
                val newMessage = Message(
                    id = System.currentTimeMillis().toString(),
                    text = messageText
                )
                apiService.postMessage(newMessage)
                messageText = ""
                loadMessages()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getRealPathFromURI(context: Context, uri: Uri): String? {
        return if (uri.scheme == "file") {
            uri.path
        } else {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                cursor.moveToFirst()
                val columnIndex = cursor.getColumnIndex("_data")
                if (columnIndex > -1) cursor.getString(columnIndex) else null
            }
        }
    }
}

@Composable
fun MessageListScreen(viewModel: MessageViewModel) {
    val context = LocalContext.current
    var showImagePicker by remember { mutableStateOf(false) }
    var showVideoPicker by remember { mutableStateOf(false) }
    var selectedMediaUri by remember { mutableStateOf<Uri?>(null) }
    var selectedMediaType by remember { mutableStateOf("") }
    var showFullImage by remember { mutableStateOf(false) }
    var fullImageUrl by remember { mutableStateOf("") }
    var showVideoPlayer by remember { mutableStateOf(false) }
    var videoPlayerUrl by remember { mutableStateOf("") }

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedMediaUri = uri
            selectedMediaType = "image"
        }
    }

    val videoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedMediaUri = uri
            selectedMediaType = "video"
        }
    }

    LaunchedEffect(selectedMediaUri) {
        if (selectedMediaUri != null && selectedMediaType.isNotEmpty()) {
            viewModel.uploadMedia(context, selectedMediaUri!!, selectedMediaType)
            selectedMediaUri = null
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // Header
        Text(
            text = "HiddenPlace Channel",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            color = Color.Black
        )
        Divider()

        // Message Feed
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(8.dp),
            reverseLayout = true
        ) {
            items(viewModel.messages.reversed()) { message ->
                MessageItemCard(
                    message = message,
                    onImageClick = { url ->
                        fullImageUrl = url
                        showFullImage = true
                    },
                    onVideoClick = { url ->
                        videoPlayerUrl = url
                        showVideoPlayer = true
                    }
                )
            }
        }

        Divider()

        // Message Composer
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = viewModel.messageText,
                    onValueChange = { viewModel.messageText = it },
                    placeholder = { Text("Type message...") },
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFFEEEEEE), RoundedCornerShape(24.dp))
                        .padding(8.dp),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFEEEEEE),
                        unfocusedContainerColor = Color(0xFFEEEEEE)
                    )
                )

                IconButton(onClick = { imageLauncher.launch("image/*") }) {
                    Icon(Icons.Default.AttachFile, contentDescription = "Attach Image")
                }

                IconButton(onClick = { viewModel.sendMessage() }) {
                    Icon(Icons.Default.Send, contentDescription = "Send")
                }
            }

            Row(modifier = Modifier.padding(top = 8.dp)) {
                Button(
                    onClick = { imageLauncher.launch("image/*") },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("📸 Image")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { videoLauncher.launch("video/*") },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("🎥 Video")
                }
            }
        }
    }

    // Full Image Viewer Dialog
    if (showFullImage) {
        Dialog(
            onDismissRequest = { showFullImage = false }
        ) {
            AsyncImage(
                model = fullImageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { showFullImage = false },
                contentScale = ContentScale.Fit
            )
        }
    }

    // Video Player Dialog
    if (showVideoPlayer) {
        Dialog(onDismissRequest = { showVideoPlayer = false }) {
            VideoPlayerView(
                url = videoPlayerUrl,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(300.dp)
            )
        }
    }
}

@Composable
fun MessageItemCard(
    message: Message,
    onImageClick: (String) -> Unit,
    onVideoClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Text content
            if (message.text.isNotBlank()) {
                Text(
                    text = message.text,
                    fontSize = 14.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Thumbnail or Media
            if (message.mediaType == "image" && message.thumbnail != null) {
                AsyncImage(
                    model = message.thumbnail,
                    contentDescription = "Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onImageClick(message.fileUrl ?: message.thumbnail!!) },
                    contentScale = ContentScale.Crop
                )
            } else if (message.mediaType == "video" && message.thumbnail != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black)
                        .clickable { onVideoClick(message.fileUrl!!) },
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = message.thumbnail,
                        contentDescription = "Video Thumbnail",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Icon(
                        imageVector = androidx.compose.material.icons.filled.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.size(64.dp)
                    )
                }
            }

            // Timestamp
            Text(
                text = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                    .format(message.timestamp),
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun VideoPlayerView(url: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val exoPlayer = remember { ExoPlayer.Builder(context).build() }

    LaunchedEffect(url) {
        exoPlayer.setMediaItem(androidx.media3.common.MediaItem.fromUri(url))
        exoPlayer.prepare()
        exoPlayer.play()
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = true
            }
        },
        modifier = modifier
    )
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val viewModel = remember { MessageViewModel() }
                MessageListScreen(viewModel = viewModel)
            }
        }
    }
}
