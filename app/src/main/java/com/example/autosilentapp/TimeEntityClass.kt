package com.example.autosilentapp

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "time_record",
    indices = [Index(value = ["startTime", "endTime"], unique = true)]
)
data class TimeEntities(

    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "startTime") val startTime: String,
    @ColumnInfo(name = "endTime") val endTime: String
) : Parcelable
