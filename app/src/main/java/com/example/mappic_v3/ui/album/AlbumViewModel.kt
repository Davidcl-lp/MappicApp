package com.example.mappic_v3.ui.album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mappic_v3.data.model.Member.AddMemberRequest
import com.example.mappic_v3.data.model.album.Album
import com.example.mappic_v3.data.model.album.CreateAlbumRequest
import com.example.mappic_v3.data.model.album.UpdateAlbumRequest
import com.example.mappic_v3.data.model.auth.User
import com.example.mappic_v3.data.repository.AlbumMemberRepository
import com.example.mappic_v3.data.repository.AlbumRepository
import com.example.mappic_v3.data.repository.UserRepository
import com.example.mappic_v3.ui.SortField
import com.example.mappic_v3.ui.SortOrder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AlbumViewModel(
    private val albumRepository: AlbumRepository,
    private val userRepository: UserRepository,
    private val albumMemberRepository: AlbumMemberRepository
) : ViewModel() {

    private var currentUserId: Int? = null



    private val repo = AlbumRepository()
    private val userRepo = UserRepository()

    private val _albums = MutableStateFlow<List<Album>>(emptyList())
    val albums: StateFlow<List<Album>> = _albums


    private val _editingAlbum = MutableStateFlow<Album?>(null)
    val editingAlbum: StateFlow<Album?> = _editingAlbum

    private val _foundUser = MutableStateFlow<User?>(null)
    val foundUser: StateFlow<User?> = _foundUser

    private val _currentMembers = MutableStateFlow<List<User>>(emptyList())
    val currentMembers: StateFlow<List<User>> = _currentMembers

    private val _memberMessage = MutableStateFlow<String?>(null)
    val memberMessage: StateFlow<String?> = _memberMessage

    init {}

    fun addMember(albumId: Int, userId: Int, role: String) {
        viewModelScope.launch {
            _memberMessage.value = null
            try {
                val user = albumMemberRepository.addMemberToAlbum(
                    AddMemberRequest(
                        albumId = albumId,
                        userId = userId,
                        role = role
                    )
                )

                if (user != null) {
                    _currentMembers.value = _currentMembers.value + user
                    _memberMessage.value = "Miembro añadido correctamente"
                } else {
                    _memberMessage.value = "No se pudo añadir al miembro"
                }
            } catch (e: Exception) {
                _memberMessage.value = "Error de conexión"
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

    fun loadAlbumsForUser(userId: Int) {
        currentUserId = userId
        viewModelScope.launch {
            try {
                val owned = albumRepository.getUserAlbums(userId)
                val shared = albumRepository.getSharedAlbums(userId)
                _albums.value = owned + shared
            } catch (e: Exception) {
                println("Error cargando álbumes: ${e.message}")
            }
        }
    }

    private fun reload() {
        val id = currentUserId
        if (id != null) {
            loadAlbumsForUser(id)
        }
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
            albumRepository.createAlbum(
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
            albumRepository.deleteAlbum(id)
            reload()
        }
    }

    fun startEditing(album: Album) {
        _editingAlbum.value = album
    }

    fun editAlbum(id: Int, title: String, description: String?) {
        viewModelScope.launch {
            albumRepository.updateAlbum(
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
            _currentMembers.value = emptyList()
            try {
                val members = albumMemberRepository.getAlbumMembers(albumId)
                _currentMembers.value = members
            } catch (e: Exception) {
                _memberMessage.value = "Error al cargar miembros"
            }
        }
    }

    fun deleteMember(albumId: Int, userId: Int) {
        viewModelScope.launch {
            val success = albumMemberRepository.removeMember(albumId, userId)

            if (success) {
                _memberMessage.value = "Miembro eliminado correctamente"
                loadMembers(albumId)
            } else {
                _memberMessage.value = "Error al intentar eliminar al miembro"
            }
        }
    }
}