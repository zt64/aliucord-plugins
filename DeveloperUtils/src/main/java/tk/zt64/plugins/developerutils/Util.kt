package tk.zt64.plugins.developerutils

import android.view.View
import android.view.ViewGroup

fun View.getAllChildren(): List<View> {
    val result = ArrayList<View>()

    if (this !is ViewGroup)
        result.add(this)
    else
        repeat(childCount) { getChildAt(it)?.getAllChildren()?.let { it1 -> result.addAll(it1) } }

    return result
}
