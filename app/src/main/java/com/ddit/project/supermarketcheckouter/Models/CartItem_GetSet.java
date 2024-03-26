package com.ddit.project.supermarketcheckouter.Models;

public class CartItem_GetSet {

    String product_id;
    String name;
    String image;
    String category_id;
    String category_name;
    String price;
    String product_code;
    String product_items;

    public CartItem_GetSet() {
    }

    public CartItem_GetSet(String id, String nme, String img, String cat_id, String cate_name, String prc, String code, String items){
        product_id = id;
        name = nme;
        image = img;
        category_id = cat_id;
        category_name = cate_name;
        price = prc;
        product_code = code;
        product_items = items;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProduct_code() {
        return product_code;
    }

    public void setProduct_code(String product_code) {
        this.product_code = product_code;
    }

    public String getProduct_items() {
        return product_items;
    }

    public void setProduct_items(String product_items) {
        this.product_items = product_items;
    }
}
