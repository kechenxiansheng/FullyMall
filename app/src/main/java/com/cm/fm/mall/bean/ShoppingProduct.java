package com.cm.fm.mall.bean;

import org.litepal.crud.DataSupport;

/**
 * 购买的当前商品信息类
 */
public class ShoppingProduct extends DataSupport {
    private int id;
    private int productID;              //商品id（编号）
    private String productName;         //商品名
    private String productDescription;  //商品描述
    private String type;        //商品类型
    private double price;       //商品价格
    private int inventory;      //商品库存数量
    private int buyNum;         //购买数量
    private String extension;   //扩展字段

    public ShoppingProduct() {
    }

    public ShoppingProduct(int id, int productID, String productName, String productDescription, String type, double price, int inventory, int buyNum, String extension) {
        this.id = id;
        this.productID = productID;
        this.productName = productName;
        this.productDescription = productDescription;
        this.type = type;
        this.price = price;
        this.inventory = inventory;
        this.buyNum = buyNum;
        this.extension = extension;
    }

    @Override
    public String toString() {
        return "ShoppingProduct{" +
                "id=" + id +
                ", productID=" + productID +
                ", productName='" + productName + '\'' +
                ", productDescription='" + productDescription + '\'' +
                ", type='" + type + '\'' +
                ", price=" + price +
                ", inventory=" + inventory +
                ", buyNum=" + buyNum +
                ", extension='" + extension + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getBuyNum() {
        return buyNum;
    }

    public void setBuyNum(int buyNum) {
        this.buyNum = buyNum;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}
