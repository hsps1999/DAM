package dam_a46104.catsndogs.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dam_a46104.catsndogs.compose.ui.details.DetailsScreen
import dam_a46104.catsndogs.compose.ui.main.MainScreen
import dam_a46104.catsndogs.compose.ui.theme.CatsNDogsTheme
import dam_a46104.catsndogs.compose.viewmodel.DetailsViewModel
import dam_a46104.catsndogs.compose.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val app = application as CatsNDogsComposeApp
        val repository = app.imageRepository

        setContent {
            CatsNDogsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "main") {
                        composable("main") {
                            val mainViewModel: MainViewModel = viewModel(
                                factory = MainViewModel.provideFactory(repository)
                            )
                            MainScreen(
                                viewModel = mainViewModel,
                                onImageClick = { imageId ->
                                    navController.navigate("details/$imageId")
                                }
                            )
                        }

                        composable(
                            route = "details/{imageId}",
                            arguments = listOf(navArgument("imageId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val imageId = backStackEntry.arguments?.getString("imageId") ?: return@composable
                            val detailsViewModel: DetailsViewModel = viewModel(
                                factory = DetailsViewModel.provideFactory(imageId, repository)
                            )
                            DetailsScreen(
                                viewModel = detailsViewModel,
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
