package it.polimi.se2018.network.server.virtual_objects;

import it.polimi.se2018.model.events.messages.*;
import it.polimi.se2018.network.client.rmi.ClientRMIInterface;
import it.polimi.se2018.network.server.Server;
import it.polimi.se2018.network.server.ServerRMIInterface;
import it.polimi.se2018.network.server.excpetions.PlayerNotFoundException;
import it.polimi.se2018.network.server.excpetions.PlayerNumberExceededException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;

public class VirtualClientRMI  implements ServerRMIInterface, VirtualClientInterface{

    private VirtualViewRMI virtualView;
    private ClientRMIInterface remoteView;
    private boolean isConnected;
    private Server server;
    private String username;

    public VirtualClientRMI(VirtualViewRMI virtualView, ClientRMIInterface remoteView, Server server) throws RemoteException{
        this.virtualView = virtualView;
        this.remoteView = remoteView;
        this.server = server;
        isConnected = true;
    }

    public VirtualClientRMI(VirtualViewRMI virtualView, ClientRMIInterface remoteView, String username, Server server) throws RemoteException{
        this.virtualView = virtualView;
        this.remoteView = remoteView;
        this.server = server;
        this.username = username;
        isConnected = true;
    }

    @Override
    public ServerRMIInterface addClient(ClientRMIInterface newClient) throws RemoteException, PlayerNumberExceededException {
        /*method to be implemented in ClientGathererRMI*/
        return null;
    }

    @Override
    public ServerRMIInterface retrieveOldClient(ClientRMIInterface newClient, String username) throws RemoteException, PlayerNotFoundException {
        /*method to be implemented in ClientGathererRMI*/
        return null;
    }

    @Override
    public void sendToServer(Message message) throws RemoteException {
        System.out.println("VirtualClientRMI -> Server: " + message.toString());
        try{
            Method updateServer = virtualView.getClass().getMethod("updateServer", message.getClass());
            updateServer.invoke(virtualView, message);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e){
            e.printStackTrace();
        }
    }

    public void notifyClient(Message message){
        if(isConnected){
            try{
                System.out.println("VirtualClientRMI -> RemoteVRMI: " + message.toString());
                remoteView.updateClient(message);
            } catch (RemoteException e) {
                isConnected = false;
                e.printStackTrace();
            }
        }
    }

    public void notifyClient(SuccessCreatePlayerMessage successCreatePlayerMessage){
        if(isConnected){
            try{
                System.out.println("VirtualClientRMI -> RemoteVRMI: " + successCreatePlayerMessage.toString());
                remoteView.updateClient(successCreatePlayerMessage);
            } catch (RemoteException e) {
                isConnected = false;
                e.printStackTrace();
            }
        }
    }

    public void notifyClient(ErrorMessage errorMessage){
        if(isConnected){
            try{
                System.out.println("VCRMI -> RemoteVRMI: " + errorMessage.toString());
                remoteView.updateClient(errorMessage);
            } catch (RemoteException e) {
                System.out.println("Giocatore #" + server.getPlayers().indexOf(virtualView) + " " + username + "disconnesso");
                isConnected = false;
                e.printStackTrace();
            }
        }
    }

    public void notifyClient(ChooseSchemaMessage chooseSchemaMessage){
        if(isConnected){
            try{
                System.out.println("VirtualClientRMI -> RemoteVRMI: " + chooseSchemaMessage.toString());
                remoteView.updateClient(chooseSchemaMessage);
            } catch (RemoteException e) {
                isConnected = false;
                e.printStackTrace();
            }
        }
    }

    public void notifyClient(ShowPrivateObjectiveCardsMessage showPrivateObjectiveCardsMessage){
        if(isConnected){
            try{
                System.out.println("VirtualClientRMI -> RemoteVRMI: " + showPrivateObjectiveCardsMessage.toString());
                remoteView.updateClient(showPrivateObjectiveCardsMessage);
            } catch (RemoteException e) {
                isConnected = false;
                e.printStackTrace();
            }
        }
    }

    public void notifyClient(DemandSchemaCardMessage demandSchemaCardMessage){
        if(isConnected){
            try{
                System.out.println("VirtualClientRMI -> RemoteVRMI: " + demandSchemaCardMessage.toString());
                remoteView.updateClient(demandSchemaCardMessage);
            } catch (RemoteException e) {
                isConnected = false;
                e.printStackTrace();
            }
        }



    }



    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public Server getServer() {
        return server;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }
}
