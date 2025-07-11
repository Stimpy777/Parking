import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EtageTest {

    @Test
    public void testFindeZusammenhaengendeFreiePlaetzeErfolgreich() {
        List<Parkplatz> plaetze = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            plaetze.add(new Parkplatz(i, 0));
        }

        // Belege Plätze 4 und 5, damit 0–2 zusammenhängend bleiben
        plaetze.get(4).setStatus(ParkplatzStatus.BELEGT);
        plaetze.get(5).setStatus(ParkplatzStatus.BELEGT);

        Etage etage = new Etage(0, plaetze);

        List<Parkplatz> gruppe = etage.findeZusammenhaengendeFreiePlaetze(3);

        assertEquals(3, gruppe.size());
        assertEquals(0, gruppe.get(0).getNummer());
        assertEquals(1, gruppe.get(1).getNummer());
        assertEquals(2, gruppe.get(2).getNummer());
    }

    @Test
    public void testFindeZusammenhaengendeFreiePlaetzeNichtGenugFrei() {
        List<Parkplatz> plaetze = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Parkplatz platz = new Parkplatz(i, 0);
            platz.setStatus(ParkplatzStatus.BELEGT); // Alle belegen
            plaetze.add(platz);
        }

        Etage etage = new Etage(1, plaetze);

        List<Parkplatz> gruppe = etage.findeZusammenhaengendeFreiePlaetze(2);

        assertTrue(gruppe.isEmpty(), "Es sollten keine freien Gruppen gefunden werden.");
    }

    @Test
    public void testBerechneAuslastungDurchSetzen() {
        List<Parkplatz> plaetze = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            plaetze.add(new Parkplatz(i, 0));
        }

        // Belege 4 von 10 Plätzen
        plaetze.get(0).setStatus(ParkplatzStatus.BELEGT);
        plaetze.get(1).setStatus(ParkplatzStatus.BELEGT);
        plaetze.get(2).setStatus(ParkplatzStatus.BELEGT);
        plaetze.get(3).setStatus(ParkplatzStatus.BELEGT);

        Etage etage = new Etage(2, plaetze);
        double auslastung = etage.berechneAuslastung();

        assertEquals(0.4, auslastung, 0.0001, "Die Auslastung sollte 40% betragen.");
    }
}
