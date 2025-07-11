package service;

import lombok.Getter;
import model.Etage;
import model.Parkhaus;
import model.Parkplatz;
import model.ParkplatzStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public record ParkingService(@Getter Parkhaus parkhaus) {

    public static ParkingService erzeugeStandardParkhaus() {
        List<Etage> etagen = new ArrayList<>();
        int etagenAnzahl = 7;
        int plaetzeProEtage = 50;
        int platzNummerGlobal = 1;
        Random random = new Random();

        for (int etageNummer = 0; etageNummer < etagenAnzahl; etageNummer++) {
            List<Parkplatz> parkplaetze = new ArrayList<>();

            for (int i = 0; i < plaetzeProEtage; i++) {
                int entfernung = 5 + random.nextInt(20);
                parkplaetze.add(new Parkplatz(platzNummerGlobal++, entfernung));
            }

            etagen.add(new Etage(etageNummer, parkplaetze));
        }

        Parkhaus parkhaus = new Parkhaus(etagen);
        return new ParkingService(parkhaus);
    }

    public List<Parkplatz> parkeFahrzeuge(int anzahl) {
        List<Parkplatz> plaetze = parkhaus.sucheParkplaetze(anzahl);
        if (plaetze.isEmpty()) {
            throw new IllegalStateException("Keine passenden Parkplätze gefunden.");
        }
        for (Parkplatz platz : plaetze) {
            platz.setStatus(ParkplatzStatus.BELEGT);
        }
        return plaetze;
    }

    // sinnvoll für die weitere Entwicklung
    public void verlasseParkplatz(Parkplatz parkplatz) {
        parkplatz.setStatus(ParkplatzStatus.FREI);
    }

    // sinnvoll für die Anzeige freier Plätze z.B. auf Werbetafeln
    public long getAnzahlFreierParkplaetze() {
        return parkhaus.getEtagen().stream()
                .flatMap(etage -> etage.getParkplaetze().stream())
                .filter(Parkplatz::istFrei)
                .count();
    }

    public long getAnzahlBelegterParkplaetze() {
        return parkhaus.getEtagen().stream()
                .flatMap(etage -> etage.getParkplaetze().stream())
                .filter(platz -> !platz.istFrei())
                .count();
    }
}

