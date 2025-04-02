# ☁️🌡️ Weather App 🌍

## 🔍 Overview
This 📱 Android app provides 🌎 real-time 🌦️ weather updates based on your 📍 location. Users can:
- 👀 View current 🌡️ weather conditions (🌧️☀️❄️💨).
- 🔎 Search locations or 🗺️ pick one on the map and ⭐ save as favorite.
- 🚨 Set ⏰ alerts for 🌧️ rain, 💨 wind, ❄️ snow, 🔥 extreme temps, etc.
- ⚙️ Customize settings: 🌡️ temp units, 💨 wind speed, 🗣️ language, and 🛎️ alert types.

## 🎯 Features
### 🌍 **Settings Screen**
- 📍 Choose location via 📡 GPS or 🗺️ map.
- 🌡️ Select temperature unit (K, °C, °F).
- 💨 Pick wind speed unit (m/s, mph).
- 🗣️ Change 🏳️ language (🇬🇧 English, 🇸🇦 Arabic).

### ☀️ **Home Screen**
Displays:
- 🌡️ Temperature
- 📅 Date & ⏳ Time
- 💧 Humidity
- 💨 Wind Speed
- 🌪️ Pressure
- ☁️ Clouds
- 📍 City Name
- 🖼️ Weather Icon & 📜 Description
- 🕰️ Past 🌡️ Hourly Data
- 📆 Past 5️⃣-Day History

### ⭐ **Favorite Locations**
- 💾 Save places.
- 👆 Click on a ⭐ favorite for 🌦️ forecast details.
- ➕ Add new via 🗺️ map or 🔎 search.
- ❌ Remove saved locations.

### 🚨 **Weather Alerts**
- 🛎️ Set 🕒 alerts for ☔ specific weather conditions.
- 🔔 Choose type: 📩 Notification 🔊.
- ⏳ Define ⏰ alert duration & 🚫 stop as needed.

## 🛠️ Tech Stack
- 💻 **Language:** Kotlin
- 🎨 **Framework:** Jetpack Compose
- 🌐 **Networking:** Retrofit
- 🗄️ **Database:** Room
- 🚀 **Navigation:** Navigation Compose
- 🎞️ **Animation:** Lottie
- 🗺️ **Maps & Location:** Google Maps API, Places API
- 🧪 **Testing:** JUnit, MockK 

## 📦 Dependencies
```gradle
// 🛤️ Navigation animation
implementation("com.exyte:animated-navigation-bar:1.0.0")
// 🎞️ Lottie animation
implementation("com.airbnb.android:lottie-compose:6.2.0")
// 🚀 Navigation
implementation("androidx.navigation:navigation-compose:2.8.8")
implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
// 👀 ViewModel
implementation("androidx.lifecycle:lifecycle-viewmodel-compose-android:2.8.7")
// 🌐 Retrofit
implementation("com.squareup.retrofit2:retrofit:2.11.0")
implementation("com.squareup.retrofit2:converter-gson:2.11.0")
// 🗄️ Room
implementation("androidx.room:room-runtime:2.6.1")
ksp("androidx.room:room-compiler:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
// 🖼️ Glide
implementation("com.github.bumptech.glide:compose:1.0.0-beta01")
// 🔄 LiveData & Compose
implementation("androidx.compose.runtime:runtime-livedata:1.0.0")
// 📐 Constraint Layout
implementation("androidx.constraintlayout:constraintlayout-compose:1.1.0-alpha10")
// 🗺️ Google Maps Services
implementation("com.google.android.gms:play-services-location:21.1.0")
implementation("com.google.android.libraries.places:places:3.3.0")
implementation("com.google.maps.android:maps-compose:6.4.1")
// ⚙️ Worker
implementation("androidx.work:work-runtime-ktx:2.7.1")
// 🧪 Testing
androidTestImplementation("androidx.test.ext:junit:1.1.5")
testImplementation("io.mockk:mockk-android:1.13.17")
testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
```

## ⚡ Installation & Setup
1. 🛠️ Clone the repo:
   ```bash
   git clone https://github.com/your-repo/weather-app.git
   ```
2. 📂 Open in **Android Studio**.
3. 🔄 Sync **Gradle** dependencies.
4. 🔑 Configure **Google Maps API key** in `local.properties`.
5. 📱 Run the app on an 📳 emulator or 🔌 physical device.

---
🌦️ **Stay prepared with real-time 🌍 weather updates!** 🚀


