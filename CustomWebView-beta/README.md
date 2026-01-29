# CustomWebView Extension

An advanced WebView extension for MIT App Inventor and its derivatives, providing enhanced web browsing capabilities with modern features.

## Latest Release - Version 14 Beta

### Developed by Sunny Gupta and Customized by TechHamara

This document outlines the features, methods, properties, and events for the `CustomWebView` extension and its helper classes: `BrowserPromptHelper` and `DownloadHelper`.

---

## 1. BrowserPromptHelper

A helper component designed to manage the app's role as a browser and handle deep linking intents.

### Methods

*   **RegisterScreen(String screenName, String startValue)**
    *   Registers the screen to be opened when the app is launched from a browser via a deep link or implicit intent.
    *   *Parameters*: `screenName` (Screen to open), `startValue` (Value to pass).

*   **GetStartUrl()**
    *   Returns the URL which started the current activity (if any).

*   **IsDefaultBrowser()**
    *   Checks if the app is currently set as the system's default browser.
    *   *Returns*: `true` if default, `false` otherwise.

*   **RequestSetDefaultBrowser()**
    *   Requests the user to set this app as the default browser.
    *   Supports modern Android (RoleManager on Android Q+) and legacy settings intents.

*   **OpenDefaultAppsSettings()**
    *   Opens the system settings screen where the user can manage default applications.

### Events

*   **OnResume(String url)**
    *   Raised when the app is resumed (e.g., via a New Intent).
    *   *Arguments*: `url` (The URL that opened the app, or empty string).

---

## 2. DownloadHelper

A helper component to manage file downloads using the native Android `DownloadManager`.

### Properties

*   **NotificationVisibility(int visibility)**
    *   Sets the visibility of the download notification (e.g., `VISIBILITY_VISIBLE_NOTIFY_COMPLETED`).

### Methods

*   **Download(String url, String mimeType, String fileName, String downloadDir)**
    *   Starts a download task.
    *   *Parameters*:
        *   `downloadDir`: Path to save. Use `~` prefix for external files directory (e.g., `~/Downloads`).

*   **DownloadWithHeaders(String url, String mimeType, String fileName, String downloadDir, String userAgent, YailDictionary headers)**
    *   Starts a download with custom HTTP headers and User-Agent.

*   **GuessFileName(String url, String mimeType, String contentDisposition)**
    *   Returns a guessed file name based on the URL and content headers.

*   **GetUriString(long id)**
    *   Returns the local file URI for a successfully downloaded file given its Download ID.

*   **GetMimeType(long id)**
    *   Returns the MIME type of the downloaded file.

*   **GetFileSize(String url)**
    *   Asynchronously fetches the file size of the given URL. Triggers the `GotFileSize` event.

*   **GetDownloadStatus(long id)**
    *   Returns the status of a download: `RUNNING`, `PAUSED`, `PENDING`, `SUCCESSFUL`, `FAILED`, or `UNKNOWN`.

*   **GetFailureReason(long id)**
    *   Returns the specific reason for a download failure (e.g., `ERROR_FILE_ERROR`, `404`, `500`).

*   **Remove(long id)**
    *   Cancels and removes the download with the specified ID.

*   **OpenFile(int id)**
    *   Attempts to open the downloaded file using the system's default handler for its MIME type.

*   **Cancel()**
    *   Cancels the *current* download request (the most recently enqueued one).

### Events

*   **DownloadStarted(long id)**
    *   Raised when a download is successfully enqueued.

*   **GotFileSize(long fileSize)**
    *   Raised after `GetFileSize` is called.

*   **DownloadCompleted()**
    *   Raised when a monitored download completes successfully.

*   **DownloadFailed()**
    *   Raised when a download fails.

*   **DownloadProgressChanged(int progress)**
    *   Raised periodically during download with the progress percentage (0-100).

---

## 3. CustomWebView

The core component providing an advanced WebView with enhanced features.

### Core Properties

*   **Visible**: Toggle visibility of the current WebView.
*   **Loading**: Check if the page is currently loading.
*   **CurrentUrl**: Get the URL of the current page.
*   **CurrentPageTitle**: Get the title of the current page.
*   **UserAgent**: Get or set the custom User-Agent string.
*   **BackgroundColor**: Set the background color.
*   **ScrollBarStyle**: Set the style of scrollbars.
*   **OverScrollMode**: Set the over-scroll behavior.
*   **LayerType**: Set hardware or software layer type.
*   **RotationAngle**: Set the rotation of the view.

### Navigation & Page Control

*   **GoToUrl(String url)**: Navigate to a URL.
*   **GoToUrl2(int id, String url)**: Navigate to a URL in a specific WebView ID.
*   **LoadHtml(String html)**: Load raw HTML content.
*   **Reload()**: Reload the current page.
*   **StopLoading()**: Stop the current loading process.
*   **GoBack() / GoForward()**: History navigation.
*   **CanGoBack() / CanGoForward()**: Check history availability.
*   **LoadWithHeaders(String url, YailDictionary headers)**: Load a URL with custom HTTP headers.
*   **ClearCache()**: Clear the resource cache.
*   **ClearInternalHistory()**: Clear the back/forward history list.
*   **ScrollToTop()**: Scroll to the top of the page.
*   **ScrollToBottom()**: Scroll to the bottom of the page.
*   **Scrollable(boolean)**: Enable or disable page scrolling.

### Ad Blocker (Enhanced)

*   **BlockAds**: Enable or disable ad blocking.
*   **InitAdBlocker()**: Initialize the enhanced ad blocker context.
*   **LoadDefaultFilterList()**: Load the built-in minimal filter list.
*   **LoadFilterListFromAsset/Url/File**: Load filter lists asynchronously from various sources.
*   **AddFilterRules(String rules)**: Add custom AdBlock Plus compatible rules.
*   **AddToWhitelist(String host) / RemoveFromWhitelist(String host)**: Manage exception lists.
*   **GetBlockedRequestsCount() / GetAllowedRequestsCount()**: Get blocking statistics.

