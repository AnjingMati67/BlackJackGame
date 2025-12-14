package edu.sdccd.cisc;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.*;

public class BlackjackGame extends Application {

    private Player player;
    private Dealer dealer = new Dealer();
    private Deck deck = new Deck();
    private Path saveFile = Paths.get("blackjack_save.txt");
    private TextArea gameLog = new TextArea();
    private TextField betField = new TextField();
    private Button hitButton = new Button("Hit");
    private Button standButton = new Button("Stand");
    private int currentBet = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Load player chips
        player = new Player("Player", loadChips());
        startAutoSave();

        // UI layout
        VBox root = new VBox(10);
        HBox controls = new HBox(10);
        controls.getChildren().addAll(new Label("Bet:"), betField, hitButton, standButton);
        root.getChildren().addAll(new Label("Blackjack Game"), gameLog, controls);

        hitButton.setDisable(true);
        standButton.setDisable(true);

        // Bet action
        betField.setOnAction(e -> {
            try {
                currentBet = Integer.parseInt(betField.getText().trim());
                if (currentBet <= 0 || currentBet > player.getChips()) {
                    appendLog("Invalid bet.");
                    return;
                }
                startRound();
                hitButton.setDisable(false);
                standButton.setDisable(false);
                betField.setDisable(true);
            } catch (NumberFormatException ex) {
                appendLog("Enter a valid number for bet.");
            }
        });

        // Hit action
        hitButton.setOnAction(e -> {
            player.addCard(deck.dealCard());
            appendLog("You drew: " + player.getHand().get(player.getHand().size() - 1));
            appendLog("Your hand: " + player.getHand() + " (" + player.handValue() + ")");
            if (player.handValue() > 21) {
                appendLog("You busted!");
                player.adjustChips(-currentBet);
                endRound();
            }
        });

        // Stand action
        standButton.setOnAction(e -> {
            dealerTurn();
            int dealerVal = dealer.handValue();
            int playerVal = player.handValue();
            appendLog("Dealer hand: " + dealer.getHand() + " (" + dealerVal + ")");
            appendLog("Your hand: " + player.getHand() + " (" + playerVal + ")");
            if (dealerVal > 21 || playerVal > dealerVal) {
                appendLog("You win!");
                player.adjustChips(currentBet);
            } else if (playerVal < dealerVal) {
                appendLog("Dealer wins!");
                player.adjustChips(-currentBet);
            } else {
                appendLog("Push (tie).");
            }
            endRound();
        });

        stage.setScene(new Scene(root, 500, 400));
        stage.setTitle("Blackjack GUI");
        stage.show();
        appendLog("Welcome! You have " + player.getChips() + " chips. Enter a bet to start.");
    }

    private void startRound() {
        player.clearHand();
        dealer.clearHand();
        deck.shuffle();
        player.addCard(deck.dealCard());
        dealer.addCard(deck.dealCard());
        player.addCard(deck.dealCard());
        dealer.addCard(deck.dealCard());
        appendLog("Dealer shows: " + dealer.getHand().get(0));
        appendLog("Your hand: " + player.getHand() + " (" + player.handValue() + ")");
    }

    private void dealerTurn() {
        while (dealer.handValue() < 17) {
            dealer.addCard(deck.dealCard());
        }
    }

    private void endRound() {
        appendLog("Chips: " + player.getChips());
        if (player.getChips() <= 0) {
            appendLog("You ran out of chips! Game over.");
            hitButton.setDisable(true);
            standButton.setDisable(true);
        }
        hitButton.setDisable(true);
        standButton.setDisable(true);
        betField.setDisable(false);
        betField.clear();
    }

    private void appendLog(String message) {
        gameLog.appendText(message + "\n");
    }

    private void saveChips() {
        try {
            Files.writeString(saveFile, String.valueOf(player.getChips()),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            appendLog("Failed to save game: " + e.getMessage());
        }
    }

    private int loadChips() {
        if (Files.exists(saveFile)) {
            try {
                String content = Files.readString(saveFile).trim();
                return Integer.parseInt(content);
            } catch (IOException | NumberFormatException e) {
                appendLog("Failed to load saved chips, starting with 100.");
            }
        }
        return 100;
    }

    private void startAutoSave() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::saveChips, 10, 10, TimeUnit.SECONDS);
    }
}