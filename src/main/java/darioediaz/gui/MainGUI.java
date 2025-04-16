package darioediaz.gui;

import darioediaz.interfaces.IDistributionGenerator;
import darioediaz.logic.Exponencial;
import darioediaz.logic.NormalConvolucion;
import darioediaz.logic.NormalBoxMuller;
import darioediaz.logic.Uniforme;
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

public class MainGUI extends JFrame {
	private final JComboBox<String> distributionBox;
	private final JTextField sampleSizeField, param1Field, param2Field;
	private final JComboBox<Integer> binsBox;
	private final JButton generateButton;
	private final JPanel chartPanel;
	private final JLabel param1Label, param2Label;
	private final JTextArea listArea;
	private final JScrollPane listScrollPane;
	private JTable frequencyTable;
	private JScrollPane tableScrollPane;

	public MainGUI() {
		setLookAndFeel();

		setTitle("Generador de Distribuciones Aleatorias");
		setSize(1200, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 5, 5, 5);

		// Panel izquierdo (tabla)
		JPanel leftPanel = new JPanel(new BorderLayout());
		frequencyTable = new JTable();
		tableScrollPane = new JScrollPane(frequencyTable);
		leftPanel.add(tableScrollPane, BorderLayout.CENTER);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridheight = 2;
		gbc.weightx = 0.25;
		gbc.weighty = 1;
		leftPanel.setPreferredSize(new Dimension(300, 700));
		add(leftPanel, gbc);

		// Panel central superior (inputs)
		JPanel inputPanel = new JPanel(new GridBagLayout());
		inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		GridBagConstraints inputGbc = new GridBagConstraints();
		inputGbc.fill = GridBagConstraints.HORIZONTAL;
		inputGbc.insets = new Insets(5, 5, 5, 5);
		inputGbc.gridx = 0;
		inputGbc.gridy = 0;

		inputPanel.add(new JLabel("Seleccione distribución:"), inputGbc);
		inputGbc.gridx = 1;
		distributionBox = new JComboBox<>(new String[]{"Uniforme", "Exponencial", "Normal Box-Muller", "Normal Convolucion"});
		inputPanel.add(distributionBox, inputGbc);

		inputGbc.gridx = 0;
		inputGbc.gridy++;
		inputPanel.add(new JLabel("Número de intervalos:"), inputGbc);
		inputGbc.gridx = 1;
		binsBox = new JComboBox<>(new Integer[]{10, 15, 20, 30});
		inputPanel.add(binsBox, inputGbc);

		inputGbc.gridx = 0;
		inputGbc.gridy++;
		inputPanel.add(new JLabel("Tamaño de la muestra:"), inputGbc);
		inputGbc.gridx = 1;
		sampleSizeField = new JTextField();
		inputPanel.add(sampleSizeField, inputGbc);

		inputGbc.gridx = 0;
		inputGbc.gridy++;
		param1Label = new JLabel();
		inputPanel.add(param1Label, inputGbc);
		inputGbc.gridx = 1;
		param1Field = new JTextField();
		inputPanel.add(param1Field, inputGbc);

		inputGbc.gridx = 0;
		inputGbc.gridy++;
		param2Label = new JLabel();
		inputPanel.add(param2Label, inputGbc);
		inputGbc.gridx = 1;
		param2Field = new JTextField();
		inputPanel.add(param2Field, inputGbc);

		inputGbc.gridx = 0;
		inputGbc.gridy++;
		inputGbc.gridwidth = 2;
		generateButton = new JButton("Generar Distribución");
		generateButton.setBackground(new Color(30, 144, 255));
		generateButton.setForeground(Color.WHITE);
		inputPanel.add(generateButton, inputGbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridheight = 1;
		gbc.weightx = 0.5;
		gbc.weighty = 0.3;
		inputPanel.setPreferredSize(new Dimension(300, 225));
		add(inputPanel, gbc);

		// Panel central inferior (histograma)
		chartPanel = new JPanel(new BorderLayout());
		chartPanel.setBackground(Color.DARK_GRAY);
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weighty = 0.7;
		chartPanel.setPreferredSize(new Dimension(300, 300));
		add(chartPanel, gbc);

		// Panel derecho (lista)
		JPanel rightPanel = new JPanel(new BorderLayout());
		listArea = new JTextArea();
		listArea.setEditable(false);
		listArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
		listScrollPane = new JScrollPane(listArea);
		rightPanel.add(listScrollPane, BorderLayout.CENTER);

		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.gridheight = 2;
		gbc.weightx = 0.25;
		gbc.weighty = 1;
		rightPanel.setPreferredSize(new Dimension(300, 700));
		add(rightPanel, gbc);

		// Eventos
		generateButton.addActionListener(e -> generateDistribution());
		distributionBox.addActionListener(e -> updateLabel());
		updateLabel();
	}


	private void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception ignored) {
		}
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
			if (isInvalidInput(sampleSizeField.getText())) {
				showError("Por favor, complete todos los campos con valores numéricos válidos.");
				return;
			}

