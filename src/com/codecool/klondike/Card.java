package com.codecool.klondike;

import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.util.*;

public class Card extends ImageView {

    private Suit suit;
    private Rank rank;
    private boolean faceDown;

    private Image backFace;
    private Image frontFace;
    private Pile containingPile;
    private DropShadow dropShadow;

    static Image cardBackImage;
    private static final Map<String, Image> cardFaceImages = new HashMap<>();
    public static final int WIDTH = 150;
    public static final int HEIGHT = 215;

    public Card(Suit suit, Rank rank, boolean faceDown) {
        this.suit = suit;
        this.rank = rank;
        this.faceDown = faceDown;
        this.dropShadow = new DropShadow(2, Color.gray(0, 0.75));
        backFace = cardBackImage;
        frontFace = cardFaceImages.get(getShortName());
        setImage(faceDown ? backFace : frontFace);
        setEffect(dropShadow);
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() { return rank; }

    public boolean isFaceDown() {
        return faceDown;
    }

    public String getShortName() {
        int rankNumber = rank.getRankNumber();
        return  suit + "R" + rankNumber;
    }

    public DropShadow getDropShadow() {
        return dropShadow;
    }

    public Pile getContainingPile() {
        return containingPile;
    }

    public void setContainingPile(Pile containingPile) {
        this.containingPile = containingPile;
    }

    public void moveToPile(Pile destPile) {
        this.getContainingPile().getCards().remove(this);
        destPile.addCard(this);
    }

    public void flip() {
        faceDown = !faceDown;
        setImage(faceDown ? backFace : frontFace);
    }

    @Override
    public String toString() {
        return "The " + "Rank" + rank + " of " + "Suit" + suit;
    }

    public static boolean isOppositeColor(Card card1, Card card2) {
        //TODO - DONE
        ArrayList<Suit> redCards = new ArrayList<Suit>(2);
        redCards.add(Suit.HEARTS);
        redCards.add(Suit.DIAMONDS);
        ArrayList<Suit> blackCards = new ArrayList<Suit>(2);
        blackCards.add(Suit.CLUBS);
        blackCards.add(Suit.SPADES);

        Suit card1suit = card1.getSuit();
        Suit card2suit = card2.getSuit();

        if (redCards.contains(card1suit) && redCards.contains(card2suit)){
            return false;
        } else if (blackCards.contains(card1suit) && blackCards.contains(card2suit)){
            return false;
        }
        return true;
    }

    public static boolean isSameSuit(Card card1, Card card2) {
        /*
        boolean tpm = Suit.HEARTS.isFollowedBy(Suit.DIAMONDS);*/
        return card1.getSuit() == card2.getSuit();
    }

    public static List<Card> createNewDeck() {
        List<Card> result = new ArrayList<>();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                result.add(new Card(suit, rank, true));
            }
        }
        return result;
    }
    public static void loadCardImages(String theme) {
        cardBackImage = new Image(theme);
        String suitName = "";
        for (Suit suit : Suit.values()) {
            suitName = suit.getName();
            for (Rank rank : Rank.values()) {
                int rankNumber = rank.getRankNumber();
                String cardName = suitName + rankNumber;
                String cardId =  suit + "R" + rankNumber;
                String imageFileName = "card_images/" + cardName + ".png";
                cardFaceImages.put(cardId, new Image(imageFileName));
            }
        }
    }

}


