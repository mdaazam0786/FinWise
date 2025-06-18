package com.example.finwise.data.model.image

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_image")
data class UserImage(
    @PrimaryKey
    val id :String = "",
    val imagePath : ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserImage

        if (id != other.id) return false
        if (imagePath != null) {
            if (other.imagePath == null) return false
            if (!imagePath.contentEquals(other.imagePath)) return false
        } else if (other.imagePath != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (imagePath?.contentHashCode() ?: 0)
        return result
    }
}
