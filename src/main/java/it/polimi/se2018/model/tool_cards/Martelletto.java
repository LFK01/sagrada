package it.polimi.se2018.model.tool_cards;

/**
 * @author Luciano
 */

public class Martelletto extends AbstractToolCard {

    private static Martelletto thisInstance;

    private Martelletto() {
        super("Martelletto", "Tira nuovamente tutti i dadi della Riserva. Questa carta può essera usata solo durante il tuo secondo turno, prima di scegliere il secondo dado", true);
    }

    public static synchronized Martelletto getThisInstance(){
        if(thisInstance==null){
            thisInstance = new Martelletto();
        }
        return thisInstance;
    }

}
