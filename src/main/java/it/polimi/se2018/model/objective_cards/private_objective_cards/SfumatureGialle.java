package it.polimi.se2018.model.objective_cards.private_objective_cards;

import it.polimi.se2018.model.Color;
import it.polimi.se2018.model.SchemaCard;
import it.polimi.se2018.model.objective_cards.AbstractObjectiveCard;

/**
 * @author Luciano
 */

public class SfumatureGialle extends AbstractObjectiveCard {

    private static SfumatureGialle thisInstance;

    private SfumatureGialle() {
        super("Sfumature Gialle", "Somma dei valori su tutti i dadi gialli", "#", true);
    }

    public synchronized static SfumatureGialle getThisInstance(){
        if(thisInstance==null){
            thisInstance = new SfumatureGialle();
        }
        return thisInstance;
    }
    /**
     * Methos that count the number of yellow dice in schema
     * @param schemaCard
     * @return n number of yellow dice in the schema
     */
    @Override
    public int countPoints(SchemaCard schemaCard) {

        int sum=0;
        for(int i=0; i<4; i++){
            for(int j=0; j<5 ; j++){
                if(schemaCard.getCell(i,j).getAssignedDice().getDiceColor().equals(Color.YELLOW))
                    sum = sum + schemaCard.getCell(i,j).getAssignedDice().getValue();
            }

        }
        return sum;
    }
}
