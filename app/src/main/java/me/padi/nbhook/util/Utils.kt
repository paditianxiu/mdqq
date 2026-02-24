package me.padi.nbhook.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import androidx.core.net.toUri
import kotlin.math.roundToInt


fun Context.getResourceId(resourceName: String, resourceType: String): Int {
    return try {
        resources.getIdentifier(resourceName, resourceType, packageName).takeIf { it != 0 }
            ?: throw Resources.NotFoundException("Resource $resourceType/$resourceName not found")
    } catch (e: Exception) {

        resources.getIdentifier(resourceName, resourceType, "android")
    }
}

fun Context.startUri(uri: String) {
    val intent = Intent(Intent.ACTION_VIEW, uri.toUri())
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    this.startActivity(intent)
}


fun Context.dp2px(dp: Int): Int {
    val scale = this.resources.displayMetrics.density
    return (dp * scale).roundToInt()
}

@SuppressLint("InternalInsetResource")
fun getStatusBarHeight(context: Context): Int {
    var result = 0
    val resourceId = context.resources.getIdentifier(
        "status_bar_height", "dimen", "android"
    )
    if (resourceId > 0) {
        result = context.resources.getDimensionPixelSize(resourceId)
    }
    return result
}