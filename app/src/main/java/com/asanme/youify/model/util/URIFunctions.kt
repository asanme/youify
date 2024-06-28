package com.asanme.youify.model.util

import java.net.URI

fun isUrlEmpty(url: String) = url.replace(" ", "") == ""

fun URI.findParameterValue(parameterName: String): String? {
    rawQuery?.let { query ->
        return query.split('&').map {
            val parts = it.split('=')
            val name = parts.firstOrNull() ?: ""
            val value = parts.drop(1).firstOrNull() ?: ""
            Pair(name, value)
        }.firstOrNull { it.first == parameterName }?.second
    }

    return null
}
