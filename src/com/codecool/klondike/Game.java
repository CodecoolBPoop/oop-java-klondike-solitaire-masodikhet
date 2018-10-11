package com.codecool.klondike;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.codecool.klondike.Pile.PileType.DISCARD;
import static com.codecool.klondike.Pile.PileType.FOUNDATION;
import static com.codecool.klondike.Pile.PileType.TABLEAU;

public class Game extends Pane {

    private List<Card> deck = new ArrayList<>();

    private Pile stockPile;
    private Pile discardPile;
    private List<Pile> foundationPiles = FXCollections.observableArrayList();
    private List<Pile> tableauPiles = FXCollections.observableArrayList();

    private double dragStartX, dragStartY;
    private List<Card> draggedCards = FXCollections.observableArrayList();

    private static double STOCK_GAP = 1;
    private static double FOUNDATION_GAP = 0;
    private static double TABLEAU_GAP = 30;


    private EventHandler<MouseEvent> onMouseClickedHandler = e -> {
        Card card = (Card) e.getSource();
        if (card.getContainingPile().getPileType() == Pile.PileType.STOCK
            && card.getContainingPile().getTopCard() == card) {
            card.moveToPile(discardPile);
            card.flip();
            card.setMouseTransparent(false);
            System.out.println("Placed " + card + " to the waste.");
        }
        if(e.getClickCount() == 2){
            for (Pile foundationPile : foundationPiles) {
                if (isMoveValid(card, foundationPile) && !card.isFaceDown()) {
                    Pile sourcePile = card.getContainingPile();
                    draggedCards.add(card);
                    int dragSize = draggedCards.size();
                    handleValidMove(card, foundationPile);
                    ObservableList<Card> cards = sourcePile.getCards();
                    Card flipCard =  cards.get(cards.size() - (dragSize + 1));
                    if (sourcePile.getPileType() == TABLEAU && !sourcePile.isEmpty() && flipCard.isFaceDown()) {
                        flipCard.flip();
                    }
                    draggedCards.clear();
                    autoMagicEnding();
                    break;
                }
            }
        }
    };

    private EventHandler<MouseEvent> stockReverseCardsHandler = e -> {
        refillStockFromDiscard();
    };

    private EventHandler<MouseEvent> onMousePressedHandler = e -> {
        dragStartX = e.getSceneX();
        dragStartY = e.getSceneY();
    };

    private EventHandler<MouseEvent> onMouseDraggedHandler = e -> {
        Card card = (Card) e.getSource();
        Pile activePile = card.getContainingPile();
        if (activePile.getPileType() == Pile.PileType.STOCK)
            return;
        if (activePile.getPileType() == Pile.PileType.TABLEAU && card.isFaceDown())
            return;
        if (activePile.getPileType() == Pile.PileType.DISCARD && card != discardPile.getTopCard())
            return;
        double offsetX = e.getSceneX() - dragStartX;
        double offsetY = e.getSceneY() - dragStartY;

        draggedCards.clear();

        for (Card currentCard : getSelectedCards(card, activePile)) {
            draggedCards.add(currentCard);
            currentCard.setTranslateX(offsetX);
            currentCard.setTranslateY(offsetY);
            currentCard.toFront();
            currentCard.getDropShadow().setRadius(20);
            currentCard.getDropShadow().setOffsetX(10);
            currentCard.getDropShadow().setOffsetY(10);
        }
    };

    private EventHandler<MouseEvent> onMouseReleasedHandler = e -> {
        if (draggedCards.isEmpty())
            return;
        int dragSize = draggedCards.size();
        Card card = (Card) e.getSource();
        Pile sourcePile = card.getContainingPile();
        List<Pile> allPiles = new ArrayList<Pile>(tableauPiles);  //added these two lines to let user place cards on foundation, not only on tableau
        allPiles.addAll(foundationPiles);
        Pile pile = getValidIntersectingPile(card, allPiles);
        //TODO
        try {
            handleValidMove(card, pile);
            if (isGameWon()){
                System.out.println("You won this game");
            }
            ObservableList<Card> cards = sourcePile.getCards();
            Card flipCard =  cards.get(cards.size() - (dragSize + 1));
            if (sourcePile.getPileType() == TABLEAU && !sourcePile.isEmpty() && flipCard.isFaceDown()) {
                flipCard.flip();
            }
            autoMagicEnding();
        } catch(NullPointerException f) {
            draggedCards.forEach(MouseUtil::slideBack);
            draggedCards.clear();
        }
    };

