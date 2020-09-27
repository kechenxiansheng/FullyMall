package com.cm.fm.mall.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.DataSupport;

/**
 * 地址信息
 */
public class AddressInfo extends DataSupport implements Parcelable {
    private int id;
    private String username;
    private String phone;
    private String tag;         //标签
    private boolean isDefault;  //是否是默认
    private String address;
    private String street;


    public AddressInfo() {
    }

    //反序列化
    protected AddressInfo(Parcel in) {
        id = in.readInt();
        username = in.readString();
        phone = in.readString();
        tag = in.readString();
        isDefault = in.readByte() != 0;
        address = in.readString();
        street = in.readString();
    }

    public static final Creator<AddressInfo> CREATOR = new Creator<AddressInfo>() {
        @Override
        public AddressInfo createFromParcel(Parcel in) {
            return new AddressInfo(in);
        }

        @Override
        public AddressInfo[] newArray(int size) {
            return new AddressInfo[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    //序列化
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(username);
        dest.writeString(phone);
        dest.writeString(tag);
        dest.writeByte((byte) (isDefault ? 1 : 0));
        dest.writeString(address);
        dest.writeString(street);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreet() {
        return street;
    }

    @Override
    public String toString() {
        return "AddressInfo{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", phone='" + phone + '\'' +
                ", tag='" + tag + '\'' +
                ", isDefault=" + isDefault +
                ", address='" + address + '\'' +
                ", street='" + street + '\'' +
                '}';
    }
}
