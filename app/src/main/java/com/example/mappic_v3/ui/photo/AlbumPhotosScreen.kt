package com.example.mappic_v3.ui.photo

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.DpOffset
import com.example.mappic_v3.data.model.Photo

@Composable
fun AlbumPhotosScreen(
    modifier: Modifier,
    albumId: Int,
    onBack: () -> Unit
) {
    BackHandler {
        onBack()
    }
    val context = LocalContext.current
    val viewModel = remember { PhotoViewModel(albumId) }
    val photos by viewModel.photos.collectAsState()

    var selectedPhoto by remember { mutableStateOf<Photo?>(null) }
    var showMenu by remember { mutableStateOf(false) }
    var pressOffset by remember { mutableStateOf(Offset.Zero) }
    var selectionMode by remember { mutableStateOf(false) }
    var selectedPhotos by remember { mutableStateOf<Set<Int>>(emptySet()) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) {
            viewModel.uploadPhotos(
                context = context,
                uris = uris,
                uploaderId = 1,
                description = null
            )
        }
    }

    Column(
        modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (selectionMode) {
            Button(onClick = {
                viewModel.deletePhotos(selectedPhotos.toList())
                selectedPhotos = emptySet()
                selectionMode = false
            }) {
                Text("Eliminar (${selectedPhotos.size})")
            }
        }


        Text("Fotos del álbum", fontSize = 22.sp)

        Spacer(Modifier.height(12.dp))

        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Text("Subir foto")
        }

        Spacer(Modifier.height(16.dp))

        if (photos.isEmpty()) {
            Text(
                text = "No hay fotos en este álbum",
                fontSize = 18.sp
            )
        } else {

            Box(Modifier.fillMaxSize()) {

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = photos,
                        key = { it.id }
                    ) { photo ->

                        val isSelected = selectedPhotos.contains(photo.id)

                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .fillMaxWidth()
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onLongPress = {
                                            selectionMode = true
                                            selectedPhotos = selectedPhotos + photo.id
                                        },
                                        onTap = {
                                            if (selectionMode) {
                                                selectedPhotos =
                                                    if (isSelected)
                                                        selectedPhotos - photo.id
                                                    else
                                                        selectedPhotos + photo.id

                                                if (selectedPhotos.isEmpty()) {
                                                    selectionMode = false
                                                }
                                            }
                                        }
                                    )
                                }
                        ) {

                            AsyncImage(
                                model = photo.url,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize()
                            )

                            if (isSelected) {
                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.4f))
                                )
                            }
                        }
                    }
                }


                if (showMenu && selectedPhoto != null) {
                    DropdownMenu(
                        expanded = true,
                        onDismissRequest = {
                            showMenu = false
                            selectedPhoto = null
                        },
                        offset = DpOffset(pressOffset.x.dp, pressOffset.y.dp)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Eliminar foto") },
                            onClick = {
                                viewModel.deletePhoto(selectedPhoto!!.id)
                                showMenu = false
                                selectedPhoto = null
                            }
                        )
                    }
                }
            }
        }
    }
}
