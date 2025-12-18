package com.example.mappic_v3.ui.album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mappic_v3.data.model.AddMemberRequest
import com.example.mappic_v3.data.model.Album
import com.example.mappic_v3.data.model.CreateAlbumRequest
import com.example.mappic_v3.data.model.UpdateAlbumRequest
import com.example.mappic_v3.data.model.auth.User
import com.example.mappic_v3.data.repository.AlbumRepository
import com.example.mappic_v3.data.repository.UserRepository
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
    private val _foundUser = MutableStateFlow<User?>(null)
    val foundUser: StateFlow<User?> = _foundUser
    private val _currentMembers = MutableStateFlow<List<User>>(emptyList())
    val currentMembers: StateFlow<List<User>> = _currentMembers
    private val _memberMessage = MutableStateFlow<String?>(null)
    val memberMessage: StateFlow<String?> = _memberMessage
    init {}

    private val userRepo = UserRepository()

    fun addMember(albumId: Int, newUserId: Int, role: String) {
        viewModelScope.launch {
            _memberMessage.value = "Añadiendo..."
            val success = repo.addMemberToAlbum(
                AddMemberRequest(album_id = albumId, user_id = newUserId, role = role)
            )
            if (success) {
                _foundUser.value = null
                _memberMessage.value = "¡Usuario añadido correctamente!"
                loadMembers(albumId)
            } else {
                _memberMessage.value = "Error: No se pudo añadir al usuario."
            }
        }
    }

    fun searchUserByEmail(email: String) {
        _foundUser.value = null
        _memberMessage.value = null
        viewModelScope.launch {
            val user = userRepo.findUserByEmail(email)
            if (user != null) {
                _foundUser.value = user
                _memberMessage.value = "User found: ${user.name}"
            } else {
                _memberMessage.value = "Error: User not found with that email."
            }
        }
    }

    fun clearMemberStatus() {
        _memberMessage.value = null
        _foundUser.value = null
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
        val ownerId = currentUserId ?: run {
            println("ERROR: No hay usuario logueado para crear el álbum.")
            return
        }

        viewModelScope.launch {
            repo.createAlbum(
                CreateAlbumRequest(
                    title = title,
                    description = description,
                    owner_id = ownerId,
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

    fun loadMembers(albumId: Int) {
        viewModelScope.launch {
            try {
                val members = repo.getAlbumMembers(albumId)
                _currentMembers.value = members
            } catch (e: Exception) {
                _memberMessage.value = "Error al cargar miembros"
            }
        }
    }

    fun loadAlbumsForUser(userId: Int) {
        currentUserId = userId
        viewModelScope.launch {
            try {
                val owned = repo.getUserAlbums(userId)
                val shared = repo.getSharedAlbums(userId)
                _albums.value = owned + shared
            } catch (e: Exception) {
                println("Error cargando álbumes: ${e.message}")
            }
        }
    }

    fun deleteMember(albumId: Int, userId: Int) {
        viewModelScope.launch {
            val success = repo.removeMember(albumId, userId)
            if (success) {
                _memberMessage.value = "Miembro eliminado correctamente"
                loadMembers(albumId)
            } else {
                _memberMessage.value = "Error al intentar eliminar al miembro"
            }
        }
    }
}