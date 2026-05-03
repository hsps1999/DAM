package dam_a46104.catsndogs.core.local

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class CacheDao_Impl(
  __db: RoomDatabase,
) : CacheDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfCachedImage: EntityInsertAdapter<CachedImage>
  init {
    this.__db = __db
    this.__insertAdapterOfCachedImage = object : EntityInsertAdapter<CachedImage>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `cached_images` (`id`,`url`,`breed`,`subBreed`,`cachedAt`) VALUES (?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: CachedImage) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.url)
        statement.bindText(3, entity.breed)
        val _tmpSubBreed: String? = entity.subBreed
        if (_tmpSubBreed == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpSubBreed)
        }
        statement.bindLong(5, entity.cachedAt)
      }
    }
  }

  public override suspend fun insertAll(images: List<CachedImage>): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfCachedImage.insert(_connection, images)
  }

  public override suspend fun getAllCached(): List<CachedImage> {
    val _sql: String = "SELECT * FROM cached_images ORDER BY cachedAt DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUrl: Int = getColumnIndexOrThrow(_stmt, "url")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfSubBreed: Int = getColumnIndexOrThrow(_stmt, "subBreed")
        val _columnIndexOfCachedAt: Int = getColumnIndexOrThrow(_stmt, "cachedAt")
        val _result: MutableList<CachedImage> = mutableListOf()
        while (_stmt.step()) {
          val _item: CachedImage
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
          val _tmpCachedAt: Long
          _tmpCachedAt = _stmt.getLong(_columnIndexOfCachedAt)
          _item = CachedImage(_tmpId,_tmpUrl,_tmpBreed,_tmpSubBreed,_tmpCachedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun findById(id: String): CachedImage? {
    val _sql: String = "SELECT * FROM cached_images WHERE id = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUrl: Int = getColumnIndexOrThrow(_stmt, "url")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfSubBreed: Int = getColumnIndexOrThrow(_stmt, "subBreed")
        val _columnIndexOfCachedAt: Int = getColumnIndexOrThrow(_stmt, "cachedAt")
        val _result: CachedImage?
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
          val _tmpCachedAt: Long
          _tmpCachedAt = _stmt.getLong(_columnIndexOfCachedAt)
          _result = CachedImage(_tmpId,_tmpUrl,_tmpBreed,_tmpSubBreed,_tmpCachedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun pruneToLimit() {
    val _sql: String = """
        |
        |        DELETE FROM cached_images
        |        WHERE id NOT IN (
        |            SELECT id FROM cached_images
        |            ORDER BY cachedAt DESC
        |            LIMIT 50
        |        )
        |        
        """.trimMargin()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
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
