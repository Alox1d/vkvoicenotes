package com.alox1d.vkvoicenotes.data.remote

import android.net.Uri
import com.alox1d.vkvoicenotes.data.model.*
import com.vk.api.sdk.VKApiJSONResponseParser
import com.vk.api.sdk.VKApiManager
import com.vk.api.sdk.VKHttpPostCall
import com.vk.api.sdk.VKMethodCall
import com.vk.api.sdk.exceptions.VKApiIllegalResponseException
import com.vk.api.sdk.internal.ApiCommand
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit


class VKUsersCommand(private val note: VoiceNoteDTO) : ApiCommand<Unit>() {

    companion object {
        const val RETRY_COUNT = 3
    }

    override fun onExecute(manager: VKApiManager) {

        val uploadInfo = getServerUploadInfo(manager)
        val fileOld = File(note.path)
        val temp = File(note.path.dropLast(1))
        fileOld.renameTo(temp) // TODO Где лучше выполнить переименование?
        val uriTemp = Uri.fromFile(temp) // Todo Где лучше преобразовывать в Uri?
        uploadDoc(note.name ?: "", uriTemp, uploadInfo, manager)
        temp.renameTo(fileOld)
    }

    private fun getServerUploadInfo(manager: VKApiManager): VKServerUploadInfo {
        val uploadInfoCall = VKMethodCall.Builder()
            .method("docs.getUploadServer")
            .version(manager.config.version)
            .build()
        return manager.execute(uploadInfoCall, ServerUploadInfoParser())
    }

    private fun uploadDoc(
        name: String,
        uri: Uri,
        serverUploadInfo: VKServerUploadInfo,
        manager: VKApiManager
    ): String {
        val fileUploadCall = VKHttpPostCall.Builder()
            .url(serverUploadInfo.uploadUrl)
            .args("file", uri, name.dropLast(1))
            .timeout(TimeUnit.MINUTES.toMillis(5))
            .retryCount(RETRY_COUNT)
            .build()
        val fileUploadInfo = manager.execute(fileUploadCall, null, FileUploadInfoParser())

        val saveCall = VKMethodCall.Builder()
            .method("docs.save")
            .args("file", fileUploadInfo.file)
            .version(manager.config.version)
            .build()

        val saveInfo = manager.execute(saveCall, SaveInfoParser())

        return saveInfo.getAttachment()
    }

    private class ServerUploadInfoParser : VKApiJSONResponseParser<VKServerUploadInfo> {
        override fun parse(responseJson: JSONObject): VKServerUploadInfo {
            try {
                val joResponse = responseJson.getJSONObject("response")
                return VKServerUploadInfo(
                    uploadUrl = joResponse.getString("upload_url")
                )
            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }
        }
    }

    private class FileUploadInfoParser : VKApiJSONResponseParser<VKFileUploadInfo> {
        override fun parse(responseJson: JSONObject): VKFileUploadInfo {
            try {
                val joResponse = responseJson

                return VKFileUploadInfo(
                    file = joResponse.getString("file"),
                )
            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }
        }
    }

    private class SaveInfoParser : VKApiJSONResponseParser<VKSaveInfo> {
        override fun parse(responseJson: JSONObject): VKSaveInfo {
            try {
                val joResponse = responseJson.getJSONObject("response")
                return VKSaveInfo(
                    type = joResponse.getString("type"),
                    doc = VKDoc.parse(joResponse.getJSONObject("doc")),
                )
            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }
        }
    }
}