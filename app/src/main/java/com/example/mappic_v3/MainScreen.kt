package com.example.mappic_v3.ui

import androidx.compose.runtime.*
import androidx.compose.material3.Scaffold
import com.example.mappic_v3.ui.album.AlbumScreen
import com.example.mappic_v3.ui.album.CreateAlbumScreen
import com.example.mappic_v3.ui.components.TopBar

import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import com.example.mappic_v3.ui.album.AlbumViewModel
import com.example.mappic_v3.ui.album.EditAlbumScreen
import com.example.mappic_v3.ui.photo.AlbumPhotosScreen



@Composable
fun MainScreen(
    viewModelAlbum: AlbumViewModel = AlbumViewModel()
) {


    var currentScreen by remember { mutableStateOf(ScreenState.LIST_ALBUMS) }
    var selectedAlbumId by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            TopBar(
                onSelectList = { currentScreen = ScreenState.LIST_ALBUMS },
                onSelectCreate = { currentScreen = ScreenState.CREATE_ALBUM }
            )
        }
    ) { padding ->

        val modifier = Modifier.padding(padding)

        when (currentScreen) {
            ScreenState.LIST_ALBUMS -> AlbumScreen(
                viewModel = viewModelAlbum,
                modifier = modifier,
                onEdit = { currentScreen = ScreenState.EDIT_ALBUM },
                onOpenPhotos = { albumId ->
                    selectedAlbumId = albumId
                    currentScreen = ScreenState.PHOTOS
                }
            )

            ScreenState.PHOTOS -> AlbumPhotosScreen(
                modifier = modifier,
                albumId = selectedAlbumId ?: 0,
                onBack = { currentScreen = ScreenState.LIST_ALBUMS }
            )

            ScreenState.CREATE_ALBUM -> CreateAlbumScreen(modifier = modifier)

            ScreenState.EDIT_ALBUM -> EditAlbumScreen(
                viewModel = viewModelAlbum,
                modifier = modifier,
                onFinishEdit = {
                    currentScreen = ScreenState.LIST_ALBUMS
                }
            )
        }
    }

}

