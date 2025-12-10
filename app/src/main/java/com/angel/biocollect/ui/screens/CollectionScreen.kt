package com.angel.biocollect.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.angel.biocollect.data.models.Specimen
import com.angel.biocollect.ui.viewmodels.SpecimenViewModel
import com.angel.biocollect.ui.viewmodels.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionScreen(
    collectionId: String,
    collectionName: String,
    collectionCategory: String,
    viewModel: SpecimenViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAddSpecimen: () -> Unit,
    onNavigateToProfile: () -> Unit,
) {
    val specimens by viewModel.specimens.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(collectionId) {
        viewModel.loadSpecimens(collectionId)
    }

    val filteredSpecimens = remember(specimens, searchQuery) {
        specimens.filter {
            searchQuery.isEmpty() ||
                    it.nombre.contains(searchQuery, ignoreCase = true) ||
                    it.familia.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BioCollect") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToAddSpecimen) {
                        Icon(Icons.Default.Add, "Agregar espécimen")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false,
                    onClick = { /* Navegar a inicio, aunque ya estamos aquí */ },
                    icon = { Icon(Icons.Default.Home, "Inicio") },
                    label = { Text("Inicio") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = {  },
                    icon = { Icon(Icons.Default.List, "Registros") },
                    label = { Text("Registros") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToProfile,
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
                .padding(16.dp)
        ) {
            // Información de la colección
            Text(
                collectionName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                collectionCategory,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Barra de búsqueda
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar espécimen") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp)
            )

            // Grid de especímenes
            if (filteredSpecimens.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                        Text(
                            "No hay especímenes en esta colección",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredSpecimens) { specimen ->
                        SpecimenCard(
                            specimen = specimen,
                            onClick = { /* Ver detalle */ }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SpecimenCard(
    specimen: Specimen,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable(onClick = onClick)
    ) {
        Box {
            // Fondo de color
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.tertiaryContainer,
                                MaterialTheme.colorScheme.secondaryContainer
                            )
                        )
                    )
            )

            // Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Text(
                    specimen.nombre,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    specimen.familia,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}