package com.cm.fm.mall.model.bean;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户信息类
 */
public class UserInfo extends DataSupport {
    private int id;
    private String name;            //账号
    private String nickName;        //昵称
    private String password;        //密码
    private String phoneNumber;     //电话号码
    private String buySumMoney;     //花费总金额
    private String levelInfo;       //购买等级
    private String extension;
    private int sex;                //性别    1 男  2 女
    private Date date;              //出生日期
    private int userType;           //用户类型。1 登录状态  0 游客状态
    private List<String> address = new ArrayList<>();   //用户收货地址


    public UserInfo() {

    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", nickName='" + nickName + '\'' +
                ", password='" + password + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", buySumMoney='" + buySumMoney + '\'' +
                ", levelInfo='" + levelInfo + '\'' +
                ", extension='" + extension + '\'' +
                ", sex=" + sex +
                ", date=" + date +
                ", userType=" + userType +
                ", address=" + address +
                '}';
    }

    public String getBuySumMoney() {
        return buySumMoney;
    }

    public void setBuySumMoney(String buySumMoney) {
        this.buySumMoney = buySumMoney;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLevelInfo() {
        return levelInfo;
    }

    public void setLevelInfo(String levelInfo) {
        this.levelInfo = levelInfo;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public List<String> getAddress() {
        return address;
    }

    public void setAddress(List<String> address) {
        this.address = address;
    }
}
