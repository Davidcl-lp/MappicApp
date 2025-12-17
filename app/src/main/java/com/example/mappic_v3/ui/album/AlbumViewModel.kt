package com.example.mappic_v3.ui.album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mappic_v3.data.model.Album
import com.example.mappic_v3.data.model.CreateAlbumRequest
import com.example.mappic_v3.data.model.UpdateAlbumRequest
import com.example.mappic_v3.data.repository.AlbumRepository
import com.example.mappic_v3.ui.SortField
import com.example.mappic_v3.ui.SortOrder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
class AlbumViewModel : ViewModel() {

    private val repo = AlbumRepository()

    private val _albums = MutableStateFlow<List<Album>>(emptyList())
    val albums: StateFlow<List<Album>> = _albums
    private var currentUserId: Int? = null
    private val _editingAlbum = MutableStateFlow<Album?>(null)
    val editingAlbum: StateFlow<Album?> = _editingAlbum

    init {}

    fun loadAlbumsForUser(userId: Int) {
        currentUserId = userId
        viewModelScope.launch {
            val result = repo.getUserAlbums(userId)
            _albums.value = result
        }
    }

    private fun reload() {
        currentUserId?.let { loadAlbumsForUser(it) }
    }

    fun createAlbum(
        title: String,
        description: String?,
        location: String?,
        lat: String?,
        lon: String?,
        isGlobal: Boolean
    ) {
        viewModelScope.launch {
            repo.createAlbum(
                CreateAlbumRequest(
                    title = title,
                    description = description,
                    owner_id = 1,
                    location_name = location,
                    latitude = lat,
                    longitude = lon,
                    is_global = isGlobal
                )
            )
            reload()
        }
    }

    fun deleteAlbum(id: Int) {
        viewModelScope.launch {
            repo.deleteAlbum(id)
            reload()
        }
    }

    fun startEditing(album: Album) {
        _editingAlbum.value = album
    }

    fun editAlbum(id: Int, title: String, description: String?) {
        viewModelScope.launch {
            repo.updateAlbum(
                id,
                UpdateAlbumRequest(
                    title = title,
                    description = description
                )
            )
            _editingAlbum.value = null
            reload()
        }
    }
    fun sortAlbums(field: SortField, order: SortOrder) {
        val sorted = when (field) {

            SortField.TITLE -> {
                if (order == SortOrder.ASC)
                    _albums.value.sortedBy { it.title.lowercase() }
                else
                    _albums.value.sortedByDescending { it.title.lowercase() }
            }

            SortField.DATE -> {
                if (order == SortOrder.ASC)
                    _albums.value.sortedBy { it.created_at }
                else
                    _albums.value.sortedByDescending { it.created_at }
            }
        }

        _albums.value = sorted
    }

}
