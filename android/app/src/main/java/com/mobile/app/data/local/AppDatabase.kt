package com.mobile.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

// Placeholder entity for now
@androidx.room.Entity
data class DummyEntity(
    @androidx.room.PrimaryKey val id: Int
)

@androidx.room.Dao
interface DummyDao {
    @androidx.room.Query("SELECT * FROM DummyEntity")
    suspend fun getAll(): List<DummyEntity>
}

@Database(entities = [DummyEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dummyDao(): DummyDao
}
