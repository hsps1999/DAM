package dam_a46104.catsndogs

import android.app.Application
import dam_a46104.catsndogs.core.local.AppDatabase

/**
 * Classe Application da app CatsNDogs (módulo :app-xml).
 *
 * Ponto de entrada do processo Android. Responsabilidades:
 * - Inicializar o [AppDatabase] singleton (agora em :core) antes de qualquer Activity arrancar.
 * - Expor [appDatabase] para injeção manual no [dam_a46104.catsndogs.core.repository.ImageRepository].
 *
 * Registada no manifesto via `android:name=".CatsNDogsApp"`.
 */
class CatsNDogsApp : Application() {

    /**
     * Instância singleton da base de dados Room (definida em :core).
     * Acessível via `(application as CatsNDogsApp).appDatabase` em qualquer Activity.
     */
    lateinit var appDatabase: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        appDatabase = AppDatabase.getInstance(this)
    }
}
