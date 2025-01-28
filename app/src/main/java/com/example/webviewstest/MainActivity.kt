package com.example.webviewstest

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class MainActivity : AppCompatActivity() {
    private val CAMERA_PERMISSION_REQUEST_CODE = 100
    private val AUDIO_PERMISSION_REQUEST_CODE = 101
    private val MODIFY_AUDIO_SETTINGS_REQUEST_CODE = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up the "Start Verification" button
        val startVerificationButton: Button = findViewById(R.id.startVerificationButton)
        startVerificationButton.setOnClickListener {
            // Start permission-checking process when the button is clicked
            checkPermissionsAndStartVerification()
        }
    }

    private fun checkPermissionsAndStartVerification() {
        // Check Camera permission first
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            // If Camera permission is granted, proceed to check audio permissions
            checkAndRequestAudioPermissions()
        }
    }

    private fun checkAndRequestAudioPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.RECORD_AUDIO)
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.MODIFY_AUDIO_SETTINGS)
            != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.MODIFY_AUDIO_SETTINGS)
        }

        if (permissionsToRequest.isNotEmpty()) {
            // Request missing permissions
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                AUDIO_PERMISSION_REQUEST_CODE
            )
        } else {
            // All necessary permissions are granted, set up WebView
            setupWebView()
        }
    }

    // Set up WebView to load the URL
    private fun setupWebView() {
        // Find WebView by ID
        // Find WebView and make it visible
        val webView: WebView = findViewById(R.id.webView)
        val startVerificationButton: Button = findViewById(R.id.startVerificationButton)

        // Hide the button once WebView is shown
        startVerificationButton.visibility = View.GONE


        Log.i("WebView", "Setting up webview")

        // Make WebView visible and full-screen
        webView.visibility = View.VISIBLE

        // Enable JavaScript
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.mediaPlaybackRequiresUserGesture = false
        webSettings.domStorageEnabled = true

        // Set WebViewClient to handle navigation
        webView.webViewClient = WebViewClient()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true)
        }

        // Set WebChromeClient to handle permissions
        webView.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest?) {
                // Handle permission requests for camera and microphone
                if (request != null) {
                    if (request.resources.contains(PermissionRequest.RESOURCE_VIDEO_CAPTURE) ||
                        request.resources.contains(PermissionRequest.RESOURCE_AUDIO_CAPTURE) ) {
                        Log.i("Permissions", "Granting camera/audio permission")
                        request.grant(request.resources)
                    }
                }
            }

            override fun onPermissionRequestCanceled(request: PermissionRequest?) {
                super.onPermissionRequestCanceled(request)
            }
        }

        webView.loadUrl("https://blinkid-webview-test.netlify.app/")
    }

    // Handle the result of the permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkAndRequestAudioPermissions()
                } else {
                    showPermissionDialog("camera access", CAMERA_PERMISSION_REQUEST_CODE)
                }
            }
            AUDIO_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkAndRequestAudioPermissions()
                } else {
                    showPermissionDialog("audio access", AUDIO_PERMISSION_REQUEST_CODE)
                }
            }
            MODIFY_AUDIO_SETTINGS_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupWebView()
                } else {
                    showPermissionDialog("audio access", MODIFY_AUDIO_SETTINGS_REQUEST_CODE)
                }
            }
        }
    }


    private fun showPermissionDialog(permission: String, requestCode: Int) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            AlertDialog.Builder(this)
                .setTitle("Permission Required")
                .setMessage("The app requires $permission to function properly. Please grant the permission.")
                .setPositiveButton("Grant") { _, _ ->
                    ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        } else {
            // User selected "Don't ask again" or permission rationale not needed
            showPermissionDialogWithSettings(permission, requestCode)
        }
    }

    private fun showPermissionDialogWithSettings(permission: String, requestCode: Int) {
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("The app requires $permission to function properly. Please enable it in settings.")
            .setPositiveButton("Open Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.fromParts("package", packageName, null)
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

}
