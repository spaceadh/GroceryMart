package com.example.plantcareapplication.Models;

public class Wallpaper {
    private String imageKey;
    private String imageUrl;
    private String plantTitle;
    private String description;
    private String instructions;
    private String watering;
    private String maintenance;

    public Wallpaper() {
        // Default constructor required for Firebase database
    }

    public Wallpaper(String imageKey, String imageUrl, String plantTitle, String instructions, String watering, String maintenance) {
        // Default constructor required for Firebase database
    }

    public Wallpaper(String imageKey, String imageUrl, String plantTitle,String description,
                     String instructions,String watering,String maintenance) {
        this.imageKey = imageKey;
        this.imageUrl = imageUrl;
        this.plantTitle = plantTitle;
        this.description=description;
        this.instructions=instructions;
        this.watering=watering;
        this.maintenance=maintenance;
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

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getWatering() {
        return watering;
    }

    public void setWatering(String watering) {
        this.watering = watering;
    }

    public String getMaintenance() {
        return maintenance;
    }

    public void setMaintenance(String maintenance) {
        this.maintenance = maintenance;
    }

}
