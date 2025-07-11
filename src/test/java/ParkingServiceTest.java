import model.Etage;
import model.Parkhaus;
import model.Parkplatz;
import model.ParkplatzStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ParkingService;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class ParkingServiceTest {

    private ParkingService service;

    @BeforeEach
    public void setUp() {
        service = ParkingService.erzeugeDemoParkhaus();
    }

    @Test
    public void testErzeugeDemoParkhaus() {
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

    @Test
    public void testFallbackAufEinzelplaetzeWennKeineGruppeVerfuegbar() {
        // Belege gezielt alle Plätze so, dass keine zusammenhängende Gruppe von 3 existiert
        List<Etage> etagen = service.parkhaus().getEtagen();
        Etage ersteEtage = etagen.getFirst();

        // Belege jeden zweiten model.Parkplatz (Plätze 0, 2, 4, ...) – keine 3 aufeinanderfolgenden Plätze frei
        List<Parkplatz> parkplaetze = ersteEtage.getParkplaetze();
        for (int i = 0; i < parkplaetze.size(); i += 2) {
            parkplaetze.get(i).setStatus(ParkplatzStatus.BELEGT);
        }

        // Nun versuche, 3 Fahrzeuge zu parken
        List<Parkplatz> zugewiesene = service.parkeFahrzeuge(3);

        // Es sollten 3 Parkplätze zugewiesen werden, obwohl keine zusammenhängenden vorhanden sind
        assertEquals(3, zugewiesene.size(), "Es sollten 3 einzelne Parkplätze gefunden werden.");

        // Sicherstellen, dass keiner der zugewiesenen Plätze vorher belegt war
        for (Parkplatz p : zugewiesene) {
            assertFalse(p.istFrei(), "Der zugewiesene model.Parkplatz sollte nun belegt sein.");
        }

        // Sicherstellen, dass die Plätze nicht zusammenhängend sind (Abstand > 1)
        boolean zusammenhaengend = true;
        for (int i = 1; i < zugewiesene.size(); i++) {
            int diff = Math.abs(
                    ersteEtage.getParkplaetze().indexOf(zugewiesene.get(i)) -
                            ersteEtage.getParkplaetze().indexOf(zugewiesene.get(i - 1))
            );
            if (diff != 1) {
                zusammenhaengend = false;
                break;
            }
        }
        assertFalse(zusammenhaengend, "Die zugewiesenen Parkplätze sollten nicht zusammenhängend sein.");
    }
}
