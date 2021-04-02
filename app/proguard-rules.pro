
# 混淆基本指令！！！

## 设置混淆的压缩比率 0 ~ 7
#-optimizationpasses 5
## 混淆时不使用大小写混合，混淆后的类名为小写
#-dontusemixedcaseclassnames
## 指定不去忽略非公共库的类
#-dontskipnonpubliclibraryclasses
## 指定不去忽略非公共库的成员
#-dontskipnonpubliclibraryclassmembers
# 混淆时不做预校验
-dontpreverify
## 混淆时不记录日志
#-verbose
# 忽略警告
-ignorewarnings
## 代码优化
#-dontshrink
## 不优化输入的类文件
#-dontoptimize
## 保留注解不混淆
#-keepattributes *Annotation*,InnerClasses
## 避免混淆泛型
#-keepattributes Signature
## 保留代码行号，方便异常信息的追踪
#-keepattributes SourceFile,LineNumberTable
## 混淆采用的算法
#-optimizations !code/simplification/cast,!field/*,!class/merging/*
#
## dump.txt文件列出apk包内所有class的内部结构
#-dump class_files.txt
## seeds.txt文件列出未混淆的类和成员
#-printseeds seeds.txt
## usage.txt文件列出从apk中删除的代码
#-printusage unused.txt
## mapping.txt文件列出混淆前后的映射
#-printmapping mapping.txt



#保持 Serializable 和 enum 类不被混淆
#-keepclassmembers class * implements java.io.Serializable {
#    static final long serialVersionUID;
#    private static final java.io.ObjectStreamField[] serialPersistentFields;
#    private void writeObject(java.io.ObjectOutputStream);
#    private void readObject(java.io.ObjectInputStream);
#    java.lang.Object writeReplace();
#    java.lang.Object readResolve();
#}

#不混淆资源类
#-keepclassmembers class **.R$* {
#    public static <fields>;
#}


# 短信验证sdk混淆（SMSDK）
-keep class com.mob.**{*;}
-keep class cn.smssdk.**{*;}
-dontwarn com.mob.**

# okhttp3 混淆
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}
-dontwarn okio.**

# fresco 混淆
-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip
-keep,allowobfuscation @interface com.facebook.soloader.DoNotOptimize
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.common.internal.DoNotStrip *;
}
-keep @com.facebook.soloader.DoNotOptimize class *
-keepclassmembers class * {
    @com.facebook.soloader.DoNotOptimize *;
}
-keepclassmembers class * {
    native <methods>;
}
-dontwarn okhttp3.**
-dontwarn javax.annotation.**
-dontwarn com.android.volley.toolbox.**
-dontwarn com.facebook.infer.**

# fastjson 混淆
-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.**{*; }
-keepattributes Signature
-keepattributes *Annotation*

# banner 混淆
-keep class com.youth.banner.** {
    *;
 }

# Glide 4 混淆
-keep public class * implements com.bumptech.glide.module.AppGlideModule
-keep public class * implements com.bumptech.glide.module.LibraryGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# support design库 混淆
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }

# v4、v7包 混淆
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**

# androidx 混淆
-keep class androidx.** {*;}
-keep interface androidx.** {*;}
-keep public class * extends androidx.**
-dontwarn androidx.**

#自定义组件 不被混淆
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#litepal 数据库
-dontwarn org.litepal.*
-keep class org.litepal.** { *; }
-keep enum org.litepal.**
-keep interface org.litepal.** { *; }
-keep public class * extends org.litepal.**
-keepclassmembers class * extends org.litepal.crud.DataSupport{*;}