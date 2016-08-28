package fr.cph.stock.report;

import fr.cph.stock.entities.Index;
import fr.cph.stock.entities.chart.PieChart;
import fr.cph.stock.entities.chart.TimeChart;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import java.awt.*;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that takes care of pdf reports
 *
 * @author Carl-Philipp Harmant
 */
public class PdfReport {

	private final String jrxml;
	private final Map<String, Object> parameters;

	/**
	 * Constructor
	 *
	 * @param jrxmlPath the jrxml file to load
	 */
	public PdfReport(final String jrxmlPath) {
		this.jrxml = jrxmlPath;
		this.parameters = new HashMap<>();
	}

	/**
	 * Add param
	 *
	 * @param param  the param
	 * @param object the object
	 */
	public final void addParam(final String param, final Object object) {
		this.parameters.put(param, object);
	}

	/**
	 * Get report
	 *
	 * @return a jasper print
	 * @throws JRException the jreException
	 */
	public final JasperPrint getReport() throws JRException {
		final InputStream inputStream = PdfReport.class.getClassLoader().getResourceAsStream(jrxml);
		final JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
		final JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
		return JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
	}

	/**
	 * Create pie chart
	 *
	 * @param pieChart the pie chart
	 * @param title    the title
	 * @return an image
	 */
	public static Image createPieChart(final PieChart pieChart, final String title) {
		final Map<String, Double> map = pieChart.getEquities();
		final DefaultPieDataset data = new DefaultPieDataset();
		map.entrySet().forEach(entry -> data.setValue(entry.getKey(), entry.getValue()));
		final JFreeChart chart = ChartFactory.createPieChart(title, data, true, true, true);
		return chart.createBufferedImage(500, 500);
	}

	/**
	 * Create a time chart
	 *
	 * @param pieChart the pie chart
	 * @param title    the title
	 * @return an image
	 */
	public static Image createTimeChart(final TimeChart pieChart, final String title) {
		final Map<Date, Double> mapShareValueChart = pieChart.getShareValue();
		final Map<String, List<Index>> mapIndexChart = pieChart.getIndexes();

		final TimeSeries series = new TimeSeries("Share value");
		final TimeSeries series2 = new TimeSeries("CAC40");
		final TimeSeries series3 = new TimeSeries("S&P 500");

		mapShareValueChart.entrySet().forEach(entry -> series.add(new Millisecond(entry.getKey()), entry.getValue()));
		mapIndexChart.get("^FCHI").forEach(entry -> series2.add(new Millisecond(entry.getDate()), entry.getShareValue()));
		mapIndexChart.get("^GSPC").forEach(entry -> series3.add(new Millisecond(entry.getDate()), entry.getShareValue()));

		final TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(series);
		dataset.addSeries(series2);
		dataset.addSeries(series3);
		JFreeChart chart = ChartFactory.createTimeSeriesChart(title, // Title
			"Time", // x-axis Label
			"Performance", // y-axis Label
			dataset, // Dataset
			true, // Show Legend
			true, // Use tooltips
			true // Configure chart to generate URLs?
		);
		return chart.createBufferedImage(1110, 600);
	}
}
