package com.dashidan.tasks;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.dashidan.R;
import com.dashidan.conf.Conf;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
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

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_new_version);
        builder.setMessage(R.string.dialog_update)
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        /** 下载app*/
//                        downLoadApk();
                        checkPermission();
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

//    private void downloadApk() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                String mSavePath = Environment.getExternalStorageDirectory() + "/";
//                File apkFile = new File(mSavePath);
//                FileOutputStream fos = null;
//                InputStream is = null;
//                try {
//                    fos = new FileOutputStream(apkFile);
//                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
////                      文件保存路径
//                        File dir = new File(mSavePath);
//                        if (!dir.exists()) {
//                            dir.mkdir();
//                        }
//                        // 下载文件
//                        HttpURLConnection conn = (HttpURLConnection) new URL(apkUrl).openConnection();
//                        conn.connect();
//                        is = conn.getInputStream();
//                        int length = conn.getContentLength();
//
//                        int count = 0;
//                        byte[] buffer = new byte[1024];
//                        while (!mIsCancel) {
//                            int numread = is.read(buffer);
//                            count += numread;
//                            // 计算进度条的当前位置
//                            mProgress = (int) (((float) count / length) * 100);
//                            // 更新进度条
//                            mUpdateProgressHandler.sendEmptyMessage(1);
//
//                            // 下载完成
//                            if (numread < 0) {
//                                mUpdateProgressHandler.sendEmptyMessage(2);
//                                break;
//                            }
//                            fos.write(buffer, 0, numread);
//                        }
//
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    try {
//                        if (fos != null) {
//                            fos.close();
//                        }
//                        if (is != null) {
//                            is.close();
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();
//    }

    private void checkPermission() {
        int permission = ActivityCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission == PackageManager.PERMISSION_GRANTED) {
            downLoadApk();
        } else {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    downLoadApk();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void downLoadApk() {
        final ProgressDialog pd = new ProgressDialog(context);
        //必须一直下载完，不可取消
        pd.setCancelable(false);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在下载安装包");
        pd.setTitle("版本更新");
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
                        Toast.makeText(context, "更新包无法下载", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(Conf.LOG_TAG, e.toString());
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
            String fileName = Environment.getExternalStorageDirectory() + "/" + Conf.FOLDER_APK + "/" + apkName;

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
