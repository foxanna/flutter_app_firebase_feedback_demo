# Firebase testers' feedback in Flutter app demo

* Firebase Console side:
    1. Create a new Firebase project with Android application configured.
    2. Visit `Release & Monitor` -> `App Distribution`, tap `Get Started` and add a tester account.
* Flutter side:  
    1. Create a new Flutter project with `flutter create --platforms android .`.  
    2. Setup Firebase usage as per [documentation](https://firebase.google.com/docs/flutter/setup?platform=android)  
        1. Run `flutter pub add firebase_core`.
        2. Run `flutterfire configure` and configure Android project.
    3. Update `main` function in `main.dart` like:
        ```dart
        Future<void> main() async {
          WidgetsFlutterBinding.ensureInitialized();
          await Firebase.initializeApp(options: DefaultFirebaseOptions.currentPlatform);
          runApp(const MyApp());
        }
        ```
    4. Update `_MyHomePageState` class in `main.dart` like:
        ```dart
        static const _platform = MethodChannel('.../feedback_notification');

        @override
        void initState() {
          super.initState();
          _platform.invokeMethod('show');
        }
        ```
* Native Android side:
    1. As per [documentation](https://firebase.google.com/docs/app-distribution/collect-feedback-from-testers) (almost):
        1. Update `android/app/build.gradle` with:
            ```
            dependencies {
                implementation("com.google.firebase:firebase-appdistribution-api-ktx:16.0.0-beta10")
                implementation("com.google.firebase:firebase-appdistribution:16.0.0-beta10")
            }
            ```
          2. Update `android/app/src/main/kotlin/.../MainActivity.kt` to:  
              ```kotlin
              package ...

              import com.google.firebase.appdistribution.InterruptionLevel
              import com.google.firebase.appdistribution.ktx.appDistribution
              import com.google.firebase.ktx.Firebase
              
              class MainActivity: FlutterActivity() {
                  fun showFeedbackNotification() {
                      Firebase.appDistribution.showFeedbackNotification(
                          "Thanks for your feedback!",
                          InterruptionLevel.HIGH
                      )
                  }
              }
              ```
              
    2. Update `android/app/src/main/AndroidManifest.xml`:  
          ```xml
          <manifest xmlns:android="http://schemas.android.com/apk/res/android">
            <uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>
            <application ...>
            </application>
          </manifest>
          ```
    3. Update `android/app/src/main/kotlin/.../MainActivity.kt` to: 
        ```kotlin
        package ...

        import androidx.annotation.NonNull
        import io.flutter.embedding.engine.FlutterEngine
        import io.flutter.plugin.common.MethodChannel
        ...
            
        class MainActivity: FlutterActivity() {
            override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
                super.configureFlutterEngine(flutterEngine)
                MethodChannel(
                    flutterEngine.dartExecutor.binaryMessenger,
                    ".../feedback_notification",
                ).setMethodCallHandler { call, result ->
                    if (call.method == "show") {
                        showFeedbackNotification()
                        result.success(null)
                    } else {
                        result.notImplemented()
                    }
                }
            }
    
            fun showFeedbackNotification() {...}
        }
        ```
* Distribute the app:
  1. Build the app with `flutter build apk`
  2. Upload the new build to Firebase App Distribution and distribute it to all testers.
        
