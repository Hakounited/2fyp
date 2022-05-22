package fsktm.um.edu.a2fyp.Models;

import java.io.Serializable;

public class Product implements Serializable {

    public String productName, productDescription, productPrice, productImg, postedBy;


    public Product(){

    }

    public Product(String productName, String productDescription, String productPrice, String productImg, String postedBy) {
        this.productName = productName;
        this.productDescription = productDescription;
        this.productPrice = productPrice;
        this.productImg = productImg;
        this.postedBy = postedBy;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public String getProductImg() {
        return productImg;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }
}
