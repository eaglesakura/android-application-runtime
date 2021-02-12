@file:Suppress("MemberVisibilityCanBePrivate")

package io.github.eaglesakura.android_application_runtime

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.IntDef
import java.util.Locale

/**
 * Utility for Application Runtime information.
 *
 * @author @eaglesakura
 * @link https://github.com/eaglesakura/android-application-runtime
 */
@Suppress("unused")
object ApplicationRuntime {

    /**
     * identifier of this process.
     *
     * @author @eaglesakura
     * @link https://github.com/eaglesakura/android-application-runtime
     */
    val pid: Int
        get() = android.os.Process.myPid()

    /**
     * Kill self process.
     *
     * @author @eaglesakura
     * @link https://github.com/eaglesakura/android-application-runtime
     */
    fun killSelf() {
        android.os.Process.killProcess(pid)
        while (true) {
            // spin lock.
        }
    }

    /**
     * Robolectric runtime is true.
     *
     * @author @eaglesakura
     * @link https://github.com/eaglesakura/android-application-runtime
     */
    val ROBOLECTRIC: Boolean = try {
        Class.forName("org.robolectric.Robolectric")
        true
    } catch (err: ClassNotFoundException) {
        false
    }

    /**
     * Run on JUnit test case.
     */
    const val RUNTIME_JUNIT = 0x01 shl 0

    /**
     * Run on JUnit with Robolectric.
     *
     * @link http://robolectric.org/
     */
    const val RUNTIME_ROBOLECTRIC = 0x01 shl 1

    /**
     * Run on JUnit with Instrumentation Test in Android device.
     */
    const val RUNTIME_INSTRUMENTATION = 0x01 shl 2

    /**
     * Run on Android device.
     */
    const val RUNTIME_ANDROID_DEVICE = 0x01 shl 3

    /**
     * Run on ART VM in Android device.
     */
    const val RUNTIME_ART = 0x01 shl 4

    /**
     * Run on Dalvik VM in Android device.
     */
    const val RUNTIME_DALVIK = 0x01 shl 5

    /**
     * Run on Java VM on PC.
     */
    const val RUNTIME_JAVA_VM = 0x01 shl 6

    /**
     * Runtime flags in current Virtual Machine(ART in Android or JVM in PC, or such else)
     *
     * @see RUNTIME_JUNIT
     * @see RUNTIME_ROBOLECTRIC
     * @see RUNTIME_INSTRUMENTATION
     * @see RUNTIME_ANDROID_DEVICE
     * @see RUNTIME_ART
     * @see RUNTIME_DALVIK
     * @see RUNTIME_JAVA_VM
     */
    val runtimeFlags: Int
        get() = getRuntimeFlagsImpl()

    @Retention(AnnotationRetention.BINARY)
    @IntDef(
        RUNTIME_JUNIT,
        RUNTIME_ROBOLECTRIC,
        RUNTIME_INSTRUMENTATION,
        RUNTIME_ANDROID_DEVICE,
        RUNTIME_ART,
        RUNTIME_DALVIK,
        RUNTIME_JAVA_VM
    )
    annotation class RuntimeFlag

    /**
     * Check runtime has all flags.
     *
     * e.g.)
     * if(ApplicationRuntime.contains(RUNTIME_ART, RUNTIME_INSTRUMENTATION)) {
     *      // ART with Instrumentation test.
     * }
     */
    @SuppressLint("WrongConstant")
    fun contains(
        @RuntimeFlag flag: Int,
        @RuntimeFlag vararg flags: Int
    ): Boolean {
        var allFlags = flag
        flags.forEach { flg ->
            allFlags = allFlags or flg
        }
        return (runtimeFlags and allFlags) == allFlags
    }

    @JvmStatic
    private fun getRuntimeFlagsImpl(): Int {
        // check Virtual Machine.
        val virtualMachineFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RUNTIME_ART
        } else {
            RUNTIME_DALVIK
        }

        try {
            Class.forName("org.junit.Assert")
        } catch (e: ClassNotFoundException) {
            return virtualMachineFlags or RUNTIME_ANDROID_DEVICE
        }

        val runtimeName =
            System.getProperty("java.runtime.name")?.toLowerCase(Locale.getDefault()) ?: ""
        return when {
            runtimeName.isEmpty() -> {
                // Android Runtime.
                virtualMachineFlags or RUNTIME_ANDROID_DEVICE
            }
            runtimeName.contains("android") -> {
                // in Instrumentation test.
                virtualMachineFlags or RUNTIME_ANDROID_DEVICE or RUNTIME_JUNIT or RUNTIME_INSTRUMENTATION
            }
            else -> {
                // Only Java VM
                RUNTIME_JUNIT or RUNTIME_ROBOLECTRIC or RUNTIME_JAVA_VM
            }
        }
    }
}
