package com.example.pranay.demoapp;

public class Review {
    private String name;
    private String review;

    public Review(){

    }

    public Review(String name, String review){
        this.name = name;
        this.review = review;



    }

    public String getName() {
        return name;
    }

    public String getReview() {
        return review;
    }
}
