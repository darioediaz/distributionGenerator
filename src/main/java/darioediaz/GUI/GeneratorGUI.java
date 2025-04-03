package darioediaz.GUI;

import darioediaz.Interfaces.DistributionGenerator;
import darioediaz.Logic.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.statistics.HistogramDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class GeneratorGUI extends JFrame {
	private JComboBox<String> distributionBox;
	private JTextField sampleSizeField, param1Field, param2Field, binsField;
	private JButton generateButton;
	private JPanel chartPanel;
	private JLabel param1Label, param2Label;

	public GeneratorGUI() {
		setLookAndFeel();

		setTitle("Generador de Distribuciones Aleatorias");
		setSize(650, 550);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		// Panel de entrada estilizado
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new GridBagLayout());
		inputPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.gridx = 0; gbc.gridy = 0;

		// Selección de distribución
		inputPanel.add(new JLabel("Seleccione distribución:"), gbc);
		gbc.gridx = 1;
		distributionBox = new JComboBox<>(new String[]{"Uniforme", "Exponencial", "Normal", "Normal Box-Muller"});
		inputPanel.add(distributionBox, gbc);

		// Tamaño de muestra
		gbc.gridx = 0; gbc.gridy++;
		inputPanel.add(new JLabel("Tamaño de la muestra:"), gbc);
		gbc.gridx = 1;
		sampleSizeField = new JTextField();
		inputPanel.add(sampleSizeField, gbc);

		// Intervalos del histograma
		gbc.gridx = 0; gbc.gridy++;
		inputPanel.add(new JLabel("Número de intervalos:"), gbc);
		gbc.gridx = 1;
		binsField = new JTextField();
		inputPanel.add(binsField, gbc);

		// Parámetros de distribución
		gbc.gridx = 0; gbc.gridy++;
		param1Label = new JLabel();
		inputPanel.add(param1Label, gbc);
		gbc.gridx = 1;
		param1Field = new JTextField();
		inputPanel.add(param1Field, gbc);

		gbc.gridx = 0; gbc.gridy++;
		param2Label = new JLabel();
		inputPanel.add(param2Label, gbc);
		gbc.gridx = 1;
		param2Field = new JTextField();
		inputPanel.add(param2Field, gbc);

		// Botón estilizado
		gbc.gridx = 0; gbc.gridy++;
		gbc.gridwidth = 2;
		generateButton = new JButton("Generar Distribución");
		generateButton.setBackground(new Color(30, 144, 255));
		generateButton.setForeground(Color.WHITE);
		generateButton.setFocusPainted(false);
		inputPanel.add(generateButton, gbc);

		add(inputPanel, BorderLayout.NORTH);

		// Panel de gráfico con fondo oscuro
		chartPanel = new JPanel();
		chartPanel.setBackground(Color.DARK_GRAY);
		add(chartPanel, BorderLayout.CENTER);

		// Eventos
		generateButton.addActionListener(e -> generateDistribution());
		distributionBox.addActionListener(e -> updateLabel());
		updateLabel();
	}

	private void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception ignored) {}
	}

	private void updateLabel() {
		int option = distributionBox.getSelectedIndex();
		switch (option) {
			case 0 -> {
				param1Label.setText("Límite Inferior (A):");
				param2Label.setText("Límite Superior (B):");
				param2Label.setVisible(true);
				param2Field.setVisible(true);
			}
			case 1 -> {
				param1Label.setText("Lambda:");
				param2Label.setVisible(false);
				param2Field.setVisible(false);
				param2Field.setText("0");
			}
			case 2, 3 -> {
				param1Label.setText("Media:");
				param2Label.setText("Desviación Estándar:");
				param2Label.setVisible(true);
				param2Field.setVisible(true);
			}
		}
		revalidate();
		repaint();
	}

	private void generateDistribution() {
		try {
			if (sampleSizeField.getText().isEmpty() || binsField.getText().isEmpty()) {
				JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			int sampleSize = Integer.parseInt(sampleSizeField.getText());
			int bins = Integer.parseInt(binsField.getText());

			Map<Integer, DistributionGenerator> distributions = new HashMap<>();
			int option = distributionBox.getSelectedIndex();

			switch (option) {
				case 0, 2, 3 -> {
					if (param1Field.getText().isEmpty() || param2Field.getText().isEmpty()) {
						JOptionPane.showMessageDialog(this, "Por favor, ingrese los parámetros necesarios.", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				case 1 -> {
					if (param1Field.getText().isEmpty()) {
						JOptionPane.showMessageDialog(this, "Por favor, ingrese Lambda.", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
			}

			distributions.put(0, new Uniform(
					Double.parseDouble(param1Field.getText()),
					Double.parseDouble(param2Field.getText())));

			distributions.put(1, new Exponential(Double.parseDouble(param1Field.getText())));

			distributions.put(2, new NormalConvolution(
					Double.parseDouble(param1Field.getText()),
					Double.parseDouble(param2Field.getText())));

			distributions.put(3, new NormalBoxMuller(
					Double.parseDouble(param1Field.getText()),
					Double.parseDouble(param2Field.getText())));

			double[] samples = distributions.get(option).generateSamples(sampleSize);
			showHistogram(samples, bins);

		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Ingrese valores numéricos válidos.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void showHistogram(double[] data, int bins) {
		HistogramDataset dataset = new HistogramDataset();
		dataset.addSeries("Frecuencia", data, bins);

		JFreeChart histogram = ChartFactory.createHistogram(
				"Histograma de Frecuencias", "Valor", "Frecuencia",
				dataset, PlotOrientation.VERTICAL, false, true, false);

		// Cambiar colores del fondo
		histogram.setBackgroundPaint(new Color(150, 150, 150)); // Fondo oscuro
		histogram.getPlot().setBackgroundPaint(new Color(230, 230, 230)); // Fondo claro del gráfico

		// Personalizar barras del histograma
		XYPlot plot = (XYPlot) histogram.getPlot();
		XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();
		renderer.setSeriesPaint(0, new Color(50, 150, 255));
		renderer.setDrawBarOutline(false); // Quitar borde de las barras
		renderer.setShadowVisible(false); // Sombras en las barras

		// Ajustar eje X dinámicamente
		double minValue = Double.MAX_VALUE;
		double maxValue = Double.MIN_VALUE;
		for (double value : data) {
			if (value < minValue) minValue = value;
			if (value > maxValue) maxValue = value;
		}
		plot.getDomainAxis().setRange(minValue, maxValue);

		// Personalizar fuentes y etiquetas
		Font labelFont = new Font("Arial", Font.BOLD, 12);
		histogram.getTitle().setFont(new Font("Arial", Font.BOLD, 16));
		plot.getDomainAxis().setLabelFont(labelFont);
		plot.getRangeAxis().setLabelFont(labelFont);
		plot.getDomainAxis().setTickLabelFont(labelFont);
		plot.getRangeAxis().setTickLabelFont(labelFont);

		// Mostrar líneas de cuadrícula
		plot.setDomainGridlinesVisible(true);
		plot.setRangeGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.GRAY);
		plot.setRangeGridlinePaint(Color.GRAY);

		ChartPanel chart = new ChartPanel(histogram);
		chart.setPreferredSize(new Dimension(600, 270));

		chartPanel.removeAll();
		chartPanel.add(chart);
		chartPanel.revalidate();
		chartPanel.repaint();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new GeneratorGUI().setVisible(true));
	}
}
