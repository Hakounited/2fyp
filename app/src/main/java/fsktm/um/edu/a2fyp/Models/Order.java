package fsktm.um.edu.a2fyp.Models;

import java.io.Serializable;

public class Order implements Serializable {
    public String buyer_name, seller_name, address, buyer_id, seller_id, order_id, order_quantity, order_status, phone_num, product_name, product_price, total_price, product_img, order_product_id;


    public Order(){

    }

    public Order(String buyer_name, String seller_name, String address, String buyer_id, String seller_id, String order_id, String order_quantity, String order_status, String phone_num, String product_name, String product_price, String total_price, String product_img, String order_product_id) {
        this.buyer_name = buyer_name;
        this.seller_name = seller_name;
        this.address = address;
        this.buyer_id = buyer_id;
        this.seller_id = seller_id;
        this.order_id = order_id;
        this.order_quantity = order_quantity;
        this.order_status = order_status;
        this.phone_num = phone_num;
        this.product_name = product_name;
        this.product_price = product_price;
        this.total_price = total_price;
        this.product_img = product_img;
        this.order_product_id = order_product_id;
    }

    public void setBuyer_name(String buyer_name) {
        this.buyer_name = buyer_name;
    }

    public void setSeller_name(String seller_name) {
        this.seller_name = seller_name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setBuyer_id(String buyer_id) {
        this.buyer_id = buyer_id;
    }

    public void setSeller_id(String seller_id) {
        this.seller_id = seller_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public void setOrder_quantity(String order_quantity) {
        this.order_quantity = order_quantity;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public void setPhone_num(String phone_num) {
        this.phone_num = phone_num;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

    public void setProduct_img(String product_img) {
        this.product_img = product_img;
    }

    public void setOrder_product_id(String order_product_id) {
        this.order_product_id = order_product_id;
    }

    public String getBuyer_name() {
        return buyer_name;
    }

    public String getSeller_name() {
        return seller_name;
    }

    public String getAddress() {
        return address;
    }

    public String getBuyer_id() {
        return buyer_id;
    }

    public String getSeller_id() {
        return seller_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public String getOrder_quantity() {
        return order_quantity;
    }

    public String getOrder_status() {
        return order_status;
    }

    public String getPhone_num() {
        return phone_num;
    }

    public String getProduct_name() {
        return product_name;
    }

    public String getProduct_price() {
        return product_price;
    }

    public String getTotal_price() {
        return total_price;
    }

    public String getProduct_img() {
        return product_img;
    }

    public String getOrder_product_id() {
        return order_product_id;
    }
}
