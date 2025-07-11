import service.ParkingService;

public class ParkingMain {

    public static void main(String[] args) {
        ParkingService service = ParkingService.erzeugeDemoParkhaus();

        System.out.println("==> Initialzustand des Parkhauses:");
        printStatus(service);

        // 1. 10 Fahrzeuge einparken
        System.out.println("\n==> Parke 10 Fahrzeuge ein...");
        service.parkeFahrzeuge(10);
        printStatus(service);

        // 2. Weitere 20 Fahrzeuge einparken
        System.out.println("\n==> Parke weitere 20 Fahrzeuge ein...");
        service.parkeFahrzeuge(20);
        printStatus(service);

        // 3. 5 Fahrzeuge wieder ausparken
        System.out.println("\n==> Entferne 5 Fahrzeuge...");
        var allePlaetze = service.getParkhaus().getEtagen().stream()
                .flatMap(etage -> etage.getParkplaetze().stream())
                .filter(platz -> !platz.istFrei())
                .limit(5)
                .toList();

        allePlaetze.forEach(service::verlasseParkplatz);
        printStatus(service);

        // 4. Nochmals 15 Fahrzeuge einparken
        System.out.println("\n==> Parke nochmals 15 Fahrzeuge ein...");
        service.parkeFahrzeuge(15);
        printStatus(service);
    }

    private static void printStatus(ParkingService service) {
        long belegte = service.getAnzahlBelegterParkplaetze();
        long gesamt = service.getParkhaus().getEtagen().stream()
                .mapToLong(etage -> etage.getParkplaetze().size())
                .sum();
        long frei = gesamt - belegte;
        double auslastung = ((double) belegte / gesamt) * 100;

        System.out.println("-- model.Parkhaus-Status --");
        System.out.println("Belegte Plätze : " + belegte);
        System.out.println("Freie Plätze   : " + frei);
        System.out.printf("Auslastung     : %.2f %%\n", auslastung);
    }
}
