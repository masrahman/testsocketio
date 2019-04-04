package maasrahman.com.testsocketio.network.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataModel(
    @SerializedName("__v")
    val v: Int?,
    @SerializedName("_id")
    val id: String?,
    val name: String?,
    val sessionId: String?
) : Parcelable