package com.example.g572_528r.as0307_bomb;

import cn.bmob.v3.BmobObject;

/**
 * Created by yls on 2017/3/7.
 */
public class Person extends BmobObject {
    private String name;
    private int age;
    private String address;
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
