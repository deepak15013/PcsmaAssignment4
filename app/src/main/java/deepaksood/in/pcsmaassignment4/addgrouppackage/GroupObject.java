package deepaksood.in.pcsmaassignment4.addgrouppackage;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBDocument;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.Set;

/**
 * Created by deepak on 27/4/16.
 */
@DynamoDBTable(tableName = "GroupDetails")
public class GroupObject {

    private Set<String> mobileNumArr;
    private String groupName;
    private String groupOwner;
    private String groupPhotoUrl;
    private String groupCoverUrl;
    private int numOfMembers;

    @DynamoDBAttribute(attributeName = "MobileNumArr")
    public Set<String> getMobileNumArr() {
        return mobileNumArr;
    }

    public void setMobileNumArr(Set<String> mobileNumArr) {
        this.mobileNumArr = mobileNumArr;
    }

    @DynamoDBHashKey(attributeName = "GroupName")
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @DynamoDBAttribute(attributeName = "GroupOwner")
    public String getGroupOwner() {
        return groupOwner;
    }

    public void setGroupOwner(String groupOwner) {
        this.groupOwner = groupOwner;
    }

    @DynamoDBAttribute(attributeName = "GroupPhotoUrl")
    public String getGroupPhotoUrl() {
        return groupPhotoUrl;
    }

    public void setGroupPhotoUrl(String groupPhotoUrl) {
        this.groupPhotoUrl = groupPhotoUrl;
    }

    @DynamoDBAttribute(attributeName = "GroupCoverUrl")
    public String getGroupCoverUrl() {
        return groupCoverUrl;
    }

    public void setGroupCoverUrl(String groupCoverUrl) {
        this.groupCoverUrl = groupCoverUrl;
    }

    @DynamoDBAttribute(attributeName = "numOfMembers")
    public int getNumOfMembers() {
        return numOfMembers;
    }

    public void setNumOfMembers(int numOfMembers) {
        this.numOfMembers = numOfMembers;
    }
}
