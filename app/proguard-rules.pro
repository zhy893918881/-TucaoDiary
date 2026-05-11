# Add project specific ProGuard rules here.
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.ai.tucaodiary.data.remote.** { *; }
-dontwarn okhttp3.**
-dontwarn retrofit2.**
