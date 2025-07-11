package gui;

import model.Etage;
import model.Parkplatz;
import service.ParkingService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ParkingGui extends JFrame {

    private final ParkingService service = ParkingService.erzeugeStandardParkhaus();
    private final JLabel statusLabel = new JLabel();
    private final JPanel parkhausPanel = new JPanel();

    private final JComboBox<Integer> cmbAmount = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 10, 100});
    private final JCheckBox belegCheckBox = new JCheckBox("Beleg");

    public ParkingGui() {
        setTitle("Intelligente Parkplatzsuche");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1400, 500);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton parkenButton = new JButton("Fahrzeuge einparken");

        topPanel.add(new JLabel("Anzahl Fahrzeuge:"));
        topPanel.add(cmbAmount);
        topPanel.add(parkenButton);
        topPanel.add(belegCheckBox);
        topPanel.add(statusLabel);

        parkenButton.addActionListener(this::handleEinparken);

        add(topPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(parkhausPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        updateView();
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

        updateView();
        zeigeBeleg(belegtePlaetze);
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

    private void updateView() {
        parkhausPanel.removeAll();
        parkhausPanel.setLayout(new BoxLayout(parkhausPanel, BoxLayout.Y_AXIS));

        service.getParkhaus().getEtagen().stream()
                .sorted(Comparator.comparingInt(Etage::getNummer).reversed())
                .forEach(etage -> {
                    JPanel etagenPanel = createEtagePanel(etage);
                    parkhausPanel.add(etagenPanel);
                });

        updateSatusLabel();
        parkhausPanel.revalidate();
        parkhausPanel.repaint();
    }

    private void updateSatusLabel() {
        long frei = service.getAnzahlFreierParkplaetze();
        long belegt = service.getAnzahlBelegterParkplaetze();
        double auslastung = belegt * 100.0 / (belegt + frei);
        statusLabel.setText(String.format("Freie Plätze: %d | Belegte Plätze: %d | Auslastung: %.2f%%",
                frei, belegt, auslastung));
    }

    private JPanel createEtagePanel(Etage etage) {
        JPanel etagenPanel = new JPanel(new GridLayout(1, 50, 2, 2));
        long frei = etage.getParkplaetze().stream().filter(Parkplatz::istFrei).count();
        long gesamt = etage.getParkplaetze().size();
        double auslastungEtage = 100.0 * (gesamt - frei) / gesamt;
        String titel = String.format("Etage %d – Auslastung: %6.2f %%", etage.getNummer(), auslastungEtage);
        etagenPanel.setBorder(BorderFactory.createTitledBorder(titel));

        for (Parkplatz platz : etage.getParkplaetze()) {
            JButton platzButton = createPlatzButton(etage, platz);
            etagenPanel.add(platzButton);
        }
        return etagenPanel;
    }

    private JButton createPlatzButton(Etage etage, Parkplatz platz) {
        JButton platzButton = new JButton();
        platzButton.setOpaque(true);
        platzButton.setBorderPainted(false);
        platzButton.setBackground(platz.istFrei() ? Color.GREEN : Color.RED);
        platzButton.setPreferredSize(new Dimension(20, 20));
        platzButton.setToolTipText("Etage " + etage.getNummer() + "Platz #" + platz.getNummer());
        platzButton.setToolTipText(String.format("Etage %d - Platz #%s", etage.getNummer(), platz.getNummer()));
        platzButton.addActionListener(ev -> {
            if (!platz.istFrei()) {
                service.verlasseParkplatz(platz);
                updateView();
            }
        });
        return platzButton;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ParkingGui::new);
    }
}
