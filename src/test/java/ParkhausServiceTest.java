import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class ParkhausServiceTest {

    private ParkhausService service;

    @BeforeEach
    public void setUp() {
        service = ParkhausService.erzeugeStandardParkhaus();
    }

    @Test
    public void testErzeugeStandardParkhaus() {
        Parkhaus parkhaus = service.parkhaus();
        List<Etage> etagen = parkhaus.getEtagen();

        assertEquals(7, etagen.size(), "Es sollten genau 7 Etagen existieren.");

        int gesamtParkplaetze = 0;
        for (Etage etage : etagen) {
            List<Parkplatz> plaetze = etage.getParkplaetze();
            assertEquals(50, plaetze.size(), "Jede Etage sollte genau 50 Parkplaetze haben.");
            long freiePlaetze = plaetze.stream().filter(Parkplatz::istFrei).count();
            assertEquals(50, freiePlaetze, "Alle Parkplaetze sollten initial frei sein.");
            gesamtParkplaetze += plaetze.size();
        }

        assertEquals(350, gesamtParkplaetze, "Insgesamt sollten 350 Parkplaetze existieren.");
    }

    @Test
    public void testeVollbelegung() {
        IntStream.range(0, 350).forEach(i -> {
            List<Parkplatz> plaetze = service.parkeFahrzeuge(1);
            assertEquals(1, plaetze.size(), "Es sollte genau 1 Parkplatz zugewiesen werden.");
        });

        assertEquals(350, service.getAnzahlBelegterParkplaetze(), "Es sollten genau 350 Parkplaetze belegt sein.");
    }

    @Test
    public void testeUeberbelegung() {
        IntStream.range(0, 7).forEach(i -> {
            List<Parkplatz> plaetze = service.parkeFahrzeuge(50);
            assertEquals(50, plaetze.size(), "Es sollten genau 50 Parkplätze zugewiesen werden.");
        });

        assertEquals(350, service.getAnzahlBelegterParkplaetze(), "Es sollten genau 350 Parkplaetze belegt sein.");

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> service.parkeFahrzeuge(1));
        assertEquals("Keine passenden Parkplätze gefunden.", exception.getMessage());
    }

    @Test
    public void testParkenUndVerlassen() {
        List<Parkplatz> plaetze = service.parkeFahrzeuge(2);
        assertEquals(2, plaetze.size());

        // Zustand prüfen
        assertTrue(plaetze.stream().noneMatch(Parkplatz::istFrei));

        // Ausparken
        plaetze.forEach(p -> service.verlasseParkplatz(p));

        // Zustand erneut prüfen
        assertTrue(plaetze.stream().allMatch(Parkplatz::istFrei));
    }
}
