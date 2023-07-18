package com.example.plantcareapplication.Models;

public class Groceries {
    private String imageKey;
    private String imageUrl;
    private String plantTitle;
    private String description;
    private String price;
   

    public Groceries() {
        // Default constructor required for Firebase database
    }
    

    public Groceries(String imageKey, String imageUrl, String plantTitle, String description,
                     String price) {
        this.imageKey = imageKey;
        this.imageUrl = imageUrl;
        this.plantTitle = plantTitle;
        this.description=description;
        this.price=price;
    }

    public String getImageKey() {
        return imageKey;
    }

    public void setImageKey(String imageKey) {
        this.imageKey = imageKey;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageTitle() {
        return plantTitle;
    }

    public void setImageTitle(String plantTitle) {
        this.plantTitle = plantTitle;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
    
}
