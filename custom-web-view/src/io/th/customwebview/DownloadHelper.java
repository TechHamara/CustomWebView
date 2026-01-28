package io.th.customwebview;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import io.th.customwebview.helpers.StatusType;

import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.Options;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent;
import com.google.appinventor.components.runtime.ComponentContainer;
import com.google.appinventor.components.runtime.EventDispatcher;
import com.google.appinventor.components.runtime.OnDestroyListener;
import com.google.appinventor.components.runtime.util.AsynchUtil;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@DesignerComponent(version = 7, versionName = "1.0", description = "Helper class of CustomWebView extension for downloading files <br> Developed by TechHamara", nonVisible = true, iconName = "icon.png")
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
        if (android.os.Build.VERSION.SDK_INT >= 34) {
            context.registerReceiver(completed, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                    Context.RECEIVER_EXPORTED);
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

    @SimpleFunction(description = "Returns the URI of the downloaded file")
    public String GetUriString(long id) {
        try {
            return downloadManager.getUriForDownloadedFile(id).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @SimpleFunction(description = "Returns the MIME type of the downloaded file")
    public String GetMimeType(long id) {
        return downloadManager.getMimeTypeForDownloadedFile(id);
    }

    @SimpleFunction(description = "Returns the status of the download: 1=Pending, 2=Running, 4=Paused, 8=Successful, 16=Failed")
    public int GetDownloadStatus(@Options(StatusType.class) long id) {
        int status = -1;
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(id);
        Cursor cursor = downloadManager.query(query);
        if (cursor != null && cursor.moveToFirst()) {
            int statusIdx = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
            if (statusIdx != -1) {
                status = cursor.getInt(statusIdx);
            }
            cursor.close();
        }
        return status;
    }

    @SimpleFunction(description = "Returns the reason code if the download failed. 1000=Unknown, 1001=File Error, 1002=Unhandled HTTP Code, 1004=HTTP Data Error, 1005=Too Many Redirects, 1006=Insufficient Space, 1007=Device Not Found, 1008=Cannot Resume, 1009=File Already Exists, 1011=Placeholder Not Supported.")
    public int GetFailureReason(long id) {
        return getIntColumn(id, DownloadManager.COLUMN_REASON);
    }

    @SimpleFunction(description = "Returns the reason code if the download is paused. 1=Waiting to Retry, 2=Waiting for Network, 3=Queued for WiFi, 4=Unknown.")
    public int GetPausedReason(long id) {
        return getIntColumn(id, DownloadManager.COLUMN_REASON);
    }

    @SimpleFunction(description = "Returns the title of the download")
    public String GetTitle(long id) {
        String title = "";
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(id);
        Cursor cursor = downloadManager.query(query);
        if (cursor != null && cursor.moveToFirst()) {
            int idx = cursor.getColumnIndex(DownloadManager.COLUMN_TITLE);
            if (idx != -1) {
                title = cursor.getString(idx);
            }
            cursor.close();
        }
        return title;
    }

    @SimpleFunction(description = "Returns the total bytes of the download")
    public long GetTotalSize(long id) {
        return getLongColumn(id, DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
    }

    @SimpleFunction(description = "Returns the bytes downloaded so far")
    public long GetDownloadedBytes(long id) {
        return getLongColumn(id, DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
    }

    private int getIntColumn(long id, String column) {
        int val = -1;
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(id);
        Cursor cursor = downloadManager.query(query);
        if (cursor != null && cursor.moveToFirst()) {
            int idx = cursor.getColumnIndex(column);
            if (idx != -1) {
                val = cursor.getInt(idx);
            }
            cursor.close();
        }
        return val;
    }

    private long getLongColumn(long id, String column) {
        long val = -1;
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(id);
        Cursor cursor = downloadManager.query(query);
        if (cursor != null && cursor.moveToFirst()) {
            int idx = cursor.getColumnIndex(column);
            if (idx != -1) {
                val = cursor.getLong(idx);
            }
            cursor.close();
        }
        return val;
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

    @SimpleFunction(description = "Downloads the given file")
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
        AsynchUtil.runAsynchronously(new Runnable() {
            @Override
            public void run() {
                boolean run = true;
                while (run) {
                    DownloadManager.Query downloadQuery = new DownloadManager.Query();
                    downloadQuery.setFilterById(lastRequestId);
                    Cursor cursor = downloadManager.query(downloadQuery);
                    if (cursor != null && cursor.moveToFirst()) {
                        int statusIdx = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        final int status = (statusIdx != -1) ? cursor.getInt(statusIdx) : DownloadManager.STATUS_FAILED;

                        if (status != DownloadManager.STATUS_FAILED && !isCancelled) {
                            int downloadedIdx = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                            int totalIdx = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);

                            int downloadedSize = (downloadedIdx != -1) ? cursor.getInt(downloadedIdx) : 0;
                            final int totalSize = (totalIdx != -1) ? cursor.getInt(totalIdx) : 1; // avoid div by zero

                            cursor.close();
                            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                run = false;
                            }
                            final int progress = (int) ((((long) downloadedSize) * 100) / ((long) totalSize));
                            form.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DownloadProgressChanged(progress);
                                }
                            });
                        } else {
                            if (!cursor.isClosed()) {
                                cursor.close();
                            }
                            run = false;
                            isCancelled = true;
                            form.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DownloadFailed();
                                }
                            });
                        }
                    } else if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        });
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

    @SimpleFunction(description = "Tries to open the downloaded file by id")
    public void OpenFile(long id) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = downloadManager.getUriForDownloadedFile(id);
            String mimeType = downloadManager.getMimeTypeForDownloadedFile(id);
            intent.setDataAndType(uri, mimeType);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
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