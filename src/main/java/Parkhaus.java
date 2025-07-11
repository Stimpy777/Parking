import lombok.Getter;

import java.util.*;

public class Parkhaus {

    @Getter
    private final List<Etage> etagen;

    public Parkhaus(List<Etage> etagen) {
        this.etagen = etagen;
    }

    public List<Parkplatz> sucheParkplaetze(int anzahl) {
        List<Etage> sortierteEtagen = new ArrayList<>(etagen);
        sortierteEtagen.sort(Comparator.comparingDouble(Etage::berechneAuslastung));

        for (Etage etage : sortierteEtagen) {
            List<Parkplatz> gruppe = etage.findeZusammenhaengendeFreiePlaetze(anzahl);
            if (!gruppe.isEmpty()) {
                gruppe.sort(Comparator.comparingInt(Parkplatz::getEntfernungZumAusgang));
                return gruppe;
            }
        }
        // if there are no contiguous pitches, then just individual ones
        for (Etage etage : sortierteEtagen) {
            List<Parkplatz> gruppe = etage.findeFreiePlaetze(anzahl);
            if (!gruppe.isEmpty()) {
                gruppe.sort(Comparator.comparingInt(Parkplatz::getEntfernungZumAusgang));
                return gruppe;
            }
        }


        return Collections.emptyList();
    }
}
