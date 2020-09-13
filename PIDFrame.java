import javax.swing.*;
import java.awt.*;

public class PIDFrame extends JFrame {
    private PID pid;

    private final int padding = 10;

    private JPanel graph;
    private JLabel pLabel;
    private JSpinner pSpinner;
    private JLabel iLabel;
    private JSpinner iSpinner;
    private JLabel dLabel;
    private JSpinner dSpinner;
    private JLabel fLabel;
    private JSpinner fSpinner;

    public PIDFrame(PID pid) {
        super("PID Tuner");

        this.pid = pid;

        Container contentPane = getContentPane();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(300, 500));
        
        contentPane.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(padding/2, padding/2, padding/2, padding/2);

        //Graph
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;

        graph = new JPanel();
        graph.setBackground(new Color(1.0f, 0.75f, 0.5f));
        add(graph, gbc);

        //Labels
        gbc.gridx = 0;
        gbc.weighty = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;

        pLabel = new JLabel("P-Gain:");
        gbc.gridy = 1;
        add(pLabel, gbc);
        
        iLabel = new JLabel("I-Gain:");
        gbc.gridy = 2;
        add(iLabel, gbc);

        dLabel = new JLabel("D-Gain:");
        gbc.gridy = 3;
        add(dLabel, gbc);

        fLabel = new JLabel("F-Gain:");
        gbc.gridy = 4;
        add(fLabel, gbc);

        //Spinners
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        pSpinner = new JSpinner(new SpinnerNumberModel(pid.getP(), 0, Double.POSITIVE_INFINITY, 0.1));
        gbc.gridy = 1;
        add(pSpinner, gbc);

        iSpinner = new JSpinner(new SpinnerNumberModel(pid.getI(), 0, Double.POSITIVE_INFINITY, 0.1));
        gbc.gridy = 2;
        add(iSpinner, gbc);

        dSpinner = new JSpinner(new SpinnerNumberModel(pid.getD(), 0, Double.POSITIVE_INFINITY, 0.1));
        gbc.gridy = 3;
        add(dSpinner, gbc);

        fSpinner = new JSpinner(new SpinnerNumberModel(pid.getF(), 0, Double.POSITIVE_INFINITY, 0.1));
        gbc.gridy = 4;
        add(fSpinner, gbc);

        pack();
        setVisible(true);
    }
}