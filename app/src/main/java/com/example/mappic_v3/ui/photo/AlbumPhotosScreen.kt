package com.example.mappic_v3.ui.photo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun AlbumPhotosScreen(
    modifier: Modifier,
    albumId: Int,
    onBack: () -> Unit
) {
    val viewModel = remember { PhotoViewModel(albumId) }
    val photos by viewModel.photos.collectAsState()

    Column(modifier.fillMaxSize()
        .padding(16.dp)

    ) {

        Text(
            text = "Fotos del álbum",
            fontSize = 22.sp,
            modifier = Modifier.padding(16.dp)
        )

        if (photos.isEmpty()) {
            Text(
                text = "No hay fotos en este álbum",
                fontSize = 18.sp,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )
            return
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(photos) { photo ->
                AsyncImage(
                    model = photo.url,
                    contentDescription = "Foto",
                    modifier = Modifier
                        .aspectRatio(1f)
                        .fillMaxWidth()
                )
            }
        }
    }
}