    public boolean isGameWon() {
        //TODO
        int winInt = 0;
        for (Pile foundationPile : foundationPiles) {
            if (foundationPile.numOfCards() == 13) {
                winInt ++;
            }
        }
        if (winInt == 4) {
            return true;
        }
        return false;
    }

    public Game() {
        deck = Card.createNewDeck();
        Collections.shuffle(deck);
        initPiles();
        dealCards();
    }

    public void addMouseEventHandlers(Card card) {
        card.setOnMousePressed(onMousePressedHandler);
        card.setOnMouseDragged(onMouseDraggedHandler);
        card.setOnMouseReleased(onMouseReleasedHandler);
        card.setOnMouseClicked(onMouseClickedHandler);
    }

    public void refillStockFromDiscard() {
        //TODO
        stockPile.clear();
        Collections.reverse(discardPile.getCards());
        Iterator<Card> discardIterator = discardPile.iterator();
        discardIterator.forEachRemaining(card -> {
            card.flip();
            stockPile.addCard(card);
        });
        discardPile.clear();
        System.out.println("Stock refilled from discard pile.");
    }


    public boolean isFollowedBy(Rank card1,Rank card2){
            return card1.ordinal()+1 == card2.ordinal();
    }


    public boolean isMoveValid(Card card, Pile destPile) {
        //TODO

        //you can only put the proper suit and rank on cards in foundation
        if (destPile.getTopCard() != null && destPile.getPileType().equals(FOUNDATION)) {
            if (!isFollowedBy(destPile.getTopCard().getRank(), card.getRank())) {
                System.out.println("Please follow the order of cards");
                return false;
            }
            if (!Card.isSameSuit(card, destPile.getTopCard())){
                System.out.println("You can't put this card on a different suit");
                return false;
            }
        }

        //you can only put aces on empty piles on foundation
        if (destPile.isEmpty() && destPile.getPileType().equals(FOUNDATION) && card.getRank() != Rank.ACE){
            System.out.println("You can only put ACES on empty piles");
            return false;
        }

        //you can only put kings on empty piles on table
        if (destPile.isEmpty() && destPile.getPileType().equals(TABLEAU) && card.getRank() != Rank.KING){
            System.out.println("You can only put KINGS on empty piles");
            return false;
        }

        //checks opposite color and ranks on table
        if (destPile.getTopCard() != null && destPile.getPileType().equals(TABLEAU)){
            if (!Card.isOppositeColor(card, destPile.getTopCard())){
                System.out.println("You can't put this card on the same color");
                return false;
            }
            if (!isFollowedBy(card.getRank(), destPile.getTopCard().getRank())){
                System.out.println("Please follow the order of cards");
                return false;
            }
        }
        return true;
    }
    private Pile getValidIntersectingPile(Card card, List<Pile> piles) {
        Pile result = null;
        for (Pile pile : piles) {
            if (!pile.equals(card.getContainingPile()) &&
                    isOverPile(card, pile) &&
                    isMoveValid(card, pile))
                result = pile;
        }
        return result;
    }

    private boolean isOverPile(Card card, Pile pile) {
        if (pile.isEmpty())
            return card.getBoundsInParent().intersects(pile.getBoundsInParent());
        else
            return card.getBoundsInParent().intersects(pile.getTopCard().getBoundsInParent());
    }

    private void handleValidMove(Card card, Pile destPile) {
        String msg = null;
        if (destPile.isEmpty()) {
            if (destPile.getPileType().equals(Pile.PileType.FOUNDATION))
                msg = String.format("Placed %s to the foundation.", card);
            if (destPile.getPileType().equals(TABLEAU))
                msg = String.format("Placed %s to a new pile.", card);
        } else {
            msg = String.format("Placed %s to %s.", card, destPile.getTopCard());
        }
        System.out.println(msg);
        MouseUtil.slideToDest(draggedCards, destPile);
        draggedCards.clear();
    }


