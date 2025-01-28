# Microblink Webview Sample

## TL;DR

This repository shows a basic proof of concept for running Microblink's Browser SDKs in a WebChromeClient so that it:

- Requests adequate permissions from the user in the native layer
- Loads the designated URL which should host one of Microblink's Browser SDKs
- Ensures permission requests from the browser are appropriately handled by the native layer

To deploy the sample application, simply clone the repository, update the [webView.loadUrl]([here](https://github.com/tyler-brutsman/example-android-webview-blinkid/blob/main/app/src/main/java/com/example/webviewstest/MainActivity.kt#L141) and deploy it on your physical Android device.

## Requirements
The most important thing to note is that one has to request the proper permissions (in [AndroidManifest.xml file](https://github.com/tyler-brutsman/example-android-webview-blinkid/blob/main/app/src/main/AndroidManifest.xml):

```
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-feature android:name="android.hardware.camera" android:required="true" />
<uses-feature android:name="android.hardware.camera.autofocus" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
```

Then, along with the usual permission handling on the Android level (see [here](https://github.com/tyler-brutsman/example-android-webview-blinkid/blob/main/app/src/main/java/com/example/webviewstest/MainActivity.kt#L150), the same permissions need to be requested within the Webview object (code in [MainActivity.kt](https://github.com/tyler-brutsman/example-android-webview-blinkid/blob/main/app/src/main/java/com/example/webviewstest/MainActivity.kt):

```
    if (request.resources.contains(PermissionRequest.RESOURCE_VIDEO_CAPTURE) ||
        request.resources.contains(PermissionRequest.RESOURCE_AUDIO_CAPTURE) ) {
        request.grant(request.resources)
    }
```

## Conclusion
By ensuring the necessary permissions are incorporated into the Android Manifest and browser permission requests are appropriately replied by the native layer, Microblink's Browser SDKs can be easily accessed in a WebView context.
Hope this helps, and if you'll have any additional questions, please [reach out](https://microblink.com/contact-us/) to us and we'll be happy to help.