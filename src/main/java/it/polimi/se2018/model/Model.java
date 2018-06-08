package it.polimi.se2018.model;

import it.polimi.se2018.model.events.messages.ChooseSchemaMessage;
import it.polimi.se2018.model.events.messages.SuccessCreatePlayerMessage;
import it.polimi.se2018.model.events.messages.*;
import it.polimi.se2018.model.events.moves.ChooseDiceMove;
import it.polimi.se2018.model.events.moves.NoActionMove;
import it.polimi.se2018.model.events.moves.UseToolCardMove;
import it.polimi.se2018.model.exceptions.FullCellException;
import it.polimi.se2018.model.game_equipment.*;
import it.polimi.se2018.model.objective_cards.AbstractObjectiveCard;
import it.polimi.se2018.model.objective_cards.private_objective_cards.*;
import it.polimi.se2018.model.exceptions.RestrictionsNotRespectedException;
import it.polimi.se2018.model.game_equipment.Player;
import it.polimi.se2018.model.game_equipment.SchemaCard;
import it.polimi.se2018.model.objective_cards.public_objective_cards.*;
import it.polimi.se2018.model.tool_cards.*;
import it.polimi.se2018.utils.ProjectObservable;

import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class is supposed to contain all the data about a game and all the
 * controls to check if any move is correct. The data is accessible through
 * getter methods.<s Whenever a modifier method is activated by an external class
 * Model verifies that the move can be made and notifies its observer with
 * a Message
 *
 * @author Giovanni
 * edited  Luciano 12/05/2018;
 * edited Luciano 14/05/2018;
 */

public class Model extends ProjectObservable implements Runnable{

    private int roundNumber;
    private static final int SCHEMA_CARDS_EXTRACT_NUMBER = 2;
    private static final int PUBLIC_OBJECTIVE_CARDS_EXTRACT_NUMBER = 3;
    private static final int TOOL_CARDS_EXTRACT_NUMBER = 3;
    private static final int SCHEMA_CARD_NUMBER = 24;
    private GameBoard gameBoard;
    /*local instance of the gameBoard used to access all objects and
     * methods of the game instrumentation*/
    private ArrayList<Player> participants;
    /*local ArrayList to memorize actual playing players*/
    private int turnOfTheRound;
    /*local variable to memorize the current turn in a round, it goes from 0
     * to participants.size()-1*/
    private boolean firstDraftOfDice;
    /*local variable to memorize if every player has been given the option to choose
        his/her first die*/


    /**
     * Constructor method initializing turnOfTheRound and the participant list
     */
    public Model() {
        this.gameBoard = new GameBoard();
        turnOfTheRound = 0;
        firstDraftOfDice = true;
        participants = new ArrayList<>();
        this.roundNumber =0;
    }

    /**
     * Getter for integer value of turnOfTheRound of the Round
     * @return integer value turnOfTheRound of the Round
     */
    public int getTurnOfTheRound() {
        return turnOfTheRound;
    }

    /**
     * Getter method to access all the gaming components
     * @return gameBoard reference
     */
    public GameBoard getGameBoard() {
        return gameBoard;
    }

    /**
     * Getter method for the participants number
     * @return integer value of the participants number
     */
    public int getParticipantsNumber() {
        return participants.size();
    }

    /**
     * Getter method for a specific player
     * @param index
     * @return reference of the player specified in the index parameter
     */
    public Player getPlayer(int index) {
        return participants.get(index);
    }

    public ArrayList<Player> getParticipants() {
        return participants;
    }

    public boolean isFirstDraftOfDice() {
        return firstDraftOfDice;
    }

    /**
     * method to check if a player can place a die in a position on his/her schema card
     * @param move data structure containing all the information about the player move
     */
    public void doDiceMove(ChooseDiceMove move) {
        /*try{
            placeDie(move.getPlayer().getSchemaCard(), move.getCol(), move.getRow(), move.getDraftPoolPos());
            removeDieFromDrafPool(move.getDraftPoolPos());
        }
        catch(FullCellException e){
            notifyObservers(new ErrorMessage(move.getPlayer(), "La posizione &eacute; gi&aacute; occupata"));
        }
        catch(RestrictionsNotRespectedException e){
            notifyObservers(new ErrorMessage(move.getPlayer(), "La posizione del dado non &eacute; valida"));
        }*/
    }

