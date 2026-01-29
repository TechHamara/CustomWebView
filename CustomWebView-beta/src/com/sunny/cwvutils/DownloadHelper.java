package com.sunny.cwvutils;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleObject;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent;
import com.google.appinventor.components.runtime.ComponentContainer;
import com.google.appinventor.components.runtime.EventDispatcher;
import com.google.appinventor.components.runtime.OnDestroyListener;
import com.google.appinventor.components.runtime.util.AsynchUtil;
import com.google.appinventor.components.runtime.util.YailDictionary;
import com.google.appinventor.components.runtime.util.YailDictionary;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

@DesignerComponent(version = 14, versionName = "14", description = "Helper class of CustomWebView extension for downloading files <br> Developed by Sunny Gupta and Customized by TechHamara", category = ComponentCategory.EXTENSION, nonVisible = true, iconName = "https://i.ibb.co/4wLNN1Hs/ktvu4bapylsvnykoyhdm-c-fill-w-20-h-20.png", helpUrl = "https://github.com/vknow360/CustomWebView", androidMinSdk = 21)
@SimpleObject(external = true)
public class DownloadHelper extends AndroidNonvisibleComponent implements OnDestroyListener {
    private final Context context;
    private final DownloadManager downloadManager;
    private long lastRequestId;
    private int nVisibility = DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED;
    private boolean isCancelled = false;
    public BroadcastReceiver completed = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1) == lastRequestId) {
                DownloadCompleted();
            }
        }
    };

    public DownloadHelper(ComponentContainer container) {
        super(container.$form());
        context = container.$context();
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(completed, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                    Context.RECEIVER_NOT_EXPORTED);
        } else {
            context.registerReceiver(completed, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }
    }

    @SimpleProperty(description = "Sets download notification visibility")
    public void NotificationVisibility(int i) {
        this.nVisibility = i;
    }

    @SimpleFunction(description = "Returns guessed file name")
    public String GuessFileName(String url, String mimeType, String contentDisposition) {
        return URLUtil.guessFileName(url, contentDisposition, mimeType);
    }

    @SimpleFunction(description = "Returns file uri")
    public String GetUriString(long id) {
        try {
            return downloadManager.getUriForDownloadedFile(id).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @SimpleFunction(description = "Returns file mime type")
    public String GetMimeType(long id) {
        return downloadManager.getMimeTypeForDownloadedFile(id);
    }

    @SimpleFunction(description = "Tries to get file size")
    public void GetFileSize(final String url) {
        AsynchUtil.runAsynchronously(new Runnable() {
            @Override
            public void run() {
                final long[] size = new long[1];
                try {
                    HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
                    con.setRequestProperty("Accept-Encoding", "identity");
                    int statusCode = con.getResponseCode();
                    size[0] = con.getContentLengthLong();
                } catch (IOException e) {
                    e.printStackTrace();
                    size[0] = -1;
                }
                form.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        GotFileSize(size[0]);
                    }
                });
            }
        });
    }

    @SimpleFunction(description = "Downloads the file from the specified URL. Example of mime type: application/pdf")
    public void Download(String url, String mimeType, String fileName, String downloadDir) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setMimeType(mimeType);
        String cookies = CookieManager.getInstance().getCookie(url);
        request.addRequestHeader("cookie", cookies);
        request.setDescription("Downloading file...");
        request.setTitle(fileName);
        request.setNotificationVisibility(nVisibility);
        request.setTitle(fileName);
        if (downloadDir.startsWith("~")) {
            request.setDestinationInExternalFilesDir(context, downloadDir.substring(1), fileName);
        } else {
            request.setDestinationInExternalPublicDir(downloadDir, fileName);
        }
        lastRequestId = downloadManager.enqueue(request);
        DownloadStarted(lastRequestId);
        isCancelled = false;
        MonitorDownload(lastRequestId);
    }

    @SimpleFunction(description = "Downloads the given file with custom headers and user agent")
    public void DownloadWithHeaders(String url, String mimeType, String fileName, String downloadDir, String userAgent,
            YailDictionary headers) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setMimeType(mimeType);
        String cookies = CookieManager.getInstance().getCookie(url);
        request.addRequestHeader("cookie", cookies);
        if (userAgent != null && !userAgent.isEmpty()) {
            request.addRequestHeader("User-Agent", userAgent);
        }
        if (headers != null) {
            for (Object key : headers.keySet()) {
                request.addRequestHeader(key.toString(), headers.get(key).toString());
            }
        }
        request.setDescription("Downloading file...");
        request.setTitle(fileName);
        request.setNotificationVisibility(nVisibility);
        if (downloadDir.startsWith("~")) {
            request.setDestinationInExternalFilesDir(context, downloadDir.substring(1), fileName);
        } else {
            request.setDestinationInExternalPublicDir(downloadDir, fileName);
        }
        lastRequestId = downloadManager.enqueue(request);
        DownloadStarted(lastRequestId);
        isCancelled = false;
        MonitorDownload(lastRequestId);
    }

    private void MonitorDownload(final long id) {
        AsynchUtil.runAsynchronously(new Runnable() {
            @Override
            public void run() {
                final Timer timer = new Timer();
                final TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        DownloadManager.Query downloadQuery = new DownloadManager.Query();
                        downloadQuery.setFilterById(id);
                        Cursor cursor = downloadManager.query(downloadQuery);
                        if (cursor.moveToFirst()) {
                            final int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                            if (status != DownloadManager.STATUS_FAILED && !isCancelled) {
                                int downloadedSize = cursor
                                        .getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                                final int totalSize = cursor
                                        .getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                                cursor.close();
                                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                    timer.cancel();
                                    form.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            DownloadCompleted();
                                        }
                                    });
                                }
                                final int progress = (totalSize > 0)
                                        ? (int) ((((long) downloadedSize) * 100) / ((long) totalSize))
                                        : 0;
                                form.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        DownloadProgressChanged(progress);
                                    }
                                });
                            } else {
                                timer.cancel();
                                if (!isCancelled) { // Only dispatch failed if not manually cancelled (which sets
                                                    // isCancelled)
                                    form.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            DownloadFailed();
                                        }
                                    });
                                }
                            }
                        } else {
                            cursor.close();
                            timer.cancel();
                        }
                    }
                };
                timer.schedule(timerTask, 0, 1000);
            }
        });
    }

    @SimpleFunction(description = "Returns the status of the download")
    public String GetDownloadStatus(long id) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(id);
        Cursor cursor = downloadManager.query(query);
        if (cursor.moveToFirst()) {
            int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            cursor.close();
            switch (status) {
                case DownloadManager.STATUS_RUNNING:
                    return "RUNNING";
                case DownloadManager.STATUS_PAUSED:
                    return "PAUSED";
                case DownloadManager.STATUS_PENDING:
                    return "PENDING";
                case DownloadManager.STATUS_SUCCESSFUL:
                    return "SUCCESSFUL";
                case DownloadManager.STATUS_FAILED:
                    return "FAILED";
            }
        }
        return "UNKNOWN";
    }

    @SimpleFunction(description = "Returns the reason for download failure")
    public String GetFailureReason(long id) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(id);
        Cursor cursor = downloadManager.query(query);
        if (cursor.moveToFirst()) {
            int reason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));
            cursor.close();
            switch (reason) {
                case DownloadManager.ERROR_CANNOT_RESUME:
                    return "ERROR_CANNOT_RESUME";
                case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                    return "ERROR_DEVICE_NOT_FOUND";
                case DownloadManager.ERROR_FILE_ERROR:
                    return "ERROR_FILE_ERROR";
                case DownloadManager.ERROR_HTTP_DATA_ERROR:
                    return "ERROR_HTTP_DATA_ERROR";
                case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                    return "ERROR_INSUFFICIENT_SPACE";
                case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                    return "ERROR_TOO_MANY_REDIRECTS";
                case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                    return "ERROR_UNHANDLED_HTTP_CODE";
                case DownloadManager.ERROR_UNKNOWN:
                    return "ERROR_UNKNOWN";
                case 400:
                    return "BAD_REQUEST";
                case 401:
                    return "UNAUTHORIZED";
                case 403:
                    return "FORBIDDEN";
                case 404:
                    return "NOT_FOUND";
                case 500:
                    return "INTERNAL_SERVER_ERROR";
                case 503:
                    return "SERVICE_UNAVAILABLE";
            }
            return "UNKNOWN_REASON_" + reason;
        }
        return "UNKNOWN";
    }

    @SimpleFunction(description = "Removes the download with given id")
    public int Remove(long id) {
        return downloadManager.remove(id);
    }

    @SimpleEvent(description = "Event invoked when downloading starts")
    public void DownloadStarted(long id) {
        EventDispatcher.dispatchEvent(this, "DownloadStarted", id);
    }

    @SimpleEvent(description = "Event invoked after getting file size")
    public void GotFileSize(long fileSize) {
        EventDispatcher.dispatchEvent(this, "GotFileSize", fileSize);
    }

    @SimpleEvent(description = "Event invoked when downloading gets completed")
    public void DownloadCompleted() {
        EventDispatcher.dispatchEvent(this, "DownloadCompleted");
    }

    @SimpleEvent(description = "Event invoked when downloading gets failed")
    public void DownloadFailed() {
        lastRequestId = 0L;
        EventDispatcher.dispatchEvent(this, "DownloadFailed");
    }

    @SimpleEvent(description = "Event invoked when downloading progress changes")
    public void DownloadProgressChanged(int progress) {
        EventDispatcher.dispatchEvent(this, "DownloadProgressChanged", progress);
    }

    @SimpleFunction(description = "Tries to open the downloaded file from id")
    public void OpenFile(int id) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = downloadManager.getUriForDownloadedFile(id);
            String mimeType = downloadManager.getMimeTypeForDownloadedFile(id);
            intent.setDataAndType(uri, mimeType);
            form.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SimpleFunction(description = "Cancels the current download request")
    public void Cancel() {
        downloadManager.remove(lastRequestId);
        isCancelled = true;
        DownloadFailed();
    }

    @Override
    public void onDestroy() {
        context.unregisterReceiver(completed);
    }

}
