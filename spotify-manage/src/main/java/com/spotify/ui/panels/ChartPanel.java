package com.spotify.ui.panels;

/*
 * Student Name: Huỳnh Vũ Anh Khoa
 * Student ID: 97482503608
 * Course: Object Oriented Programming
 * Project: Spotify
 */

/*
 * Student Name: Nguyễn Thiên Kỳ
 * Student ID: 77482503643
 * Course: Object Oriented Programming
 * Project: Spotify
 */

import com.spotify.dao.DAOFactory;
import com.spotify.dao.TrackDAO;
import com.spotify.model.UserDTO;
import com.spotify.pattern.DataChangePublisher;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Charts panel – 4 loại biểu đồ:
 * 1. Bar   – Avg popularity by genre
 * 2. Pie   – Track count by genre
 * 3. Scatter – Energy vs Popularity
 * 4. Bar   – Avg audio features
 *
 * Dùng tên đầy đủ org.jfree.chart.ChartPanel để tránh conflict với class này.
 */
public class ChartPanel extends JPanel implements DataChangePublisher.DataChangeListener {

    private final TrackDAO trackDAO = DAOFactory.getTrackDAO();
    private final JComboBox<String> cmbGenreFilter = new JComboBox<>();
    private final JButton btnRefresh = new JButton("🔄 Refresh");

    // Dùng tên đầy đủ để tránh trùng với class này
    private org.jfree.chart.ChartPanel barPanel;
    private org.jfree.chart.ChartPanel piePanel;
    private org.jfree.chart.ChartPanel scatterPanel;
    private org.jfree.chart.ChartPanel featurePanel;

    public ChartPanel(UserDTO user) {
        setLayout(new BorderLayout(0, 12));
        setBorder(new EmptyBorder(16, 16, 16, 16));
        buildUI();
        DataChangePublisher.addListener(this);
        loadCharts();
    }

    private void buildUI() {
        // ── Title + filter ────────────────────────────────────────
        var top = new JPanel(new BorderLayout());
        var title = new JLabel("Data Visualisation");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(30, 215, 96));

        var filterBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filterBar.add(new JLabel("Genre Filter:"));
        cmbGenreFilter.addItem("All");
        try {
            trackDAO.distinctGenres().forEach(cmbGenreFilter::addItem);
        } catch (Exception ignored) {}
        filterBar.add(cmbGenreFilter);
        filterBar.add(btnRefresh);

        top.add(title, BorderLayout.WEST);
        top.add(filterBar, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        btnRefresh.addActionListener(e -> loadCharts());
        cmbGenreFilter.addActionListener(e -> loadCharts());

        // ── Tạo placeholder charts ────────────────────────────────
        barPanel     = makeEmptyPanel();
        piePanel     = makeEmptyPanel();
        scatterPanel = makeEmptyPanel();
        featurePanel = makeEmptyPanel();

        var grid = new JPanel(new GridLayout(2, 2, 12, 12));
        grid.add(wrapPanel(barPanel,     "📊  Avg Popularity by Genre"));
        grid.add(wrapPanel(piePanel,     "🥧  Track Count by Genre"));
        grid.add(wrapPanel(scatterPanel, "⚡  Energy vs Popularity"));
        grid.add(wrapPanel(featurePanel, "🎵  Avg Audio Features"));
        add(grid, BorderLayout.CENTER);
    }

    // ── Helper: tạo panel trống ───────────────────────────────────
    private org.jfree.chart.ChartPanel makeEmptyPanel() {
        JFreeChart empty = ChartFactory.createBarChart(
                "Loading...", "", "", new DefaultCategoryDataset());
        var cp = new org.jfree.chart.ChartPanel(empty);
        cp.setPreferredSize(new Dimension(400, 260));
        return cp;
    }

