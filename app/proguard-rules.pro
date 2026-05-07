-keep class ru.ya.practicum.shopper.feature.auth.** { *; }
-keep class ru.ya.practicum.shopper.feature.auth.**$* { *; }

-keepclassmembers class ru.ya.practicum.shopper.feature.auth.** {
    <init>(...);
    *** email;
    *** password;
    *** userId;
    *** accessToken;
    *** refreshToken;
    *** success;
    *** refresh;
}

-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName *;
}

-keepclassmembers class ru.ya.practicum.shopper.feature.auth.** {
    *** get*();
    *** set*(...);
}
