package com.aliucord.plugins.accountswitcher

import com.aliucord.Http
import com.discord.models.user.MeUser
import com.discord.stores.StoreAuthentication
import com.discord.utilities.rest.RestAPI

val StoreAuthentication.authToken: String
    get() = this.`authToken$app_productionBetaRelease`

fun fetchUser(token: String): MeUser? = try {
    Http.Request("https://discord.com/api/v9/users/@me")
            .setHeader("Authorization", token)
            .setHeader("User-Agent", RestAPI.AppHeadersProvider.INSTANCE.userAgent)
            .setHeader("accept", "application/json")
            .execute()
            .json(MeUser::class.java)
} catch (e: Throwable) {
    null
}