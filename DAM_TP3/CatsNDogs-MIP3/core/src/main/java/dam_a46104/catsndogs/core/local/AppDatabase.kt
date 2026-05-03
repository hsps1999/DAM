package dam_a46104.catsndogs.core.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Base de dados Room da aplicação CatsNDogs.
 *
 * **Singleton:** aceder sempre via [AppDatabase.getInstance].
 * Inicializado na `Application` class de cada módulo de app antes de qualquer Activity.
 *
 * **Versão 2 (Extensão F):** adicionada tabela `favorites` ([FavoriteEntry]).
 * Migração de 1→2 via [MIGRATION_1_2] — preserva os dados da `cached_images` existente.
 *
 * `exportSchema = false` — esquema não exportado para JSON nesta fase de desenvolvimento.
 */
@Database(
    entities = [CachedImage::class, FavoriteEntry::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    /** Acesso ao DAO de cache de imagens. */
    abstract fun cacheDao(): CacheDao

    /** Acesso ao DAO de favoritos. */
    abstract fun favoriteDao(): FavoriteDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Migration de versão 1 para 2.
         * Cria a tabela `favorites` sem destruir os dados da `cached_images`.
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS favorites (
                        id TEXT NOT NULL PRIMARY KEY,
                        url TEXT NOT NULL,
                        breed TEXT NOT NULL,
                        subBreed TEXT,
                        favoritedAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        /**
         * Devolve a instância singleton de [AppDatabase].
         * Thread-safe via double-checked locking.
         *
         * @param context Qualquer [Context] — usa [Context.getApplicationContext] internamente.
         */
        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cats_and_dogs.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build().also { INSTANCE = it }
            }
    }
}
