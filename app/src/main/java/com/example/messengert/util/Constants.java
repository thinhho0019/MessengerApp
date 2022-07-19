package com.example.messengert.util;

import java.util.Date;
import java.util.HashMap;

public class Constants {
    public static final String KEY_COLLECTION_USER = "users";
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PREFERENCE_NAME = "chatAppPreference";
    public static final String KEY_USERID = "userId";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_IS_SIGNED_IN = "isSignIn";
    public static final String KEY_FCM_TOKEN = "fcmToken";
    public static final String KEY_USER="user";
    public static final String KEY_COLLECTION_CHAT ="chat";
    public static final String KEY_SENDER_ID="senderId";
    public static final String KEY_RECEIVER_ID="receiverId";
    public static final String KEY_MESSAGE="message";
    public static final String KEY_TIMESTAMP ="timeStamp";
    public static final String KEY_COLLECTION_CONVERSION ="conversion";
    public static final String KEY_SENDER_NAME ="senderName";
    public static final String KEY_RECEIVER_NAME="receiverName";
    public static final String KEY_IMAGE_RECEIVER= "receiverImage";
    public static final String KEY_MESSAGE_LAST ="lastMessage";
    public static final String KEY_AVAILABILITY="availbility";
    public static final String REMOTE_MSG_AUTHORIZATION ="Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE="Content-Type";
    public static final String REMOTE_MESSAGE_DATA="data";
    public static final String REMOTE_MSG_REGISTRATION_IDS ="registrasion_ids";
    public static HashMap<String ,String>remoteMsgHeader =null;
    public static HashMap<String,String> getRemoteMsgHeader (){
        if(remoteMsgHeader ==null){
            remoteMsgHeader = new HashMap<>();
            remoteMsgHeader.put(REMOTE_MSG_AUTHORIZATION,
                    "key=AAAAXDO0Mu0:APA91bGrNUKcV5hS8_lraCRINms5Z2blUXqAySP3rj29KFAqR0-QgS_KXDx8wOgNsznxlHmbuKuWsit4mOBWiajr19buXtG5hxDivpFM-XWWT47xcGpqQ9Czm3RjDc_5m0MmQ29Hshfe");
            remoteMsgHeader.put(REMOTE_MSG_CONTENT_TYPE,"application/json");
        }
        return remoteMsgHeader;
    }
    public static final String KEY_ID_SEEN="IdSeen";
    public static final String KEY_RECEIVER_ID_SEEN="ReceiverIdSeen";
    public static final String KEY_OLD_SENT="oldSent";
    public static final String KEY_LIST_CONVERSION ="listConversion";
}
