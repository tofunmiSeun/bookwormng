package com.bookwormng.android.Data;

/**
 * Created by Tofunmi Seun on 09/11/2015.
 */
public class TwitterConstants {

    public static final String CONSUMER_KEY = "NCQjRYmjJRxSvKLbl5Wq5FzPm";
    public static final String CONSUMER_SECRET= "uO9vNzBB3RD4Eib5G3n1HEduBzmK9B6LUBMSjGVlis2ZDsPCJS";

    public static final String REQUEST_URL = "https://api.twitter.com/oauth/request_token";
    public static final String ACCESS_URL = "https://api.twitter.com/oauth/access_token";
    public static final String AUTHORIZE_URL = "https://api.twitter.com/oauth/authorize";

    final public static String  CALLBACK_SCHEME = "http";
    final public static String  CALLBACK_URL = CALLBACK_SCHEME + "://bookwormng.boo";

    public static long UserId;
    public static String avatarUrl = "";
}
