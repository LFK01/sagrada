package it.polimi.se2018.network.client.socket;

import it.polimi.se2018.model.events.ChangeDieValueMessage;
import it.polimi.se2018.model.events.messages.*;
import it.polimi.se2018.model.events.moves.ChooseDiceMove;
import it.polimi.se2018.model.events.moves.NoActionMove;
import it.polimi.se2018.model.events.moves.UseToolCardMove;
import it.polimi.se2018.network.server.ServerSocketInterface;
import it.polimi.se2018.utils.ProjectObservable;
import it.polimi.se2018.utils.ProjectObserver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.SocketAddress;
import java.util.Observable;
import java.util.Observer;

/**
 * @author Luciano
 */
public class RemoteViewSocket extends ProjectObservable implements ProjectObserver {

    private ServerSocketInterface server;
    private String username;
    private boolean serverIsUp;

    public RemoteViewSocket(String localhost, int port){
        server = new NetworkHandler(localhost, port, this);
    }

    public RemoteViewSocket(String localhost, int port, String oldUsername){
        System.out.println("Socket comeback: RemoteView created");
        server = new NetworkHandler(localhost, port, this, oldUsername);
    }

    public void notifyView(Message message){
        if(message.getRecipient().equals(username) || message.getRecipient().equals("@all")){
            setChanged();
            notifyObservers(message);
        }
    }

    public void notifyView(ChooseSchemaMessage chooseSchemaMessage){
        if(chooseSchemaMessage.getRecipient().equals(username) || chooseSchemaMessage.getRecipient().equals("@all")){
            System.out.println("RemoteWSocket -> Client: " + chooseSchemaMessage.toString());
            setChanged();
            notifyObservers(chooseSchemaMessage);
        }
    }

    public void notifyView(ErrorMessage errorMessage){
        if(errorMessage.getRecipient().equals(username) || server.getAddress().equals(errorMessage.getRecipient())){
            if(errorMessage.toString().equals("NotValidUsername")){
                System.out.println("RemoteWSocket -> Client: error message not valid username");
                username = "";
                setChanged();
                notifyObservers(errorMessage);
            }
            else{
                setChanged();
                notifyObservers(errorMessage);
            }
        }
    }

    public void notifyView(GameInitializationMessage gameInitializationMessage){
        if(gameInitializationMessage.getRecipient().equals(username)){
            setChanged();
            notifyObservers(gameInitializationMessage);
        }
    }

    public void notifyView(SelectedSchemaMessage selectedSchemaMessage){
        if(selectedSchemaMessage.getRecipient().equals(username)){
            setChanged();
            notifyObservers(selectedSchemaMessage);
        }
    }

    public void notifyView(ShowPrivateObjectiveCardsMessage showPrivateObjectiveCardsMessage){
        if(showPrivateObjectiveCardsMessage.getRecipient().equals(username)){
            System.out.println("RemoteWSocket -> Client: success message");
            setChanged();
            notifyObservers(showPrivateObjectiveCardsMessage);
        }
    }

    public void notifyView(SuccessMessage successMessage){
        if(successMessage.getRecipient().equals(username)){
            System.out.println("RemoteWSocket -> Client: success message");
            setChanged();
            notifyObservers(successMessage);
        }
    }

    public void notifyView(SuccessCreatePlayerMessage successCreatePlayerMessage){
        if(successCreatePlayerMessage.getRecipient().equals(username)){
            System.out.println("RemoteWSocket -> Server: success create message");
            setChanged();
            notifyObservers(successCreatePlayerMessage);
        }
    }

    @Override
    public void update(Message message) {
        System.out.println("RemoteWSocket -> Server: " + message.toString());
        server.sendToServer(message);
    }

    @Override
    public void update(ChooseSchemaMessage chooseSchemaMessage) {
        System.out.println("RemoteWSocket -> Server: " + chooseSchemaMessage.toString());
        server.sendToServer(chooseSchemaMessage);
    }

    @Override
    public void update(ComebackSocketMessage comebackSocketMessage) {
        System.out.println("RemoteWSocket -> Server: comeback player message");
        server.sendToServer(comebackSocketMessage);
    }

    @Override
    public void update(CreatePlayerMessage createPlayerMessage) {
        username = createPlayerMessage.getPlayerName();
        System.out.println("RemoteWSocket -> Server: create player message");
        server.sendToServer(createPlayerMessage);
    }

    @Override
    public void update(ErrorMessage errorMessage) {
        System.out.println("RemoteWSocket -> Server: " + errorMessage.toString());
        server.sendToServer(errorMessage);
    }

    @Override
    public void update(GameInitializationMessage gameInitializationMessage) {
        System.out.println("RemoteWSocket -> Server: " + gameInitializationMessage.toString());
        server.sendToServer(gameInitializationMessage);
    }

    @Override
    public void update(NewRoundMessage newRoundMessage) {
        System.out.println("RemoteWSocket -> Server: " + newRoundMessage.toString());
        server.sendToServer(newRoundMessage);
    }

    @Override
    public void update(SelectedSchemaMessage selectedSchemaMessage) {
        System.out.println("RemoteWSocket -> Server: " + selectedSchemaMessage.toString());
        server.sendToServer(selectedSchemaMessage);
    }

    @Override
    public void update(ShowPrivateObjectiveCardsMessage showPrivateObjectiveCardsMessage) {
        System.out.println("RemoteWSocket -> Server: " + showPrivateObjectiveCardsMessage.toString());
        server.sendToServer(showPrivateObjectiveCardsMessage);
    }

    @Override
    public void update(SuccessCreatePlayerMessage successCreatePlayerMessage) {
        System.out.println("RemoteWSocket -> Server: " + successCreatePlayerMessage.toString());
        server.sendToServer(successCreatePlayerMessage);
    }

    @Override
    public void update(SuccessMoveMessage successMoveMessage) {
        System.out.println("RemoteWSocket -> Server: " + successMoveMessage.toString());
        server.sendToServer(successMoveMessage);
    }

    @Override
    public void update(NoActionMove noActionMove){
        System.out.println("RemoteWSocket -> Server: " + noActionMove.toString());
        server.sendToServer(noActionMove);
    }

    @Override
    public void update(RequestMessage requestMessage) {

    }


    @Override
    public void update(UpdateTurnMessage updateTurnMessage) {
        System.out.println("RemoteWSocket -> Server: " + updateTurnMessage.toString());
        server.sendToServer(updateTurnMessage);
    }

    @Override
    public void update(UseToolCardMove useToolCardMove) {

    }

    @Override
    public void update(ChooseDiceMove chooseDiceMove) {
        System.out.println("RemoteWSocket -> Server: " + chooseDiceMove.toString());
        server.sendToServer(chooseDiceMove);

    }

    @Override
    public void update(ChangeDieValueMessage changeDieValueMessage) {

    }
}