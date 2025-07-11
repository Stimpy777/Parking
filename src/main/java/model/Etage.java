package model;

import lombok.Getter;

import java.util.*;

@Getter
public class Etage {
    private final int nummer;
    private final List<Parkplatz> parkplaetze;

    public Etage(int nummer, List<Parkplatz> parkplaetze) {
        this.nummer = nummer;
        this.parkplaetze = parkplaetze;
    }

    public List<Parkplatz> findeZusammenhaengendeFreiePlaetze(int anzahl) {
        List<Parkplatz> gruppe = new ArrayList<>();
        for (int i = 0; i < parkplaetze.size(); i++) {
            gruppe.clear();
            for (int j = i; j < i + anzahl && j < parkplaetze.size(); j++) {
                if (parkplaetze.get(j).istFrei()) {
                    gruppe.add(parkplaetze.get(j));
                } else {
                    break;
                }
            }
            if (gruppe.size() == anzahl) {
                return gruppe;
            }
        }
        return Collections.emptyList();
    }

    public List<Parkplatz> findeAlleFreienPlaetze() {
        List<Parkplatz> gruppe = new ArrayList<>();
        for (Parkplatz p : parkplaetze) {
            if (p.istFrei()) {
                gruppe.add(p);
            }
        }
        if (!gruppe.isEmpty()) {
            return gruppe;
        }
        return Collections.emptyList();
    }

    public double berechneAuslastung() {
        long frei = parkplaetze.stream().filter(Parkplatz::istFrei).count();
        return 1.0 - ((double) frei / parkplaetze.size());
    }

}
