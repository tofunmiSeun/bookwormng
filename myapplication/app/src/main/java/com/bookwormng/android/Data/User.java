package com.bookwormng.android.Data;

/**
 * Created by Tofunmi Seun on 05/09/2015.
 */
public class User
{
    private String handle = "new-User";
    private String Name;
    private String Location;
    private String Bio;
    private String AvatarString ="empty" ;
    private String picString = "empty";
    private String accessToken;
    private String accessTokenSecret;
    private int followers;
    private int friends;
    private int score;
    private int adshared;
    private int swipeLimit;
    private long lastQuestionId;
    private long lastAdvertId;
    private long userId;


    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getAdshared() {
        return adshared;
    }

    public void setAdshared(int adshared) {
        this.adshared = adshared;
    }

    public void setPicString(String theString)
    {this.picString = theString;}

    public String getPicString()
    {return  this.picString;}

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getBio() {
        return Bio;
    }

    public void setBio(String bio) {
        Bio = bio;
    }

    public String getAvatarString() {
        return AvatarString;
    }

    public void setAvatarString(String avatarString) {
        AvatarString = avatarString;
    }

    public int getFriends() {
        return friends;
    }

    public void setFriends(int friends) {
        this.friends = friends;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessTokenSecret() {
        return accessTokenSecret;
    }

    public void setAccessTokenSecret(String accessTokenSecret) {
        this.accessTokenSecret = accessTokenSecret;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getLastQuestionId() {
        return lastQuestionId;
    }

    public void setLastQuestionId(long lastQuestionId) {
        this.lastQuestionId = lastQuestionId;
    }

    public long getLastAdvertId() {
        return lastAdvertId;
    }

    public void setLastAdvertId(long lastAdvertId) {
        this.lastAdvertId = lastAdvertId;
    }

    public int getSwipeLimit() {
        return swipeLimit;
    }

    public void setSwipeLimit(int swipeLimit) {
        this.swipeLimit = swipeLimit;
    }
}
