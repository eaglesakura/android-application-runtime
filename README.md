# What is this Library?

`android-application-runtime` gets the runtime information of the Android device.

# Example

```kotlin
if(ApplicationRuntime.contains(ApplicationRuntime.RUNTIME_ROBOLECTRIC)) {
    // robolectric only.
}

if(ApplicationRuntime.contains(ApplicationRuntime.RUNTIME_ANDROID_DEVICE)) {
    // Android device only(Instrumentation Test or Application)
}

if(ApplicationRuntime.contains(ApplicationRuntime.RUNTIME_JUNIT)) {
    // Test only.
}
```

# How to Install.

```groovy
// /app/build.gradle
dependencies {
    implementation 'io.github.eaglesakura.android-application-runtime:android-application-runtime:1.0.0'
}
```
