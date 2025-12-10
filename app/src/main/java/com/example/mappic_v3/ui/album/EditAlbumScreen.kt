package com.example.mappic_v3.ui.album

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EditAlbumScreen(
    modifier: Modifier = Modifier,
    viewModel: AlbumViewModel,
    onFinishEdit: () -> Unit
) {
    val album = viewModel.editingAlbum.collectAsState().value
        ?: return Text("Error: no hay álbum para editar")

    var title by remember { mutableStateOf(album.title) }
    var description by remember { mutableStateOf(album.description ?: "") }

    Column(modifier.padding(20.dp)) {

        Text("Editar álbum", fontSize = 22.sp)

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                viewModel.editAlbum(album.id, title, description)
                onFinishEdit()
            }
        ) {
            Text("Guardar cambios")
        }
    }
}

