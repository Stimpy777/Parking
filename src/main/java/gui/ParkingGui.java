package gui;

import model.Etage;
import model.Parkplatz;
import service.ParkingService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Comparator;

public class ParkingGui extends JFrame {

    private final ParkingService service = ParkingService.erzeugeStandardParkhaus();
    private final JLabel statusLabel = new JLabel();
    private final JPanel parkhausPanel = new JPanel();

    private final JComboBox<Integer> cmbAmount = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 10});

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
        int anzahl = (selected != null) ? selected : 1;  // fallback to 1 or any default

        try {
            service.parkeFahrzeuge(anzahl);
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this,
                    "Nicht genügend freie Parkplätze für " + anzahl + " Fahrzeuge!",
                    "Info", JOptionPane.WARNING_MESSAGE);
        }
        updateView();
    }

    private void updateView() {
        parkhausPanel.removeAll();
        parkhausPanel.setLayout(new BoxLayout(parkhausPanel, BoxLayout.Y_AXIS));

        service.getParkhaus().getEtagen().stream()
                .sorted(Comparator.comparingInt(Etage::getNummer).reversed()) // Etagen absteigend sortieren
                .forEach(etage -> {
                    JPanel etagenPanel = new JPanel(new GridLayout(1, 50, 2, 2));
                    etagenPanel.setBorder(BorderFactory.createTitledBorder("Etage " + etage.getNummer()));

                    for (Parkplatz platz : etage.getParkplaetze()) {
                        JButton platzButton = createPlatzButton(etage, platz);
                        etagenPanel.add(platzButton);
                    }

                    parkhausPanel.add(etagenPanel);
                });

        long frei = service.getAnzahlFreierParkplaetze();
        long belegt = service.getAnzahlBelegterParkplaetze();
        double auslastung = belegt * 100.0 / (belegt + frei);
        statusLabel.setText(String.format("Freie Plätze: %d | Belegte Plätze: %d | Auslastung: %.2f%%",
                frei, belegt, auslastung));

        parkhausPanel.revalidate();
        parkhausPanel.repaint();
    }

    private JButton createPlatzButton(Etage etage, Parkplatz platz) {
        JButton platzButton = new JButton();
        platzButton.setOpaque(true);
        platzButton.setBorderPainted(false);
        platzButton.setBackground(platz.istFrei() ? Color.GREEN : Color.RED);
        platzButton.setPreferredSize(new Dimension(20, 20));
        platzButton.setToolTipText("Platz #" + platz.getNummer() + " auf Etage " + etage.getNummer());

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
