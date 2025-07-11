import static org.junit.jupiter.api.Assertions.*;

import model.Parkplatz;
import model.ParkplatzStatus;
import org.junit.jupiter.api.Test;

public class ParkplatzTest {

    @Test
    public void testKonstruktorUndInitialstatus() {
        Parkplatz platz = new Parkplatz(1, 5);
        assertEquals(1, platz.getNummer());
        assertEquals(5, platz.getEntfernungZumAusgang());
        assertEquals(ParkplatzStatus.FREI, platz.getStatus());
        assertTrue(platz.istFrei());
    }

    @Test
    public void testBelegenDesParkplatzes() {
        Parkplatz platz = new Parkplatz(2, 3);
        platz.setStatus(ParkplatzStatus.BELEGT);
        assertEquals(ParkplatzStatus.BELEGT, platz.getStatus());
        assertFalse(platz.istFrei());
    }

    @Test
    public void testFreigebenDesParkplatzes() {
        Parkplatz platz = new Parkplatz(3, 7);
        platz.setStatus(ParkplatzStatus.BELEGT);
        assertFalse(platz.istFrei());

        platz.setStatus(ParkplatzStatus.FREI);
        assertTrue(platz.istFrei());
    }
}
