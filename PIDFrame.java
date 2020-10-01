import java.util.*;
import java.math.*;
import java.text.DecimalFormat;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

public class PIDFrame extends JFrame {
    private PID pid;

    private final int padding = 15;

    private JPanel contentPane;

    private Graph graph;

    private ArrayList<Double> timeData;
    private ArrayList<Double> actualData;
    private ArrayList<Double> errorData;
    private ArrayList<Double> setpointData;
    private double lowerLimit;
    private double upperLimit;

    private JPanel currentValPanel;
    private JLabel currentTime;
    private JLabel currentError;
    private JLabel currentIntegral;
    private JLabel currentDerivative;

    private JPanel successPanel;
    private JLabel eSuccessLabel;
    private JSpinner eSuccessSpinner;
    private JLabel eSuccessValue;
    private JLabel tSuccessLabel;
    private JSpinner tSuccessSpinner;
    private JLabel tSuccessValue;

    private JPanel pidPanel;
    private JLabel pLabel;
    private JSpinner pSpinner;
    private JLabel iLabel;
    private JSpinner iSpinner;
    private JLabel dLabel;
    private JSpinner dSpinner;
    private JLabel fLabel;
    private JSpinner fSpinner;
    private ChangeListener spinnerChangeListener;

    private JPanel buttonPanel;
    private JButton resetValuesButton;
    private JButton updateValuesButton;

    public PIDFrame(final PID pid) {
        super("PID Tuner");

        this.pid = pid;
        pid.setFrame(this);

        timeData = new ArrayList<>();
        actualData = new ArrayList<>();
        setpointData = new ArrayList<>();
        errorData = new ArrayList<>();
        upperLimit = 1.0;
        lowerLimit = -0.1;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(400, 700));
        setResizable(false);
        
        contentPane = new JPanel();
        contentPane.setBorder(BorderFactory.createEmptyBorder(padding/2, padding/2, padding/2, padding/2));
        setContentPane(contentPane);
        contentPane.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        //Graph
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(padding/2, padding/2, padding/2, padding/2);

        graph = new Graph();
        contentPane.add(graph, gbc);

        //Current Values
        currentValPanel = new JPanel();
        currentValPanel.setLayout(new GridBagLayout());
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 0;
        gbc.insets = new Insets(0,0,0,0);
        contentPane.add(currentValPanel, gbc);

