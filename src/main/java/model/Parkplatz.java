package model;

import lombok.Data;

@Data
public class Parkplatz {
    private final int nummer;
    private final int entfernungZumAusgang;
    private ParkplatzStatus status;

    public Parkplatz(int nummer, int entfernungZumAusgang) {
        this.nummer = nummer;
        this.entfernungZumAusgang = entfernungZumAusgang;
        this.status = ParkplatzStatus.FREI;
    }

    public boolean istFrei() {
        return status == ParkplatzStatus.FREI;
    }

}
