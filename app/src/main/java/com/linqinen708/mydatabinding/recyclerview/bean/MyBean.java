package com.linqinen708.mydatabinding.recyclerview.bean;

/**
 * Created by Ian on 2019/12/25.
 */
public class MyBean {

    private String name;

    private int age;

    /**头像*/
    private int avatar;

    /**如果没被选中，年龄不显示*/
    private boolean isChecked;

    public MyBean(String name, int age, int avatar, boolean isChecked) {
        this.name = name;
        this.age = age;
        this.avatar = avatar;
        this.isChecked = isChecked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
