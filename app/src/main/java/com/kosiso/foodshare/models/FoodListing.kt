import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class FoodListing(
    val uniqueId:String = "",
    val uid:String = "",
    val nameOfItem:String = "",
    val itemDescription:String = "",
    val foodImg: String,
    val listingCategory:String = "", // we have perishable and non-perishable
    val foodWeight:Int = 0,
    val status:String = "", // we have Unclaimed, Claimed and Delivered
    val foodListedTime: Timestamp = Timestamp.now(),// this is the time the food was listed
    val foodListingLocation: GeoPoint? = null,
    val expiryDate: Timestamp? = null
): Parcelable {

    // No-argument constructor required by Firebase Firestore
    constructor() : this("", "", "", "", "", "", 0, "", Timestamp.now(), null, null)

    // Describe the kind of special object we have or 0 if no special object
    override fun describeContents(): Int {
        return 0
    }

    // Write the object’s data to the passed-in Parcel
    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(uniqueId)
        dest.writeString(uid)
        dest.writeString(nameOfItem)
        dest.writeString(itemDescription)
        dest.writeString(foodImg)
        dest.writeString(listingCategory)
        dest.writeInt(foodWeight)
        dest.writeString(status)
        dest.writeLong(foodListedTime.seconds)
        dest.writeInt(foodListedTime.nanoseconds)
        dest.writeDouble(foodListingLocation?.latitude ?: 0.0)
        dest.writeDouble(foodListingLocation?.longitude ?: 0.0)
        dest.writeLong(expiryDate?.seconds ?: 0)
        dest.writeInt(expiryDate?.nanoseconds ?: 0)
    }

    // This is used to regenerate the object. All Parcelables must have a CREATOR that implements these two methods
    companion object CREATOR : Parcelable.Creator<FoodListing> {
        override fun createFromParcel(parcel: Parcel): FoodListing {
            return FoodListing(
                parcel.readString() ?: "",
                parcel.readString() ?: "",
                parcel.readString() ?: "",
                parcel.readString() ?: "",
                parcel.readString() ?: "",
                parcel.readString() ?: "",
                parcel.readInt(),
                parcel.readString() ?: "",
                Timestamp(parcel.readLong(), parcel.readInt()),
                GeoPoint(parcel.readDouble(), parcel.readDouble()),
                Timestamp(parcel.readLong(), parcel.readInt()).takeIf { it.seconds != 0L }
            )
        }

        override fun newArray(size: Int): Array<FoodListing?> {
            return arrayOfNulls(size)
        }
    }
}
