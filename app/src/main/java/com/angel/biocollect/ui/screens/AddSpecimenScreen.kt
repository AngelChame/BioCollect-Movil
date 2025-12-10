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
import com.angel.biocollect.data.models.Specimen
import com.angel.biocollect.ui.viewmodels.SpecimenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSpecimenScreen(
    userId: String,
    collectionId: String,
    viewModel: SpecimenViewModel,
    onNavigateBack: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var familia by remember { mutableStateOf("") }
    var especie by remember { mutableStateOf("") }
    var pais by remember { mutableStateOf("México") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar espécimen") },
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
                "Nuevo Espécimen",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                placeholder = { Text("ej. Escarabajo de la calabaza") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = familia,
                onValueChange = { familia = it },
                label = { Text("Familia") },
                placeholder = { Text("ej. Rosas") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = especie,
                onValueChange = { especie = it },
                label = { Text("Especie") },
                placeholder = { Text("ej. Desconocido") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            Text(
                "Ubicación",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = pais,
                onValueChange = { pais = it },
                label = { Text("País") },
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
                        val specimen = Specimen(
                            userId = userId,
                            collectionId = collectionId,
                            nombre = nombre,
                            familia = familia,
                            especie = especie,
                            pais = pais
                        )
                        viewModel.createSpecimen(specimen)
                        onNavigateBack()
                    },
                    modifier = Modifier.weight(1f),
                    enabled = nombre.isNotBlank() && familia.isNotBlank()
                ) {
                    Text("Guardar")
                }
            }
        }
    }
}