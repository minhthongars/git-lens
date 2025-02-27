package com.thongars.data.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteQueryBuilder
import com.thongars.data.database.DatabaseConstant
import com.thongars.data.database.UserDatabase

class UserContentProvider : ContentProvider() {

    companion object {
        // Authority được định nghĩa dựa trên package (phải là duy nhất)
        private const val AUTHORITY = "com.thongars.gitlens.provider"
        private const val PATH_USER = "users"

        // Content URI
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/$PATH_USER")

        private const val USERS = 1
        private const val USER_ID = 2

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, PATH_USER, USERS)
            addURI(AUTHORITY, "$PATH_USER/#", USER_ID)
        }
    }

    private lateinit var appDatabase: UserDatabase

    override fun onCreate(): Boolean {
        context?.let { ctx ->
            // allowMainThreadQueries() chỉ dùng cho demo, tránh trong production
            appDatabase = Room.databaseBuilder(ctx, UserDatabase::class.java, DatabaseConstant.DATABASE_NAME)
                .allowMainThreadQueries()
                .build()
        }
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor {
        val db = appDatabase.openHelper.readableDatabase

        val tableName = DatabaseConstant.USER_TABLE_NAME
        val builder = SupportSQLiteQueryBuilder.builder(tableName)
            .columns(projection)
            .selection(selection, selectionArgs)
            .orderBy(sortOrder ?: "")

        // Nếu URI chứa ID, lọc theo cột login
        when (uriMatcher.match(uri)) {
            USER_ID -> {
                val id = uri.lastPathSegment ?: throw IllegalArgumentException("Invalid URI: $uri")
                val newSelection = if (selection.isNullOrEmpty())
                    "${DatabaseConstant.LOGIN_FOREIGN_KEY} = ?"
                else
                    "${DatabaseConstant.LOGIN_FOREIGN_KEY} = ? AND ($selection)"
                val newSelectionArgs = if (selectionArgs == null)
                    arrayOf(id)
                else
                    arrayOf(id) + selectionArgs
                builder.selection(newSelection, newSelectionArgs)
            }
            USERS -> { /* không cần lọc thêm */ }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        val query = builder.create()
        val cursor = db.query(query)
        cursor.setNotificationUri(context?.contentResolver, uri)
        return cursor
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri {
        val db = appDatabase.openHelper.writableDatabase
        val tableName = DatabaseConstant.USER_TABLE_NAME
        val rowId = db.insert(tableName, 0, values!!)
        if (rowId > 0) {
            val returnUri = ContentUris.withAppendedId(CONTENT_URI, rowId)
            context?.contentResolver?.notifyChange(returnUri, null)
            return returnUri
        }
        throw android.database.SQLException("Failed to insert row into $uri")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        val db = appDatabase.openHelper.writableDatabase
        val tableName = DatabaseConstant.USER_TABLE_NAME
        val rowsUpdated: Int = when (uriMatcher.match(uri)) {
            USERS -> db.update(tableName, SQLiteDatabase.CONFLICT_REPLACE, values!!, selection, selectionArgs)
            USER_ID -> {
                val id = uri.lastPathSegment ?: throw IllegalArgumentException("Invalid URI: $uri")
                val newSelection = if (selection.isNullOrEmpty())
                    "${DatabaseConstant.LOGIN_FOREIGN_KEY} = ?"
                else
                    "${DatabaseConstant.LOGIN_FOREIGN_KEY} = ? AND ($selection)"
                val newSelectionArgs = if (selectionArgs == null)
                    arrayOf(id)
                else
                    arrayOf(id) + selectionArgs

                db.update(tableName, SQLiteDatabase.CONFLICT_REPLACE, values!!, newSelection, newSelectionArgs)
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        if (rowsUpdated > 0) {
            context?.contentResolver?.notifyChange(uri, null)
        }
        return rowsUpdated
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val db = appDatabase.openHelper.writableDatabase
        val tableName = DatabaseConstant.USER_TABLE_NAME
        val rowsDeleted: Int = when (uriMatcher.match(uri)) {
            USERS -> db.delete(tableName, selection, selectionArgs)
            USER_ID -> {
                val id = uri.lastPathSegment ?: throw IllegalArgumentException("Invalid URI: $uri")
                val newSelection = if (selection.isNullOrEmpty())
                    "${DatabaseConstant.LOGIN_FOREIGN_KEY} = ?"
                else
                    "${DatabaseConstant.LOGIN_FOREIGN_KEY} = ? AND ($selection)"
                val newSelectionArgs = if (selectionArgs == null)
                    arrayOf(id)
                else
                    arrayOf(id) + selectionArgs
                db.delete(tableName, newSelection, newSelectionArgs)
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        if (rowsDeleted > 0) {
            context?.contentResolver?.notifyChange(uri, null)
        }
        return rowsDeleted
    }

    override fun getType(uri: Uri): String {
        return when (uriMatcher.match(uri)) {
            USERS -> "vnd.android.cursor.dir/vnd.$AUTHORITY.$PATH_USER"
            USER_ID -> "vnd.android.cursor.item/vnd.$AUTHORITY.$PATH_USER"
            else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }
    }
}