    private void initPiles() {
        stockPile = new Pile(Pile.PileType.STOCK, "Stock", STOCK_GAP);
        stockPile.setBlurredBackground();
        stockPile.setLayoutX(95);
        stockPile.setLayoutY(20);
        stockPile.setOnMouseClicked(stockReverseCardsHandler);
        getChildren().add(stockPile);

        discardPile = new Pile(Pile.PileType.DISCARD, "Discard", STOCK_GAP);
        discardPile.setBlurredBackground();
        discardPile.setLayoutX(285);
        discardPile.setLayoutY(20);
        getChildren().add(discardPile);

        for (int i = 0; i < 4; i++) {
            Pile foundationPile = new Pile(Pile.PileType.FOUNDATION, "Foundation " + i, FOUNDATION_GAP);
            foundationPile.setBlurredBackground();
            foundationPile.setLayoutX(610 + i * 180);
            foundationPile.setLayoutY(20);
            foundationPiles.add(foundationPile);
            getChildren().add(foundationPile);
        }
        for (int i = 0; i < 7; i++) {
            Pile tableauPile = new Pile(TABLEAU, "Tableau " + i, TABLEAU_GAP);
            tableauPile.setBlurredBackground();
            tableauPile.setLayoutX(95 + i * 180);
            tableauPile.setLayoutY(275);
            tableauPiles.add(tableauPile);
            getChildren().add(tableauPile);
        }
    }

    public void dealCards() {
        //TODO
        for (Pile tableauPile : tableauPiles) {
            for (int i = 0;i <= tableauPiles.indexOf(tableauPile);i++) {
                Card currentCard = deck.get(i);
                tableauPile.addCard(currentCard);
                addMouseEventHandlers(currentCard);
                getChildren().add(currentCard);
                if (i == tableauPiles.indexOf(tableauPile)) { currentCard.flip(); }
                deck.remove(i);
            }
        }
        Iterator<Card> deckIterator = deck.iterator();
        deckIterator.forEachRemaining(card -> {
            stockPile.addCard(card);
            addMouseEventHandlers(card);
            getChildren().add(card);
        });

    }

    public void setTableBackground(Image tableBackground) {
        setBackground(new Background(new BackgroundImage(tableBackground,
                BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT,
                BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
    }

    public static List<Card> getSelectedCards( Card currentCard, Pile activePile) {

        List<Card> selectedCards = new ArrayList<>();

        int i = activePile.getCards().indexOf(currentCard);
        for( int j=i; j < activePile.getCards().size(); j++) {
            selectedCards.add( activePile.getCards().get(j));
        }

        return selectedCards;
    }

    public boolean isAllRevealed() {
        for (Pile tableauPile : tableauPiles) {
            for (Card card : tableauPile.getCards()) {
                if (card.isFaceDown()) {
                    return false;
                }
            }
        }
        return true;
    }

    public void autoMagicEnding() {
        List<Pile> pilesToCheck = new ArrayList<Pile>(tableauPiles);
        pilesToCheck.add(discardPile);
        Card topCard;
        if (stockPile.getCards().isEmpty() && isAllRevealed()) {
            while (!isGameWon()) {
                for (Pile pileToCheck : pilesToCheck) {
                    if (pileToCheck.getPileType() == DISCARD) {
                        Iterator<Card> pileIterator = discardPile.getCards().iterator();
                        pileIterator.forEachRemaining(card -> {
                            for (Pile foundationPile : foundationPiles) {
                                if (isMoveValid(card, foundationPile)) {
                                    foundationPile.addCard(card);
                                    pileIterator.remove();
                                    break;
                                }
                            }
                        });
                    } else {
                        topCard = pileToCheck.getTopCard();
                        if (topCard != null) {
                            for (Pile foundationPile : foundationPiles) {
                                if (isMoveValid(topCard, foundationPile)) {
                                    topCard.moveToPile(foundationPile);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
