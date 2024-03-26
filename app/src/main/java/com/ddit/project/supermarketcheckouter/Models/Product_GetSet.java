package com.ddit.project.supermarketcheckouter.Models;

public class Product_GetSet {

    String product_id;
    String name;
    String image;
    String category_id;
    String category_name;
    String price;
    String product_code;

    public Product_GetSet() {
    }

    public Product_GetSet(String id,String nme,String img,String cat_id,String cate_name,String prc, String code){
        product_id = id;
        name = nme;
        image = img;
        category_id = cat_id;
        category_name = cate_name;
        price = prc;
        product_code = code;
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
}
