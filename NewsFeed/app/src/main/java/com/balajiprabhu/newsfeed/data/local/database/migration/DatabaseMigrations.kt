package com.balajiprabhu.newsfeed.data.local.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE posts ADD COLUMN isLiked INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE posts ADD COLUMN syncStatus TEXT NOT NULL DEFAULT 'SYNCED'")
    }
}
