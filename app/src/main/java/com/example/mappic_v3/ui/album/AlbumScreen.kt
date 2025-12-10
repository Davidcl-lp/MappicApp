package com.example.mappic_v3.ui.album

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import com.example.mappic_v3.data.model.Album
import com.example.mappic_v3.ui.components.SortBar

@Composable
fun AlbumScreen(
    viewModel: AlbumViewModel,
    modifier: Modifier = Modifier,
    onEdit: () -> Unit,
    onOpenPhotos: (Int) -> Unit
) {
    val albums by viewModel.albums.collectAsState()

    var showMenu by remember { mutableStateOf(false) }
    var selectedAlbum by remember { mutableStateOf<Album?>(null) }
    var pressOffset by remember { mutableStateOf(Offset.Zero) }

    Column(
        modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)

    ) {

        SortBar { field, order ->
            viewModel.sortAlbums(field, order)
        }

        Box(Modifier.fillMaxSize()) {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(albums) { album ->

                    AlbumCard(
                        album = album,
                        onClick = {
                            onOpenPhotos(album.id)
                        },
                        onLongPress = { offset ->
                            selectedAlbum = album
                            pressOffset = offset
                            showMenu = true
                        }
                    )
                }
            }

            if (showMenu && selectedAlbum != null) {
                ContextMenu(
                    offset = pressOffset,
                    onDismiss = { showMenu = false },
                    onEdit = {
                        showMenu = false
                        viewModel.startEditing(selectedAlbum!!)
                        onEdit()
                    },
                    onDelete = {
                        showMenu = false
                        viewModel.deleteAlbum(selectedAlbum!!.id)
                    }
                )
            }
        }
    }
}

@Composable
fun AlbumCard(
    album: Album,
    onLongPress: (Offset) -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick() },
                    onLongPress = { offset -> onLongPress(offset) }
                )
            },
        border = BorderStroke(1.dp, Color.Black),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(Modifier.padding(12.dp)) {

            Text(
                text = album.title,
                fontSize = 18.sp
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = album.created_at.take(10),
                fontSize = 12.sp,
                color = Color.Gray
            )

            Text(
                text = album.location_name ?: "Sin ubicación",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun ContextMenu(
    offset: Offset,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    DropdownMenu(
        expanded = true,
        onDismissRequest = onDismiss,
        offset = DpOffset(offset.x.dp, offset.y.dp)
    ) {
        DropdownMenuItem(
            text = { Text("Editar") },
            onClick = onEdit
        )
        DropdownMenuItem(
            text = { Text("Eliminar") },
            onClick = onDelete
        )
    }
}
