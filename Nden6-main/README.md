# نديم - تطبيق تسميع القرآن الكريم 📖

![Android CI](https://github.com/YOUR_USERNAME/nadeem-app/workflows/Android%20CI/badge.svg)
![Platform](https://img.shields.io/badge/Platform-Android-green.svg)
![Language](https://img.shields.io/badge/Language-Kotlin-purple.svg)

## 🌟 عن التطبيق

نديم هو تطبيق أندرويد مصمم لمساعدة المسلمين على حفظ ومراجعة القرآن الكريم من خلال التسميع الصوتي وتتبع التقدم.

**"وَرَتِّلِ الْقُرْآنَ تَرْتِيلًا"** - سورة المزمل

## ✨ المميزات

- 🔐 نظام مصادقة آمن (Firebase Authentication)
- 🎤 تسميع صوتي للسور
- 📊 تتبع التقدم والإحصائيات
- 🎨 واجهة عربية جميلة (Jetpack Compose)
- 🌙 دعم الوضع الليلي
- 📖 قائمة بجميع السور القرآنية
- 👤 إدارة الملف الشخصي

## 🛠️ التقنيات

- **اللغة**: Kotlin
- **UI**: Jetpack Compose + Material Design 3
- **Navigation**: Navigation Compose
- **Backend**: Firebase (Auth, Firestore, Storage)
- **Async**: Coroutines + Flow
- **DI**: Manual DI

## 📋 المتطلبات

- Android Studio Hedgehog+ (2023.1.1)
- JDK 17
- Android SDK 34
- Gradle 8.2
- Min SDK: 24 (Android 7.0)
- Target SDK: 34 (Android 14)

## 🚀 البناء والتشغيل

### 1. الاستنساخ

```bash
git clone https://github.com/YOUR_USERNAME/nadeem-app.git
cd nadeem-app
```

### 2. إضافة ملفات الشاشات

**⚠️ مهم**: المشروع يحتوي على الهيكل الأساسي. تحتاج لإضافة ملفات الشاشات من الرسائل السابقة.

راجع ملف `SCREENS_CODE_COMPLETE.md` للحصول على قائمة بجميع الملفات المطلوبة.

الملفات المطلوبة (11 ملف):
- Navigation.kt
- SplashScreen.kt
- LoginScreen.kt
- RegisterScreen.kt  
- ForgotPasswordScreen.kt
- HomeScreen.kt
- RecitationScreen.kt
- ProfileScreen.kt
- Color.kt
- Theme.kt
- Type.kt

**جميع هذه الأكواد موجودة في الرسائل السابقة** - فقط انسخها وضعها في المسارات المحددة في `SCREENS_CODE_COMPLETE.md`

### 3. إعداد Firebase

1. اذهب إلى [Firebase Console](https://console.firebase.google.com/)
2. أنشئ مشروع جديد
3. أضف تطبيق Android:
   - Package name: `awab.quran.ar`
4. حمّل `google-services.json`
5. ضعه في `app/google-services.json`
6. فعّل:
   - Authentication (Email/Password)
   - Cloud Firestore
   - Cloud Storage

### 4. البناء المحلي

```bash
chmod +x gradlew
./gradlew clean
./gradlew build
```

## 📁 هيكل المشروع

```
app/
├── src/main/
│   ├── java/awab/quran/ar/
│   │   ├── MainActivity.kt              ✅ موجود
│   │   ├── NadeemApplication.kt         ✅ موجود
│   │   └── ui/
│   │       ├── navigation/
│   │       │   └── Navigation.kt        ⚠️ أضفه من الرسائل
│   │       ├── screens/
│   │       │   ├── auth/                ⚠️ 3 ملفات
│   │       │   ├── home/                ⚠️ 1 ملف
│   │       │   ├── profile/             ⚠️ 1 ملف
│   │       │   ├── recitation/          ⚠️ 1 ملف
│   │       │   └── splash/              ⚠️ 1 ملف
│   │       └── theme/                   ⚠️ 3 ملفات
│   ├── res/                             ✅ جميع XML موجودة
│   └── AndroidManifest.xml              ✅ موجود
└── build.gradle                         ✅ موجود
```

## 🔧 GitHub Actions

المشروع جاهز للبناء التلقائي على GitHub!

### الإعداد:

1. ارفع المشروع على GitHub
2. اذهب إلى **Settings > Secrets and variables > Actions**
3. أضف secret:
   - Name: `GOOGLE_SERVICES_JSON`
   - Value: محتوى ملف `app/google-services.json` كاملاً

### النتيجة:

- ✅ بناء تلقائي عند كل push
- ✅ تحميل APK من Artifacts
- ✅ تشغيل الاختبارات

## 📝 ملاحظات مهمة

### ❗ الملفات الناقصة

هذا المشروع يحتوي على:
- ✅ جميع إعدادات Gradle
- ✅ جميع ملفات XML
- ✅ AndroidManifest كامل
- ✅ MainActivity و Application class
- ✅ GitHub Actions workflow
- ✅ البنية الكاملة للمجلدات

لكنه **لا يحتوي على** ملفات الشاشات (11 ملف Kotlin).

**السبب**: حجم الملفات كبير جداً (كل ملف 200-400 سطر).

**الحل**: جميع الأكواد موجودة في الرسائل السابقة في المحادثة.
فقط انسخها وضعها في المسارات الصحيحة.

راجع `SCREENS_CODE_COMPLETE.md` للتفاصيل.

### 🔥 Firebase Rules

بعد إعداد Firebase، أضف هذه القواعد:

**Firestore**:
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

**Storage**:
```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /recitations/{userId}/{allPaths=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

## 🤝 المساهمة

المساهمات مرحب بها!

1. Fork المشروع
2. أنشئ branch (`git checkout -b feature/AmazingFeature`)
3. Commit التغييرات (`git commit -m 'Add AmazingFeature'`)
4. Push للـ branch (`git push origin feature/AmazingFeature`)
5. افتح Pull Request

## 📄 الترخيص

MIT License - انظر [LICENSE](LICENSE)

## 🙏 شكر وتقدير

- Firebase
- Jetpack Compose
- Material Design
- المجتمع المفتوح المصدر

---

<div align="center">
  <p>صُنع بـ ❤️ لخدمة القرآن الكريم</p>
  <p><strong>بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ</strong></p>
</div>
