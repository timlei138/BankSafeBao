package com.android.bsb;

public class AppComm {

    public static final String DES_PWD = "Bank_"+AppComm.class.hashCode();

    public static final String SHAREDPREF_NAME = "app_pref";

    public static final String KEY_ACCOUNT = "account";

    public static final String KEY_FIRST_USED = "key_first_used";

    public static final int ROLE_TYPE_ADMIN = 0;

    public static final int ROLE_TYPE_MANAGER = 1;

    public static final int ROLE_TYPE_POSTMAN = 2;

    public static final int ROLE_TYPE_SECURITY = 3;
}
