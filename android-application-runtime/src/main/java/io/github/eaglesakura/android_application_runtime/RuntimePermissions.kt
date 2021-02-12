package io.github.eaglesakura.android_application_runtime

import android.annotation.TargetApi
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings

/**
 * Utilities for Runtime permissions.
 *
 * @author @eaglesakura
 * @link https://github.com/eaglesakura/android-application-runtime
 */
@Suppress("unused")
object RuntimePermissions {

    /**
     * This method returns Intent for Application settings.
     * User can change this application setting.
     *
     * @author @eaglesakura
     * @link https://github.com/eaglesakura/android-application-runtime
     */
    fun getSettingIntent(context: Context): Intent {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:" + context.packageName)
        return intent
    }

    /**
     * This method returns Intent for Overlay setting.
     * User can change overlay settings.
     *
     * @author @eaglesakura
     * @link https://github.com/eaglesakura/android-application-runtime
     */
    @TargetApi(Build.VERSION_CODES.M)
    fun getOverlaySettingIntent(context: Context): Intent {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.data = Uri.parse("package:" + context.packageName)
        return intent
    }

    /**
     * This method returns Intent for "usage stats access permission".
     *
     * @author @eaglesakura
     * @link https://github.com/eaglesakura/android-application-runtime
     */
    fun getUsageStatusSettingIntent(@Suppress("UNUSED_PARAMETER") context: Context): Intent {
        return Intent("android.settings.USAGE_ACCESS_SETTINGS")
    }

    /**
     * If this app has permission for draw on system-layer, this method returns true.
     *
     * @author @eaglesakura
     * @link https://github.com/eaglesakura/android-application-runtime
     */
    fun canDrawOverlays(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            true
        } else {
            Settings.canDrawOverlays(context)
        }
    }

    /**
     * If this app has permission for "usage stats access", this method returns true.
     *
     * Required, in AndroidManifest.xml
     * <manifest>
     *      <uses-permission android:name="android.permission.GET_TASKS"/>
     *      <uses-permission android:name="android.permission.PACKAGE_USAGE_STATS" tools:ignore="ProtectedPermissions"/>
     * </manifest>
     *
     * @author @eaglesakura
     * @link https://github.com/eaglesakura/android-application-runtime
     */
    fun canAccessUsageStatus(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val uid = android.os.Process.myUid()
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                uid,
                context.packageName
            )
        } else {
            @Suppress("DEPRECATION")
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                uid,
                context.packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }
}
