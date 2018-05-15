package it.polimi.se2018.model;

import it.polimi.se2018.model.exceptions.FullCellException;
import it.polimi.se2018.model.exceptions.NoColorException;
import it.polimi.se2018.model.exceptions.RestrictionsNotRespectedException;

/**
 * Class meant to contain a Die and all the information about the placing restrictions
 * @author Giorgia
 */

//edited Luciano 13/05/2018 comments
public class Cell {

    private Color cellColor; /*color value for color restriction*/
    private boolean noColor; /*boolean value to know if there's color restriction*/
    private int value; /*integer value for value restriction, 0 means no value restriction*/
    private boolean containsDie; /*boolean value to know if there's a die in the cell*/
    private boolean avoidValueRestriction;
    private boolean avoidColorRestriction;
    private boolean avoidNearnessRestriction;
    /*boolean values to know if some tool cards have been activated and we dont have to consider value restriction
    * on this cell*/
    private Dice assignedDice; /*Dice reference to the die placed on the cell*/

    /**
     * constructor method
     * @param color to assign a color restriction, if null noColor becomes true
     * @param value to assign value restriction, equals 0 if there's no vaue restrictions on the cell
     */
    public Cell(Color color, int value){

        this.cellColor = color;

        //controllo per gestione delle celle senza restrizioni di colore
        this.noColor = ( color == null );

        this.value = value;
        this.containsDie = false;
        this.avoidValueRestriction = false;
        this.avoidColorRestriction = false;
        this.avoidNearnessRestriction = false;
        this.assignedDice = null;

    }

    /**
     * Return true if the cell hasn't any color restrictions
     * @return noColor
     */
    public boolean hasNoColorRestrictions() {
        return noColor;
    }

    /**
     * Returns the color of the cell color restriction
     * @exception NoColorException
     * @return cellColor
     */
    public Color getCellColor() throws NoColorException {
        if(this.noColor) throw new NoColorException();
        return cellColor;
    }

    /**
     * Returns the int value of the cell value restriction, returns 0 if there isn't any value restriction
     * @return value
     */
    public int getValue() {
        return value;
    }

    /**
     * Returns true if the cell contains a die
     * @return
     */
    public boolean isFull() {
        return containsDie;
    }

    /**
     * Places a die on the cell, throws an Excpetion if the die doesn't respect
     * any of the placing restrictions
     * @param dice Die to be placed
     * @throws RestrictionsNotRespectedException, FullCellException
     */
    public void setAssignedDice(Dice dice) throws RestrictionsNotRespectedException, FullCellException{
        if(isFull()){
            throw (new FullCellException("Cella piena!"));
        }
        if(noColor){
            if(this.value==0){
                this.assignedDice = dice;
                containsDie = true;
            }
            else{
                if(dice.getValue()!=this.value){
                    throw new RestrictionsNotRespectedException("Valore dado non corretto!");
                }
                else{
                    this.assignedDice = dice;
                    containsDie = true;
                }
            }
        }
        else{
            if(dice.getDiceColor()!=this.cellColor){
                throw new RestrictionsNotRespectedException("Colore dado non corretto!");
            }
            else{
                this.assignedDice = dice;
                containsDie = true;
            }
        }
    }

    /**
     * Removes the die from the cell and sets containsDie to false
     * @return the removed die
     */
    public Dice removeDieFromCell(){
        Dice removedDie = this.assignedDice;
        this.assignedDice = null;
        this.containsDie = false;
        return removedDie;
    }
    /**
     * Returns a reference to the Dice placed on the schemaCard
     * @return assignedDice
     */
    public Dice getAssignedDice() {
        return assignedDice;
    }

}
