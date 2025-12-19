package com.example.mappic_v3.ui.photo

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mappic_v3.data.model.Photo.Photo
import com.example.mappic_v3.data.repository.PhotoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PhotoViewModel(private val repo: PhotoRepository) : ViewModel() {

    private val _photos = MutableStateFlow<List<Photo>>(emptyList())
    val photos: StateFlow<List<Photo>> = _photos

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadPhotos(albumId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repo.getPhotos(albumId)
                _photos.value = result
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar fotos"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun uploadPhotos(
        context: Context,
        uris: List<Uri>,
        albumId: Int,
        uploaderId: Int,
        description: String?
    ) {
        if (uploaderId <= 0) {
            _errorMessage.value = "ID de usuario no válido"
            return
        }

        viewModelScope.launch {
            _isUploading.value = true
            try {
                val newPhotos = repo.uploadPhotos(context, uris, albumId, uploaderId, description)
                if (newPhotos != null) _photos.value = _photos.value + newPhotos
                else _errorMessage.value = "Error al subir fotos"
            } catch (e: Exception) {
                _errorMessage.value = "Error al subir fotos"
            } finally {
                _isUploading.value = false
            }
        }
    }

    fun deletePhotos(photoIds: List<Int>) {
        viewModelScope.launch {
            try {
                photoIds.forEach { repo.deletePhoto(it) }
                _photos.value = _photos.value.filter { it.id !in photoIds }
            } catch (e: Exception) {
                _errorMessage.value = "Error al eliminar fotos"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
