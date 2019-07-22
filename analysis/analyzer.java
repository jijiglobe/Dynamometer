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

class analyzer extends ApplicationFrame {
    //---------------- DATA VISUALIZATION CODE BELOW------------------
    public analyzer( String applicationTitle , String chartTitle,
		     ArrayList<double[]> dataArray) {
	super(applicationTitle);
	JFreeChart lineChart = ChartFactory.createXYLineChart(
							    chartTitle,
							    "Torque(n*m)",
							    "Angular Velocity(rad/s)",
							    createDataset(dataArray));
         
	//CategoryPlot plot = (CategoryPlot) lineChart.getPlot();
	//plot.getDomainAxis().setTickUnit(0.5);
	ChartPanel chartPanel = new ChartPanel( lineChart );
	chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
	setContentPane( chartPanel );
    }

    private XYSeriesCollection createDataset(ArrayList<double[]> dataArray) {
	XYSeriesCollection dataset = new XYSeriesCollection( );
	XYSeries series = new XYSeries("bad approximation");
	for(double[] row : dataArray){
	    //dataset.addValue((Number)row[1], "Speed" , row[0] );
	    series.add(row[0],row[1]);
	}
	dataset.addSeries(series);
	return dataset;
    }


    //-------------- DATA PROCESSING STUFF BELOW---------------
    private static int cols = 6;
    private static ArrayList<double[]> readCSV(String filename){
	int row = 0;
	int col = 0;
	ArrayList<double[]> csv = new ArrayList<double[]>(0);
	try(BufferedReader reader = new BufferedReader(new FileReader(filename))){
	    String line;
	    reader.readLine();
	    while((line = reader.readLine())!= null){
		String[] stringRow = line.split(","); 
		double[] currentRow = new double[cols];
		for(int i = 0; i < stringRow.length; i++){
		    currentRow[i] = Double.parseDouble(stringRow[i]);
		}
		csv.add(currentRow);
	    }
	}
	catch(IOException e){
	    System.out.println(e);
	}
	return csv;
    }

    public static void update(ArrayList<double[]> dataArray){
	//[time(s),edges,totalEdges,Angle(rad)]
	int totalEdges = 0;
	for(double[] row : dataArray){
	    totalEdges += row[1];
	    row[4] = totalEdges;
	    row[5] = 2*Math.PI*totalEdges/4096;
	}
    }
    public static void printArray(ArrayList<double[]> dataArray){
	for(double[] row : dataArray){
	    for(double data : row){
		System.out.printf("%f",data);
		System.out.printf(",");
	    }
	    System.out.printf("\n");
	}
    }	
    
    public static ArrayList<double[]> basicVelocityArray(ArrayList<double[]> dataArray){
	ArrayList<double[]> velocityArray = new ArrayList<double[]>();
	for(int i = 100; i < dataArray.size()-100;i++){
	    double[] newRow = new double[3];
	    //newRow[0] = dataArray.get(i)[5]*springCoefficient;
	    newRow[0] = dataArray.get(i)[0];
	    newRow[1] = (dataArray.get(i+100)[5]-dataArray.get(i-100)[5]) /
		(dataArray.get(i+100)[0] - dataArray.get(i-100)[0]);
	    newRow[2] = dataArray.get(i)[5];
	    velocityArray.add(newRow);
	}
	return velocityArray;
    }
    
    public static ArrayList<double[]> basicAccelerationArray(ArrayList<double[]> dataArray){
	ArrayList<double[]> accelerationArray = new ArrayList<double[]>();
	for(int i = 500; i < dataArray.size()-500;i++){
	    double[] newRow = new double[2];
	    //newRow[0] = dataArray.get(i)[5]*springCoefficient;
	    newRow[0] = dataArray.get(i)[0];
	    newRow[1] = (dataArray.get(i+500)[1]-dataArray.get(i-500)[1]) /
		(dataArray.get(i+500)[0] - dataArray.get(i-500)[0]);
	    accelerationArray.add(newRow);
	}
	return accelerationArray;
    }

    public static ArrayList<double[]> generateMotorCurve(ArrayList<double[]> velocity,
							 ArrayList<double[]> acceleration,
							 double springCoeff,
							 double diskInertia){
	ArrayList<double[]> curve = new ArrayList<double[]>();
	int offset;
	for(offset = 0; velocity.get(offset)[0] < acceleration.get(0)[0];offset++){}
	for(int i = 0; i < acceleration.size(); i++){
	    double accel = acceleration.get(i)[1];
	    double vel = velocity.get(i+offset)[1];
	    double SpringTorque = velocity.get(i+offset)[2] * springCoeff;
	    double inertialTorque = accel * diskInertia;
	    double[] newRow = new double[2];
	    newRow[0] = SpringTorque + inertialTorque;
	    newRow[1] = vel;
	    curve.add(newRow);
	}
	return curve;
    }
    public static void main(String[] args){
	ArrayList<double[]> data = readCSV("model.csv");
	update(data);
	ArrayList<double[]> dumbVelocityArray = basicVelocityArray(data);
	ArrayList<double[]> dumbAccelerationArray = basicAccelerationArray(data);
	double torque = 0.091106;
	double rotationalInertia=0.0001;
	ArrayList<double[]> motorCurve = generateMotorCurve(dumbVelocityArray,
							    dumbAccelerationArray,
							    torque,rotationalInertia);
	//ArrayList<double[]> dumbVelocityArray = basicVelocityArray(data,0.091106);

	//printArray(dumbVelocityArray);

	analyzer chart = new analyzer(
				      "Torque Vs. Speed" ,
				      "Torque Vs. Speed",
				      motorCurve);
	
	chart.pack( );
	RefineryUtilities.centerFrameOnScreen( chart );
	chart.setVisible( true );
    }
}
