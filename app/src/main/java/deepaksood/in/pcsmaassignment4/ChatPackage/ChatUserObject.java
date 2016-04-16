package deepaksood.in.pcsmaassignment4.ChatPackage;

import java.io.Serializable;

/**
 * Created by deepak on 16/4/16.
 */
public class ChatUserObject implements Serializable{
    private String chatuserMobileNum;
    private String chatUserDisplayName;
    private String chatUserPhotoUrl;
    private String chatUserDisplayEmailId;
    private String chatUserCoverUrl;

    public ChatUserObject(String chatuserMobileNum, String chatUserDisplayName, String chatUserPhotoUrl, String chatUserDisplayEmailId, String chatUserCoverUrl) {
        this.chatuserMobileNum = chatuserMobileNum;
        this.chatUserDisplayName = chatUserDisplayName;
        this.chatUserPhotoUrl = chatUserPhotoUrl;
        this.chatUserDisplayEmailId = chatUserDisplayEmailId;
        this.chatUserCoverUrl = chatUserCoverUrl;
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
