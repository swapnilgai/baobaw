-dontwarn org.slf4j.impl.StaticLoggerBinder
#
#-keepclassmembers @kotlinx.serialization.Serializable class com.java.cherrypick.** {
#    # lookup for plugin generated serializable classes
#    *** Companion;
#    # lookup for serializable objects
#    *** INSTANCE;
#    kotlinx.serialization.KSerializer serializer(...);
#}
## lookup for plugin generated serializable classes
#-if @kotlinx.serialization.Serializable class com.java.cherrypick.**
#-keepclassmembers class com.java.cherrypick.<1>$Companion {
#    kotlinx.serialization.KSerializer serializer(...);
#}
#
#-if @kotlinx.serialization.Serializable class com.java.cherrypick.**
#-keepclassmembers class <1>$Companion {
#    kotlinx.serialization.KSerializer serializer(...);
#}