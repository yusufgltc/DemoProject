package com.example.demoproject.Model;
import java.io.Serializable;

public class Products implements Serializable {
    String imageUrl;
    String title;
    String description;
    Double rating;
    String category;
    String date;
    String email;

    public Products(){
    }
    public Products(String imageUrl, String title, String description, Double rating, String category, String date, String email) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.description = description;
        this.rating = rating;
        this.category = category;
        this.date = date;
        this.email = email;
    }
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
