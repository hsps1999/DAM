package dam_a46104.catsndogs.core.local

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class FavoriteDao_Impl(
  __db: RoomDatabase,
) : FavoriteDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfFavoriteEntry: EntityInsertAdapter<FavoriteEntry>
  init {
    this.__db = __db
    this.__insertAdapterOfFavoriteEntry = object : EntityInsertAdapter<FavoriteEntry>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `favorites` (`id`,`url`,`breed`,`subBreed`,`favoritedAt`) VALUES (?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: FavoriteEntry) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.url)
        statement.bindText(3, entity.breed)
        val _tmpSubBreed: String? = entity.subBreed
        if (_tmpSubBreed == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpSubBreed)
        }
        statement.bindLong(5, entity.favoritedAt)
      }
    }
  }

  public override suspend fun insert(entry: FavoriteEntry): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfFavoriteEntry.insert(_connection, entry)
  }

  public override fun getAll(): Flow<List<FavoriteEntry>> {
    val _sql: String = "SELECT * FROM favorites ORDER BY favoritedAt ASC"
    return createFlow(__db, false, arrayOf("favorites")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUrl: Int = getColumnIndexOrThrow(_stmt, "url")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfSubBreed: Int = getColumnIndexOrThrow(_stmt, "subBreed")
        val _columnIndexOfFavoritedAt: Int = getColumnIndexOrThrow(_stmt, "favoritedAt")
        val _result: MutableList<FavoriteEntry> = mutableListOf()
        while (_stmt.step()) {
          val _item: FavoriteEntry
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpUrl: String
          _tmpUrl = _stmt.getText(_columnIndexOfUrl)
          val _tmpBreed: String
          _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          val _tmpSubBreed: String?
          if (_stmt.isNull(_columnIndexOfSubBreed)) {
            _tmpSubBreed = null
          } else {
            _tmpSubBreed = _stmt.getText(_columnIndexOfSubBreed)
          }
          val _tmpFavoritedAt: Long
          _tmpFavoritedAt = _stmt.getLong(_columnIndexOfFavoritedAt)
          _item = FavoriteEntry(_tmpId,_tmpUrl,_tmpBreed,_tmpSubBreed,_tmpFavoritedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun count(): Int {
    val _sql: String = "SELECT COUNT(*) FROM favorites"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getOldest(): FavoriteEntry? {
    val _sql: String = "SELECT * FROM favorites ORDER BY favoritedAt ASC LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUrl: Int = getColumnIndexOrThrow(_stmt, "url")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfSubBreed: Int = getColumnIndexOrThrow(_stmt, "subBreed")
        val _columnIndexOfFavoritedAt: Int = getColumnIndexOrThrow(_stmt, "favoritedAt")
        val _result: FavoriteEntry?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpUrl: String
          _tmpUrl = _stmt.getText(_columnIndexOfUrl)
          val _tmpBreed: String
          _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          val _tmpSubBreed: String?
          if (_stmt.isNull(_columnIndexOfSubBreed)) {
            _tmpSubBreed = null
          } else {
            _tmpSubBreed = _stmt.getText(_columnIndexOfSubBreed)
          }
          val _tmpFavoritedAt: Long
          _tmpFavoritedAt = _stmt.getLong(_columnIndexOfFavoritedAt)
          _result = FavoriteEntry(_tmpId,_tmpUrl,_tmpBreed,_tmpSubBreed,_tmpFavoritedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun isFavorite(id: String): Flow<Boolean> {
    val _sql: String = "SELECT EXISTS(SELECT 1 FROM favorites WHERE id = ?)"
    return createFlow(__db, false, arrayOf("favorites")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _result: Boolean
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp != 0
        } else {
          _result = false
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun isFavoriteSync(id: String): Boolean {
    val _sql: String = "SELECT EXISTS(SELECT 1 FROM favorites WHERE id = ?)"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _result: Boolean
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp != 0
        } else {
          _result = false
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun findByIdSync(id: String): FavoriteEntry? {
    val _sql: String = "SELECT * FROM favorites WHERE id = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUrl: Int = getColumnIndexOrThrow(_stmt, "url")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfSubBreed: Int = getColumnIndexOrThrow(_stmt, "subBreed")
        val _columnIndexOfFavoritedAt: Int = getColumnIndexOrThrow(_stmt, "favoritedAt")
        val _result: FavoriteEntry?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpUrl: String
          _tmpUrl = _stmt.getText(_columnIndexOfUrl)
          val _tmpBreed: String
          _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          val _tmpSubBreed: String?
          if (_stmt.isNull(_columnIndexOfSubBreed)) {
            _tmpSubBreed = null
          } else {
            _tmpSubBreed = _stmt.getText(_columnIndexOfSubBreed)
          }
          val _tmpFavoritedAt: Long
          _tmpFavoritedAt = _stmt.getLong(_columnIndexOfFavoritedAt)
          _result = FavoriteEntry(_tmpId,_tmpUrl,_tmpBreed,_tmpSubBreed,_tmpFavoritedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteById(id: String) {
    val _sql: String = "DELETE FROM favorites WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
