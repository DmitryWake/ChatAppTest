package com.example.chatapp.utilities

import android.media.MediaRecorder
import java.io.File

class AppVoiceRecorder {

    private val mediaRecorder = MediaRecorder()
    private lateinit var file: File
    private lateinit var messageKey: String

    fun startRecord(messKey: String) {
        try {
            messageKey = messKey
            createFileForRecord()
            prepareMediaRecorder()
            mediaRecorder.start()
        } catch (e: Exception) {
            showToast(e.message.toString())
        }
    }

    private fun prepareMediaRecorder() {
        mediaRecorder.apply {
            reset()
            setAudioSource(MediaRecorder.AudioSource.DEFAULT)
            setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
            setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
            setOutputFile(file.absolutePath)
            prepare()
        }
    }

    private fun createFileForRecord() {
        file = File(APP_ACTIVITY.filesDir, messageKey)
        file.createNewFile()
    }

    fun stopRecord(onSuccess: (fun_file: File, messKey: String) -> Unit) {
        try {
            mediaRecorder.stop()
            onSuccess(file, messageKey)
        } catch (e: Exception) {
            showToast(e.message.toString())
            file.delete()
        }
    }

    fun releaseRecorder() {
        try {
            mediaRecorder.release()
        } catch (e: Exception) {
            showToast(e.message.toString())
        }
    }
}