        currentTime = new JLabel("Time: 0.00 s", SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        currentValPanel.add(currentTime, gbc);

        currentError = new JLabel("Error: 0.00", SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 1;
        currentValPanel.add(currentError, gbc);

        currentIntegral = new JLabel("Integral: 0.00", SwingConstants.CENTER);
        gbc.gridx = 1;
        gbc.gridy = 0;
        currentValPanel.add(currentIntegral, gbc);
        
        currentDerivative = new JLabel("Derivative: 0.00", SwingConstants.CENTER);
        gbc.gridx = 1;
        gbc.gridy = 1;
        currentValPanel.add(currentDerivative, gbc);

        //Success Conditions
        successPanel = new JPanel();
        successPanel.setBorder(BorderFactory.createTitledBorder("Success Conditions"));
        successPanel.setLayout(new GridBagLayout());
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 0;
        gbc.insets = new Insets(padding/2, padding/2, padding/2, padding/2);
        contentPane.add(successPanel, gbc);

        eSuccessLabel = new JLabel("Max. Error:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        successPanel.add(eSuccessLabel, gbc);

        tSuccessLabel = new JLabel("Min. Time:");
        gbc.gridy = 1;
        successPanel.add(tSuccessLabel, gbc);

        eSuccessSpinner = new JSpinner(new SpinnerNumberModel(0.1, 0, Double.POSITIVE_INFINITY, 0.01));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1;
        successPanel.add(eSuccessSpinner, gbc);

        tSuccessSpinner = new JSpinner(new SpinnerNumberModel(1, 0, Double.POSITIVE_INFINITY, 0.1));
        gbc.gridy = 1;
        successPanel.add(tSuccessSpinner, gbc);

        eSuccessValue = new JLabel();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0;
        successPanel.add(eSuccessValue, gbc);

        tSuccessValue = new JLabel("0.000");
        gbc.gridy = 1;
        successPanel.add(tSuccessValue, gbc);

        //PID Labels
        pidPanel = new JPanel();
        pidPanel.setBorder(BorderFactory.createTitledBorder("PID Values"));
        pidPanel.setLayout(new GridBagLayout());
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weighty = 0;
        contentPane.add(pidPanel, gbc);

        pLabel = new JLabel("P-Gain:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        pidPanel.add(pLabel, gbc);
        
        iLabel = new JLabel("I-Gain:");
        gbc.gridy = 1;
        pidPanel.add(iLabel, gbc);

        dLabel = new JLabel("D-Gain:");
        gbc.gridy = 2;
        pidPanel.add(dLabel, gbc);

        fLabel = new JLabel("F-Gain:");
        gbc.gridy = 3;
        pidPanel.add(fLabel, gbc);

        //Spinners
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;

        spinnerChangeListener = new ChangeListener() {
            public void stateChanged(final ChangeEvent e) {
                resetValuesButton.setEnabled(true);
                updateValuesButton.setEnabled(true);
            } 
        };

        pSpinner = new JSpinner(new SpinnerNumberModel(pid.getP(), 0, Double.POSITIVE_INFINITY, 0.1));
        ((DefaultFormatter) ((JFormattedTextField) pSpinner.getEditor().getComponent(0)).getFormatter()).setCommitsOnValidEdit(true);
        pSpinner.addChangeListener(spinnerChangeListener);
        gbc.gridy = 0;
        pidPanel.add(pSpinner, gbc);

        iSpinner = new JSpinner(new SpinnerNumberModel(pid.getI(), 0, Double.POSITIVE_INFINITY, 0.1));
        ((DefaultFormatter) ((JFormattedTextField) iSpinner.getEditor().getComponent(0)).getFormatter()).setCommitsOnValidEdit(true);
        iSpinner.addChangeListener(spinnerChangeListener);
        gbc.gridy = 1;
        pidPanel.add(iSpinner, gbc);

        dSpinner = new JSpinner(new SpinnerNumberModel(pid.getD(), 0, Double.POSITIVE_INFINITY, 0.1));
        ((DefaultFormatter) ((JFormattedTextField) dSpinner.getEditor().getComponent(0)).getFormatter()).setCommitsOnValidEdit(true);
        dSpinner.addChangeListener(spinnerChangeListener);
        gbc.gridy = 2;
        pidPanel.add(dSpinner, gbc);

        fSpinner = new JSpinner(new SpinnerNumberModel(pid.getF(), 0, Double.POSITIVE_INFINITY, 0.1));
        ((DefaultFormatter) ((JFormattedTextField) fSpinner.getEditor().getComponent(0)).getFormatter()).setCommitsOnValidEdit(true);
        fSpinner.addChangeListener(spinnerChangeListener);
        gbc.gridy = 3;
        pidPanel.add(fSpinner, gbc);

        //Button Panel
        buttonPanel = new JPanel();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        pidPanel.add(buttonPanel, gbc);

        resetValuesButton = new JButton("Reset Values");
        resetValuesButton.setEnabled(false);
        resetValuesButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                pSpinner.setValue(pid.getP());
                iSpinner.setValue(pid.getI());
                dSpinner.setValue(pid.getD());
                fSpinner.setValue(pid.getF());
                resetValuesButton.setEnabled(false);
                updateValuesButton.setEnabled(false);
            }
        });
        buttonPanel.add(resetValuesButton);

        updateValuesButton = new JButton("Update Values");
        updateValuesButton.setEnabled(false);
        updateValuesButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                pid.setP((double) pSpinner.getValue());
                pid.setI((double) iSpinner.getValue());
                pid.setD((double) dSpinner.getValue());
                pid.setF((double) fSpinner.getValue());
                resetValuesButton.setEnabled(false);
                updateValuesButton.setEnabled(false);
            }
        });
        buttonPanel.add(updateValuesButton);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void updateData(double time, double actual, double setpoint, double integral, double derivative) {
        timeData.add(time);
        actualData.add(actual);
        setpointData.add(setpoint);
        errorData.add(setpoint - actual);

        currentTime.setText("Time: " + new DecimalFormat("######0.00").format(time - (timeData.size()>0 ? timeData.get(0) : 0)) + " s");
        currentError.setText("Error: " + new DecimalFormat("######0.00").format(setpoint - actual));
        currentIntegral.setText("Integral: " + new DecimalFormat("######0.00").format(integral));
        currentDerivative.setText("Derivative: " + new DecimalFormat("######0.00").format(derivative));

        if(actual-graph.unitSubdivision < lowerLimit) {
            lowerLimit = actual-graph.unitSubdivision;
        } else if(actual+graph.unitSubdivision > upperLimit) {
            upperLimit = actual+graph.unitSubdivision;
        }

        if(setpoint-graph.unitSubdivision < lowerLimit) {
            lowerLimit = setpoint-graph.unitSubdivision;
        } else if(setpoint+graph.unitSubdivision > upperLimit) {
            upperLimit = setpoint+graph.unitSubdivision;
        }

        if(setpoint-actual-graph.unitSubdivision < lowerLimit) {
            lowerLimit = setpoint-actual-graph.unitSubdivision;
        } else if(setpoint-actual+graph.unitSubdivision > upperLimit) {
            upperLimit = setpoint-actual+graph.unitSubdivision;
        }

        graph.repaint();
    }

    private class Graph extends JPanel {
        public int graphWidth;
        public int graphHeight;
        public final int labelSpace = 30;

        public double maxTime;
        public double pixelsPerSecond;
        public double timeSubdivision;
        public double pixelsPerUnit;
        public double unitSubdivision;
        public int zeroPixel;

        @Override
        public void paintComponent(final Graphics g) {
            super.paintComponent(g);

            graphWidth = getWidth() - labelSpace;
            graphHeight = getHeight() - labelSpace;

            final Graphics2D g2 = (Graphics2D) g;

            setDimensions();

            g2.setColor(Color.WHITE);
            g2.fillRect(labelSpace, 0, graphWidth, graphHeight);
            
            g2.setStroke(new BasicStroke(1.0f));
            for (int i = 0; pixelsPerSecond*timeSubdivision*i < graphWidth; i++) {
                g2.setColor(Color.GRAY);
                g2.drawLine((int) (labelSpace + pixelsPerSecond*timeSubdivision*i), 0, (int) (labelSpace + pixelsPerSecond*timeSubdivision*i), graphHeight);

                g2.setColor(Color.BLACK);
                String str = String.valueOf(Math.round(timeSubdivision*i));
                g2.drawString(str, (int) (labelSpace + pixelsPerSecond*timeSubdivision*i - g2.getFontMetrics().stringWidth(str)/2), graphHeight+5+g2.getFontMetrics().getHeight());
            }
            for (int i = 0; zeroPixel - pixelsPerUnit*unitSubdivision*i > 0; i++) {
                g2.setColor(Color.GRAY);
                g2.drawLine(labelSpace, (int) (zeroPixel - pixelsPerUnit*unitSubdivision*i), labelSpace+graphWidth, (int) (zeroPixel - pixelsPerUnit*unitSubdivision*i));
            
                g2.setColor(Color.BLACK);
                String str = new BigDecimal(String.valueOf(unitSubdivision)).multiply(new BigDecimal(String.valueOf(i))).toPlainString();
                g2.drawString(str, (int) (labelSpace - g2.getFontMetrics().stringWidth(str) - 10), (int) (zeroPixel - pixelsPerUnit*unitSubdivision*i+g2.getFontMetrics().getHeight()/2));
            }
            for (int i = 1; zeroPixel + pixelsPerUnit*unitSubdivision*i < graphHeight; i++) {
                g2.setColor(Color.GRAY);
                g2.drawLine(labelSpace, (int) (zeroPixel + pixelsPerUnit*unitSubdivision*i), labelSpace+graphWidth, (int) (zeroPixel + pixelsPerUnit*unitSubdivision*i));

                g2.setColor(Color.BLACK);
                String str = "-" + new BigDecimal(String.valueOf(unitSubdivision)).multiply(new BigDecimal(String.valueOf(i))).toPlainString();
                g2.drawString(str, (int) (labelSpace - g2.getFontMetrics().stringWidth(str) - 10), (int) (zeroPixel + pixelsPerUnit*unitSubdivision*i+g2.getFontMetrics().getHeight()/2));
            }

            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2.0f));
            g2.drawLine(labelSpace, 0, labelSpace, graphHeight);
            g2.drawLine(labelSpace, zeroPixel, labelSpace+graphWidth, zeroPixel);

            final int[] timeCoors = timeData.stream().mapToInt(x -> (int) (labelSpace + pixelsPerSecond * (x-timeData.get(0)))).toArray();

            g2.setColor(new Color(0f, 0.75f, 0f));
            g2.drawPolyline(
                timeCoors,
                setpointData.stream().mapToInt(x -> (int) (zeroPixel - pixelsPerUnit*x)).toArray(),
                timeCoors.length
            );
            g2.setColor(Color.BLUE);
            g2.drawPolyline(
                timeCoors,
                actualData.stream().mapToInt(x -> (int) (zeroPixel - pixelsPerUnit*x)).toArray(),
                timeCoors.length
            );
            g2.setColor(Color.RED);
            g2.drawPolyline(
                timeCoors,
                errorData.stream().mapToInt(x -> (int) (zeroPixel - pixelsPerUnit*x)).toArray(),
                timeCoors.length
            );
        }

        private void setDimensions() {
            maxTime = timeData.size()>0 ? Math.max(timeData.get(timeData.size()-1) - timeData.get(0) + 1, 5) : 5;
            pixelsPerSecond = graphWidth / maxTime;
            timeSubdivision = Math.pow(10, Math.ceil(Math.log10(maxTime/2))-1);

            pixelsPerUnit = graphHeight / (upperLimit - lowerLimit);
            zeroPixel = (int) Math.round(upperLimit * pixelsPerUnit);
            unitSubdivision = Math.pow(10, Math.ceil(Math.log10((upperLimit - lowerLimit)/2))-1);
        }
    }
}