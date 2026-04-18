package com.traveling

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.traveling.ui.navigation.Screen
import com.traveling.ui.theme.TravelingDeepPurple
import com.traveling.ui.theme.TravelingTheme
import com.traveling.ui.travelpath.CreateGroupScreen
import com.traveling.ui.travelpath.CreatePathScreen
import com.traveling.ui.travelpath.GroupDetailScreen
import com.traveling.ui.travelpath.TravelPathScreen
import com.traveling.ui.travelshare.AuthViewModel
import com.traveling.ui.travelshare.CreatePostScreen
import com.traveling.ui.travelshare.FeedScreen
import com.traveling.ui.travelshare.HomeScreen
import com.traveling.ui.travelshare.LoginScreen
import com.traveling.ui.travelshare.MapScreen
import com.traveling.ui.travelshare.PostDetailScreen
import com.traveling.ui.travelshare.PostViewModel
import com.traveling.ui.travelshare.ProfileScreen
import com.traveling.ui.travelshare.SignupScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TravelingTheme {
                MainNavigation()
            }
        }
    }
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // ViewModels partagés au niveau de la navigation
    val authViewModel: AuthViewModel = hiltViewModel()
    val postViewModel: PostViewModel = hiltViewModel()

    Scaffold(
        bottomBar = {
            val hideBottomBar = currentRoute?.startsWith("post_detail") == true || 
                                currentRoute?.startsWith("group_detail") == true ||
                                currentRoute == Screen.CreatePost.route ||
                                currentRoute?.startsWith("create_post") == true ||
                                currentRoute == Screen.Login.route ||
                                currentRoute == Screen.Signup.route ||
                                currentRoute == Screen.CreateGroup.route ||
                                currentRoute == Screen.CreatePath.route
            
            if (!hideBottomBar) {
                NavigationBar(
                    containerColor = TravelingDeepPurple,
                    contentColor = Color.White
                ) {
                    NavigationBarItem(
                        selected = currentRoute == Screen.Home.route,
                        onClick = { 
                            navController.navigate(Screen.Home.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = null, tint = Color.White) }
                    )
                    NavigationBarItem(
                        selected = currentRoute?.startsWith("feed") == true,
                        onClick = { 
                            navController.navigate(Screen.Feed.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(Icons.Default.Language, contentDescription = null, tint = Color.White) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Screen.Map.route,
                        onClick = { 
                            navController.navigate(Screen.Map.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(Icons.Default.Map, contentDescription = null, tint = Color.White) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Screen.Path.route,
                        onClick = { 
                            navController.navigate(Screen.Path.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(Icons.AutoMirrored.Filled.DirectionsWalk, contentDescription = null, tint = Color.White) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Screen.Profile.route,
                        onClick = { 
                            navController.navigate(Screen.Profile.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Color.White) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = postViewModel,
                    onPostClick = { postId ->
                        navController.navigate(Screen.PostDetail.createRoute(postId))
                    }
                )
            }
            composable(
                route = "feed?groupName={groupName}&search={search}",
                arguments = listOf(
                    navArgument("groupName") { 
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                    navArgument("search") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val groupName = backStackEntry.arguments?.getString("groupName")
                val search = backStackEntry.arguments?.getString("search")
                FeedScreen(
                    viewModel = postViewModel,
                    authViewModel = authViewModel,
                    initialGroupName = groupName,
                    initialSearch = search,
                    onPostClick = { postId ->
                        navController.navigate(Screen.PostDetail.createRoute(postId))
                    },
                    onCreatePostClick = {
                        navController.navigate(Screen.CreatePost.route)
                    }
                )
            }
            composable(Screen.Map.route) {
                MapScreen(
                    onNavigateToFeed = { search ->
                        navController.navigate(Screen.Feed.createRoute(search = search))
                    },
                    onNavigateToCreatePost = { location, lat, lon ->
                        navController.navigate(Screen.CreatePost.createRoute(location, lat, lon))
                    },
                    viewModel = postViewModel
                )
            }
            composable(
                route = Screen.PostDetail.route,
                arguments = listOf(navArgument("postId") { type = NavType.StringType })
            ) { backStackEntry ->
                val postId = backStackEntry.arguments?.getString("postId") ?: ""
                PostDetailScreen(
                    postId = postId, 
                    onBack = { navController.popBackStack() },
                    viewModel = postViewModel,
                    authViewModel = authViewModel
                )
            }
            composable(
                route = "create_post?location={location}&lat={lat}&lon={lon}",
                arguments = listOf(
                    navArgument("location") { type = NavType.StringType; nullable = true; defaultValue = null },
                    navArgument("lat") { type = NavType.FloatType; defaultValue = 0f },
                    navArgument("lon") { type = NavType.FloatType; defaultValue = 0f }
                )
            ) { backStackEntry ->
                val location = backStackEntry.arguments?.getString("location")
                val lat = backStackEntry.arguments?.getFloat("lat")?.toDouble()
                val lon = backStackEntry.arguments?.getFloat("lon")?.toDouble()
                CreatePostScreen(
                    onBack = { navController.popBackStack() },
                    initialLocation = location,
                    initialLat = lat,
                    initialLon = lon,
                    viewModel = postViewModel,
                    authViewModel = authViewModel
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    viewModel = authViewModel,
                    onLoginClick = { navController.navigate(Screen.Login.route) },
                    onSignupClick = { navController.navigate(Screen.Signup.route) }
                )
            }
            composable(Screen.Login.route) {
                LoginScreen(
                    viewModel = authViewModel,
                    onLoginSuccess = {
                        navController.navigate(Screen.Profile.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Signup.route) {
                SignupScreen(
                    viewModel = authViewModel,
                    onSignupSuccess = {
                        navController.navigate(Screen.Profile.route) {
                            popUpTo(Screen.Signup.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Path.route) {
                TravelPathScreen(
                    onCreatePathClick = { navController.navigate(Screen.CreatePath.route) },
                    onCreateGroupClick = { navController.navigate(Screen.CreateGroup.route) },
                    onGroupClick = { groupId ->
                        navController.navigate(Screen.GroupDetail.createRoute(groupId))
                    },
                    viewModel = postViewModel
                )
            }
            composable(Screen.CreateGroup.route) {
                CreateGroupScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.CreatePath.route) {
                CreatePathScreen(onBack = { navController.popBackStack() })
            }
            composable(
                route = Screen.GroupDetail.route,
                arguments = listOf(navArgument("groupId") { type = NavType.IntType })
            ) { backStackEntry ->
                val groupId = backStackEntry.arguments?.getInt("groupId") ?: 0
                GroupDetailScreen(
                    groupId = groupId,
                    onBack = { navController.popBackStack() },
                    onNavigateToGroupFeed = { groupName ->
                        navController.navigate(Screen.Feed.createRoute(groupName = groupName))
                    },
                    viewModel = postViewModel
                )
            }
        }
    }
}
