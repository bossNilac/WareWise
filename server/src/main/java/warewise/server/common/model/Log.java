package warewise.server.common.model;


public class Log {
    private int ID;
    private int userID;
    private String action;
    private String description;
    private String createdAt;

    public Log(int ID, int userID, String action, String description, String createdAt) {
        this.ID = ID;
        this.userID = userID;
        this.action = action;
        this.description = description;
        this.createdAt = createdAt;
    }
    public Log(int userID, String action, String description, String createdAt) {
        this.userID = userID;
        this.action = action;
        this.description = description;
        this.createdAt = createdAt;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

}
