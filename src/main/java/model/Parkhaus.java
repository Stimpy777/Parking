package model;

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

        // 1. connected parking lots
        for (Etage etage : sortierteEtagen) {
            List<Parkplatz> gruppe = etage.findeZusammenhaengendeFreiePlaetze(anzahl);
            if (!gruppe.isEmpty()) {
                gruppe.sort(Comparator.comparingInt(Parkplatz::getEntfernungZumAusgang));
                return gruppe;
            }
        }

        // 2. unconnected parking lots
        List<Parkplatz> verstreutePlaetze = new ArrayList<>();
        for (Etage etage : sortierteEtagen) {
            List<Parkplatz> freiePlaetze = etage.findeAlleFreienPlaetze();
            freiePlaetze.sort(Comparator.comparingInt(Parkplatz::getEntfernungZumAusgang));
            for (Parkplatz platz : freiePlaetze) {
                verstreutePlaetze.add(platz);
                if (verstreutePlaetze.size() == anzahl) {
                    return verstreutePlaetze;
                }
            }
        }
        return Collections.emptyList();
    }
}
