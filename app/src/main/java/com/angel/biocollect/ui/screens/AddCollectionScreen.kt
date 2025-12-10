package com.angel.biocollect.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.angel.biocollect.data.models.Collection
import com.angel.biocollect.ui.viewmodels.CollectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCollectionScreen(
    userId: String,
    viewModel: CollectionViewModel,
    onNavigateBack: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar colección") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                "Nueva Colección",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre de la colección") },
                placeholder = { Text("ej. Escarabajos") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = categoria,
                onValueChange = { categoria = it },
                label = { Text("Categoría") },
                placeholder = { Text("ej. Tropicales, Rosas, etc.") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                placeholder = { Text("Describe tu colección...") },
                minLines = 4,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar")
                }

                Button(
                    onClick = {
                        val collection = Collection(
                            userId = userId,
                            nombre = nombre,
                            categoria = categoria,
                            descripcion = descripcion
                        )
                        viewModel.createCollection(collection)
                        onNavigateBack()
                    },
                    modifier = Modifier.weight(1f),
                    enabled = nombre.isNotBlank() && categoria.isNotBlank()
                ) {
                    Text("Guardar")
                }
            }
        }
    }
}