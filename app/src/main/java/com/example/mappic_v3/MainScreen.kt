package com.example.mappic_v3.ui

import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mappic_v3.ui.album.*
import com.example.mappic_v3.ui.auth.*
import com.example.mappic_v3.ui.components.TopBar
import com.example.mappic_v3.ui.photo.AlbumPhotosScreen
import kotlinx.coroutines.launch
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
            ScreenState.LIST_ALBUMS -> AlbumScreen(
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
                onFinishCreate = { currentScreen = ScreenState.LIST_ALBUMS },
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("My Profile", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { showDialog = true }) {
            Text("Delete Account")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            authViewModel.logout()
            onLogout()
        }) {
            Text("Logout")
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirm Delete") },
            text = { Text("Are you sure you want to delete your account? This action cannot be undone.") },
            confirmButton = {
                val coroutineScope = rememberCoroutineScope()
                Button(onClick = {
                    coroutineScope.launch {
                        authViewModel.token?.let {
                            authViewModel.logout()
                            onLogout()
                        }
                    }
                    showDialog = false
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("No")
                }
            }
        )
    }
}
