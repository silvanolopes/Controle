package br.ufrn.software.controle.src;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Point;
import java.awt.Stroke;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class RTChart {
    
    private String title;
    private String[] lines_titles;
    private XYSeries[] series;
    private XYSeriesCollection data;
    private JFreeChart chart;
    private NumberAxis domain, range;
    private ChartPanel chartPanel;
    private JPanel area;
    private XYLineAndShapeRenderer renderer;
    private int lines_number;
    
    public RTChart(String title, String labe_Y, int lines_number, String[] lines_titles, JPanel area) {
        this.title = title;
        this.lines_number = lines_number;
        this.lines_titles = lines_titles;
        this.renderer = new XYLineAndShapeRenderer();
        this.series = new XYSeries[lines_number];
        this.domain = new NumberAxis("Tempo");
        this.range = new NumberAxis(labe_Y);
        this.data = new XYSeriesCollection();
        this.area = area;        
        initConfig();
    }
    
    private void initConfig() {
        for (int i = 0; i < lines_number; i++)
            series[i] = new XYSeries(lines_titles[i]);
        for (int i = 0; i < series.length; i++)
            data.addSeries(series[i]);
        
        this.domain.setRange(new Range(0, 120));
        this.domain.setAutoRange(false);
        this.range.setAutoRange(true);
        createChart();
    }
    
    private void createChart() {
        chart = ChartFactory.createXYLineChart(
                title, "Tempo", "Tensao",
                data,
                PlotOrientation.VERTICAL,
                true, true, false );
        Stroke stroke = new BasicStroke(1);
        chart.getXYPlot().setBackgroundPaint(new Color(255, 255, 255));
        chart.getXYPlot().setDomainGridlinePaint(new Color(0, 0, 0));
        chart.getXYPlot().setRangeGridlinePaint(new Color(0, 0, 0));
        chart.getXYPlot().setRangeAxis(range);
        chart.getXYPlot().setDomainAxis(domain);
        
        for (int i=0; i < lines_number; i++) {
            renderer.setSeriesLinesVisible(i, true);
            renderer.setSeriesShapesVisible(i, false);
        }
        XYPlot plot = (XYPlot) chart.getPlot();
        renderer.setSeriesStroke(0, stroke);
        renderer.setSeriesStroke(1, stroke);
        plot.setRenderer(this.renderer);
        plot.setDomainMinorGridlinesVisible(true);
        chartPanel = new ChartPanel(chart);
        chartPanel.setSize(new java.awt.Dimension((int)(area.getWidth()*0.95), (int)(area.getHeight()*0.95)));
        chartPanel.setLocation(new Point((int)(area.getWidth()*0.02), (int)(area.getWidth()*0.01)));
        area.add(chartPanel, BorderLayout.CENTER);
        area.repaint();
    }
    
    public void setLineVisible(int id, boolean visible) {
        renderer.setSeriesLinesVisible(id, visible);
    }

    public void addPoint(double x, double y, int id) {
        series[id].addOrUpdate(x, y);
    }

    public void drawChart(double id) {
        area.validate();
        if(id > 120){
            this.domain.setRange(this.domain.getRange().getLowerBound()+0.1, this.domain.getRange().getUpperBound()+0.1);
        }
    }
    
    public void clearChart(){
        
    }
    
}
