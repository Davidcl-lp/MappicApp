package com.example.mappic_v3.ui.album

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mappic_v3.data.model.auth.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemberScreen(
    albumId: Int,
    viewModel: AlbumViewModel,
    onBack: () -> Unit
) {
    val foundUser by viewModel.foundUser.collectAsState()
    val memberMessage by viewModel.memberMessage.collectAsState()

    var emailInput by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        viewModel.clearMemberStatus()
        onDispose {
            viewModel.clearMemberStatus()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Añadir Miembro al Álbum $albumId") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            SearchBlock(
                emailInput = emailInput,
                onEmailChange = { emailInput = it },
                onSearch = {
                    isSearching = true
                    viewModel.searchUserByEmail(emailInput.trim())
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (memberMessage != null) {
                Text(
                    text = memberMessage!!,
                    color = if (memberMessage!!.startsWith("Error")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            foundUser?.let { user ->
                UserActionsBlock(
                    user = user,
                    onAddMember = { role ->
                        viewModel.addMember(
                            albumId = albumId,
                            newUserId = user.id,
                            role = role
                        )
                    }
                )
            }

            if (isSearching && foundUser == null && memberMessage == null) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun SearchBlock(emailInput: String, onEmailChange: (String) -> Unit, onSearch: () -> Unit) {
    OutlinedTextField(
        value = emailInput,
        onValueChange = onEmailChange,
        label = { Text("Email del nuevo miembro") },
        placeholder = { Text("ejemplo@correo.com") },
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(8.dp))

    Button(
        onClick = onSearch,
        enabled = emailInput.isNotBlank(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Buscar Usuario")
    }
}

// Composable para mostrar el usuario encontrado y los botones de rol
@Composable
fun UserActionsBlock(user: User, onAddMember: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Usuario Encontrado:", style = MaterialTheme.typography.titleMedium)
            Text(text = "${user.name} (${user.email})", style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { onAddMember("viewer") },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Añadir como Viewer")
                }

                Button(
                    onClick = { onAddMember("editor") }
                ) {
                    Text("Añadir como Editor")
                }
            }
        }
    }
}