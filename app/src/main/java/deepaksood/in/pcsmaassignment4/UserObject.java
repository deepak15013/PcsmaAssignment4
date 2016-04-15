package deepaksood.in.pcsmaassignment4;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by deepak on 5/4/16.
 */

@DynamoDBTable(tableName = "UserDetails")
public class UserObject {
    private String mobileNum;
    private String displayName;
    private String displayEmailId;
    private String photoUrl;
    private String coverUrl;

    @DynamoDBHashKey(attributeName = "MobileNumber")
    public String getMobileNum() {
        return mobileNum;
    }

    public void setMobileNum(String mobileNum) {
        this.mobileNum = mobileNum;
    }

    @DynamoDBAttribute(attributeName = "DisplayName")
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @DynamoDBAttribute(attributeName = "DisplayEmailId")
    public String getDisplayEmailId() {
        return displayEmailId;
    }

    public void setDisplayEmailId(String displayEmailId) {
        this.displayEmailId = displayEmailId;
    }

    @DynamoDBAttribute(attributeName = "PhotoUrl")
    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @DynamoDBAttribute(attributeName = "CoverUrl")
    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }
}
