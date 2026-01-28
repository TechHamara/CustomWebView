# CustomWebView

An advanced WebView extension for MIT App Inventor 2 with browser-mode support, ad-blocking, and customized download management.

**Created by**: TechHamara  
**Compiled by**: FAST

## Features

- **Default Browser Support**: Can be recognized as a browser by Android to open links from other apps (e.g., WhatsApp).
- **Multiple WebViews**: Create and manage multiple WebView instances dynamically in different arrangements.
- **Deep Linking**: Built-in support for `tel:`, `mailto:`, `whatsapp:`, `geo:`, and custom schemes.
- **Ad Blocking**: Block ads by specifying a list of hosts.
- **Download Management**: Dedicated `DownloadHelper` to handle file downloads with progress tracking.
- **Desktop Mode**: Toggle between Mobile and Desktop user agents.
- **File Uploads**: Support for file chooser and uploads.
- **Printing**: Print web content directly to PDF/Printer.
- **JavaScript Interface**: Two-way communication between App Inventor blocks and JavaScript.

## Blocks & Functions

### CustomWebView

**Lifecycle & Setup**
- `CreateWebView(container, id)`: Creates a new WebView in the specified arrangement.
- `SetWebView(id)`: Sets the active WebView to the given ID.
- `RemoveWebView(id)`: Destroys and removes the specified WebView.
- `CurrentId()`: Returns the ID of the currently active WebView.

**Loading & Navigation**
- `GoToUrl(url)`: Loads a webpage.
- `LoadHtml(html)`: Loads raw HTML content.
- `Reload()`: Reloads the current page.
- `StopLoading()`: Stops the current load.
- `GoBack()`, `GoForward()`: Navigate history.
- `CanGoBack()`, `CanGoForward()`: Check history availability.
- `ClearInternalHistory()`: Clears the back/forward list.

**Settings & Behavior**
- `FollowLinks`: Whether to load links inside the WebView.
- `DeepLinks`: Enable/disable handling of external schemes (`tel:`, `whatsapp:`, etc.).
- `IgnoreSslErrors`: (New) Ignore SSL certificate errors (useful for self-signed certs).
- `BlockAds`: Enable/disable ad blocking.
- `AdHosts`: String of hosts to block.
- `DesktopMode`: Switch user agent to request desktop sites.
- `UserAgent`: Get or set custom user agent string.
- `ZoomEnabled`, `DisplayZoom`: Control zoom gestures and controls.
- `FileAccess`: Allow/deny file access.
- `Autofill`: Enable/disable autofill for forms (Android 8+).

**Design & Display (Professional)**
- `ForceDarkMode`: Force the web content to render in dark mode (Off, On, Auto) [Android 10+].
- `MixedContentMode`: Control loading of HTTP resources on HTTPS pages (Always Allow, Never Allow, Compatibility) [Android 5+].
- `ScrollToTop()`, `ScrollToBottom()`: Quickly scroll to page boundaries.
- `CaptureScreenshot(fileName)`: Save current view as an image.

**JavaScript & Interface**
- `EvaluateJavaScript(script)`: Run JS on the page.
- `WebViewString`: Exchange strings with JS via `window.AppInventor.getWebViewString()` and `setWebViewString()`.
- `OnConsoleMessage`: Event for console logs.

**Other Features**
- `PrintWebContent(documentName)`: Print the current page.
- `Find(string)`, `FindNext(forward)`: Search text in page.
- `ClearCookies()`, `GetCookies(url)`: Manage cookies.
- **Swipe Gestures**: `Swiped(id, direction)` (1=Right, 2=Left, 3=Up, 4=Down).

### DownloadHelper

- `Download(url, mimeType, fileName, downloadDir)`: Starts a download.
- `Cancel()`: Cancels current download.
- `OpenFile(id)`: Opens the downloaded file (requires valid ID).
- `GuessFileName(url, mimeType, disposition)`: Helper to name files.
- `GetDownloadStatus(id)`: Returns current status code.
- `GetFailureReason(id)`, `GetPausedReason(id)`: Detailed reason codes.
- `GetTitle(id)`: Returns the download title.
- `GetTotalSize(id)`, `GetDownloadedBytes(id)`: Track size info.
- **Events**: `DownloadStarted`, `DownloadProgressChanged`, `DownloadCompleted`, `DownloadFailed`.

### BrowserPromptHelper

- `GetStartUrl()`: Returns the URL that opened the app (if set as default browser).
- `IsDefaultBrowser()`: Check if App is currently the default browser.
- `RequestDefaultBrowser()`: Request to set as default browser (Android 10+).
- `OnResume(url)`: Event triggered when the app is brought to foreground with a new URL.

## Usage Examples

### 1. Setting up As Default Browser
To make your app appear in the Android "Open with" list for web links:
1.  **Manifest**: The extension automatically adds necessary intent filters (`VIEW`, `http`, `https`).
2.  **Logic**:
    - Check `IsDefaultBrowser`. If false, call `RequestDefaultBrowser`.
    - In `Screen.Initialize`: Call `BrowserPromptHelper.GetStartUrl()`.
    - If it returns a non-empty string, load it in your WebView.
    - Handle `BrowserPromptHelper.OnResume` to load new links when the app is already running.

### 2. Downloading Files
```blocks
// OnDownloadNeeded Event
CustomWebView.OnDownloadNeeded(id, url, contentDisposition, mimeType, size) {
    String filename = DownloadHelper.GuessFileName(url, mimeType, contentDisposition);
    DownloadHelper.Download(url, mimeType, filename, "/Download/");
}
```

### 3. Handling SSL Errors
If you are testing with a local server or self-signed certificate:
- Set `CustomWebView.IgnoreSslErrors` to `true` in the Designer or Blocks.

## Permissions
The extension requests:
- `INTERNET`
- `WRITE_EXTERNAL_STORAGE` / `READ_EXTERNAL_STORAGE`
- `ACCESS_FINE_LOCATION` (if geolocation is used)
- `CAMERA` (for uploads)

---
*Note: For Android 11+ compatibility, ensure you deal with scoped storage and package visibility queries.*
