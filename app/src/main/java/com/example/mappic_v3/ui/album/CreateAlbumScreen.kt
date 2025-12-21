package com.example.mappic_v3.ui.album
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CreateAlbumScreen(
    modifier: Modifier = Modifier,
    viewModel: AlbumViewModel,
    onFinishCreate: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    val isTitleValid = title.isNotBlank()

    Column(
        modifier = modifier.padding(20.dp)
    ) {

        Text(
            text = "Crear álbum",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = title.isBlank() && title.isNotEmpty()
        )

        if (title.isBlank() && title.isNotEmpty()) {
            Text(
                text = "El título es obligatorio",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }

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

        // Dentro de CreateAlbumScreen.kt
        Button(
            onClick = {
                // Obtenemos el ID del usuario actual desde el ViewModel o pasándolo como parámetro
                // Si el userId no está en el AlbumViewModel, asegúrate de refrescarlo en el MainScreen
                viewModel.createAlbum(
                    title = title.trim(),
                    description = description.ifBlank { null },
                    location = location.ifBlank { null },
                    lat = "0",
                    lon = "0",
                    isGlobal = false
                )

                onFinishCreate()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isTitleValid
        ) {
            Text("Guardar álbum")
        }
    }
}