    private void removeDieFromDrafPool(int draftPoolPos) {
        int currentRound = gameBoard.getRoundTrack().getCurrentRound();
        gameBoard.getRoundDice()[currentRound].removeDiceFromDraftPool(draftPoolPos);
        notifyObservers();
    }

    /**
     * method to check if a player can activate a card specified on the parameter move
     * @param move data structure containing all the information about the player move
     */
    public void doToolCardMove(UseToolCardMove move) {

    }

    /**
     * method to check if a player can skip a move
     * @param move
     */
    public void checkNoActionMove(NoActionMove move) {

    }

    /**
     * method to check if the turnOfTheRound of the player specified in the parameter field
     * @param player
     * @return true if is the turnOfTheRound of the current player
     */
    public boolean isPlayerTurn(Player player) {
        return participants.indexOf(player) == turnOfTheRound;
    }

    private void placeDie(SchemaCard schemaCard, int drafPoolPos, int row, int col) throws RestrictionsNotRespectedException, FullCellException{
        Dice chosenDie = gameBoard.getRoundDice()[gameBoard.getRoundTrack().getCurrentRound()].getDice(drafPoolPos);
        schemaCard.placeDie(chosenDie, row, col);
        notifyObservers();
    }

    /**
     *
     * method to notifyView the current player turnOfTheRound in a round
     * once the first run is completed, the method proceeds to count backwards
     */
    public void updateTurnOfTheRound(){
        if(isFirstDraftOfDice()){
            /*first run of turns to choose a die*/
            if(turnOfTheRound == participants.size()-1){
                turnOfTheRound = participants.size()-1;
                firstDraftOfDice = false;
            }
            else {
                turnOfTheRound++;
            }
        }
        else{
            /*second run of turns to choose a die*/
                turnOfTheRound--;
        }
        notifyObservers();
    }

    /**
     * method to add a new player
     * @param name
     */
    public void addPlayer(String name){
        System.out.println("Model adds player");
        participants.add(new Player(name));
        System.out.println("Model -> VWInterface");
        setChanged();
        notifyObservers(new SuccessCreatePlayerMessage("server",name));
    }

    /**
     * Method to remove a player from the model
     * @param username String to retrieve player name
     *
     */
    public void removePlayer(String username){

    }

    /**
     * method to modify the token numeber of a specific player, gets called after a tool card has been used
     * @param toolCardIndex number of tokens to deduct
     * @param playerPosition integer number to get the player reference
     */
    public  void  updateFavorTokens(int toolCardIndex, int playerPosition){
        if(gameBoard.getToolCard(toolCardIndex).isFirstUsage()){
            participants.get(playerPosition).decreaseFavorTokens(false);
        }
        else {
            participants.get(playerPosition).decreaseFavorTokens(true);
        }
        setChanged();
        notifyObservers();
    }

    public void extractToolCards() {

        ArrayList<Integer> cardIndex = new ArrayList<>(12);

        for(int i = 1; i <= 12; i++)
            cardIndex.add(i);

        Collections.shuffle(cardIndex);

        for(int i = 0; i < 3; i++) {

            switch (cardIndex.get(i)) {

                case 1:

                    gameBoard.setToolCards(PinzaSgrossatrice.getThisInstance(), i);
                    break;

                case 2:
                    gameBoard.setToolCards(PennelloPerEglomise.getThisInstance(), i);
                    break;

                case 3:
                    gameBoard.setToolCards(AlesatorePerLaminaDiRame.getThisInstance(), i);
                    break;

                case 4:
                    gameBoard.setToolCards(Lathekin.getThisInstance(), i);
                    break;

                case 5:
                    gameBoard.setToolCards(TaglierinaCircolare.getThisInstance(), i);
                    break;

                case 6:
                    gameBoard.setToolCards(PennelloPerPastaSalda.getThisInstance(), i);
                    break;

                case 7:
                    gameBoard.setToolCards(Martelletto.getThisInstance(), i);
                    break;

                case 8:
                    gameBoard.setToolCards(TenagliaARotelle.getThisInstance(), i);
                    break;

                case 9:
                    gameBoard.setToolCards(RigaInSughero.getThisInstance(), i);
                    break;

                case 10:
                    gameBoard.setToolCards(TamponeDiamantato.getThisInstance(), i);
                    break;

                case 11:
                    gameBoard.setToolCards(DiluentePerPastaSalda.getThisInstance(), i);
                    break;

                case 12:
                    gameBoard.setToolCards(TaglierinaManuale.getThisInstance(), i);
                    break;

            }
        }
    }

