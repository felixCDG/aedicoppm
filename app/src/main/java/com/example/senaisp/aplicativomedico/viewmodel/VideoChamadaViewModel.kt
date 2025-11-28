package com.example.senaisp.aplicativomedico.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.twilio.video.ConnectOptions
import com.twilio.video.RemoteParticipant
import com.twilio.video.Room
import com.twilio.video.TwilioException
import com.twilio.video.Video

class VideoChamadaViewModel(
    private val context: Context
) : ViewModel() {

    var isConnected by mutableStateOf(false)
    var isConnecting by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    private var currentRoom: Room? = null

    fun connectToTwilio(jwt: String, roomName: String) {
        try {
            isConnecting = true

            val connectOptions = ConnectOptions.Builder(jwt)
                .roomName(roomName)
                .build()

            val roomListener = object : Room.Listener {
                override fun onConnected(room: Room) {
                    isConnected = true
                    isConnecting = false
                    currentRoom = room
                }

                override fun onConnectFailure(room: Room, e: TwilioException) {
                    isConnected = false
                    isConnecting = false
                    errorMessage = "Falha ao conectar: ${e.message}"
                }

                override fun onReconnecting(
                    room: Room,
                    twilioException: TwilioException
                ) {
                    TODO("Not yet implemented")
                }

                override fun onReconnected(room: Room) {
                    TODO("Not yet implemented")
                }

                override fun onDisconnected(room: Room, e: TwilioException?) {
                    isConnected = false
                    isConnecting = false
                    currentRoom = null
                }

                override fun onParticipantConnected(room: Room, participant: RemoteParticipant) {}
                override fun onParticipantDisconnected(room: Room, participant: RemoteParticipant) {}
                override fun onRecordingStarted(room: Room) {}
                override fun onRecordingStopped(room: Room) {}
            }

            currentRoom = Video.connect(context, connectOptions, roomListener)

        } catch (e: Exception) {
            isConnecting = false
            errorMessage = "Erro conectar Twilio: ${e.message}"
        }
    }

    fun disconnect() {
        currentRoom?.disconnect()
        currentRoom = null
        isConnected = false
    }
}
