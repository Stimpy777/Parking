package gui;

import model.Etage;
import model.Parkplatz;
import service.ParkingService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ParkingGui extends JFrame {

    private final ParkingService service = ParkingService.erzeugeStandardParkhaus();
    private final JPanel parkhausPanel = new JPanel();
    private final JComboBox<Integer> cmbAmount = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 10, 100});
    private final JCheckBox belegCheckBox = new JCheckBox("Beleg");
    private final List<String> historie = new ArrayList<>();
    private int buchungsnummer = 1;
    private final DateTimeFormatter formatter= DateTimeFormatter.ofPattern("yyyy-MM-dd 'um' HH:mm:ss");

    public ParkingGui() {
        initGui();
    }

    private void initGui() {
        setTitle("Intelligente Parkplatzsuche");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1400, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());


        JButton btnPark = new JButton("Fahrzeuge einparken");
        JButton btnHistorie = new JButton("Historie");

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Anzahl Fahrzeuge:"));
        topPanel.add(cmbAmount);
        topPanel.add(btnPark);
        topPanel.add(belegCheckBox);
        topPanel.add(btnHistorie); // <<<<< NEU

        btnPark.addActionListener(this::handleEinparken);
        btnHistorie.addActionListener(e -> zeigeHistorieDialog());

        add(topPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(parkhausPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        ansichtAktualisieren();
        setResizable(false);
        setVisible(true);
    }

    private void handleEinparken(ActionEvent e) {
        Integer selected = (Integer) cmbAmount.getSelectedItem();
        int anzahl = (selected != null) ? selected : 1;

        List<Parkplatz> belegtePlaetze;
        try {
            belegtePlaetze = service.parkeFahrzeuge(anzahl);
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this,
                    "Nicht genügend freie Parkplätze für " + anzahl + " Fahrzeuge!",
                    "Info", JOptionPane.WARNING_MESSAGE);
            return;
        }

        historieAnlegen(belegtePlaetze);
        ansichtAktualisieren();
        zeigeBeleg(belegtePlaetze);
    }

    private void historieAnlegen(List<Parkplatz> belegtePlaetze) {
        LocalDateTime jetzt = LocalDateTime.now();
        historie.add(String.format("%s - Buchung: %s | Gesamt: %s ",jetzt.format(formatter), buchungsnummer++, belegtePlaetze.size() ));
        for (Parkplatz platz : belegtePlaetze) {
            String eintrag = String.format("Platz #%s ", platz.getNummer());
            historie.add(eintrag);
        }
    }

    private void zeigeBeleg(List<Parkplatz> belegtePlaetze) {
        if (belegCheckBox.isSelected() && belegtePlaetze != null && !belegtePlaetze.isEmpty()) {
            StringBuilder sb = new StringBuilder("Neu belegte Parkplätze:\n");
            service.getParkhaus().getEtagen().stream()
                    .sorted(Comparator.comparingInt(Etage::getNummer).reversed())
                    .forEach(etage -> {
                        List<Integer> plaetzeInEtage = belegtePlaetze.stream()
                                .filter(p -> etage.getParkplaetze().contains(p))
                                .map(Parkplatz::getNummer)
                                .sorted()
                                .toList();
                        if (!plaetzeInEtage.isEmpty()) {
                            sb.append("Etage ").append(etage.getNummer()).append(": ");
                            sb.append(plaetzeInEtage.stream()
                                    .map(String::valueOf)
                                    .collect(Collectors.joining(", ")));
                            sb.append("\n");
                        }
                    });

            JOptionPane.showMessageDialog(this, sb.toString(), "Neue belegte Parkplätze", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void zeigeHistorieDialog() {
        if (historie.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Noch keine Buchungen vorhanden.", "Historie", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        for (String element : historie) {
            area.append(element + "\n");
        }
        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        JOptionPane.showMessageDialog(this, scrollPane, "Buchungshistorie", JOptionPane.INFORMATION_MESSAGE);
    }

    private void ansichtAktualisieren() {
        parkhausPanel.removeAll();
        parkhausPanel.setLayout(new BoxLayout(parkhausPanel, BoxLayout.Y_AXIS));

        service.getParkhaus().getEtagen().stream()
                .sorted(Comparator.comparingInt(Etage::getNummer).reversed())
                .forEach(etage -> {
                    JPanel etagenPanel = erstelleEtagenPanel(etage);
                    parkhausPanel.add(etagenPanel);
                });

        titelAktualisieren();
        parkhausPanel.revalidate();
        parkhausPanel.repaint();
    }

    private void titelAktualisieren() {
        long frei = service.getAnzahlFreierParkplaetze();
        long belegt = service.getAnzahlBelegterParkplaetze();
        double auslastung = belegt * 100.0 / (belegt + frei);
        setTitle(String.format(
                "Intelligente Parkplatzsuche | Freie Plätze: %4d | Belegte Plätze: %4d | Auslastung: %6.2f%%",
                frei, belegt, auslastung));
    }

    private JPanel erstelleEtagenPanel(Etage etage) {
        JPanel etagenPanel = new JPanel(new GridLayout(1, 50, 2, 2));
        long frei = etage.getParkplaetze().stream().filter(Parkplatz::istFrei).count();
        long gesamt = etage.getParkplaetze().size();
        double auslastungEtage = 100.0 * (gesamt - frei) / gesamt;
        String titel = String.format("Etage %d – Auslastung: %6.2f %%", etage.getNummer(), auslastungEtage);
        etagenPanel.setBorder(BorderFactory.createTitledBorder(titel));

        for (Parkplatz platz : etage.getParkplaetze()) {
            JButton platzButton = erzeugePlatzButton(etage, platz);
            etagenPanel.add(platzButton);
        }
        return etagenPanel;
    }
    private ImageIcon createCarFrontViewIcon() {
        BufferedImage img = new BufferedImage(18, 18, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Karosserie (Hauptkörper)
        g2.setColor(new Color(220, 0, 0)); // Rot
        g2.fillRoundRect(4, 6, 10, 8, 4, 4);

        // Windschutzscheibe
        g2.setColor(new Color(180, 220, 255)); // Hellblau
        g2.fillRoundRect(5, 4, 8, 4, 3, 3);

        // Scheinwerfer
        g2.setColor(Color.WHITE);
        g2.fillOval(3, 7, 3, 3);  // Linker Scheinwerfer
        g2.fillOval(12, 7, 3, 3); // Rechter Scheinwerfer

        // Scheinwerfer-Lichtkegel (gelb)
        g2.setColor(Color.YELLOW);
        g2.fillOval(3, 8, 2, 2);
        g2.fillOval(13, 8, 2, 2);

        // Stoßstange
        g2.setColor(new Color(100, 100, 100)); // Grau
        g2.fillRect(2, 14, 14, 1);

        // Kennzeichen
        g2.setColor(Color.WHITE);
        g2.fillRect(7, 12, 4, 2);
        g2.setColor(Color.BLACK);
        g2.drawRect(7, 12, 4, 2);

        // Reifen/Räder (von unten sichtbar)
        g2.setColor(Color.BLACK);
        g2.fillRect(2, 15, 3, 2);  // Linker Reifen
        g2.fillRect(13, 15, 3, 2); // Rechter Reifen

        // Felgen-Details
        g2.setColor(new Color(180, 180, 180)); // Silber
        g2.fillOval(2, 15, 3, 2);
        g2.fillOval(13, 15, 3, 2);

        // Umriss für bessere Erkennbarkeit
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(0.8f));
        g2.drawRoundRect(4, 6, 10, 8, 4, 4); // Karosserie-Umriss
        g2.drawRoundRect(5, 4, 8, 4, 3, 3);  // Windschutzscheibe-Umriss

        // Motorhaube-Linie
        g2.setColor(new Color(150, 0, 0)); // Dunkleres Rot
        g2.drawLine(6, 10, 12, 10);

        g2.dispose();
        return new ImageIcon(img);
    }

    private JButton erzeugePlatzButton(Etage etage, Parkplatz platz) {
        JButton btn = new JButton();

        if (!platz.istFrei()) {
            btn.setIcon(createCarFrontViewIcon());
        } else {
            btn.setIcon(null);
        }
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);
        btn.setBackground(Color.WHITE);
        btn.setPreferredSize(new Dimension(20, 20));
        btn.setToolTipText(String.format("Etage %d - Platz #%s", etage.getNummer(), platz.getNummer()));

        btn.addActionListener(ev -> {
            if (!platz.istFrei()) {
                service.verlasseParkplatz(platz);
                LocalDateTime jetzt = LocalDateTime.now();
                historie.add(String.format("%s - Frei #%s", jetzt.format(formatter), platz.getNummer()));
                ansichtAktualisieren();
            }
        });
        return btn;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(ParkingGui::new);
    }
}
