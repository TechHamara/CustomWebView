# Repackages optimized classes into io.th.customwebview.repacked package in resulting
# AIX. Repackaging is necessary to avoid clashes with the other extensions that
# might be using same libraries as you.
-repackageclasses io.th.customwebview.repacked

-android
-optimizationpasses 4
-allowaccessmodification
-mergeinterfacesaggressively
-overloadaggressively
-useuniqueclassmembernames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmember


# Keep Javascript interfaces
-keepattributes JavascriptInterface
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
