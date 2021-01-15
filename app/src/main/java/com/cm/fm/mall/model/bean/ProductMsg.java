package com.cm.fm.mall.model.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 商品类
 * Parcelable 让类可以序列化传送
 */
public class ProductMsg implements Parcelable {

    private int productID;              //商品id（编号）
    private String productName;         //商品名
    private String productDescription;  //商品描述
    private String type;        //商品类型
    private double price;       //商品价格
    private int inventory;      //商品库存数量
    private String extension;   //扩展字段,暂时存的图片名称

    public ProductMsg(int productID, String productName, String productDescription, String type, double price, int inventory, String extension) {
        this.productID = productID;
        this.productName = productName;
        this.productDescription = productDescription;
        this.type = type;
        this.price = price;
        this.inventory = inventory;
        this.extension = extension;
    }


    private ProductMsg(Parcel in) {
        productID = in.readInt();
        productName = in.readString();
        productDescription = in.readString();
        type = in.readString();
        price = in.readDouble();
        inventory = in.readInt();
        extension = in.readString();
        /** 此处反序列化时，如果对象也是一个可序列化的，那么需要传入当前线程的上下文类加载器，否则会报错 找不到类 */
        //示例 addressInfo = in.readParcelable(Thread.currentThread().getContextClassLoader());
    }
    //反序列化
    public static final Creator<ProductMsg> CREATOR = new Creator<ProductMsg>() {
        @Override
        public ProductMsg createFromParcel(Parcel in) {
            return new ProductMsg(in);
        }

        @Override
        public ProductMsg[] newArray(int size) {
            return new ProductMsg[size];
        }
    };
    //返回当前内容的内容描述（大部分情况返回0，当有文件描述符时，返回 1 ：CONTENTS_FILE_DESCRIPTOR）
    @Override
    public int describeContents() {
        return 0;
    }

    //序列化
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.productID);
        dest.writeString(this.productName);
        dest.writeString(this.productDescription);
        dest.writeString(this.type);
        dest.writeDouble(this.price);
        dest.writeInt(this.inventory);
        dest.writeString(this.extension);
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getInventory() {
        return inventory;
    }

    public void setInventory(int inventory) {
        this.inventory = inventory;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    @Override
    public String toString() {
        return "ProductMsg{" +
                "productID=" + productID +
                ", productName='" + productName + '\'' +
                ", productDescription='" + productDescription + '\'' +
                ", type='" + type + '\'' +
                ", price=" + price +
                ", inventory=" + inventory +
                ", extension='" + extension + '\'' +
                '}';
    }


}
