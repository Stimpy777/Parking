import model.Etage;
import model.Parkhaus;
import model.Parkplatz;
import model.ParkplatzStatus;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ParkhausTest {

    @Test
    public void testSucheParkplaetzeMitErfolg() {
        // model.Etage 1 – vollständig belegt
        List<Parkplatz> etage1Plaetze = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Parkplatz platz = new Parkplatz(i, 10 - i); // verschiedene Entfernungen
            platz.setStatus(ParkplatzStatus.BELEGT);
            etage1Plaetze.add(platz);
        }

        // model.Etage 2 – einige freie, drei zusammenhängende in der Mitte
        List<Parkplatz> etage2Plaetze = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Parkplatz platz = new Parkplatz(i, 5 - i);
            if (i == 1 || i == 2 || i == 3) {
                // zusammenhängende freie Plätze
                // default = FREI
            } else {
                platz.setStatus(ParkplatzStatus.BELEGT);
            }
            etage2Plaetze.add(platz);
        }

        Etage etage1 = new Etage(1, etage1Plaetze);
        Etage etage2 = new Etage(2, etage2Plaetze);

        Parkhaus parkhaus = new Parkhaus(List.of(etage1, etage2));

        List<Parkplatz> gefundenePlaetze = parkhaus.sucheParkplaetze(3);

        assertEquals(3, gefundenePlaetze.size(), "Es sollten 3 freie zusammenhängende Plätze gefunden werden.");
        assertTrue(gefundenePlaetze.stream().allMatch(Parkplatz::istFrei), "Alle gefundenen Plätze müssen frei sein.");

        // Prüfe Sortierung nach Entfernung (aufsteigend)
        for (int i = 1; i < gefundenePlaetze.size(); i++) {
            assertTrue(
                    gefundenePlaetze.get(i - 1).getEntfernungZumAusgang()
                            <= gefundenePlaetze.get(i).getEntfernungZumAusgang(),
                    "Die Parkplätze sollten aufsteigend nach Entfernung sortiert sein."
            );
        }
    }

    @Test
    public void testSucheParkplaetzeFehlschlag() {
        // Alle Plätze belegt in beiden Etagen
        List<Etage> etagen = new ArrayList<>();

        for (int e = 0; e < 2; e++) {
            List<Parkplatz> plaetze = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                Parkplatz platz = new Parkplatz(i, i);
                platz.setStatus(ParkplatzStatus.BELEGT);
                plaetze.add(platz);
            }
            etagen.add(new Etage(e, plaetze));
        }

        Parkhaus parkhaus = new Parkhaus(etagen);
        List<Parkplatz> ergebnis = parkhaus.sucheParkplaetze(2);

        assertTrue(ergebnis.isEmpty(), "Es sollten keine zusammenhängenden freien Plätze gefunden werden.");
    }
}
