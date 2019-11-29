package com.panda_doc.conf;

public class Conf {
    public static final String DOMAIN = "https://panda-doc.com";
    //    public static final String DOMAIN = "http://192.168.18.3";
    public static final String PANDA_DOC = "panda_doc";
    /**
     * version check
     */
    public static final String KEY_VERSION = "v";
    public static final String KEY_APK_URL = "apk_url";
    public static final String KEY_APK_NAME = "apk_name";

    public static final String DOC_TAG = "python3.7.4";
    public static final String URL_DOC_CONTENT_PRE = DOMAIN + "/doc/" + DOC_TAG + "/";
    public static final String URL_VERSION = "version.json";
//    public static final String URL_CATALOG = "catalog.json";
    public static final String URL_CATALOG = "catalog.php";

    public static final String URL_USER_LOGIN = DOMAIN + "/php/app_login.php";
}
