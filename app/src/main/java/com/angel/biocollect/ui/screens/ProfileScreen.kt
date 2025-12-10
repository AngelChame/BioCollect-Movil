package com.angel.biocollect.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.angel.biocollect.data.models.User
import com.angel.biocollect.ui.viewmodels.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: String,
    viewModel: UserViewModel,
    onNavigateBack: () -> Unit,
    onSignOut: () -> Unit
) {
    val user by viewModel.user.collectAsState()
    var isEditing by remember { mutableStateOf(false) }

    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var institucion by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    LaunchedEffect(userId) {
        viewModel.loadUser(userId)
    }

    LaunchedEffect(user) {
        user?.let {
            nombre = "${it.nombre} ${it.apellidos}"
            correo = it.email
            institucion = it.institucion
            descripcion = it.descripcion
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Default.Home, "Inicio") },
                    label = { Text("Inicio") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Default.List, "Colecciones") },
                    label = { Text("Colecciones") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Person, "Perfil") },
                    label = { Text("Perfil") }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Foto de perfil
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier.size(60.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Formulario
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                enabled = isEditing,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo") },
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = institucion,
                onValueChange = { institucion = it },
                label = { Text("Institución") },
                enabled = isEditing,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                enabled = isEditing,
                minLines = 3,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            // Botón de guardar/editar
            Button(
                onClick = {
                    if (isEditing) {
                        user?.let {
                            val nameParts = nombre.split(" ", limit = 2)
                            viewModel.updateUser(
                                it.copy(
                                    nombre = nameParts.getOrNull(0) ?: "",
                                    apellidos = nameParts.getOrNull(1) ?: "",
                                    institucion = institucion,
                                    descripcion = descripcion
                                )
                            )
                        }
                    }
                    isEditing = !isEditing
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEditing) "Guardar" else "Editar perfil")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de cerrar sesión
            OutlinedButton(
                onClick = onSignOut,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    Icons.Default.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar sesión")
            }
        }
    }
}