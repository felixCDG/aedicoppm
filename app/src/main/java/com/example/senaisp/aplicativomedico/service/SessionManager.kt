package com.example.senaisp.aplicativomedico.service

import android.content.Context

object SessionManager {
    private const val PREFS_NAME = "app_prefs"

    // Chaves para SharedPreferences
    private const val KEY_USER_ID = "key_user_id"
    private const val KEY_RESPONSAVEL_ID = "key_responsavel_id"
    private const val KEY_MEDICO_ID = "key_medico_id"

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

    // ------------------- CLEAR ALL -------------------
    fun clearAll(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}
