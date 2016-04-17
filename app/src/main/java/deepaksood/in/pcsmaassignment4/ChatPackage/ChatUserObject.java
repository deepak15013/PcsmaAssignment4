package deepaksood.in.pcsmaassignment4.ChatPackage;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by deepak on 16/4/16.
 */
public class ChatUserObject implements Serializable{

    private static final String TAG = ChatUserObject.class.getSimpleName();

    private String chatuserMobileNum;
    private String chatUserDisplayName;
    private String chatUserPhotoUrl;
    private String chatUserDisplayEmailId;
    private String chatUserCoverUrl;
    public ArrayList<ChatMessage> chatMessages;

    public ChatUserObject(String chatuserMobileNum, String chatUserDisplayName, String chatUserPhotoUrl, String chatUserDisplayEmailId, String chatUserCoverUrl) {
        this.chatuserMobileNum = chatuserMobileNum;
        this.chatUserDisplayName = chatUserDisplayName;
        this.chatUserPhotoUrl = chatUserPhotoUrl;
        this.chatUserDisplayEmailId = chatUserDisplayEmailId;
        this.chatUserCoverUrl = chatUserCoverUrl;
        chatMessages = new ArrayList<>();
        Log.v(TAG,"new Chat User object ");
    }

    public ArrayList<ChatMessage> getChatMessages() {
        return chatMessages;
    }

    public void setChatMessages(ArrayList<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    public String getChatuserMobileNum() {
        return chatuserMobileNum;
    }

    public void setChatuserMobileNum(String chatuserMobileNum) {
        this.chatuserMobileNum = chatuserMobileNum;
    }

    public String getChatUserDisplayName() {
        return chatUserDisplayName;
    }

    public void setChatUserDisplayName(String chatUserDisplayName) {
        this.chatUserDisplayName = chatUserDisplayName;
    }

    public String getChatUserPhotoUrl() {
        return chatUserPhotoUrl;
    }

    public void setChatUserPhotoUrl(String chatUserPhotoUrl) {
        this.chatUserPhotoUrl = chatUserPhotoUrl;
    }

    public String getChatUserDisplayEmailId() {
        return chatUserDisplayEmailId;
    }

    public void setChatUserDisplayEmailId(String chatUserDisplayEmailId) {
        this.chatUserDisplayEmailId = chatUserDisplayEmailId;
    }

    public String getChatUserCoverUrl() {
        return chatUserCoverUrl;
    }

    public void setChatUserCoverUrl(String chatUserCoverUrl) {
        this.chatUserCoverUrl = chatUserCoverUrl;
    }
}
