package awab.quran.ar.utils

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

object ForceUpdateChecker {

    private const val KEY_MIN_VERSION = "min_version_code"

    /**
     * يتحقق من Firebase Remote Config إذا كان الإصدار الحالي مسموحاً به.
     *
     * الاستخدام في Firebase Console:
     *   Remote Config → أضف مفتاح: min_version_code
     *   القيمة: رقم versionCode الأدنى المسموح (مثلاً 3)
     *
     * @param currentVersionCode  versionCode الحالي للتطبيق
     * @param onUpdateRequired    يُستدعى إذا كان التحديث إجبارياً
     * @param onUpToDate          يُستدعى إذا كان التطبيق محدّثاً
     */
    fun check(
        currentVersionCode: Int,
        onUpdateRequired: () -> Unit,
        onUpToDate: () -> Unit
    ) {
        val remoteConfig = FirebaseRemoteConfig.getInstance()

        // إعدادات الفحص: كل ساعة في الإنتاج
        val settings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600)
            .build()
        remoteConfig.setConfigSettingsAsync(settings)

        // قيمة افتراضية: 0 = لا تحديث إجباري
        remoteConfig.setDefaultsAsync(mapOf(KEY_MIN_VERSION to 0L))

        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val minVersion = remoteConfig.getLong(KEY_MIN_VERSION).toInt()
                if (currentVersionCode < minVersion) {
                    onUpdateRequired()
                } else {
                    onUpToDate()
                }
            } else {
                // فشل الجلب → لا نمنع المستخدم
                onUpToDate()
            }
        }
    }
}
