package com.panda_doc.python.version;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.panda_doc.python.R;
import com.panda_doc.python.conf.Conf;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

public class CheckVersionDialogFragment extends DialogFragment {

    Context context;
    String apkUrl;
    String apkName;

    static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 99;

    public void setApkUrl(Context context, String apkUrl, String apkName) {
        this.context = context;
        this.apkUrl = apkUrl;
        this.apkName = apkName;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 创建更新对话框
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_new_version);
        builder.setMessage(R.string.dialog_update)
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        /** 检测权限*/
                        checkPermission();
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        return builder.create();
    }

    private void checkPermission() {
        int permission = ActivityCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission == PackageManager.PERMISSION_GRANTED) {
            downLoadApk();
        } else {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        downLoadApk();
                    } else {
                        Log.e(Conf.DOMAIN, " onRequestPermissionsResult " + requestCode + " grantResults[0] " + grantResults[0]);
                    }
                } else {
                    Log.e(Conf.DOMAIN, " onRequestPermissionsResult " + requestCode + " grantResults " + grantResults.length);
                }
                break;
            default:
                Log.e(Conf.DOMAIN, " onRequestPermissionsResult " + requestCode);
                break;
        }
    }

    private void downLoadApk() {
        final ProgressDialog pd = new ProgressDialog(context);
        //必须一直下载完，不可取消
        pd.setCancelable(false);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setTitle(getString(R.string.dialog_downloading));
        pd.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = downloadFile(pd);
                    // 结束掉进度条对话框
                    pd.dismiss();

                    if (file != null) {
                        installApk(file);
                    } else {
                        Toast.makeText(context, R.string.dialog_try_again, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(Conf.DOMAIN, e.toString());
                }
            }
        }).start();
    }

    private File downloadFile(ProgressDialog pd) throws Exception {
        // 如果相等的话表示当前的sdcard挂载在手机上并且是可用的
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            URL url = new URL(apkUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            // 获取到文件的大小
            pd.setMax(conn.getContentLength());
            InputStream is = conn.getInputStream();
            String fileName = Environment.getExternalStorageDirectory() + "/" + Conf.DOC_NAME + "/" + apkName;

            File file = new File(fileName);
            // 目录不存在创建目录
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);

            byte[] buffer = new byte[1024];
            int len;
            int total = 0;
            while ((len = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                total += len;
                // 获取当前下载量
                pd.setProgress(total);
            }
            fos.close();
            bis.close();
            is.close();
            return file;
        } else {
            return null;
        }
    }

    /**
     * 安装apk
     */
    private void installApk(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // TODO 暂时注掉7.0的 启动应用的方法， 华为7.0测试正常，回头测试一下 oppo, vivo
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            String authority = "com.dashidan.fileprovider";
//            Uri contentUri = FileProvider.getUriForFile(context, authority, file);
//            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
//        } else {
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        }
        context.startActivity(intent);
    }
}
