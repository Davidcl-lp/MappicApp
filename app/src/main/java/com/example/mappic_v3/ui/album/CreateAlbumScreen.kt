package com.example.mappic_v3.ui.album

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CreateAlbumScreen(
    modifier: Modifier = Modifier,
    viewModel: AlbumViewModel = AlbumViewModel(),
    onFinishCreate: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    Column(
        modifier = modifier.padding(20.dp)
    ) {

        Text(
            text = "Crear álbum",
            fontSize = 22.sp
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Ubicación") },
            placeholder = { Text("Ej: Madrid, España") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.createAlbum(
                    title = title,
                    description = description,
                    location = location.ifBlank { null },
                    lat = "0",
                    lon = "0",
                    isGlobal = false
                )
                onFinishCreate()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar álbum")
        }
    }
}
