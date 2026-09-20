package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "announcements")
data class Announcement(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val author: String = "Direksi / Human Capital Management",
    val dateText: String,
    val isPriority: Boolean = false,
    val isPushBroadcasted: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
