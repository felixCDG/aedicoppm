package com.example.senaisp.aplicativomedico.repository

import android.util.Log
import com.example.senaisp.aplicativomedico.model.BuscarContato
import com.example.senaisp.aplicativomedico.service.BuscarContatoInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ContatoRepository(
    private val service: BuscarContatoInterface,
    private val bearerProvider: () -> String?
) {

    companion object {
        private const val TAG = "BuscarContatoAPI"
    }

    suspend fun buscarPorNome(prefixo: String): Result<List<BuscarContato>> {
        return try {
            val bearerRaw = bearerProvider() ?: ""
            val bearer = if (bearerRaw.isBlank()) "" else bearerRaw
            val requestBody = mapOf(
                "name" to prefixo,
                "nome" to prefixo,
                "nome_responsavel" to prefixo
            )

            val response = withContext(Dispatchers.IO) {
                service.buscarContato(bearer, requestBody)
            }

            // Log detalhado para debug
            if (response.isSuccessful) {
                val body = response.body()
                Log.i(TAG, "Sucesso raw body: $body")
                val lista = body?.Nomes ?: emptyList()
                Log.i(TAG, "Quantidade API: ${body?.Quantidade} | Lista de contatos (tamanho=${lista.size}): $lista")
                Result.success(lista)
            } else {
                val err = response.errorBody()?.string()
                Log.e(TAG, "Erro HTTP ${response.code()}: $err")
                Result.failure(Exception("HTTP ${response.code()}: ${err ?: "Sem corpo"}"))
            }

        } catch (e: com.google.gson.JsonSyntaxException) {
            Log.e(TAG, "JsonSyntaxException: ${e.message}")
            Result.failure(Exception("Erro de parsing da resposta: ${e.message}"))
        } catch (e: Exception) {
            Log.e(TAG, "Exceção ao buscar contatos: ${e.message}")
            Result.failure(e)
        }
    }
}
