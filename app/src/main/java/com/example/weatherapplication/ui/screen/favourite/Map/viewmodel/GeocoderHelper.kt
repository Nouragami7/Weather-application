import android.content.Context
import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import java.io.IOException
import java.util.Locale


data class LocationInfo (val city: String?, val country: String?)
class GeocoderHelper(private val context: Context) {
    fun getLocationInfo(latLng: LatLng): LocationInfo {
        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            val address = addresses?.firstOrNull()
            val city = address?.adminArea
            val country = address?.countryName
            LocationInfo(city, country)
        } catch (e: IOException) {
            e.printStackTrace()
            LocationInfo(null, null)
        }
    }
}
