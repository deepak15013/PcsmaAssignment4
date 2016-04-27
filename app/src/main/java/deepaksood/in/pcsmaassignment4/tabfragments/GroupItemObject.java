package deepaksood.in.pcsmaassignment4.tabfragments;

/**
 * Created by deepak on 27/4/16.
 */
public class GroupItemObject {
    private String groupName;
    private String ownerName;
    private String photoUrl;

    public GroupItemObject(String groupName,String ownerName, String photoUrl) {
        this.groupName = groupName;
        this.ownerName = ownerName;
        this.photoUrl = photoUrl;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
