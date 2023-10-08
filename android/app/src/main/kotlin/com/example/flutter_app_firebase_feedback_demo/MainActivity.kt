package com.example.flutter_app_firebase_feedback_demo

import androidx.annotation.NonNull
import com.google.firebase.appdistribution.InterruptionLevel
import com.google.firebase.appdistribution.ktx.appDistribution
import com.google.firebase.ktx.Firebase
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {
    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            "com.example.flutter_app_firebase_feedback_demo/feedback_notification",
        ).setMethodCallHandler { call, result ->
            if (call.method == "show") {
                showFeedbackNotification()
                result.success(null)
            } else {
                result.notImplemented()
            }
        }
    }

    fun showFeedbackNotification() {
        Firebase.appDistribution.showFeedbackNotification(
            "Thanks for your feedback!",
            InterruptionLevel.HIGH
        )
    }
}
