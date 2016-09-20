package com.bookwormng.android.Data;

/**
 * Created by Tofunmi Seun on 20/11/2015.
 */
public class FireBaseUser
{
    private String handle;
    private int AdShared;
    private int score;
    private int paid;
    private int swipeLimit;
    private long Id;
    private long lastQuestionId;
    private long lastAdvertId;

    public FireBaseUser(){}

    public FireBaseUser (String handle, int ad, int score, long id, long lastQuestionId, long lastAdvertId, int swipe)
    {
        this.setHandle(handle);
        this.setAdShared(ad);
        this.setScore(score);
        this.setPaid(0);
        this.setId(id);
        this.setLastQuestionId(lastQuestionId);
        this.setLastAdvertId(lastAdvertId);
        this.setSwipeLimit(swipe);
    }

    public  FireBaseUser (User user)
    {
        this.handle = user.getHandle();
        this.score = user.getScore();
        this.AdShared = user.getAdshared();
        this.paid = 0;
        this.Id = user.getUserId();
        this.lastQuestionId = user.getLastQuestionId();
        this.lastAdvertId = user.getLastAdvertId();
        this.swipeLimit = user.getSwipeLimit();
    }
    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public int getAdShared() {
        return AdShared;
    }

    public void setAdShared(int adShared) {
        AdShared = adShared;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getPaid() {
        return paid;
    }

    public void setPaid(int paid) {
        this.paid = paid;
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
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
