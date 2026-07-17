package com.linxia.exam.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index

@Entity(
    tableName = "offline_cache",
    indices = [Index(value = ["cache_key"], unique = true)]
)
data class OfflineCache(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "cache_key") var cacheKey: String = "",
    @ColumnInfo(name = "cache_data") var cacheData: String = "",
    var version: Int = 1,
    @ColumnInfo(name = "expire_time") var expireTime: Long = 0,
    @ColumnInfo(name = "created_at") var createdAt: Long = System.currentTimeMillis()
)