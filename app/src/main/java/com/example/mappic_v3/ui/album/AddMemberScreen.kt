package com.example.mappic_v3.ui.album

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mappic_v3.data.model.auth.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemberScreen(
    albumId: Int,
    viewModel: AlbumViewModel,
    userToken: String,
    onBack: () -> Unit
) {
    val foundUser by viewModel.foundUser.collectAsState()
    val memberMessage by viewModel.memberMessage.collectAsState()
    val currentMembers by viewModel.currentMembers.collectAsState()
    var emailInput by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    // IMPORTANTE: Asegúrate de que en AlbumViewModel esta variable NO sea private
    val myUserId = viewModel.currentUserId

    LaunchedEffect(albumId) {
        viewModel.loadMembers(albumId)
    }

    DisposableEffect(Unit) {
        viewModel.clearMemberStatus()
        onDispose { viewModel.clearMemberStatus() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Miembros") },
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

            memberMessage?.let { msg ->
                Text(
                    text = msg,
                    color = if (msg.startsWith("Error")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            foundUser?.let { user ->
                UserActionsBlock(
                    user = user,
                    onAddMember = { role ->
                        viewModel.addMember(albumId, user.id, role)
                        emailInput = ""
                    }
                )
            }

            if (isSearching && foundUser == null && memberMessage == null) {
                CircularProgressIndicator(modifier = Modifier.size(30.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Miembros con acceso",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (currentMembers.isEmpty()) {
                Text(
                    "No hay colaboradores aún.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 20.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(currentMembers) { member ->
                        // Lógica de permisos para ver el botón eliminar
                        val myMemberRecord = currentMembers.find { it.id == myUserId }
                        val myRole = myMemberRecord?.role?.lowercase() ?: "viewer"
                        val canDelete = myRole == "owner" || myRole == "editor"

                        ListItem(
                            headlineContent = { Text(member.name) },
                            supportingContent = {
                                Text("${member.email} • ${member.role?.uppercase() ?: "MEMBER"}")
                            },
                            leadingContent = { Icon(Icons.Default.Person, contentDescription = null) },
                            trailingContent = {
                                if (canDelete) {
                                    IconButton(onClick = {
                                        viewModel.deleteMember(albumId, member.id)
                                    }) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Eliminar",
                                            tint = Color.Red.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

// ESTAS FUNCIONES DEBEN ESTAR FUERA DE LA FUNCIÓN PRINCIPAL
@Composable
fun SearchBlock(emailInput: String, onEmailChange: (String) -> Unit, onSearch: () -> Unit) {
    OutlinedTextField(
        value = emailInput,
        onValueChange = onEmailChange,
        label = { Text("Email del nuevo miembro") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
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

@Composable
fun UserActionsBlock(user: User, onAddMember: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Resultado:", style = MaterialTheme.typography.labelLarge)
            Text(text = user.name, style = MaterialTheme.typography.titleMedium)
            Text(text = user.email, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilledTonalButton(onClick = { onAddMember("viewer") }, modifier = Modifier.weight(1f)) {
                    Text("Lector")
                }
                Button(onClick = { onAddMember("editor") }, modifier = Modifier.weight(1f)) {
                    Text("Editor")
                }
            }
        }
    }
}