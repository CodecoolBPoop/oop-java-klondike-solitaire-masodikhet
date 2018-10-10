package com.codecool.klondike;

public enum Suit {
    HEARTS("hearts"), DIAMONDS("diamonds"), SPADES("spades"), CLUBS("clubs");

    public String getName() {
        return suitName;
    }

    private String suitName;


        /*public boolean isFollowedBy(Suit other){
            return this.ordinal()+1 == other.ordinal();
        }*/

    Suit(String suitName){
        this.suitName = suitName;
    }

}