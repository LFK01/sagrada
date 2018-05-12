package it.polimi.se2018;
import static org.junit.Assert.*;
import it.polimi.se2018.model.Player;
import org.junit.Test;




public class TestPlayerClass {
    @Test
    public void testplayerInitialization(){
        Player player = null;
        try {
            player = new Player("giovanni");
        }
        catch(NullPointerException e){
            fail();
        }

        assertEquals(0,player.getPoints());

    }


}
