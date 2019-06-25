package example.testproject.database

import android.content.Context
import androidx.room.RoomDatabase
import androidx.room.Database
import androidx.room.Room
import example.testproject.LocateData


@Database(entities = [LocateData::class], version = 2)
abstract class LocateDatabase : RoomDatabase() {
    abstract fun locateDataDao(): LocateDataDao

    companion object {

        @Volatile
        private var INSTANCE: LocateDatabase? = null

        fun getInstance(context: Context) : LocateDatabase {
            synchronized(this){
                var instance = INSTANCE

                if(instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        LocateDatabase::class.java,
                        "locates_history_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}