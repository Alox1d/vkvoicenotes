package com.alox1d.vkvoicenotes.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

class VKServerUploadInfo(val uploadUrl: String)
class VKFileUploadInfo(val file: String)

class VKSaveInfo(
    val type: String,
    val doc: VKDoc
) {
    fun getAttachment() = "doc${type}_${doc.title}"
}

@Parcelize
data class VKDoc(
    val id: Int = 0,
    val owner_id: Int = 0,
    val title: String = ""
) : Parcelable {

    companion object {
        fun parse(json: JSONObject) = VKDoc(
            id = json.optInt("id", 0),
            owner_id = json.optInt("owner_id", 0),
            title = json.optString("title", ""),
        )
    }
}