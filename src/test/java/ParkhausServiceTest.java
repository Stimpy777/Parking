import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.IntStream;

public class ParkhausServiceTest {

    @Test
    public void testErzeugeStandardParkhaus() {
        ParkhausService service = ParkhausService.erzeugeStandardParkhaus();
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
    public void testeVollbelegung(){
        ParkhausService service = ParkhausService.erzeugeStandardParkhaus();


        IntStream.range(0, 350).forEach(i -> {
            List<Parkplatz> plaetze = service.parkeFahrzeuge(1);
            assertEquals(1, plaetze.size(), "Es sollte genau 1 Parkplatz zugewiesen werden.");
        });

        assertEquals(350, service.getAnzahlBelegterParkplaetze(), "Es sollten genau 350 Parkplaetze belegt sein.");
    }

    @Test
    public void testeUeberbelegung(){
        ParkhausService service = ParkhausService.erzeugeStandardParkhaus();
        // das Parkhaus voll belegen
        IntStream.range(0, 7).forEach(i -> {
            List<Parkplatz> plaetze = service.parkeFahrzeuge(50);
            assertEquals(50, plaetze.size(), "Es sollten genau 50 Parkplatz zugewiesen werden.");
        });
        // ist es voll?
        assertEquals(350, service.getAnzahlBelegterParkplaetze(), "Es sollten genau 350 Parkplaetze belegt sein.");
        // auf Exception pruefen
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> service.parkeFahrzeuge(1));
        assertEquals("Keine passenden Parkplätze gefunden.", exception.getMessage());


    }

}

