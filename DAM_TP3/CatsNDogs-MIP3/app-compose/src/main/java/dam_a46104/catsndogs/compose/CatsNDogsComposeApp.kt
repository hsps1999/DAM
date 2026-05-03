package dam_a46104.catsndogs.compose

import android.app.Application
import dam_a46104.catsndogs.core.local.AppDatabase
import dam_a46104.catsndogs.core.remote.RetrofitClient
import dam_a46104.catsndogs.core.repository.ImageRepository

class CatsNDogsComposeApp : Application() {
    val database by lazy { AppDatabase.getInstance(this) }
    val apiService by lazy { RetrofitClient.dogApiService }

    val imageRepository by lazy {
        ImageRepository.getInstance(
            api = apiService,
            database = database
        )
    }
}