    private JPanel wrapPanel(org.jfree.chart.ChartPanel cp, String title) {
        var p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 60)), "  " + title));
        p.add(cp, BorderLayout.CENTER);
        return p;
    }

    // ── Load tất cả charts bằng SwingWorker ──────────────────────
    private void loadCharts() {
        String genre = (String) cmbGenreFilter.getSelectedItem();

        new SwingWorker<Void, Void>() {
            Map<String, Double>  avgPop;
            Map<String, Integer> genreCount;
            List<double[]>       scatter;
            Map<String, Double>  features;

            @Override
            protected Void doInBackground() {
                try { avgPop     = trackDAO.avgPopularityByGenre(); } catch (Exception e) { avgPop = Map.of(); }
                try { genreCount = trackDAO.trackCountByGenre();    } catch (Exception e) { genreCount = Map.of(); }
                try { scatter    = trackDAO.energyVsPopularity(genre); } catch (Exception e) { scatter = List.of(); }
                try { features   = trackDAO.avgAudioFeatures();     } catch (Exception e) { features = Map.of(); }
                return null;
            }

            @Override
            protected void done() {
                barPanel.setChart(buildBarChart(avgPop));
                piePanel.setChart(buildPieChart(genreCount));
                scatterPanel.setChart(buildScatterChart(scatter, genre));
                featurePanel.setChart(buildFeatureChart(features));
                barPanel.repaint();
                piePanel.repaint();
                scatterPanel.repaint();
                featurePanel.repaint();
            }
        }.execute();
    }

    // ── Chart 1: Bar – Avg Popularity by Genre ────────────────────
    private JFreeChart buildBarChart(Map<String, Double> data) {
        var dataset = new DefaultCategoryDataset();
        data.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .limit(15)
            .forEach(e -> dataset.addValue(e.getValue(), "Popularity", e.getKey()));

        JFreeChart chart = ChartFactory.createBarChart(
                "Average Popularity by Genre", "Genre", "Avg Popularity",
                dataset, PlotOrientation.VERTICAL, false, true, false);

        styleChart(chart);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(30, 215, 96));
        return chart;
    }

    // ── Chart 2: Pie – Track Count by Genre ──────────────────────
    @SuppressWarnings("unchecked")
    private JFreeChart buildPieChart(Map<String, Integer> data) {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        data.entrySet().stream()
            .limit(10)
            .forEach(e -> dataset.setValue(e.getKey(), e.getValue()));

        JFreeChart chart = ChartFactory.createPieChart(
                "Track Distribution by Genre", dataset, true, true, false);

        styleChart(chart);
        PiePlot<String> plot = (PiePlot<String>) chart.getPlot();
        plot.setBackgroundPaint(new Color(30, 30, 30));
        plot.setLabelBackgroundPaint(new Color(50, 50, 50));
        plot.setLabelPaint(Color.WHITE);
        return chart;
    }

    // ── Chart 3: Scatter – Energy vs Popularity ───────────────────
    private JFreeChart buildScatterChart(List<double[]> points, String genre) {
        String label = (genre == null || "All".equals(genre)) ? "All Genres" : genre;
        XYSeries series = new XYSeries(label);
        for (double[] p : points) {
            series.add(p[0], p[1]);
        }
        XYSeriesCollection dataset = new XYSeriesCollection(series);

        JFreeChart chart = ChartFactory.createScatterPlot(
                "Energy vs Popularity", "Energy (0-1)", "Popularity (0-100)", dataset);

        styleChart(chart);
        XYPlot plot = (XYPlot) chart.getPlot();
        XYDotRenderer renderer = new XYDotRenderer();
        renderer.setDotWidth(4);
        renderer.setDotHeight(4);
        renderer.setSeriesPaint(0, new Color(30, 215, 96));
        plot.setRenderer(renderer);
        return chart;
    }

    // ── Chart 4: Bar – Avg Audio Features ────────────────────────
    private JFreeChart buildFeatureChart(Map<String, Double> features) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        features.forEach((k, v) -> dataset.addValue(v, "Average", k));

        JFreeChart chart = ChartFactory.createBarChart(
                "Average Audio Features", "Feature", "Value (0-1)",
                dataset, PlotOrientation.HORIZONTAL, false, true, false);

        styleChart(chart);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(29, 185, 84));
        return chart;
    }

    // ── Style chung cho tất cả charts ────────────────────────────
    private void styleChart(JFreeChart chart) {
        chart.setBackgroundPaint(new Color(24, 24, 24));
        if (chart.getTitle() != null) {
            chart.getTitle().setPaint(Color.WHITE);
        }

        var plot = chart.getPlot();
        plot.setBackgroundPaint(new Color(30, 30, 30));
        plot.setOutlinePaint(new Color(60, 60, 60));

        if (plot instanceof CategoryPlot cp) {
            cp.getDomainAxis().setTickLabelPaint(Color.LIGHT_GRAY);
            cp.getDomainAxis().setLabelPaint(Color.LIGHT_GRAY);
            cp.getRangeAxis().setTickLabelPaint(Color.LIGHT_GRAY);
            cp.getRangeAxis().setLabelPaint(Color.LIGHT_GRAY);
            cp.setRangeGridlinePaint(new Color(60, 60, 60));
        } else if (plot instanceof XYPlot xyp) {
            xyp.getDomainAxis().setTickLabelPaint(Color.LIGHT_GRAY);
            xyp.getDomainAxis().setLabelPaint(Color.LIGHT_GRAY);
            xyp.getRangeAxis().setTickLabelPaint(Color.LIGHT_GRAY);
            xyp.getRangeAxis().setLabelPaint(Color.LIGHT_GRAY);
            xyp.setDomainGridlinePaint(new Color(60, 60, 60));
            xyp.setRangeGridlinePaint(new Color(60, 60, 60));
        }
    }

    @Override
    public void onDataChanged() {
        loadCharts();
    }
}