import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class ParkingTest {

    @Test
    public void testSucheZusammenhaengendePlaetze() {
        List<Parkplatz> parkPlaetze = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            parkPlaetze.add(new Parkplatz(i, 10 - i));
        }
        Etage etage = new Etage(0, parkPlaetze);
        Parkhaus haus = new Parkhaus(List.of(etage));
        ParkhausService service = new ParkhausService(haus);

        List<Parkplatz> gefunden = service.parkeFahrzeuge(3);
        assertEquals(3, gefunden.size());
        assertTrue(gefunden.stream().noneMatch(Parkplatz::istFrei));
    }

    @Test
    public void testVerlassenDesPlatzes() {
        Parkplatz p = new Parkplatz(1, 2);
        p.setStatus(ParkplatzStatus.BELEGT);
        assertFalse(p.istFrei());
        p.setStatus(ParkplatzStatus.FREI);
        assertTrue(p.istFrei());
    }
}
