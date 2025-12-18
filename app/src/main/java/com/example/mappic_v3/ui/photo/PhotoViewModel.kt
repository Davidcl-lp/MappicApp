package com.example.mappic_v3.ui.photo

import android.content.Context
import android.net.Uri
import android.util.Log
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

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        loadPhotos()
    }

    fun loadPhotos() {
        viewModelScope.launch {
            try {
                // Limpiamos la lista actual para forzar a Compose a redibujar
                _photos.value = emptyList()

                // Traemos las fotos nuevas del servidor
                val result = repo.getPhotos(albumId)
                _photos.value = result
            } catch (e: Exception) {
                _errorMessage.value = "Error al recargar fotos"
            }
        }
    }

    fun uploadPhotos(
        context: Context,
        uris: List<Uri>,
        uploaderId: Int,
        description: String?
    ) {
        if (uploaderId <= 0) {
            _errorMessage.value = "ID de usuario no válido"
            return
        }

        viewModelScope.launch {
            _isUploading.value = true
            _errorMessage.value = null

            try {
                val newPhotos = repo.uploadPhotos(
                    context,
                    uris,
                    albumId,
                    uploaderId,
                    description
                )

                if (newPhotos != null) {
                    _photos.value = _photos.value + newPhotos
                } else {
                    _errorMessage.value = "Error al subir fotos"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión"
            } finally {
                _isUploading.value = false
            }
        }
    }


    fun deletePhotos(photoIds: List<Int>) {
        viewModelScope.launch {
            try {
                photoIds.forEach { repo.deletePhoto(it) }
                loadPhotos()
            } catch (e: Exception) {
                _errorMessage.value = "Error al eliminar"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}