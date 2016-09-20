package com.bookwormng.android.Data;

import android.provider.BaseColumns;

/**
 * Created by Tofunmi Seun on 17/08/2015.
 */
public class Contract {
    public static  final class DataEntry implements BaseColumns
    {
        public static final String tableName = "questions";
        public static final String columnQuestion = "questionColumn";
        public static final String columnAnswer = "answerColumn";
        public static final String columnId = "IdColumn";
        public static  final String columnAnswered = "UNANSWERED";
    }
    public static final class UserEntry implements BaseColumns
    {
        public static final String tableName = "user";
        public static final String handleColumn = "handleColumn";
        public static final String nameColumn = "nameColumn";
        public static final String locationColumn = "locationColumn";
        public static final String bioColumn = "bioColumn";
        public static final String avatarColumn = "avatarColumn";
        public static final String picColumn = "picColumn";
        public static final String accessTokenColumn = "accessTokenColumn";
        public static final String accessTokenSecretColumn = "accessTokenSecretColumn";
        public static final String followerColumn = "followersColumn";
        public static final String friendsColumn = "friendsColumn";
        public static final String scoreColumn = "scoreColumn";
        public static final String adColumn = "adColumn";
        public static final String userIdColumn = "userIdColumn";
        public static final String swipeLimitColumn = "swipeLimitColumn";
        public static final String lastQuestionIdColumn = "lastQuestionIdColumn";
        public static final String lastAdvertIdColumn = "lastAdvertIdColumn";
        }
    public static final class AdsEntry implements BaseColumns
    {
        public static final String tableName = "ads";
        public static final String contentColumn = "contentColumn";
        public static final String brandColumn = "brandColumn";
        public static final String sharedColumn = "sharedColumn";
        public static final String idColumn = "IdColumn";
    }
}
