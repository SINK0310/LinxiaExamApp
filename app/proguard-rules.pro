# ProGuard rules for LinxiaExamApp

# Room
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao interface *

# Hilt
-keep class dagger.hilt.** { *; }
-dontwarn dagger.hilt.**

# Kotlinx Serialization
-keep class kotlinx.serialization.** { *; }
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
}

# Coil
-keep class coil.** { *; }

# MPAndroidChart
-keep class com.github.mikephil.charting.** { *; }
-dontwarn com.github.mikephil.charting.**

# DataStore
-keep class androidx.datastore.** { *; }

# WorkManager
-keep class androidx.work.** { *; }

# Navigation
-keep class androidx.navigation.** { *; }

# Keep compose generated classes
-keep class androidx.compose.** { *; }