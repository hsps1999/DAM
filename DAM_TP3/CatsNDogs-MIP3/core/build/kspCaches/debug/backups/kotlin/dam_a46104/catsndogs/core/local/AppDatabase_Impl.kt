package dam_a46104.catsndogs.core.local

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDatabase_Impl : AppDatabase() {
  private val _cacheDao: Lazy<CacheDao> = lazy {
    CacheDao_Impl(this)
  }

  private val _favoriteDao: Lazy<FavoriteDao> = lazy {
    FavoriteDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(2,
        "8a096e395bacfc3fe68cf422c526d70f", "95da4e51976733bc329afc91fba7ac06") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `cached_images` (`id` TEXT NOT NULL, `url` TEXT NOT NULL, `breed` TEXT NOT NULL, `subBreed` TEXT, `cachedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `favorites` (`id` TEXT NOT NULL, `url` TEXT NOT NULL, `breed` TEXT NOT NULL, `subBreed` TEXT, `favoritedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '8a096e395bacfc3fe68cf422c526d70f')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `cached_images`")
        connection.execSQL("DROP TABLE IF EXISTS `favorites`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsCachedImages: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsCachedImages.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedImages.put("url", TableInfo.Column("url", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedImages.put("breed", TableInfo.Column("breed", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedImages.put("subBreed", TableInfo.Column("subBreed", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedImages.put("cachedAt", TableInfo.Column("cachedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysCachedImages: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesCachedImages: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoCachedImages: TableInfo = TableInfo("cached_images", _columnsCachedImages,
            _foreignKeysCachedImages, _indicesCachedImages)
        val _existingCachedImages: TableInfo = read(connection, "cached_images")
        if (!_infoCachedImages.equals(_existingCachedImages)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |cached_images(dam_a46104.catsndogs.core.local.CachedImage).
              | Expected:
              |""".trimMargin() + _infoCachedImages + """
              |
              | Found:
              |""".trimMargin() + _existingCachedImages)
        }
        val _columnsFavorites: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsFavorites.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFavorites.put("url", TableInfo.Column("url", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFavorites.put("breed", TableInfo.Column("breed", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFavorites.put("subBreed", TableInfo.Column("subBreed", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFavorites.put("favoritedAt", TableInfo.Column("favoritedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysFavorites: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesFavorites: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoFavorites: TableInfo = TableInfo("favorites", _columnsFavorites,
            _foreignKeysFavorites, _indicesFavorites)
        val _existingFavorites: TableInfo = read(connection, "favorites")
        if (!_infoFavorites.equals(_existingFavorites)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |favorites(dam_a46104.catsndogs.core.local.FavoriteEntry).
              | Expected:
              |""".trimMargin() + _infoFavorites + """
              |
              | Found:
              |""".trimMargin() + _existingFavorites)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "cached_images", "favorites")
  }

  public override fun clearAllTables() {
    super.performClear(false, "cached_images", "favorites")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(CacheDao::class, CacheDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(FavoriteDao::class, FavoriteDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun cacheDao(): CacheDao = _cacheDao.value

  public override fun favoriteDao(): FavoriteDao = _favoriteDao.value
}
