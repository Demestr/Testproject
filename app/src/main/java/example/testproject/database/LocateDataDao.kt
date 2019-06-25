package example.testproject.database

import androidx.room.*
import example.testproject.LocateData


@Dao
interface LocateDataDao {

    @get:Query("SELECT * FROM locatedata")
    val all: List<LocateData>

    @Query("SELECT * FROM locatedata WHERE id = :id")
    fun getById(id: Long): LocateData

    @Insert
    fun insert(employee: LocateData)

    @Update
    fun update(employee: LocateData)

    @Delete
    fun delete(employee: LocateData)

    @Query("DELETE FROM locatedata")
    fun clearAll()

}