package com.example.mappic_v3.ui.photo
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.mappic_v3.data.model.Photo


@Composable
fun AlbumPhotosScreen(
    userRole: String,
    albumId: Int,
    albumTitle: String,
    albumDescription: String,
    uploaderId: Int,
    albumOwnerId: Int,
    modifier: Modifier,
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    val context = LocalContext.current
    val viewModel = remember { PhotoViewModel(albumId) }
    val photos by viewModel.photos.collectAsState()
    var selectionMode by remember { mutableStateOf(false) }
    var selectedPhotos by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var viewerOpen by remember { mutableStateOf(false) }
    var startIndex by remember { mutableStateOf(0) }
    val isOwner = uploaderId == albumOwnerId
    val canUpload = userRole == "owner" || userRole == "editor" || isOwner

    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) {
            uris.forEach { uri ->
                try {
                    context.contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (e: Exception) {

                }
            }

            viewModel.uploadPhotos(
                context = context,
                uris = uris,
                uploaderId = uploaderId,
                description = null
            )
        }
    }

    Column(
        modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {


        Text(albumTitle, style = MaterialTheme.typography.headlineLarge)

        if (albumDescription.isNotEmpty()) {
            Text(
                text = albumDescription,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

        }

        Spacer(Modifier.height(12.dp))


        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

            if (userRole == "owner" || userRole == "editor" || uploaderId == albumOwnerId) {
                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Text("Añadir Fotos")
                }
            } else {
                Text("Modo lectura", style = MaterialTheme.typography.bodySmall)
            }

            if (selectionMode) {
                OutlinedButton(
                    onClick = {
                        selectedPhotos =
                            if (selectedPhotos.size == photos.size)
                                emptySet()
                            else
                                photos.map { it.id }.toSet()
                        if (selectedPhotos.isEmpty()) {
                            selectionMode = false
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        if (selectedPhotos.size == photos.size)
                            "Deseleccionar todo"
                        else
                            "Seleccionar todo"
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        if (selectionMode) {
            Button(
                onClick = {
                    viewModel.deletePhotos(selectedPhotos.toList())
                    selectedPhotos = emptySet()
                    selectionMode = false
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Eliminar (${selectedPhotos.size})")
            }

            Spacer(Modifier.height(12.dp))
        }

        if (photos.isEmpty()) {
            Text(
                "No hay fotos en este álbum",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            return
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(6.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(
                items = photos,
                key = { it.id }
            ) { photo ->

                val isSelected = photo.id in selectedPhotos

                Card(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .pointerInput(selectionMode, selectedPhotos) {
                            detectTapGestures(
                                onLongPress = {
                                    selectionMode = true
                                    selectedPhotos = setOf(photo.id)
                                },
                                onTap = {
                                    if (selectionMode) {
                                        selectedPhotos =
                                            if (photo.id in selectedPhotos)
                                                selectedPhotos - photo.id
                                            else
                                                selectedPhotos + photo.id

                                        if (selectedPhotos.isEmpty()) {
                                            selectionMode = false
                                        }
                                    } else {
                                        viewerOpen = true
                                        startIndex = photos.indexOf(photo)
                                    }
                                }
                            )
                        },
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Box {

                        AsyncImage(
                            model = photo.url,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        if (photo.id in selectedPhotos) {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }

            }
        }
    }


    if (viewerOpen) {
        PhotoViewer(
            photos = photos,
            startIndex = startIndex,
            onDismiss = { viewerOpen = false }
        )
    }
}
@Composable
fun PhotoViewer(
    photos: List<Photo>,
    startIndex: Int,
    onDismiss: () -> Unit
) {
    val pagerState = rememberPagerState(
        initialPage = startIndex,
        pageCount = { photos.size }
    )

    var dragOffset by remember { mutableStateOf(0f) }

    BackHandler { onDismiss() }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onVerticalDrag = { _, dragAmount ->
                            dragOffset += dragAmount
                        },
                        onDragEnd = {
                            if (kotlin.math.abs(dragOffset) > 200f) {
                                onDismiss()
                            }
                            dragOffset = 0f
                        }
                    )
                }
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                AsyncImage(
                    model = photos[page].url,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

