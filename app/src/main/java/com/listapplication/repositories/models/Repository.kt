package com.listapplication.repositories.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.listapplication.utils.ConstantUtil.Companion.TABLE_REPO

@Entity(tableName = TABLE_REPO)
data class Repository(
    @PrimaryKey(autoGenerate = true)
    val auto_id: Int?,
    @ColumnInfo(name = "repo_author")
    val author: String?,
    @ColumnInfo(name = "repo_name")
    val name: String?,
    @ColumnInfo(name = "repo_avatar")
    val avatar: String?,
    @ColumnInfo(name = "repo_description")
    val description: String?,
    @ColumnInfo(name = "repo_language")
    val language: String?,
    @ColumnInfo(name = "repo_language_color")
    val languageColor: String?,
    @ColumnInfo(name = "repo_stars")
    val stars: Int?,
    @ColumnInfo(name = "repo_forks")
    val forks: Int?
)