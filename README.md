# Microblink Webviews Testing

## TL;DR

This repo shows a basic POC for running Microblink solutions (BlinkID in this case) in a Webview so that it:

- Has just one screen with a Webview component (basically a so-called ‘headless’ browser)
- Loads the web demo site https://blinkid-test.netlify.app/ into the browser
- Makes sure that scanning and extraction work

## Gotchas
The most important thing to note is that one has to request the proper permissions (in [AndroidManifest.xml file](https://github.com/Hitman666/MicroblinkWebViewsTesting/blob/main/app/src/main/AndroidManifest.xml):

```
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-feature android:name="android.hardware.camera" android:required="true" />
<uses-feature android:name="android.hardware.camera.autofocus" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
```

Then, along with the usual permission handling on the Android level (see [here](https://github.com/Hitman666/MicroblinkWebViewsTesting/blob/main/app/src/main/java/com/example/webviewstest/MainActivity.kt#L132) catch is that the same permissions need to be requested within the Webview object (code in [MainActivity.kt](http://MainActivity.kt):

```
if(request.resources.contains(android.webkit.PermissionRequest.RESOURCE_VIDEO_CAPTURE)) {
    if (ContextCompat.checkSelfPermission(
            this@MainActivity, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED) {
        request.grant(request.resources)
    } else {
        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }
}
```

There’s also the ‘else’ part of that branch that checks for the `android.webkit.PermissionRequest.RESOURCE_AUDIO_CAPTURE` permission, but you can check that in the code [here](https://github.com/Hitman666/MicroblinkWebViewsTesting/blob/main/app/src/main/java/com/example/webviewstest/MainActivity.kt#L111C59-L111C114).

## Conclusion
Hope this helps, and if you'll have any additional questions, please [reach out](https://microblink.com/contact-us/) to us and we'll be happy to help.