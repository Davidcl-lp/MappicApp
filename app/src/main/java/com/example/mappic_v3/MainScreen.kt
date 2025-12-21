package com.example.mappic_v3.ui

import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mappic_v3.ui.album.*
import com.example.mappic_v3.ui.auth.*
import com.example.mappic_v3.ui.components.TopBar
import com.example.mappic_v3.ui.photo.AlbumPhotosScreen
import com.example.mappic_v3.data.repository.*
import com.example.mappic_v3.ui.photo.PhotoViewModel




@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun MainScreen(
) {

    var currentScreen by remember { mutableStateOf(ScreenState.LOGIN) }
    var selectedAlbumId by remember { mutableStateOf<Int?>(null) }
    var selectedAlbumTitle by remember { mutableStateOf<String?>(null) }
    var selectedAlbumDescription by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val authViewModel = remember { AuthViewModel(context) }
    var currentUserId by remember { mutableStateOf<Int?>(null) }
    var currentUserRole by remember { mutableStateOf("viewer") }
    var selectedAlbumOwnerId by remember { mutableStateOf<Int?>(null) }

    val viewModelAlbum = remember {
        AlbumViewModel(
            albumRepository = AlbumRepository(),
            userRepository = UserRepository(),
            albumMemberRepository = AlbumMemberRepository()
        )
    }
    val photoRepo = PhotoRepository()
    val photoViewModel = PhotoViewModel(photoRepo)
    LaunchedEffect(selectedAlbumId) {
        selectedAlbumId?.let { id ->
            photoViewModel.loadPhotos(id)
        }
    }






    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            if (currentScreen !in listOf(ScreenState.LOGIN, ScreenState.REGISTER)) {
                TopBar(
                    onSelectList = { currentScreen = ScreenState.LIST_ALBUMS },
                    onSelectCreate = { currentScreen = ScreenState.CREATE_ALBUM },
                    onSelectProfile = { currentScreen = ScreenState.PROFILE }
                )
            }
        }
    ) { padding ->

        val modifier = Modifier
            .padding(padding)
            .fillMaxSize()

        val userToken = authViewModel.token ?: ""

        when (currentScreen) {
            ScreenState.LOGIN -> LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = { userId ->
                    currentUserId = userId
                    viewModelAlbum.loadAlbumsForUser(userId)
                    currentScreen = ScreenState.LIST_ALBUMS
                },
                onRegisterClick = { currentScreen = ScreenState.REGISTER }
            )

            ScreenState.REGISTER -> RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = { userId ->
                    currentUserId = userId
                    viewModelAlbum.loadAlbumsForUser(userId)
                    currentScreen = ScreenState.LIST_ALBUMS
                },
                onBackToLogin = { currentScreen = ScreenState.LOGIN }
            )
            ScreenState.LIST_ALBUMS ->


                AlbumScreen(

                viewModel = viewModelAlbum,
                modifier = modifier,

                onEdit = { currentScreen = ScreenState.EDIT_ALBUM },
                onOpenPhotos = { id, title, description, role, ownerId ->
                    selectedAlbumId = id
                    selectedAlbumTitle = title
                    selectedAlbumDescription = description
                    selectedAlbumOwnerId = ownerId
                    currentUserRole = if (currentUserId == ownerId) "owner" else (role ?: "viewer")
                    currentScreen = ScreenState.PHOTOS
                },
                onManageMembers = { albumId ->
                    selectedAlbumId = albumId
                    currentScreen = ScreenState.MEMBER_ADD
                }
            )

            ScreenState.PHOTOS -> AlbumPhotosScreen(
                userRole = currentUserRole,
                albumId = selectedAlbumId ?: 0,
                albumTitle = selectedAlbumTitle ?: "",
                albumDescription = selectedAlbumDescription ?: "",
                uploaderId = currentUserId ?: 0,
                albumOwnerId = selectedAlbumOwnerId ?: 0,
                albumViewModel = viewModelAlbum,
                photoViewModel = photoViewModel!!,
                modifier = modifier,
                onBack = { currentScreen = ScreenState.LIST_ALBUMS }
            )


            ScreenState.CREATE_ALBUM -> CreateAlbumScreen(
                modifier = modifier,
                viewModel = viewModelAlbum,
                onFinishCreate = {
                    currentScreen = ScreenState.LIST_ALBUMS
                },
                onBack = { currentScreen = ScreenState.LIST_ALBUMS }
            )


            ScreenState.EDIT_ALBUM -> EditAlbumScreen(
                modifier = modifier,
                viewModel = viewModelAlbum,
                onFinishEdit = { currentScreen = ScreenState.LIST_ALBUMS },
                onBack = { currentScreen = ScreenState.LIST_ALBUMS }
            )


            ScreenState.PROFILE -> ProfileScreen(
                authViewModel = authViewModel,
                onLogout = { currentScreen = ScreenState.LOGIN }
            )
            ScreenState.MEMBER_ADD -> {
                val albumId = selectedAlbumId
                if (albumId != null) {
                    AddMemberScreen(
                        albumId = albumId,
                        viewModel = viewModelAlbum,
                        userToken = userToken,
                        onBack = { currentScreen = ScreenState.LIST_ALBUMS }
                    )
                } else {
                    currentScreen = ScreenState.LIST_ALBUMS
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    // Obtenemos el correo directamente del ViewModel
    val userEmail = authViewModel.email ?: "usuario@ejemplo.com"

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Espacio superior para bajar el contenido
            Spacer(modifier = Modifier.height(100.dp))

            // --- IMAGEN DE PERFIL (LOGO FIJO) ---
            Surface(
                modifier = Modifier.size(130.dp),
                shape = androidx.compose.foundation.shape.CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                tonalElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = com.example.mappic_v3.R.drawable.ic_mappic),
                        contentDescription = "MapPic Logo",
                        modifier = Modifier.size(90.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Mi Perfil",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(40.dp))

            // --- INFO CARD (SOLO CORREO ELECTRÓNICO) ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Email,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Correo Electrónico",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = userEmail,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                            )
                        )
                    }
                }
            }

            // Empuja los botones hacia el final de la pantalla
            Spacer(modifier = Modifier.weight(1f))

            // --- BOTÓN CERRAR SESIÓN ---
            Button(
                onClick = {
                    authViewModel.logout()
                    onLogout()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            ) {
                Icon(androidx.compose.material.icons.Icons.Default.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- BOTÓN ELIMINAR CUENTA ---
            TextButton(
                onClick = { showDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Eliminar cuenta permanentemente",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(50.dp))
        }
    }

    // DIÁLOGO DE CONFIRMACIÓN
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que deseas eliminar tu cuenta? Esta acción no se puede deshacer.") },
            confirmButton = {
                val coroutineScope = rememberCoroutineScope()

                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    onClick = {
                        coroutineScope.launch {
                            authViewModel.logout()
                            onLogout()
                        }
                        showDialog = false
                    }
                ) {
                    Text("Eliminar", color = androidx.compose.ui.graphics.Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}