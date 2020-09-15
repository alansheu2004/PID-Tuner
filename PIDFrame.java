import java.util.*;
import java.util.stream.*;
import java.math.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class PIDFrame extends JFrame {
    private final PID pid;

    private final int padding = 15;

    private final Graph graph;

    private final ArrayList<Double> timeData;
    private final ArrayList<Double> actualData;
    private final ArrayList<Double> errorData;
    private final ArrayList<Double> setpointData;
    private double lowerLimit;
    private double upperLimit;

    private final JLabel pLabel;
    private final JSpinner pSpinner;
    private final JLabel iLabel;
    private final JSpinner iSpinner;
    private final JLabel dLabel;
    private final JSpinner dSpinner;
    private final JLabel fLabel;
    private final JSpinner fSpinner;
    private final ChangeListener spinnerChangeListener;

    private final JPanel buttonPanel;
    private final JButton resetValuesButton;
    private final JButton updateValuesButton;

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

        final Container contentPane = getContentPane();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(400, 600));
        setResizable(false);
        
        contentPane.setLayout(new GridBagLayout());
        final GridBagConstraints gbc = new GridBagConstraints();

        //Graph
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(padding, padding, padding, padding);

        graph = new Graph();
        add(graph, gbc);

        //Labels
        gbc.gridx = 0;
        gbc.weighty = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.insets = new Insets(padding/2, padding/2, padding/2, padding/2);

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

        spinnerChangeListener = new ChangeListener() {
            public void stateChanged(final ChangeEvent e) {
                resetValuesButton.setEnabled(true);
                updateValuesButton.setEnabled(true);
            } 
        };

        pSpinner = new JSpinner(new SpinnerNumberModel(pid.getP(), 0, Double.POSITIVE_INFINITY, 0.1));
        ((DefaultFormatter) ((JFormattedTextField) pSpinner.getEditor().getComponent(0)).getFormatter()).setCommitsOnValidEdit(true);
        pSpinner.addChangeListener(spinnerChangeListener);
        gbc.gridy = 1;
        add(pSpinner, gbc);

        iSpinner = new JSpinner(new SpinnerNumberModel(pid.getI(), 0, Double.POSITIVE_INFINITY, 0.1));
        ((DefaultFormatter) ((JFormattedTextField) iSpinner.getEditor().getComponent(0)).getFormatter()).setCommitsOnValidEdit(true);
        iSpinner.addChangeListener(spinnerChangeListener);
        gbc.gridy = 2;
        add(iSpinner, gbc);

        dSpinner = new JSpinner(new SpinnerNumberModel(pid.getD(), 0, Double.POSITIVE_INFINITY, 0.1));
        ((DefaultFormatter) ((JFormattedTextField) dSpinner.getEditor().getComponent(0)).getFormatter()).setCommitsOnValidEdit(true);
        dSpinner.addChangeListener(spinnerChangeListener);
        gbc.gridy = 3;
        add(dSpinner, gbc);

        fSpinner = new JSpinner(new SpinnerNumberModel(pid.getF(), 0, Double.POSITIVE_INFINITY, 0.1));
        ((DefaultFormatter) ((JFormattedTextField) fSpinner.getEditor().getComponent(0)).getFormatter()).setCommitsOnValidEdit(true);
        fSpinner.addChangeListener(spinnerChangeListener);
        gbc.gridy = 4;
        add(fSpinner, gbc);

        //Button Panel
        buttonPanel = new JPanel();

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

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        pack();
        setVisible(true);
    }

    public void updateData(final double time, final double actual, final double setpoint) {
        timeData.add(time);
        actualData.add(actual);
        setpointData.add(setpoint);
        errorData.add(setpoint - actual);

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
                String str = new BigDecimal(String.valueOf(timeSubdivision)).multiply(new BigDecimal(String.valueOf(i))).toPlainString();
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

            g2.setColor(Color.GREEN);
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
            maxTime = timeData.size()>0 ? Math.max(timeData.get(timeData.size()-1) - timeData.get(0), 1) : 1;
            pixelsPerSecond = graphWidth / maxTime;
            timeSubdivision = Math.round(Math.pow(10, Math.ceil(Math.log10(maxTime/2))-1) * 10) / 10.0;

            pixelsPerUnit = graphHeight / (upperLimit - lowerLimit);
            zeroPixel = (int) Math.round(upperLimit * pixelsPerUnit);
            unitSubdivision = Math.round(Math.pow(10, Math.ceil(Math.log10((upperLimit - lowerLimit)/2))-1) * 10) / 10.0;
        }
    }
}