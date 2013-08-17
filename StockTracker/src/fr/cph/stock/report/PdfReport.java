package fr.cph.stock.report;

import java.awt.Image;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import fr.cph.stock.entities.Index;
import fr.cph.stock.entities.chart.PieChart;
import fr.cph.stock.entities.chart.TimeChart;

/**
 * Class that takes care of pdf reports
 * 
 * @author Carl-Philipp Harmant
 * 
 */
public class PdfReport {

	/** **/
	private String jrxml;

	/** **/
	private Map<String, Object> parameters;

	/**
	 * Constructor
	 * 
	 * @param jrxmlPath
	 *            the jrxml file to load
	 */
	public PdfReport(final String jrxmlPath) {
		this.jrxml = jrxmlPath;
		this.parameters = new HashMap<String, Object>();
	}

	/**
	 * Add param
	 * 
	 * @param param
	 *            the param
	 * @param object
	 *            the object
	 */
	public final void addParam(final String param, final Object object) {
		this.parameters.put(param, object);
	}

	/**
	 * Get report
	 * 
	 * @return a jasper print
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws JRException
	 *             the jreException
	 */
	public final JasperPrint getReport() throws FileNotFoundException, JRException {
		InputStream inputStream = PdfReport.class.getClassLoader().getResourceAsStream(jrxml);
		JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
		JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
		return jasperPrint;
	}

	/**
	 * Create pie chart
	 * 
	 * @param pieChart
	 *            the pie chart
	 * @param title
	 *            the title
	 * @return an image
	 */
	public static Image createPieChart(final PieChart pieChart, final String title) {
		Map<String, Double> map = pieChart.getEquities();
		DefaultPieDataset data = new DefaultPieDataset();
		for (Entry<String, Double> e : map.entrySet()) {
			data.setValue(e.getKey(), e.getValue());
		}
		JFreeChart chart = ChartFactory.createPieChart(title, data, true, true, true);
		return chart.createBufferedImage(500, 500);
	}

	/**
	 * Create a time chart
	 * 
	 * @param pieChart
	 *            the pie chart
	 * @param title
	 *            the title
	 * @return an image
	 */
	public static Image createTimeChart(final TimeChart pieChart, final String title) {
		Map<Date, Double> mapShareValueChart = pieChart.getShareValue();
		Map<String, List<Index>> mapIndexChart = pieChart.getIndexes();

		TimeSeries series = new TimeSeries("Share value");
		for (Entry<Date, Double> e : mapShareValueChart.entrySet()) {
			series.add(new Millisecond(e.getKey()), e.getValue());
		}
		TimeSeries series2 = new TimeSeries("CAC40");
		for (Index e : mapIndexChart.get("^FCHI")) {
			series2.add(new Millisecond(e.getDate()), e.getShareValue());
		}
		TimeSeries series3 = new TimeSeries("S&P 500");
		for (Index e : mapIndexChart.get("^GSPC")) {
			series3.add(new Millisecond(e.getDate()), e.getShareValue());
		}
		TimeSeriesCollection dataset = new TimeSeriesCollection();
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
