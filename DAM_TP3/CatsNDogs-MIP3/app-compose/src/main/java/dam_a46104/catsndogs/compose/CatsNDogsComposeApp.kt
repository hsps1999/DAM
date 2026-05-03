package dam_a46104.catsndogs.compose

import android.app.Application
import dam_a46104.catsndogs.core.local.AppDatabase

class CatsNDogsComposeApp : Application() {
    lateinit var appDatabase: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        appDatabase = AppDatabase.getInstance(this)
    }
}
