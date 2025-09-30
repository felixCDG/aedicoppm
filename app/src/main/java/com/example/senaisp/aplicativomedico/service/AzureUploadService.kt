package com.example.senaisp.aplicativomedico.service

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.util.UUID

object AzureUploadService {

    /**
     * Faz upload de um Uri (imagem) para o Azure Blob Storage via SAS Token.
     * @return String URL pública do blob (sem o SAS).
     */
    suspend fun uploadImageToAzure(
        context: Context,
        uri: Uri
    ): String {
        // 1) Definições de container/account e SAS
        val accountName = "asei"
        val containerName = "foto"
        val sasToken = "sp=racwdli&st=2025-06-12T17:12:24Z&se=2025-06-19T01:12:24Z&sv=2024-11-04&sr=c&sig=uF4r1CjXznKe6M2wztgcpYW8lkxMxlljIW6Hy3Vk8w4%3D"

        // 2) Gera nome único
        val fileName = "${UUID.randomUUID()}.jpg"

        // 3) URL completa para o PUT
        val uploadUrl = "https://$accountName.blob.core.windows.net/$containerName/$fileName?$sasToken"

        // 4) Lê bytes do Uri
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Não foi possível ler o arquivo")
        val bytes = inputStream.readBytes()

        // 5) Prepara requisição
        val client = OkHttpClient()
        val mediaType = "image/jpeg".toMediaType()
        val body = RequestBody.Companion.create(mediaType, bytes)
        val request = Request.Builder()
            .url(uploadUrl)
            .addHeader("x-ms-blob-type", "BlockBlob")      // ← header obrigatório
            .put(body)
            .build()

        // 6) Executa síncrono (dentro de coroutine)
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("Upload falhou: HTTP ${response.code}")
            }
        }

        // 7) Retorna a URL pública (sem o SAS, se quiser esconder o token)
        return "https://$accountName.blob.core.windows.net/$containerName/$fileName"
    }

}