package com.kasandco.familyfinance.app.user.group;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelGroup {
    @SerializedName("id")
    private long id;
    @SerializedName("group_name")
    private String groupName;
    @SerializedName("users")
    private List<String> users;

    public ModelGroup(String _group_name, List<String> _users){
        groupName  = _group_name;
        users = _users;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
