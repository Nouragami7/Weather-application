import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapplication.ui.screen.favourite.Map.MapViewModel
import com.example.weatherapplication.ui.theme.IceBlue
import com.example.weatherapplication.ui.theme.LightSkyBlue
import com.example.weatherapplication.ui.theme.SoftSkyBlue
import com.google.android.gms.maps.model.LatLng


@Composable
fun AddToFavorites(
    longitude: Double,
    latitude: Double,
    mapViewModel: MapViewModel,
    selectedPoint: LatLng,
    selectedCountry: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier
                    .fillMaxWidth()
                .shadow(16.dp, shape = RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.9f)
            ),
            elevation = CardDefaults.elevatedCardElevation(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Country: $selectedCountry",
                    color = Color.Gray,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Lat: ${selectedPoint.latitude}, Lng: ${selectedPoint.longitude}",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(SoftSkyBlue, LightSkyBlue)
                            )
                        )
                        .shadow(8.dp, shape = RoundedCornerShape(24.dp))
                        .padding(1.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.White.copy(alpha = 0.4f), Color.Transparent)
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Add to Favorites",
                        color = IceBlue,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
