package com.angel.biocollect.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.angel.biocollect.data.models.User
import com.angel.biocollect.ui.viewmodels.AuthViewModel
import com.angel.biocollect.ui.viewmodels.AuthState

@Composable
fun SignUpScreen(
    viewModel: AuthViewModel,
    onSignUpSuccess: () -> Unit,
    onNavigateToSignIn: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var institucion by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            onSignUpSuccess()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Crea una cuenta",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre") },
                        placeholder = { Text("ej. Cesar Yair") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = apellidos,
                        onValueChange = { apellidos = it },
                        label = { Text("Apellidos") },
                        placeholder = { Text("ej. Toledo Villarreal") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = institucion,
                        onValueChange = { institucion = it },
                        label = { Text("Institución (Opcional)") },
                        placeholder = { Text("ej. Ecosur") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Gmail") },
                        placeholder = { Text("mail@gmail.com") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        placeholder = { Text("Contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                    )

                    Button(
                        onClick = {
                            val user = User(
                                nombre = nombre,
                                apellidos = apellidos,
                                institucion = institucion
                            )
                            viewModel.signUp(email, password, user)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = nombre.isNotBlank() &&
                                apellidos.isNotBlank() &&
                                email.isNotBlank() &&
                                password.length >= 6
                    ) {
                        if (authState is AuthState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Crear cuenta")
                        }
                    }

                    TextButton(
                        onClick = onNavigateToSignIn,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("¿Ya tienes cuenta? Inicia sesión")
                    }

                    if (authState is AuthState.Error) {
                        Text(
                            text = (authState as AuthState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}