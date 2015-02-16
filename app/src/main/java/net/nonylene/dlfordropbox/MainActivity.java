package net.nonylene.dlfordropbox;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends Activity implements SaveDialogFragment.SaveDialogListener {
    private String file_url;
    private File dir;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            Uri uri = getIntent().getData();
            String url = uri.toString();
            Pattern pattern = Pattern.compile("^https?://www.dropbox.com/s/([^/]+)/([^\\?]+)");
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                Log.v("match", "success");
            }
            String name = matcher.group(2);
            file_url = "https://dl.dropboxusercontent.com/s/" + matcher.group(1) + "/" + name;
            Bundle bundle = new Bundle();
            bundle.putString("filename", name);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            // set download directory
            String directory = preferences.getString("download_dir", "Download");
            File root = Environment.getExternalStorageDirectory();
            // set filename (follow setting)
            dir = new File(root, directory);

            bundle.putString("dir", dir.toString());

            if (preferences.getBoolean("skip_dialog", false)) {
                save(name);
            } else {
                SaveDialogFragment dialogFragment = new SaveDialogFragment();
                dialogFragment.setArguments(bundle);
                dialogFragment.setTargetFragment(null, 0);
                dialogFragment.show(getFragmentManager(), "download");
            }
        }
    }

    private void save(String filename) {
        dir.mkdirs();
        File path = new File(dir, filename);
        //save file
        Uri uri = Uri.parse(file_url);
        // use download manager
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDestinationUri(Uri.fromFile(path));
        request.setTitle(filename);
        request.setDescription("Downloader for Dropbox");
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        // notify
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        downloadManager.enqueue(request);
        Toast.makeText(this, getString(R.string.download_file_title) + path.toString(), Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onPositive(String filename) {
        save(filename);
    }

    @Override
    public void onCancel() {
        finish();
    }
}
