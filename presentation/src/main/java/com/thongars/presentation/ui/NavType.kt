package com.thongars.presentation.ui

import android.os.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

inline fun <reified T : Any?> serializableNavType(
    json: Json = Json
) = object : NavType<T?>(isNullableAllowed = null is T) {
    val encodeType = StandardCharsets.UTF_8.toString()
    override fun get(bundle: Bundle, key: String): T? {
        val encodedValue = bundle.getString(key)
        return if (encodedValue != null) {
            val decodedValue = URLDecoder.decode(encodedValue, encodeType)
            json.decodeFromString<T?>(decodedValue)
        } else {
            null
        }
    }
    override fun parseValue(value: String): T? {
        val decodedValue = URLDecoder.decode(value, encodeType)
        return json.decodeFromString<T?>(decodedValue)
    }
    override fun serializeAsValue(value: T?): String {
        return value?.let { URLEncoder.encode(json.encodeToString(it), encodeType) } ?: "null"
    }
    override fun put(bundle: Bundle, key: String, value: T?) {
        if (value != null) {
            val encodedValue = URLEncoder.encode(json.encodeToString(value), encodeType)
            bundle.putString(key, encodedValue)
        } else {
            bundle.putString(key, null)
        }
    }
}