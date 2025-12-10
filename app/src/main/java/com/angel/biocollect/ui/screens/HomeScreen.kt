// ui/screens/HomeScreen.kt
package com.angel.biocollect.ui.screens // ⬅️ Corregido a 'angel.biocollect' según tu package

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
import androidx.compose.ui.graphics.vector.ImageVector // Importación explícita
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.angel.biocollect.data.models.Collection // ⬅️ Corregido a 'angel.biocollect'
import com.angel.biocollect.ui.viewmodels.UserViewModel // ⬅️ Corregido a 'angel.biocollect'
import com.angel.biocollect.ui.viewmodels.CollectionViewModel // ⬅️ Corregido a 'angel.biocollect'

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userId: String,
    userViewModel: UserViewModel,
    collectionViewModel: CollectionViewModel,
    onNavigateToCollection: (String, String, String) -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToAddCollection: () -> Unit,
) {
    val user by userViewModel.user.collectAsState()
    val collections by collectionViewModel.collections.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    // ✅ MEJORA 1: Cálculo de especímenes cacheado con 'remember'
    val totalSpecimens = remember(collections) {
        collections.sumOf { it.especimenesCount }
    }

    LaunchedEffect(userId) {
        userViewModel.loadUser(userId)
        collectionViewModel.loadCollections(userId)
    }

    // Filtra las colecciones (ya protegido si 'collections' no es null)
    val filteredCollections = remember(collections, searchQuery) {
        collections.filter {
            searchQuery.isEmpty() ||
                    it.nombre.contains(searchQuery, ignoreCase = true) ||
                    it.categoria.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Hola, ${user?.nombre ?: "Usuario"}",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            "Bienvenido a BioCollect",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Notificaciones */ }) {
                        Icon(Icons.Default.Notifications, "Notificaciones")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Navegar a inicio, aunque ya estamos aquí */ },
                    icon = { Icon(Icons.Default.Home, "Inicio") },
                    label = { Text("Inicio") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigateToCollection },
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddCollection,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, "Agregar colección")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp) // Reducido el padding vertical para mejor uso de espacio
        ) {
            // Barra de búsqueda
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp), // Ajustado padding
                shape = RoundedCornerShape(12.dp)
            )

            // Tarjetas de estadísticas
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Colecciones",
                    value = collections.size.toString(),
                    icon = Icons.Default.CheckCircle,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Especímenes",
                    value = totalSpecimens.toString(), // ✅ Usa el valor cacheado
                    icon = Icons.Default.Add,
                    modifier = Modifier.weight(1f)
                )
            }

            // Sección de colecciones
            Text(
                "Colecciones",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (filteredCollections.isEmpty()) {
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
                            if (searchQuery.isEmpty()) "No hay colecciones aún" else "No se encontraron resultados",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        if (searchQuery.isEmpty()) {
                            Text(
                                "Presiona el botón + para crear una",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredCollections, key = { it.id }) { collection -> // ✅ Añadido key para mejor rendimiento
                        CollectionCard(
                            collection = collection,
                            onClick = {
                                onNavigateToCollection(
                                    collection.id,
                                    collection.nombre,
                                    collection.categoria
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

// ✅ CORRECCIÓN: La firma de la función StatCard ahora usa el tipo correcto ImageVector
@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector, // Utiliza el tipo importado
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        // ✅ MEJORA 2: Añadida elevación para que parezca una tarjeta
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    title,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun CollectionCard(
    collection: Collection,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable(onClick = onClick),
        // ✅ MEJORA 2: Añadida elevación también a CollectionCard
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box {
            // Fondo de color si no hay imagen
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.secondaryContainer
                            )
                        )
                    )
            )

            // Overlay con información
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
                    collection.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    collection.categoria,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}