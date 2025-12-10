package com.angel.biocollect.ui.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.angel.biocollect.ui.screens.*
import com.angel.biocollect.ui.viewmodels.*

sealed class Screen(val route: String) {
    object SignUp : Screen("signup")
    object SignIn : Screen("signin")
    object Home : Screen("home/{userId}") {
        fun createRoute(userId: String) = "home/$userId"
    }
    object Collection : Screen("collection/{collectionId}/{name}/{category}") {
        fun createRoute(collectionId: String, name: String, category: String) =
            "collection/$collectionId/$name/$category"
    }
    object AddCollection : Screen("add_collection/{userId}") {
        fun createRoute(userId: String) = "add_collection/$userId"
    }
    object AddSpecimen : Screen("add_specimen/{userId}/{collectionId}") {
        fun createRoute(userId: String, collectionId: String) =
            "add_specimen/$userId/$collectionId"
    }
    object Profile : Screen("profile/{userId}") {
        fun createRoute(userId: String) = "profile/$userId"
    }
}

@Composable
fun BioCollectNavigation(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel()
) {
    val authState by authViewModel.authState.collectAsState()

    val startDestination = when (authState) {
        is AuthState.Authenticated -> Screen.Home.createRoute((authState as AuthState.Authenticated).userId)
        else -> Screen.SignIn.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // ==================== AUTH SCREENS ====================

        composable(Screen.SignUp.route) {
            SignUpScreen(
                viewModel = authViewModel,
                onSignUpSuccess = {
                    val userId = (authViewModel.authState.value as? AuthState.Authenticated)?.userId
                    userId?.let {
                        navController.navigate(Screen.Home.createRoute(it)) {
                            popUpTo(Screen.SignUp.route) { inclusive = true }
                        }
                    }
                },
                onNavigateToSignIn = {
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.SignIn.route) {
            SignInScreen(
                viewModel = authViewModel,
                onSignInSuccess = {
                    val userId = (authViewModel.authState.value as? AuthState.Authenticated)?.userId
                    userId?.let {
                        navController.navigate(Screen.Home.createRoute(it)) {
                            popUpTo(Screen.SignIn.route) { inclusive = true }
                        }
                    }
                },
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUp.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                }
            )
        }

        // ==================== HOME SCREEN ====================

        composable(
            route = Screen.Home.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            val userViewModel: UserViewModel = viewModel()
            val collectionViewModel: CollectionViewModel = viewModel()

            HomeScreen(
                userId = userId,
                userViewModel = userViewModel,
                collectionViewModel = collectionViewModel,
                onNavigateToCollection = { collectionId, name, category ->
                    navController.navigate(
                        Screen.Collection.createRoute(collectionId, name, category)
                    )
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.createRoute(userId))
                },
                onNavigateToAddCollection = {
                    navController.navigate(Screen.AddCollection.createRoute(userId))
                }
            )
        }

        // ==================== COLLECTION SCREEN ====================

        composable(
            route = Screen.Collection.route,
            arguments = listOf(
                navArgument("collectionId") { type = NavType.StringType },
                navArgument("name") { type = NavType.StringType },
                navArgument("category") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val collectionId = backStackEntry.arguments?.getString("collectionId") ?: return@composable
            val name = backStackEntry.arguments?.getString("name") ?: ""
            val category = backStackEntry.arguments?.getString("category") ?: ""
            val specimenViewModel: SpecimenViewModel = viewModel()
            val userId = (authState as? AuthState.Authenticated)?.userId ?: return@composable

            CollectionScreen(
                collectionId = collectionId,
                collectionName = name,
                collectionCategory = category,
                viewModel = specimenViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddSpecimen = {
                    navController.navigate(Screen.AddSpecimen.createRoute(userId, collectionId))
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.createRoute(userId))
                },
            )
        }

        // ==================== ADD COLLECTION SCREEN ====================

        composable(
            route = Screen.AddCollection.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            val collectionViewModel: CollectionViewModel = viewModel()

            AddCollectionScreen(
                userId = userId,
                viewModel = collectionViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ==================== ADD SPECIMEN SCREEN ====================

        composable(
            route = Screen.AddSpecimen.route,
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType },
                navArgument("collectionId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            val collectionId = backStackEntry.arguments?.getString("collectionId") ?: return@composable
            val specimenViewModel: SpecimenViewModel = viewModel()

            AddSpecimenScreen(
                userId = userId,
                collectionId = collectionId,
                viewModel = specimenViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ==================== PROFILE SCREEN ====================

        composable(
            route = Screen.Profile.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            val userViewModel: UserViewModel = viewModel()

            ProfileScreen(
                userId = userId,
                viewModel = userViewModel,
                onNavigateBack = { navController.popBackStack() },
                onSignOut = {
                    authViewModel.signOut()
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}