    public void extractPublicObjectiveCards() {
        ArrayList<Integer> cardIndex = new ArrayList<>(12);

        for(int i = 1; i <= 10; i++){
            cardIndex.add(i);
        }
        Collections.shuffle(cardIndex);
        for(int i = 0; i < 3; i++) {
            switch (cardIndex.get(i)) {
                case 1:{
                    gameBoard.setPublicObjectiveCards(ColoriDiversiRiga.getThisInstance(), i);
                    break;
                }
                case 2: {
                    gameBoard.setPublicObjectiveCards(ColoriDiversiColonna.getThisInstance(), i);
                    break;
                }
                case 3: {
                    gameBoard.setPublicObjectiveCards(SfumatureDiverseRiga.getThisInstance(), i);
                    break;
                }
                case 4: {
                    gameBoard.setPublicObjectiveCards(SfumatureDiverseRiga.getThisInstance(), i);
                    break;
                }
                case 5: {
                    gameBoard.setPublicObjectiveCards(SfumatureChiare.getThisInstance(), i);
                    break;
                }
                case 6: {
                    gameBoard.setPublicObjectiveCards(SfumatureMedie.getThisInstance(), i);
                    break;
                }
                case 7: {
                    gameBoard.setPublicObjectiveCards(SfumatureScure.getThisInstance(), i);
                    break;
                }
                case 8: {
                    gameBoard.setPublicObjectiveCards(SfumatureDiverse.getThisInstance(), i);
                    break;
                }
                case 9: {
                    gameBoard.setPublicObjectiveCards(DiagonaliColorate.getThisInstance(), i);
                    break;
                }
                case 10: {
                    gameBoard.setPublicObjectiveCards(VarietaDiColore.getThisInstance(), i);
                    break;
                }
            }
        }
    }

    /**
     * Extracts Dice from DiceBag and puts them on the RoundTrack
     */
    public void extractRoundTrack(){
        getGameBoard().getRoundTrack().getRoundDice()[turnOfTheRound] = new RoundDice(participants.size(),getGameBoard().getDiceBag(),turnOfTheRound);
    }

