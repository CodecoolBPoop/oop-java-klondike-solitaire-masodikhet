package com.codecool.klondike;

public enum Suit {
    HEARTS("hearts"), DIAMONDS("diamonds"), SPADES("spades"), CLUBS("clubs");

    public String getName() {
        return suitName;
    }

    private String suitName;


    Suit(String suitName){
        this.suitName = suitName;
    }

}