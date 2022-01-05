package com.kasandco.familyfinance.app.user.group;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelGroup {
    @SerializedName("id")
    private long id;
    @SerializedName("users")
    private List<String> users;

    public ModelGroup(List<String> _users){
        users = _users;
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