    public void sendSchemaCard(){
        System.out.println("Dealing SchemaCards to players ");
        Semaphore available = new Semaphore(1);
        addSemaphore(available);
        int extractedCardIndex = 0;
        ArrayList<Integer> randomValues = new ArrayList<Integer>();
        for(int i=0; i<participants.size(); i++){
            System.out.println("Participant: " + participants.get(i).getName());
        }
        for(int i = 1; i<= SCHEMA_CARD_NUMBER/2; i++){
            randomValues.add(i);
        }
        Collections.shuffle(randomValues);
        for(int t=0; t < participants.size(); t++) {
            System.out.println("preparing message for #" + t + " client");
            SchemaCard[] extractedSchemaCards = new SchemaCard[SCHEMA_CARDS_EXTRACT_NUMBER *2];
            String[] schemaCards = new String[SCHEMA_CARDS_EXTRACT_NUMBER *2];
            for(int i = 0; i< SCHEMA_CARDS_EXTRACT_NUMBER*2; i+=2){
                extractedSchemaCards[i] = new SchemaCard(randomValues.get(extractedCardIndex));
                schemaCards[i] = extractedSchemaCards[i].toString();
                extractedSchemaCards[i+1] = new SchemaCard((SCHEMA_CARD_NUMBER+1)-randomValues.get(extractedCardIndex));
                schemaCards[i+1] = extractedSchemaCards[i+1].toString();
                extractedCardIndex++;
            }
            try{
                available.acquire();
                Message sentMessage = new ChooseSchemaMessage("model", participants.get(t).getName(), schemaCards);
                memorizeMessage(sentMessage);
                new Thread(this).start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        removeSemaphore();
    }

    public void setSchemaCardPlayer(int playerPos, String schemaName){
        SchemaCard schema = new SchemaCard(schemaName);
        participants.get(playerPos).setSchemaCard(schema);
    }

    public void sendPrivateObjectiveCard(){
        ArrayList<AbstractObjectiveCard> privateCards = new ArrayList<AbstractObjectiveCard>();
        privateCards.add(SfumatureBlu.getThisInstance());
        privateCards.add(SfumatureGialle.getThisInstance());
        privateCards.add(SfumatureVerdi.getThisInstance());
        privateCards.add(SfumatureRosse.getThisInstance());
        privateCards.add(SfumatureViola.getThisInstance());
        Collections.shuffle(privateCards);
        int s =0;
        for(int i=0; i < participants.size(); i++) {
            String colorString = privateCards.get(i).getDescription();
            setChanged();
            notifyObservers(new ShowPrivateObjectiveCardsMessage("model", participants.get(i).getName(), colorString));
            s = s+1;
        }
        sendSchemaCard();
    }

    public void sendInitializationMessage(){
        System.out.println("Sending initialization messages");
        String[] publicObjectiveCardsDescription = new String[PUBLIC_OBJECTIVE_CARDS_EXTRACT_NUMBER];
        String[] toolCardDescription = new String[TOOL_CARDS_EXTRACT_NUMBER];
        String roundTrack = null;
        Object synchronizationObject = new Object();
        for(int i=0; i<PUBLIC_OBJECTIVE_CARDS_EXTRACT_NUMBER; i++){
            StringBuilder builderPublicObjectiveCards = new StringBuilder();
            builderPublicObjectiveCards.append("Name: " + gameBoard.getPublicObjectiveCardName(i) + "\n");
            builderPublicObjectiveCards.append("Description: " + gameBoard.getPublicObjectiveCardDescription(i) + "\n");
            publicObjectiveCardsDescription[i] = builderPublicObjectiveCards.toString();
        }
        for (int i=0; i<TOOL_CARDS_EXTRACT_NUMBER; i++){
            StringBuilder builderToolCards = new StringBuilder();
            builderToolCards.append("Name: " + gameBoard.getToolCardName(i) + "\n");
            builderToolCards.append("Description: " + gameBoard.getToolCardDescription(i) + "\n");
            toolCardDescription[i] = builderToolCards.toString();
        }
        StringBuilder builderRoundTrack = new StringBuilder();
        RoundDice currentRoundDice = gameBoard.getRoundDice()[turnOfTheRound];
        List<Dice> currentDiceList = currentRoundDice.getDiceList();
        for(int i=0; i<currentDiceList.size(); i++){
            builderRoundTrack.append(currentDiceList.get(i).toString() + " ");
        }
        builderRoundTrack.append("\n");
        roundTrack = builderRoundTrack.toString();
        String[] schemaInGame = new String [participants.size()];
        for (int i =0; i<schemaInGame.length;i++){
            schemaInGame[i] = new String(participants.get(i).getSchemaCard().toString());
        }
        Semaphore available = new Semaphore(1);
        addSemaphore(available);
        for(int i = 0; i<participants.size(); i++){
            try{
                available.acquire();
                memorizeMessage(new GameInitializationMessage("model", participants.get(i).getName(), publicObjectiveCardsDescription, toolCardDescription, roundTrack,schemaInGame,participants.get(turnOfTheRound).getName()));
                new Thread(this).start();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        removeSemaphore();
    }

    /**
     * a method to send updated schema cards and playerTurn to the view
     */
    public void sendSchemaAndTurn(){
        setChanged();
        //notifyObservers(new SendSchemaAndTurn("model",participants.get(turnOfTheRound).getName(),schemaInGame));

    }

    public int getRoundNumber() {
        return roundNumber;
    }

    @Override
    public void run() {
        setChanged();
        notifyObservers();
    }
}
