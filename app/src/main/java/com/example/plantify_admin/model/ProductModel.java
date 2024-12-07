package com.example.plantify_admin.model;

public class ProductModel {

    private String ProductName;
    private String Price;
    private String Quantity;
    private String ImageUrl;
    private String key;
    private String Category; // New field for Category

    // Getter and Setter for ProductName
    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    // Getter and Setter for Price
    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    // Getter and Setter for Quantity
    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    // Getter and Setter for ImageUrl
    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    // Getter and Setter for Key
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    // Getter and Setter for Category
    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }
}
