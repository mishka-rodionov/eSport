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

# Crashlytics: сохраняем номера строк и имена файлов для читаемых стектрейсов
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception

# Firebase Messaging
-keep class com.google.firebase.messaging.** { *; }

# Koin DSL использует рефлексию по сигнатурам конструкторов
-keep class org.koin.** { *; }
-keepclassmembers class * { @org.koin.core.annotation.* *; }

# Сохраняем сигнатуры/аннотации для Retrofit, Gson, kotlinx.serialization
-keepattributes Signature, InnerClasses, EnclosingMethod, *Annotation*, EnclosingMethod

# Доменные модели, Room-сущности и сетевые DTO — реальные названия пакетов
# (models/entities/response/request), а не сгенерированные шаблоном model/entity/dto.
# Gson-(де)сериализация и Room ссылаются на поля этих классов по имени — без -keep
# R8 может переименовать или удалить их в релизной сборке.
-keep class com.competra.domain.models.** { *; }
-keep class com.competra.local.entities.** { *; }
-keep class com.competra.remote.request.** { *; }
-keep class com.competra.remote.response.** { *; }
-keep class com.competra.**.dto.** { *; }

# kotlinx.serialization
-keepclassmembers class **$Companion {
    kotlinx.serialization.KSerializer serializer(...);
}
-keepclasseswithmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}
-keep,includedescriptorclasses class com.competra.**$$serializer { *; }

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.internal.MainDispatcherFactory {
    public static ** INSTANCE;
}

# Room — KSP-сгенерированные классы _Impl
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.* class * { *; }