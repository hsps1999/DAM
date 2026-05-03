package dam_a46104.catsndogs

import android.app.Application
import dam_a46104.catsndogs.data.local.AppDatabase

/**
 * Classe Application da app CatsNDogs.
 *
 * Ponto de entrada do processo Android. Responsabilidades nesta fase:
 * - Inicializar o [AppDatabase] singleton antes de qualquer Activity arrancar.
 * - Expor [appDatabase] para injeção manual no [dam_a46104.catsndogs.data.repository.ImageRepository].
 *
 * Registada no manifesto via `android:name=".CatsNDogsApp"`.
 */
class CatsNDogsApp : Application() {

    /**
     * Instância singleton da base de dados Room.
     * Acessível via `(application as CatsNDogsApp).appDatabase` em qualquer Activity.
     */
    lateinit var appDatabase: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        appDatabase = AppDatabase.getInstance(this)
    }
}