### JavaScript Integration

*   **EnableJS**: Enable or disable JavaScript execution.
*   **EvaluateJavaScript(String script)**: Asynchronously execute JavaScript. Result returned in `AfterJavaScriptEvaluated`.
*   **WebViewString**: Property to exchange string data with the web page via `window.AppInventor.getWebViewString()` and `setWebViewString()`.
*   **InjectCSS(String css)**: Inject CSS styles into the current page.

### Files, Uploads, & Downloads

*   **FileAccess**: Allow or deny access to local files.
*   **FileUploadNeeded**: Event raised when a file upload interface is triggered.
*   **UploadFile(String contentUri)**: Respond to the file upload request.
*   **DownloadBlob(String url, String mime, String name, String dir)**: Special method to handle Blob URL downloads.
*   **SaveArchive(String dir)**: Save the current page as a Web Archive (.mht).
*   **CaptureScreenshot(String fileName)**: Save a screenshot of the visible WebView.

### Zoom & Display

*   **ZoomEnabled**: Enable pinch-to-zoom and gestures.
*   **DisplayZoom**: Show or hide on-screen zoom controls.
*   **ZoomPercent**: Set the default text zoom percentage.
*   **DesktopMode**: Toggle Desktop User-Agent mode.
*   **FontSize**: Set the default font size.
*   **SetDarkMode(boolean enable)**: Force dark mode for web contents.
*   **InvokeZoomPicker()**: Show the system zoom widget.

### Advanced Features

*   **DeepLinks**: Enable support for external schemes (tel:, whatsapp:, etc.).
*   **RegisterDeepLink(String scheme)**: Register specific schemes to be intercepted.
*   **CreateWebView(Object container, int id)**: Dynamically create a new WebView inside a container.
*   **RemoveWebView(int id)**: Destroy a dynamically created WebView.
*   **Find(String text)**: Search for text on the page.
*   **PrintWebContent(String documentName)**: Print the current page to PDF.
*   **Cookie Management**: `SetCookies`, `GetCookies`, `ClearCookies`.
*   **SSL Certificates**: `GetSslCertificate` to retrieve info, `ProceedSslError` to handle SSL errors, `ClearSslPreferences` to clear stored preferences.
*   **VibrationEnabled(boolean)**: Enable or disable haptic feedback.
*   **ClearFormData(int id)**: Clear auto-fill form data.

### Key Events

*   **PageStarted / PageLoaded**: Standard loading lifecycle events.
*   **OnErrorReceived**: Triggered when a loading error occurs.
*   **OnProgressChanged**: periodic loading progress updates.
*   **OnConsoleMessage**: Relays JavaScript console messages to the app.
*   **OnDownloadNeeded**: Triggered when a file download is requested by the page.
*   **OnNewWindowRequest**: Triggered when a link tries to open in a new window/tab.
*   **OnPermissionRequest**: Web page requesting permissions (e.g., Camera, Mic).
*   **OnGeolocationRequested**: Web page requesting location access.
*   **OnJsAlert / OnJsConfirm / OnJsPrompt**: Handling of standard JS dialogs.
*   **Swiped**: Triggered on swipe gestures (if enabled).

## Usage Examples

### 1. Basic Initialization
Initialize the web view and load a URL when the screen initializes.

```blocks
// When Screen1.Initialize
CustomWebView1.CreateWebView(Container: VerticalArrangement1, ID: 1)
CustomWebView1.GoToUrl(url: "https://www.google.com")
```

### 2. Handling File Downloads
Use the `DownloadHelper` to manage downloads triggered from the web view.

```blocks
// When CustomWebView1.OnDownloadNeeded
DownloadHelper1.Download(url, mimeType, contentDisposition, "~/Downloads/myfile.ext")
```

### 3. JavaSript Communication
Exchange data between your app and the web page.

**In App Inventor/Kodular:**
```blocks
// Execute JavaScript and get result in AfterJavaScriptEvaluated event
CustomWebView1.EvaluateJavaScript("document.title;")
```

**In Web Page (HTML/JS):**
```html
<script>
    // Send data to the app
    window.AppInventor.setWebViewString("Hello from JavaScript!");
    
    // Get data from the app
    var data = window.AppInventor.getWebViewString();
</script>
```

## FAQ
> Will disabling `DeepLink` property not add my app in browsers list?<br>
Ans:- No, that's entirely a different thing.It specifies whether deep urls (such as `tel:`, `whatsapp:`) should open respective apps or not.

> What is CustomWebViewHelper extension? <br>
Ans:- It is an helper class/extension which you can use if you want to add your app in browsers list.

> How can I set a screen name as activity to be launched after clicking external link? <br>
Ans:- use `BrowserPromptHelper`'s `RegisterScreen` method.

> Why are file uploads not working? <br>
Ans:- Ensure that you have granted `WriteExternalStorage` permission and that you are handling the `FileUploadNeeded` event correctly.

> Why do videos not auto-play? <br>
Ans:- Android blocks auto-play by default to save data and battery. Videos usually require a user gesture to start. You can try adjusting `MediaPlaybackRequiresUserGesture` if available, or ensure the user interacts with the page.

> Why does the WebView not resize correctly? <br>
Ans:- Ensure that your WebView component's Width and Height are set to "Fill Parent" or specific percentages, and that its container allows for expansion. Avoid using fixed pixels if you want responsive resizing.


**Note**: This is a beta release. Please test thoroughly before using in production applications.