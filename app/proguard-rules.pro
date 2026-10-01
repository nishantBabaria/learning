# Room keep rules
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Retrofit & Gson models
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keep class com.learning.app.data.**.dto.** { *; }

# Security Crypto & Tink
-keep class com.google.crypto.tink.** { *; }

# Strip android.util.Log calls in release build
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}

#missing_rules.txt.
-dontwarn com.google.errorprone.annotations.Immutable