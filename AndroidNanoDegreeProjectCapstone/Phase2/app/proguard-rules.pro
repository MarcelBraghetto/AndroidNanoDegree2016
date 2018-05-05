-keepattributes Signature
-keepattributes *Annotation*

# Parceler library
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keep class org.parceler.Parceler$$Parcels

# OkHttp
-dontwarn okio.**
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**

# Joda Time
-dontwarn org.joda.convert.**
-keep class org.joda.** { *; }

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule

# EventBus
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Custom views for example the action views in the home menu.
-keep public class * extends android.widget.RelativeLayout { *; }