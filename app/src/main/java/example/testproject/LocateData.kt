package example.testproject

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class LocateData(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val date: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val weather: String
) : Serializable