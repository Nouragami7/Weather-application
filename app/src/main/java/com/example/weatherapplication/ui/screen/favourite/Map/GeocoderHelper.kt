import android.content.Context
import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import java.io.IOException
import java.util.Locale

class GeocoderHelper(private val context: Context) {
    fun getCountryName(latLng: LatLng): String? {
        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            addresses?.firstOrNull()?.countryName
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}