			int sampleSize = Integer.parseInt(sampleSizeField.getText());
			int bins = (Integer) binsBox.getSelectedItem();
			int option = distributionBox.getSelectedIndex();

			if (!validateParameters(option)) {
				return;
			}

			IDistributionGenerator generator = switch (option) {
				case 0 -> new Uniforme(getDouble(param1Field), getDouble(param2Field));
				case 1 -> new Exponencial(getDouble(param1Field));
				case 2 -> new NormalBoxMuller(getDouble(param1Field), getDouble(param2Field));
				case 3 -> new NormalConvolucion(getDouble(param1Field), getDouble(param2Field));
				default -> throw new IllegalStateException("Opción de distribución no válida: " + option);
			};

			double[] samples = generator.generateSamples(sampleSize);
			showHistogram(samples, bins);
			showFrequencyTable(samples, bins, generator);
			listArea.setText(arrayToString(samples));

		} catch (NumberFormatException e) {
			showError("Ingrese valores numéricos válidos.");
		}
	}

	private boolean isInvalidInput(String text) {
		try {
			if (text.isEmpty()) {
				return true;
			}
		} catch (NumberFormatException e) {
			return true;
		}
		return false;
	}

	private boolean validateParameters(int option) {
		try {
			double p1 = getDouble(param1Field);
			double p2 = param2Field.isVisible() ? getDouble(param2Field) : 0;
			int sampleSize = Integer.parseInt(sampleSizeField.getText());

			if (option == 0 && p1 >= p2) {
				showError("En la distribución uniforme, el primer parámetro debe ser menor que el segundo.");
				return false;
			}
			if (option == 1 && p1 <= 0) {
				showError("Lambda debe ser mayor a cero para la distribución exponencial.");
				return false;
			}
			if ((option == 2 || option == 3) && p2 <= 0) {
				showError("La desviación estándar debe ser mayor a cero para distribuciones normales.");
				return false;
			}
			if (sampleSize > 1_000_000) {
				showError("EL tamaño de muestra debe ser menor o igual a 1,000,000.");
				return false;
			}
			if (sampleSize < 0 || sampleSize == 0) {
				showError("EL tamaño de muestra no debe ser negativo ni cero.");
				return false;
			}
			if ((option == 0 || option == 1 || option == 2) && (sampleSize <= 1)) {
				showError("EL tamaño de muestra para esta distribucion debe ser al menos 2.");
				return false;
			}
			if (option == 3 && sampleSize < 12) {
				showError("EL tamaño de muestra para esta distribucion debe ser al menos 12.");
				return false;
			}
		} catch (NumberFormatException e) {
			showError("Por favor, complete los parámetros correctamente.");
			return false;
		}
		return true;
	}

	private double getDouble(JTextField field) {
		return Double.parseDouble(field.getText().trim());
	}

	private String arrayToString(double[] array) {
		StringBuilder sb = new StringBuilder();
		for (double num : array) {
			sb.append(String.format("%.4f", num)).append("\n");
		}
		return sb.toString();
	}

	private void showError(String message) {
		JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
	}

	private void showHistogram(double[] data, int bins) {
		HistogramDataset dataset = new HistogramDataset();
		dataset.addSeries("Frecuencia", data, bins);

		JFreeChart histogram = ChartFactory.createHistogram(
				"Histograma de Frecuencias", "Valor", "Frecuencia Observada",
				dataset, PlotOrientation.VERTICAL, false, true, false);

		histogram.setBackgroundPaint(new Color(150, 150, 150));
		histogram.getPlot().setBackgroundPaint(new Color(230, 230, 230));

		XYPlot plot = (XYPlot) histogram.getPlot();
		XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();
		renderer.setSeriesPaint(0, new Color(2, 60, 122));
		renderer.setBarPainter(new org.jfree.chart.renderer.xy.StandardXYBarPainter());
		renderer.setMargin(0.0);
		renderer.setDrawBarOutline(true);
		renderer.setShadowVisible(false);

		int option = distributionBox.getSelectedIndex();
		double minValue, maxValue;
		if (option == 0) {
			minValue = getDouble(param1Field);
			maxValue = getDouble(param2Field);
		} else {
			minValue = Double.MAX_VALUE;
			maxValue = Double.MIN_VALUE;
			for (double value : data) {
				if (value < minValue) minValue = value;
				if (value > maxValue) maxValue = value;
			}
		}
		plot.getDomainAxis().setRange(minValue, maxValue);

		Font labelFont = new Font("Arial", Font.BOLD, 12);
		histogram.getTitle().setFont(new Font("Arial", Font.BOLD, 16));
		plot.getDomainAxis().setLabelFont(labelFont);
		plot.getRangeAxis().setLabelFont(labelFont);
		plot.getDomainAxis().setTickLabelFont(labelFont);
		plot.getRangeAxis().setTickLabelFont(labelFont);

		plot.setDomainGridlinesVisible(true);
		plot.setRangeGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.GRAY);
		plot.setRangeGridlinePaint(Color.GRAY);

		ChartPanel chart = new ChartPanel(histogram);
		chartPanel.removeAll();
		chartPanel.add(chart, BorderLayout.CENTER);
		chartPanel.revalidate();
		chartPanel.repaint();
	}

	private void showFrequencyTable(double[] data, int bins, IDistributionGenerator generator) {
		int option = distributionBox.getSelectedIndex();
		double min, max;

		if (option == 0) {
			min = getDouble(param1Field);
			max = getDouble(param2Field);
		} else {
			min = Double.MAX_VALUE;
			max = Double.MIN_VALUE;
			for (double val : data) {
				if (val < min) min = val;
				if (val > max) max = val;
			}
		}

		double width = (max - min) / bins;
		int[] observed = new int[bins];

		for (double val : data) {
			int bin = (int) ((val - min) / width);
			if (bin >= bins) bin = bins - 1;
			if (bin < 0) bin = 0;
			observed[bin]++;
		}

		String[] columnNames = {"Desde", "Hasta", "Frec.Obs."};
		Object[][] tableData = new Object[bins][3];

		for (int i = 0; i < bins; i++) {
			double from = min + i * width;
			double to = from + width;
			tableData[i][0] = String.format("%.4f", from);
			tableData[i][1] = String.format("%.4f", to);
			tableData[i][2] = observed[i];
		}

		frequencyTable.setModel(new javax.swing.table.DefaultTableModel(tableData, columnNames));
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			MainGUI gui = new MainGUI();
			gui.setLocationRelativeTo(null);
			gui.setVisible(true);
		});
	}
}

