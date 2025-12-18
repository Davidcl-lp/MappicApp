package com.example.mappic_v3.ui.album

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.mappic_v3.data.model.Album
import com.example.mappic_v3.ui.components.SortBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumScreen(
    viewModel: AlbumViewModel,
    modifier: Modifier = Modifier,
    onEdit: () -> Unit,
    onOpenPhotos: (Int, String, String?) -> Unit,
    onManageMembers: (Int) -> Unit
) {
    val albums by viewModel.albums.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showMenu by remember { mutableStateOf(false) }
    var selectedAlbum by remember { mutableStateOf<Album?>(null) }
    var pressOffset by remember { mutableStateOf(Offset.Zero) }

    val filteredAlbums = remember(albums, searchQuery) {
        if (searchQuery.isBlank()) albums
        else albums.filter { album ->
            album.title.contains(searchQuery, ignoreCase = true) ||
                    (album.description?.contains(searchQuery, ignoreCase = true) ?: false)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
            placeholder = { Text("Buscar álbumes…") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(Modifier.height(12.dp))

        SortBar { field, order ->
            viewModel.sortAlbums(field, order)
        }

        Spacer(Modifier.height(12.dp))

        Box(Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(
                    items = filteredAlbums,
                    key = { it.id }
                ) { album ->
                    AlbumCard(
                        album = album,
                        onClick = {
                            onOpenPhotos(album.id, album.title, album.description)
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
                    },
                    onManageMembers = {
                        showMenu = false
                        onManageMembers(selectedAlbum!!.id)
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
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        label = "press-scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onClick() },
                    onLongPress = { offset -> onLongPress(offset) }
                )
            },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isPressed) 2.dp else 6.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box {
            Column(Modifier.padding(16.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = album.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1
                    )

                    Text(
                        text = album.created_at.take(10),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = album.location_name ?: "Sin ubicación",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isPressed) {
                Box(
                    Modifier
                        .matchParentSize()
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                        )
                )
            }
        }
    }
}

@Composable
fun ContextMenu(
    offset: Offset,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onManageMembers: () -> Unit
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
            text = { Text("Gestionar Miembros") },
            onClick = onManageMembers
        )
        DropdownMenuItem(
            text = { Text("Eliminar") },
            onClick = onDelete
        )
    }
}