package com.bookwormng.android.Data;

/**
 * Created by Tofunmi Seun on 08/11/2015.
 */
public class Advert {
    private String brand;
    private String content;
    private String Shared;
    private Long Id;
    public Advert() {}
    public Advert(String brand, String content, long Id)
    {
        this.brand = brand;
        this.content = content;
        this.setId(Id);
    }
    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getShared() {
        return Shared;
    }

    public void setShared(String shared) {
        Shared = shared;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }
}
