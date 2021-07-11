# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
# Jackson
-keep class kotlin.** { *; }
-keep class org.jetbrains.** { *; }

# General
-keepattributes SourceFile,LineNumberTable,*Annotation*,EnclosingMethod,Signature,Exceptions,InnerClasses
-keep class de.micmun.android.nextcloudcookbook.** { *; }
-keep class androidx.appcompat.graphics.drawable.DrawerArrowDrawable { *; }

# Navigation
-keep class androidx.navigation.** { *; }
-keep class androidx.fragment.** { *; }

-dontnote kotlinx.serialization.AnnotationsKt # core serialization annotations

# kotlinx-serialization-json specific. Add this if you have java.lang.NoClassDefFoundError kotlinx.serialization.json.JsonObjectSerializer
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Change here com.yourcompany.yourpackage
-keep,includedescriptorclasses class de.micmun.android.nextcloudcookbook.**$$serializer { *; } # <-- change package name to your app's
-keepclassmembers class de.micmun.android.nextcloudcookbook.** { # <-- change package name to your app's
    *** Companion;
}
-keepclasseswithmembers class de.micmun.android.nextcloudcookbook.** { # <-- change package name to your app's
    kotlinx.serialization.KSerializer serializer(...);
}