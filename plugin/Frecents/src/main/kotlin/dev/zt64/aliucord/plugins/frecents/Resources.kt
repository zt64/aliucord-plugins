package dev.zt64.aliucord.plugins.frecents

import com.aliucord.Utils

object Resources {
    val gif_category_item_icon by lazy { Utils.getResId("gif_category_item_icon", "id") }
    val gif_category_item_title by lazy { Utils.getResId("gif_category_item_title", "id") }
    val gif_category_title by lazy { Utils.getResId("gif_category_title", "id") }
    val gif_picker_result_type_trending_gifs by lazy {
        Utils.getResId("gif_picker_result_type_trending_gifs", "string")
    }
}