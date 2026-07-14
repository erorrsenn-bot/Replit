# Nova Client ProGuard rules

# Keep Gson model classes
-keepclassmembers class com.nova.client.data.models.** { *; }
-keep class com.nova.client.data.models.** { *; }

# Keep Compose
-keep class androidx.compose.** { *; }

# Kotlin Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers @kotlinx.serialization.Serializable class ** { *** Companion; }

# OkHttp / Networking (if added later)
-dontwarn okhttp3.**
-dontwarn okio.**
