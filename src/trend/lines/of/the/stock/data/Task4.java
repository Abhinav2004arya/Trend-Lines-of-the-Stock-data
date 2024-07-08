/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package trend.lines.of.the.stock.data;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.awt.Color;
import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;


public class Task4 extends ApplicationFrame{
    
    public Task4 (String title){
    
        super(title);
        JFreeChart chart = createChart();
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800,600));
        setContentPane(chartPanel);
    
    }
    
    private JFreeChart createChart(){
        
        XYSeries closingPrices = new XYSeries("Closing Prices");
        String inputFilePath = "e:/StockDataHDFCBANK.csv";
        ArrayList<Date> dates = new ArrayList<>();
        ArrayList<Double> prices = new ArrayList<>();
        
        try(CSVReader csvReader = new CSVReader(new FileReader(inputFilePath)))
        {
            csvReader.readNext();
            String[] lines;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
            
            while ((lines = csvReader.readNext())!= null)
            {
                Date date = dateFormat.parse(lines[1]);
                double closePrice = Double.parseDouble(lines[5]);
                dates.add(date);
                prices.add(closePrice);
                closingPrices.add(date.getTime(), closePrice);
            }
        } 
        
        
        
        catch (FileNotFoundException ex) {
            Logger.getLogger(Task4.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Task4.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(Task4.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CsvValidationException ex) {
            Logger.getLogger(Task4.class.getName()).log(Level.SEVERE, null, ex);
        }
        //calculatin trend lines
        double[] trendLine = calculateTrendLine(dates, prices);

        XYSeries trendSeries = new XYSeries("Trend Line");
        for (int i = 0; i < dates.size(); i++) {
            double trendValue = trendLine[0] * i + trendLine[1];
            trendSeries.add(dates.get(i).getTime(), trendValue);
        }

        // Calculate support and resistance levels
        double supportLevel = prices.stream().min(Double::compareTo).orElse(0.0);
        double resistanceLevel = prices.stream().max(Double::compareTo).orElse(0.0);

        XYSeries supportSeries = new XYSeries("Support Level");
        XYSeries resistanceSeries = new XYSeries("Resistance Level");
        for (Date date : dates) {
            supportSeries.add(date.getTime(), supportLevel);
            resistanceSeries.add(date.getTime(), resistanceLevel);
        }

        // Calculate UP Trend and Down Trend lines
        XYSeries upTrendSeries = new XYSeries("Up Trend Line");
        XYSeries downTrendSeries = new XYSeries("Down Trend Line");
        double upTrendSlope = trendLine[0];
        double upTrendIntercept = trendLine[1] + supportLevel * 0.01; // Adjust as needed
        double downTrendSlope = trendLine[0];
        double downTrendIntercept = trendLine[1] - resistanceLevel * 0.01; // Adjust as needed

        for (int i = 0; i < dates.size(); i++) {
            double upTrendValue = upTrendSlope * i + upTrendIntercept;
            double downTrendValue = downTrendSlope * i + downTrendIntercept;
            upTrendSeries.add(dates.get(i).getTime(), upTrendValue);
            downTrendSeries.add(dates.get(i).getTime(), downTrendValue);
        }
        
        
         XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(closingPrices);
        dataset.addSeries(trendSeries);
        dataset.addSeries(supportSeries);
        dataset.addSeries(resistanceSeries);
        dataset.addSeries(upTrendSeries);
        dataset.addSeries(downTrendSeries);
        
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Trend Line For HDFC Stock Data",
                "Date",
                "Closing Prices",
                dataset,
                true,
                true,
                false
        
        );
        
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setOrientation(PlotOrientation.VERTICAL);
        
         XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.BLUE); 
        renderer.setSeriesPaint(1, Color.RED); 
        renderer.setSeriesPaint(2, Color.GREEN); 
        renderer.setSeriesPaint(3, Color.ORANGE); 
        renderer.setSeriesPaint(4, Color.MAGENTA); 
        renderer.setSeriesPaint(5, Color.CYAN); 
        
        plot.setRenderer(renderer);
        
    return chart;
    }
    
     
    private double[] calculateTrendLine(ArrayList<Date> dates, ArrayList<Double> prices) {
        int n = dates.size();
        double sumX = 0.0, sumY = 0.0, sumXY = 0.0, sumX2 = 0.0;

        for (int i = 0; i < n; i++) {
            double x = i;
            double y = prices.get(i);
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
        }

        double slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        double intercept = (sumY - slope * sumX) / n;

        return new double[]{slope, intercept};
    }

    
    
    
    
    
    
    public static void main(String[] args) {
        Task4 chart = new Task4("Trend Lines");
        chart.pack();
        RefineryUtilities.centerFrameOnScreen(chart);
        chart.setVisible(true);
    }
    
}
