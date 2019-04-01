package com.panda_doc.python.conf;

public class Conf {
    public static final String DOMAIN = "https://dashidan.com";
    //    public static final String DOMAIN = "http://192.168.18.4";
    public static final String URL_INDEX = "index";
    /**
     * version check
     */
    public static final String KEY_VERSION = "v";
    public static final String KEY_APK_URL = "apk_url";
    public static final String KEY_APK_NAME = "apk_name";

    public static final String FOLDER_APK = "python3";

    public static final String URL_DOC_CONTENT_PRE = DOMAIN + "/and_doc/python3/";
    public static final String URL_VERSION = URL_DOC_CONTENT_PRE + "version.json";
    public static final String URL_VERSION_CN = URL_DOC_CONTENT_PRE + "version.cn.json";
    public static final String URL_CATALOG = URL_DOC_CONTENT_PRE + "catalog.txt";
    public static final String URL_CATALOG_CN = URL_DOC_CONTENT_PRE + "catalog.cn.txt";

    public static final String URL_NOTE_GET = DOMAIN + "/php/note_get.php?num=";
    public static final String URL_NOTE_ADD = DOMAIN + "/php/note_add.php";
    public static final String URL_RANK_LIST = DOMAIN + "/php/rank_list.php?p=python";
    public static final String URL_MORE_APP = DOMAIN + "/php/more_app_list.php?p=python";
    public static final String URL_USER_LOGIN = DOMAIN + "/php/user_login.php";
    public static final String URL_UPLOAD_IMG = DOMAIN + "/php/upload_img.php";

    public static final int NOTE_MAX_LENGTH = 500;
}
