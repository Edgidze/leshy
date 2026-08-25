# --- атрибуты, нужные serialization / Koin / Room ---
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault
-keepattributes Signature,InnerClasses,EnclosingMethod
-keepattributes SourceFile,LineNumberTable

# --- kotlinx.serialization ---
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}
-if @kotlinx.serialization.Serializable class ** {
    static **$* *;
}
-keepclassmembers class <2>$<3> {
    kotlinx.serialization.KSerializer serializer(...);
}
-if @kotlinx.serialization.Serializable class ** {
    public static ** INSTANCE;
}
-keepclassmembers class <1> {
    public static <1> INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}

# --- маршруты Compose Navigation (type-safe routes сериализуются,
#     имена классов уходят в serial name и в сохраняемое состояние) ---
-keep class leshy.mushrooms.map.ui.navigation.** { *; }

# --- Room ---
-keep class * extends androidx.room.RoomDatabase { <init>(); }
-keep @androidx.room.Entity class *
-keepclassmembers class * {
    @androidx.room.TypeConverter <methods>;
}

# --- MapLibre (JNI) ---
-dontwarn org.maplibre.**
