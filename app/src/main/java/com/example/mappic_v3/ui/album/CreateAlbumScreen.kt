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
    viewModel: AlbumViewModel, // Recibe el de MainScreen
    onFinishCreate: () -> Unit,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    Column(modifier.padding(20.dp)) {
        Text("Crear álbum", style = MaterialTheme.typography.headlineMedium)

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

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Ubicación") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                if (title.isNotBlank()) {
                    viewModel.createAlbum(
                        title = title,
                        description = description,
                        location = location,
                        lat = "0",
                        lon = "0",
                        isGlobal = false,
                        onComplete = {
                            onFinishCreate() // Te lleva a ScreenState.LIST_ALBUMS
                        }
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank()
        ) {
            Text("Guardar álbum")
        }
    }
}