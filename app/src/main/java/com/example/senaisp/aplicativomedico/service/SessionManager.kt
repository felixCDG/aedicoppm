package com.example.senaisp.aplicativomedico.service

import android.content.Context
import android.util.Log

object SessionManager {
    private const val PREFS_NAME = "app_prefs"

    // Chaves para SharedPreferences
    private const val KEY_USER_ID = "key_user_id"
    private const val KEY_RESPONSAVEL_ID = "key_responsavel_id"
    private const val KEY_AUTH_TOKEN = "key_auth_token"
    private const val KEY_ROTINA_ID = "key_rotina_id"
    private const val KEY_ITEM_IDS = "key_item_ids"

    private const val KEY_MEDICO_ID = "key_medico_id"
    private const val KEY_MEDICO_USER_ID = "key_medico_user_id"

    // ------------------- USER ID -------------------
    fun saveUserId(context: Context, userId: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_USER_ID, userId).apply()
    }

    fun getUserId(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_USER_ID, -1)
    }

    fun clearUserId(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_USER_ID).apply()
    }

    // ------------------- RESPONSAVEL ID -------------------
    fun saveResponsavelId(context: Context, responsavelId: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_RESPONSAVEL_ID, responsavelId).apply()
    }

    fun getResponsavelId(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_RESPONSAVEL_ID, -1)
    }

    fun clearResponsavelId(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_RESPONSAVEL_ID).apply()
    }

    // ------------------- AUTH TOKEN -------------------
    fun saveAuthToken(context: Context, token: String) {
        Log.d("SESSION_MANAGER", "💾 Salvando token: $token")
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
        Log.d("SESSION_MANAGER", "✅ Token salvo com sucesso")
    }

    fun getAuthToken(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val token = prefs.getString(KEY_AUTH_TOKEN, null)
        Log.d("SESSION_MANAGER", "🔍 Recuperando token: $token")
        return token
    }

    fun clearAuthToken(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_AUTH_TOKEN).apply()
    }

    fun getBearerToken(context: Context): String? {
        val token = getAuthToken(context)
        val bearerToken = if (token != null) "Bearer $token" else null
        Log.d("SESSION_MANAGER", "🎯 getBearerToken - Token bruto: $token")
        Log.d("SESSION_MANAGER", "🎯 getBearerToken - Bearer formatado: $bearerToken")
        Log.d("SESSION_MANAGER", "🎯 getBearerToken - Token é null? ${token == null}")
        Log.d("SESSION_MANAGER", "🎯 getBearerToken - Token está vazio? ${token?.isEmpty()}")
        return bearerToken
    }

    // ------------------- ROTINA ID -------------------
    fun saveRotinaId(context: Context, rotinaId: Int) {
        Log.d("SESSION_MANAGER", "💾 Salvando ID da rotina: $rotinaId")
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_ROTINA_ID, rotinaId).apply()
        Log.d("SESSION_MANAGER", "✅ ID da rotina salvo com sucesso")
    }

    fun getRotinaId(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val rotinaId = prefs.getInt(KEY_ROTINA_ID, -1)
        Log.d("SESSION_MANAGER", "🔍 Recuperando ID da rotina: $rotinaId")
        return rotinaId
    }

    fun clearRotinaId(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_ROTINA_ID).apply()
    }

    // ------------------- ITEM IDS -------------------
    fun saveItemIds(context: Context, itemIds: List<Int>) {
        Log.d("SESSION_MANAGER", "💾 Salvando IDs dos itens: $itemIds")
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val idsString = itemIds.joinToString(",")
        prefs.edit().putString(KEY_ITEM_IDS, idsString).apply()
        Log.d("SESSION_MANAGER", "✅ IDs dos itens salvos com sucesso: $idsString")
    }

    fun getItemIds(context: Context): List<Int> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val idsString = prefs.getString(KEY_ITEM_IDS, "")
        val itemIds = if (idsString.isNullOrEmpty()) {
            emptyList()
        } else {
            idsString.split(",").mapNotNull { it.toIntOrNull() }
        }
        Log.d("SESSION_MANAGER", "🔍 Recuperando IDs dos itens: $itemIds")
        return itemIds
    }

    fun clearItemIds(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_ITEM_IDS).apply()
    }

    // ------------------- MEDICO ID -------------------
    fun saveMedicoId(context: Context, medicoId: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_MEDICO_ID, medicoId).apply()
    }

    fun getMedicoId(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_MEDICO_ID, -1)
    }

    fun clearMedicoId(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_MEDICO_ID).apply()
    }

    // ------------------- MEDICO USER ID -------------------
    fun saveMedicoUserId(context: Context, medicoUserId: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_MEDICO_USER_ID, medicoUserId).apply()
        Log.d("SESSION_MANAGER", "✅ medicoUserId salvo: $medicoUserId")
    }

    fun getMedicoUserId(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val id = prefs.getInt(KEY_MEDICO_USER_ID, -1)
        Log.d("SESSION_MANAGER", "🔍 recuperando medicoUserId: $id")
        return id
    }

    fun clearMedicoUserId(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_MEDICO_USER_ID).apply()
    }

    // ------------------- CLEAR ALL -------------------
    fun clearAll(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}
