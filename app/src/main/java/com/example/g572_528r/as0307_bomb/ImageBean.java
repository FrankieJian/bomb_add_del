package com.example.g572_528r.as0307_bomb;

import cn.bmob.v3.BmobObject;

/**
 * Created by yls on 2017/3/7.
 */
public class ImageBean extends BmobObject {
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private  String url;
}
