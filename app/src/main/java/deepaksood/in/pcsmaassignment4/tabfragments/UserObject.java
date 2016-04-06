package deepaksood.in.pcsmaassignment4.tabfragments;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by deepak on 5/4/16.
 */

@DynamoDBTable(tableName = "UserDetails")
public class UserObject {
    private String mobileNum;

    @DynamoDBHashKey(attributeName = "MobileNumber")
    public String getMobileNum() {
        return mobileNum;
    }

    public void setMobileNum(String mobileNum) {
        this.mobileNum = mobileNum;
    }
}
