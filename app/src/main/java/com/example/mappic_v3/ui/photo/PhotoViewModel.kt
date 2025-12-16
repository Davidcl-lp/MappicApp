package com.example.mappic_v3.ui.photo

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mappic_v3.data.model.Photo
import com.example.mappic_v3.data.repository.PhotoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PhotoViewModel(private val albumId: Int) : ViewModel() {

    private val repo = PhotoRepository()

    private val _photos = MutableStateFlow<List<Photo>>(emptyList())
    val photos: StateFlow<List<Photo>> = _photos


    init {
        loadPhotos()
    }

    private fun loadPhotos() {
        viewModelScope.launch {
            _photos.value = repo.getPhotos(albumId)
        }
    }

    fun uploadPhotos(
        context: Context,
        uris: List<Uri>,
        uploaderId: Int,
        description: String?
    ) {
        viewModelScope.launch {
            repo.uploadPhotos(
                context = context,
                uris = uris,
                albumId = albumId,
                uploaderId = uploaderId,
                description = description
            )
            loadPhotos()
        }
    }

    fun deletePhoto(photoId: Int) {
        viewModelScope.launch {
            repo.deletePhoto(photoId)
            loadPhotos()
        }
    }
    fun deletePhotos(photoIds: List<Int>) {
        viewModelScope.launch {
            photoIds.forEach { repo.deletePhoto(it) }
            loadPhotos()
        }
    }

}
