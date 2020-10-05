package it.ap.mytemp.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "temperatures")
data class Temperature(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int,
    val day: String, // TODO: Change to more complex data.
    val hour: String,
    val temp: Double,
    val cough: Boolean?,
    val cold: Boolean?,
    val notes: String?
)