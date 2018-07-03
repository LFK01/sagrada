package it.polimi.se2018.model;

import it.polimi.se2018.file_parser.FileParser;
import it.polimi.se2018.model.events.messages.*;
import it.polimi.se2018.exceptions.FullCellException;
import it.polimi.se2018.model.game_equipment.*;
import it.polimi.se2018.model.objective_cards.ObjectiveCard;
import it.polimi.se2018.exceptions.RestrictionsNotRespectedException;
import it.polimi.se2018.model.player.Player;
import it.polimi.se2018.utils.ProjectObservable;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    public static final int MAXIMUM_ROUND_NUMBER = 10;
    public static final int MAXIMUM_PLAYER_NUMBER = 4;
    public static final int MAXIMUM_DIE_NUMBER = 6;
    public static final int TOOL_CARDS_NUMBER = 12;
    public static final int PUBLIC_OBJECTIVE_CARDS_NUMBER = 10;
    public static final int PRIVATE_OBJECTIVE_CARDS_NUMBER = 5;
    public static final int SCHEMA_CARDS_NUMBER = 24;
    public static final int SCHEMA_CARDS_EXTRACT_NUMBER = 2;
    public static final int PUBLIC_OBJECTIVE_CARDS_EXTRACT_NUMBER = 3;
    public static final int TOOL_CARDS_EXTRACT_NUMBER = 3;
    public static final int SCHEMA_CARD_ROWS_NUMBER = 4;
    public static final int SCHEMA_CARD_COLUMNS_NUMBER = 5;
    public static final String FOLDER_ADDRESS_TOOL_CARDS =
            "src\\main\\java\\it\\polimi\\se2018\\controller\\tool_cards\\resources_tool_cards";
    public static final String FOLDER_ADDRESS_SCHEMA_CARDS =
            "src\\main\\java\\it\\polimi\\se2018\\model\\resources_schema_card";
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
     * @param index player number, used to get the current turn player
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
     *@param draftPoolPos Position of the die in the draftPool
     * @param row Row where I want to insert the die
     * @param col Col where I want to insert the die
     */
    public void doDiceMove(int draftPoolPos,int row,int col){
        try{
            placeDie(participants.get(turnOfTheRound).getSchemaCard(),draftPoolPos,row,col, false, false,false);
            removeDieFromDraftPool(draftPoolPos);
            if(isFirstDraftOfDice()) {
                participants.get(turnOfTheRound).getPlayerTurns()[roundNumber].getTurn1().getDieMove().setBeenUsed(true);
                if(participants.get(turnOfTheRound).getPlayerTurns()[roundNumber].getTurn1().getToolMove().isBeenUsed()){
                    updateTurnOfTheRound();
                } else {
                    updateGameboard();
                }
            }
            else {
                participants.get(turnOfTheRound).getPlayerTurns()[roundNumber].getTurn2().getDieMove().setBeenUsed(true);
                if(participants.get(turnOfTheRound).getPlayerTurns()[roundNumber].getTurn2().getToolMove().isBeenUsed()){
                    updateTurnOfTheRound();
                } else {
                    updateGameboard();
                }
            }
        }
        catch(FullCellException e){
            setChanged();
            notifyObservers(new ErrorMessage("model",participants.get(turnOfTheRound).getName(),"FullCellError"));
        }
        catch(RestrictionsNotRespectedException e){
            setChanged();
            notifyObservers(new ErrorMessage("model",participants.get(turnOfTheRound).getName(), "InvalidPositionError"));
        }
    }


    /** method that removes a draftpool die
     * @param draftPoolPos position of the die to be removed
     */
    public void removeDieFromDraftPool(int draftPoolPos) {
        gameBoard.getRoundDice()[roundNumber].removeDiceFromList(draftPoolPos);
    }

    /**
     * method to check if the turnOfTheRound of the player specified in the parameter field
     * @param player
     * @return true if is the turnOfTheRound of the current player
     */
    public boolean isPlayerTurn(Player player) {
        return participants.indexOf(player) == turnOfTheRound;
    }

    public void placeDie(SchemaCard schemaCard, int drafPoolPos, int row, int col,
                        boolean avoidColorRestrictions, boolean avoidValueRestrictions,
                        boolean avoidNearnessRestrictions) throws RestrictionsNotRespectedException,
                        FullCellException{
        Dice chosenDie = gameBoard.getRoundDice()[roundNumber].getDice(drafPoolPos);
        schemaCard.placeDie(chosenDie, row, col, avoidColorRestrictions, avoidValueRestrictions, avoidNearnessRestrictions);
    }

    /**
     *
     * method to notifyView the current player turnOfTheRound in a round
     * once the first run is completed, the method proceeds to count backwards
     */
    public void updateTurnOfTheRound(){
        if(isFirstDraftOfDice()){
            /*first run of turns to choose a die*/
            increaseTurnNumber();
        }
        else{
            /*second run of turns to choose a die*/
            decreaseTurnNumber();
        }
    }

    private void increaseTurnNumber(){
        if(turnOfTheRound == participants.size()-1){
            turnOfTheRound = participants.size()-1;
            firstDraftOfDice = false;
        }
        else {
            turnOfTheRound++;
        }
    }

    private void decreaseTurnNumber(){
        turnOfTheRound--;
        if(turnOfTheRound<0){
            turnOfTheRound = 0;
            firstDraftOfDice = true;
            updateRound();
            if(roundNumber == 9){
                countPoints();
            }
        }
    }

    /**
     * method to add a new player
     * @param name
     */
    public void addPlayer(String name){
        System.out.println("add player");
        participants.add(new Player(name));
        setChanged();
        notifyObservers(new SuccessCreatePlayerMessage("server",name));
    }

    /**
     * method to extract and set ToolCard
     */
    public void extractToolCards() {
        FileParser parser = new FileParser();
        parser.writeTapWheelFirstColor(Model.FOLDER_ADDRESS_TOOL_CARDS, null);
        parser.writeLathekinPositions(Model.FOLDER_ADDRESS_TOOL_CARDS,
                -1, -1, -1, -1);
        parser.writeTapWheelUsingValue(Model.FOLDER_ADDRESS_TOOL_CARDS, false);
        ArrayList<Integer> cardIndex = new ArrayList<>(TOOL_CARDS_NUMBER);
        for(int i = 1; i <= TOOL_CARDS_NUMBER; i++){
            cardIndex.add(i);
        }
        Collections.shuffle(cardIndex);
        for(int i = 0; i < Model.TOOL_CARDS_EXTRACT_NUMBER; i++) {
            gameBoard.setToolCards(parser.createToolCard(Model.FOLDER_ADDRESS_TOOL_CARDS, cardIndex.get(i)), i);
        }
        /*
        gameBoard.setToolCards(parser.createToolCard(Model.FOLDER_ADDRESS_TOOL_CARDS, 1), 0);
        gameBoard.setToolCards(parser.createToolCard(Model.FOLDER_ADDRESS_TOOL_CARDS, 2), 1);
        gameBoard.setToolCards(parser.createToolCard(Model.FOLDER_ADDRESS_TOOL_CARDS, 3), 2);
        */
    }

    /**
     *method to extract and set PublicObjectiveCard
     */
    public void extractPublicObjectiveCards() {
        FileParser parser = new FileParser();
        ArrayList<Integer> cardIndex = new ArrayList<>();
        for(int i = 1; i <= PUBLIC_OBJECTIVE_CARDS_NUMBER; i++){
            System.out.println("added: " + i);
            cardIndex.add(i);
        }
        Collections.shuffle(cardIndex);
        for(int i = 0; i < PUBLIC_OBJECTIVE_CARDS_EXTRACT_NUMBER; i++) {
            System.out.println("extracted: " + cardIndex.get(i));
            gameBoard.setPublicObjectiveCards(parser.createObjectiveCard(false,cardIndex.get(i)), i);
        }
    }

    /**
     * Extracts Dice from DiceBag and puts them on the RoundTrack
     */
    public void extractRoundTrack(){
        getGameBoard().getRoundTrack().getRoundDice()[roundNumber] = new RoundDice(participants.size(),getGameBoard().getDiceBag(),turnOfTheRound);
    }

    /**
     * method that extract and sends players the schemaCards to choose from
     */
    public void sendSchemaCard(){
        Thread sendingMessageThread;
        ArrayList<Integer> randomValues = new ArrayList<>();
        FileParser parser = new FileParser();
        int cardsExtractedIndex = 0;
        int actualSchemaCardNumber = SCHEMA_CARDS_NUMBER + parser.countExcessSchemaCards(FOLDER_ADDRESS_SCHEMA_CARDS);
        for(int i = 1; i<= actualSchemaCardNumber; i++){
            randomValues.add(i);
        }
        Collections.shuffle(randomValues);
        for(Player player: participants) {
            SchemaCard[] extractedSchemaCards = new SchemaCard[SCHEMA_CARDS_EXTRACT_NUMBER *2];
            String[] schemaCards = new String[SCHEMA_CARDS_EXTRACT_NUMBER *2];
            sendingMessageThread = new Thread(this);
            for(int i = 0; i< SCHEMA_CARDS_EXTRACT_NUMBER*2; i++){
                extractedSchemaCards[i] = parser
                        .createSchemaCardByNumber(FOLDER_ADDRESS_SCHEMA_CARDS, randomValues.get(cardsExtractedIndex));
                schemaCards[i] = extractedSchemaCards[i].toString();
                cardsExtractedIndex++;
            }
            setDefaultSchemaCard(player, extractedSchemaCards[0]);
            try{
                Message sentMessage = new ChooseSchemaMessage("model", player.getName(), schemaCards);
                memorizeMessage(sentMessage);
                sendingMessageThread.start();
                sendingMessageThread.join();
            } catch (InterruptedException e) {
                Logger.getAnonymousLogger().log(Level.SEVERE, "{0}", e);
            }
        }
    }


    /**give each player the chosen card
     * @param playerPos  Position of the player in the ArrayList
     * @param schemaName Schemacard chosen by the player
     */
    public void setSchemaCardPlayer(int playerPos, String schemaName){
        FileParser parser = new FileParser();
        SchemaCard schema = parser.createSchemaCardByName(FOLDER_ADDRESS_SCHEMA_CARDS, schemaName);
        participants.get(playerPos).setSchemaCard(schema);
    }

    private void setDefaultSchemaCard(Player player, SchemaCard schemaCard){
        player.setSchemaCard(schemaCard);
    }

    /**
     *method that extracts and sends each player his PrivateObjectivecard
     */
    public void sendPrivateObjectiveCard(){
        FileParser parser = new FileParser();
        ArrayList<Integer> cardIndex = new ArrayList<>(3);
        for(int i = 1; i <= PRIVATE_OBJECTIVE_CARDS_NUMBER; i++){
            cardIndex.add(i);
        }
        Collections.shuffle(cardIndex);
        participants.forEach(
                p -> {
                    int playerIndex = participants.indexOf(p);
                    p.setPrivateObjectiveCard(parser.createObjectiveCard(true, cardIndex.get(playerIndex)));
                }
        );
        participants.stream().filter(
                Player::isConnected
        ).forEach(
                p -> {
                    setChanged();
                    notifyObservers(new ShowPrivateObjectiveCardsMessage("model", p.getName(),
                            p.getPrivateObjective().getDescription(),
                            participants.size()));
                }
        );
        sendSchemaCard();
    }


    /**
     * send the gameboard to all the players
     */
    public void updateGameboard(){
        participants.stream().filter(
                Player::isConnected
        ).forEach(
                p -> {
                    StringBuilder builderGameboard = buildMessage();
                    StringBuilder personalBuilder = new StringBuilder();
                    personalBuilder.append("FavorTokens:/" + p.getFavorTokens()).append("/");
                    personalBuilder.append("playingPlayer:/")
                            .append(participants.get(turnOfTheRound).getName()).append("/");
                    builderGameboard.append(personalBuilder.toString());
                    Thread sendingMessageThread = new Thread(this);
                    try{
                        memorizeMessage(new SendGameboardMessage("model", p.getName(),
                                builderGameboard.toString()));
                        sendingMessageThread.start();
                        sendingMessageThread.join();
                    } catch (InterruptedException e){
                        sendingMessageThread.interrupt();
                        Logger.getAnonymousLogger().log(Level.SEVERE, "{0}", e);
                    }
                }
        );
    }

    public void updateGameboardToolCard() {
        Thread sendingMessageThread;
        StringBuilder builderGameboard = buildMessage();
        for(Player player: participants){
            sendingMessageThread = new Thread(this);
            try{
                memorizeMessage(new SendGameboardMessage("model", player.getName(),
                        builderGameboard.toString()));
                sendingMessageThread.start();
                sendingMessageThread.join();
            } catch (InterruptedException e){
                sendingMessageThread.interrupt();
                Logger.getAnonymousLogger().log(Level.SEVERE, "{0}", e);
            }
        }
    }

    public void updatePlayerDisconnected(Player disconnectedPlayer){
        Thread sendingMessageThread;
        StringBuilder builderGameboard = buildMessage();
        for(Player player: participants){
            if(player.getName()!=disconnectedPlayer.getName()){
                sendingMessageThread = new Thread(this);
                try{
                    memorizeMessage(new SendGameboardMessage("model", player.getName(),
                            builderGameboard.toString()));
                    sendingMessageThread.start();
                    sendingMessageThread.join();
                } catch (InterruptedException e){
                    sendingMessageThread.interrupt();
                    Logger.getAnonymousLogger().log(Level.SEVERE, "{0}", e);
                }
            }
        }
    }

    private StringBuilder buildMessage(){
        StringBuilder builderGameboard = new StringBuilder();
        builderGameboard.append("PublicObjectiveCards:/");
        for(int i=0; i<PUBLIC_OBJECTIVE_CARDS_EXTRACT_NUMBER; i++){
            builderGameboard.append("Name:/").append(gameBoard.getPublicObjectiveCardName(i)).append("/");
            builderGameboard.append("Description:/").append(gameBoard.getPublicObjectiveCardDescription(i)).append("/");
        }
        builderGameboard.append("ToolCards:/");
        for (int i=0; i<TOOL_CARDS_EXTRACT_NUMBER; i++){
            builderGameboard.append("Name:/").append(gameBoard.getToolCardName(i)).append("/");
            builderGameboard.append("ID:/").append(gameBoard.getToolCards()[i].getIdentificationName())
                    .append("/");
            builderGameboard.append("Description:/").append(gameBoard.getToolCardDescription(i)).append("/");
        }
        builderGameboard.append("SchemaCards:/");
        for (int i=participants.size()-1; i>=0;i--){
            builderGameboard.append(participants.get(i).getName()).append("\n");
            builderGameboard.append(participants.get(i).getSchemaCard().toString()).append("/");
        }
        builderGameboard.append("schemaStop:/");
        builderGameboard.append("DiceList:/");
        RoundDice currentRoundDice = gameBoard.getRoundDice()[roundNumber];
        List<Dice> currentDiceList = currentRoundDice.getDiceList();
        currentDiceList.forEach(
                die -> builderGameboard.append(die.toString()).append("/")
        );
        builderGameboard.append("DiceStop/");
        return builderGameboard;
    }



    /**
     * update the Round
     */
    public void updateRound(){
        roundNumber++;
        changeFirstPlayer();
        extractRoundTrack();
    }

    /**
     * change the order of the players ArrayList
     */
    public void changeFirstPlayer(){
        Player lastPlayer = participants.remove(0);
        participants.add(lastPlayer);
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    /**
     * method that counts points of all players and sorts them according to the winner
     */
    public void countPoints(){
        Arrays.asList(gameBoard.getPublicObjectiveCards()).forEach(
                objectiveCard -> objectiveCard.countPoints(this, objectiveCard.getName(), objectiveCard.getPoints())
        );
        participants.stream().forEach(
                p -> p.getPrivateObjective()
                        .countPoints(this, p.getPrivateObjective().getName(), p.getPrivateObjective().getPoints())
        );
        Collections.sort(participants, Comparator.comparingInt( p -> p.getPoints()));
        participants.stream().filter(
                Player::isConnected
        ).forEach(
                p -> {
                    this.setChanged();
                    this.notifyObservers(new SendWinnerMessage("model", p.getName(), participants));
                }
        );
    }

    public void singlePlayerWinning(Player player){
        Arrays.asList(gameBoard.getPublicObjectiveCards()).forEach(
                objectiveCard -> objectiveCard.countPoints(this, objectiveCard.getName(), objectiveCard.getPoints())
        );
        player.getPrivateObjective().countPoints(this, player.getName(), player.getPoints());
        setChanged();
        ArrayList<Player> singlePlayerWinner = new ArrayList<>();
        singlePlayerWinner.add(player);
        notifyObservers(new SendWinnerMessage("server", player.getName(), singlePlayerWinner));
    }

    @Override
    public void run() {
        setChanged();
        notifyObservers();
    }
}
