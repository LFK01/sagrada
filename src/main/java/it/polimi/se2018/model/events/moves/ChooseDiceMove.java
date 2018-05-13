package it.polimi.se2018.model.events.moves;

import it.polimi.se2018.model.Player;

/**
 * @author Giorgia
 * edited Giovanni
 */
public class ChooseDiceMove extends PlayerMove {

    private int draftPoolPos;
    private int row;                //dado da posizionare scelto nel controller
    private int col;

    /**
     * Initializes ChooseDiceMove with reference of player, die posizione and new die position
     * @param draftPoolPos
     * @param row
     * @param col
     * @param player
     */
    public ChooseDiceMove(int draftPoolPos , int row, int col, Player player) {
        super(player);
        this.draftPoolPos = draftPoolPos;
        this.row=row;
        this.col=col;
    }

    public int getDraftPoolPos() {
        return draftPoolPos;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public boolean isDiceMove() {
        return true;
    }

    @Override
    public boolean isNoActionMove() {
        return false;
    }

}