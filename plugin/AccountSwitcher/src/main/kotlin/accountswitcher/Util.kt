@file:Suppress("MISSING_DEPENDENCY_SUPERCLASS", "MISSING_DEPENDENCY_SUPERCLASS_WARNING")

package accountswitcher

import AccountSwitcher.Companion.accounts
import android.content.Context
import com.aliucord.Http
import com.aliucord.api.SettingsAPI
import com.aliucord.utils.GsonUtils
import com.aliucord.utils.GsonUtils.fromJson
import com.aliucord.utils.GsonUtils.toJson
import com.discord.models.user.MeUser
import com.discord.utilities.rest.RestAPI
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

private val type: Type = object : TypeToken<ArrayList<Account>>() {}.type

class SharedPreferencesBackedMap(context: Context) : AbstractMutableMap<Long, Account>() {
    private val prefs = context.getSharedPreferences("AccountSwitcher", Context.MODE_PRIVATE)

    private fun getMap(): MutableMap<Long, Account> {
        val json = prefs.getString("accounts", "[]")
        val accounts: List<Account> = GsonUtils.gson.fromJson(json, type)
        return accounts.associateBy { it.id }.toMutableMap()
    }

    private fun saveMap(map: Map<Long, Account>) {
        val json = GsonUtils.gson.toJson(map.values)
        prefs.edit().putString("accounts", json).apply()
    }

    override val entries: MutableSet<MutableMap.MutableEntry<Long, Account>>
        get() = getMap().entries

    override fun put(key: Long, value: Account): Account? {
        val map = getMap()
        val previousValue = map.put(key, value)
        saveMap(map)
        return previousValue
    }

    override fun remove(key: Long): Account? {
        val map = getMap()
        val previousValue = map.remove(key)
        saveMap(map)
        return previousValue
    }

    fun toJson(): String {
        return GsonUtils.gsonPretty.toJson(getMap().values)
    }

    fun import(json: String): Int {
        val accounts: List<Account> = GsonUtils.gson.fromJson(json, type)
        putAll(accounts.associateBy { it.id })
        return accounts.size
    }
}

fun migrate(oldSettings: SettingsAPI) {
    if (!oldSettings.exists("accounts")) return

    oldSettings
        .getObject("accounts", ArrayList<Account>(), type)
        .forEach { accounts[it.id] = it }

    oldSettings.resetSettings()
}

fun fetchUser(token: String): MeUser? = try {
    Http
        .Request("https://discord.com/api/v9/users/@me")
        .setHeader("Authorization", token)
        .setHeader("User-Agent", RestAPI.AppHeadersProvider.INSTANCE.userAgent)
        .setHeader("accept", "application/json")
        .execute()
        .json(MeUser::class.java)
} catch (e: Throwable) {
    null
}