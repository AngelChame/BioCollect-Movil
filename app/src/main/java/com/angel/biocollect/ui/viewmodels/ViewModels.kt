package com.angel.biocollect.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.angel.biocollect.data.models.*
import com.angel.biocollect.data.repository.BioCollectRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// ==================== AUTH VIEW MODEL ====================

class AuthViewModel(
    private val repository: BioCollectRepository = BioCollectRepository()
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState = _authState.asStateFlow()

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        val userId = repository.getCurrentUserId()
        if (userId != null) {
            _authState.value = AuthState.Authenticated(userId)
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun signUp(email: String, password: String, user: User) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            repository.signUp(email, password, user).fold(
                onSuccess = { userId ->
                    _authState.value = AuthState.Authenticated(userId)
                },
                onFailure = { error ->
                    _authState.value = AuthState.Error(error.message ?: "Error desconocido")
                }
            )
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            repository.signIn(email, password).fold(
                onSuccess = { userId ->
                    _authState.value = AuthState.Authenticated(userId)
                },
                onFailure = { error ->
                    _authState.value = AuthState.Error(error.message ?: "Error desconocido")
                }
            )
        }
    }

    fun signOut() {
        repository.signOut()
        _authState.value = AuthState.Unauthenticated
    }
}

sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val userId: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

// ==================== COLLECTION VIEW MODEL ====================

class CollectionViewModel(
    private val repository: BioCollectRepository = BioCollectRepository()
) : ViewModel() {

    private val _collections = MutableStateFlow<List<Collection>>(emptyList())
    val collections = _collections.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun loadCollections(userId: String) {
        viewModelScope.launch {
            repository.getCollections(userId).collect {
                _collections.value = it
            }
        }
    }

    fun createCollection(collection: Collection) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.createCollection(collection).fold(
                onSuccess = {
                    _isLoading.value = false
                },
                onFailure = { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
            )
        }
    }

    fun deleteCollection(collectionId: String) {
        viewModelScope.launch {
            repository.deleteCollection(collectionId).fold(
                onSuccess = { /* Success */ },
                onFailure = { e -> _error.value = e.message }
            )
        }
    }

    fun clearError() {
        _error.value = null
    }
}

// ==================== SPECIMEN VIEW MODEL ====================

class SpecimenViewModel(
    private val repository: BioCollectRepository = BioCollectRepository()
) : ViewModel() {

    private val _specimens = MutableStateFlow<List<Specimen>>(emptyList())
    val specimens = _specimens.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun loadSpecimens(collectionId: String) {
        viewModelScope.launch {
            repository.getSpecimens(collectionId).collect {
                _specimens.value = it
            }
        }
    }

    fun createSpecimen(specimen: Specimen) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.createSpecimen(specimen).fold(
                onSuccess = {
                    _isLoading.value = false
                },
                onFailure = { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
            )
        }
    }

    fun deleteSpecimen(specimen: Specimen) {
        viewModelScope.launch {
            repository.deleteSpecimen(specimen).fold(
                onSuccess = { /* Success */ },
                onFailure = { e -> _error.value = e.message }
            )
        }
    }

    fun clearError() {
        _error.value = null
    }
}

// ==================== USER VIEW MODEL ====================

class UserViewModel(
    private val repository: BioCollectRepository = BioCollectRepository()
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun loadUser(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getUser(userId).fold(
                onSuccess = {
                    _user.value = it
                    _isLoading.value = false
                },
                onFailure = { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
            )
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateUser(user).fold(
                onSuccess = {
                    _user.value = user
                    _isLoading.value = false
                },
                onFailure = { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
            )
        }
    }

    fun clearError() {
        _error.value = null
    }
}