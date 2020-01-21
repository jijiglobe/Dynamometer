import java.util.*;
import java.io.*;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

class display extends ApplicationFrame {
    //---------------- DATA VISUALIZATION CODE BELOW------------------
    //insantiates a chart object and displays charts based on the two curve arrays
    //This version graphs two data series on each chart
    public static void displayCharts(ArrayList<ArrayList<double[]>> curves,ArrayList<ArrayList<double[]>> curves2){
	display chart = new display(
				      "Time(s)",
				      "Position(rad)" ,
				      curves.get(0),curves2.get(0));
	
	chart.pack( );
	chart.setVisible( true ); 
	
	display chart2 = new display(
				       "Time(s)",
				       "Angular Velocity(rad/s)" ,
				       curves.get(1),curves2.get(1));
	
	chart2.pack( );
	chart2.setLocation(630,0);
	chart2.setVisible( true );
	display chart3 = new display(
				       "Time(s)",
				       "Angular Acceleration(rad/s^2)" ,
				       curves.get(2),curves2.get(2));
	
	chart3.pack( );
	chart3.setLocation(0,427);
	chart3.setVisible( true ); 
	display chart4 = new display(
				       "Torque(N*M)" ,
				       "Angular Velocity(Rad/S)",
				       curves.get(3),curves2.get(3));
	
	chart4.pack( );
	chart4.setLocation(630,427);
	chart4.setVisible( true );	
    }


    //instantiates an display and displays all the data in the array of curves
    public static void displayCharts(ArrayList<ArrayList<double[]>> curves){
	display chart = new display(
				      "Time(s)",
				      "Position(rad)" ,
				      curves.get(0));
	
	chart.pack( );
	chart.setVisible( true ); 
	
	display chart2 = new display(
				       "Time(s)",
				       "Angular Velocity(rad/s)" ,
				       curves.get(1));
	
	chart2.pack( );
	chart2.setLocation(630,0);
	chart2.setVisible( true );
	display chart3 = new display(
				       "Time(s)",
				       "Angular Acceleration(rad/s^2)" ,
				       curves.get(2));
	
	chart3.pack( );
	chart3.setLocation(0,427);
	chart3.setVisible( true ); 
	display chart4 = new display(
				       "Torque(N*M)" ,
				       "Angular Velocity(Rad/S)",
				       curves.get(3));
	
	chart4.pack( );
	chart4.setLocation(630,427);
	chart4.setVisible( true );	
    }
    
    //Constructor for a graph with only one data series.
    //xvar is the x axis label, yvar is the y axis label
    //dataArray is the data series in an array of coordinate pairs
    public display( String xvar , String yvar,
		     ArrayList<double[]> dataArray) {
	super(yvar+" vs. "+xvar);
	JFreeChart lineChart = ChartFactory.createXYLineChart(
							    yvar+" vs. "+xvar,
							    xvar, yvar,
							    createDataset(dataArray));
         
	ChartPanel chartPanel = new ChartPanel( lineChart );
	chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
	setContentPane( chartPanel );
    }

    //Constructor for a graph with multiple data series
    //xvar is the x axis label, yvar is the y axis label
    //dataArrays 1 and 2 are the data series
    public display( String xvar , String yvar,
		     ArrayList<double[]> dataArray,ArrayList<double[]> dataArray2) {
	super(yvar+" vs. "+xvar);
	JFreeChart lineChart = ChartFactory.createXYLineChart(
							    yvar+" vs. "+xvar,
							    xvar, yvar,
							    createDataset(dataArray,dataArray2));
         
	ChartPanel chartPanel = new ChartPanel( lineChart );
	chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
	setContentPane( chartPanel );
    }

    //Converts a data series in ArrayList<double[]> for to a XYSeriesCollection for internal use in graphing function
    private XYSeriesCollection createDataset(ArrayList<double[]> dataArray) {
	XYSeriesCollection dataset = new XYSeriesCollection( );
	XYSeries series = new XYSeries("movingAverageFilter(100) - no Noise");
	for(double[] row : dataArray){
	    series.add(row[0],row[1]);
	}
	dataset.addSeries(series);
	return dataset;
    }

    //Converts a data series in ArrayList<double[]> for to a XYSeriesCollection for internal use in graphing function
    private XYSeriesCollection createDataset(ArrayList<double[]> dataArray, ArrayList<double[]> dataArray2) {
	XYSeriesCollection dataset = new XYSeriesCollection( );
	XYSeries series = new XYSeries("Kalman Filter");
	for(double[] row : dataArray){
	    series.add(row[0],row[1]);
	}
	dataset.addSeries(series);
	XYSeries series2 = new XYSeries("\"true data\" with no noise");
	for(double[] row : dataArray2){
	    series2.add(row[0],row[1]);
	}
	dataset.addSeries(series2);
	return dataset;
    }

}
