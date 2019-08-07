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
    public analyzer( String xvar , String yvar,
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
    public analyzer( String xvar , String yvar,
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

    private XYSeriesCollection createDataset(ArrayList<double[]> dataArray) {
	XYSeriesCollection dataset = new XYSeriesCollection( );
	XYSeries series = new XYSeries("movingAverageFilter(100) - no Noise");
	for(double[] row : dataArray){
	    series.add(row[0],row[1]);
	}
	dataset.addSeries(series);
	return dataset;
    }

    private XYSeriesCollection createDataset(ArrayList<double[]> dataArray, ArrayList<double[]> dataArray2) {
	XYSeriesCollection dataset = new XYSeriesCollection( );
	XYSeries series = new XYSeries("movingAverageFilter(100) - no Noise");
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
	//[time(s),edges,trueAngle,trueVelocity,totalEdges,Angle(rad)]
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

    public static ArrayList<double[]> createTruePositionArray(ArrayList<double[]> dataArray){
	ArrayList<double[]> ans = new ArrayList<double[]>();
	for(double[] row : dataArray){
	    double[] newRow = new double[2];
	    newRow[0] = row[0];
	    newRow[1] = row[2];
	    ans.add(newRow);
	}
	return ans;

    }
    public static ArrayList<double[]> createPositionArray(ArrayList<double[]> dataArray){
	ArrayList<double[]> ans = new ArrayList<double[]>();
	for(double[] row : dataArray){
	    double[] newRow = new double[2];
	    newRow[0] = row[0];
	    newRow[1] = row[5];
	    ans.add(newRow);
	}
	return ans;
    }
    
    public static ArrayList<double[]> basicVelocityArray(ArrayList<double[]> dataArray){
	int width = 100;
	ArrayList<double[]> velocityArray = new ArrayList<double[]>();
	for(int i = 0; i < dataArray.size();i++){
	    double[] newRow = new double[3];
	    if( i < width ){
		newRow[0] = dataArray.get(i)[0];
		newRow[1] = (dataArray.get(i+width)[1]-dataArray.get(0)[1]) /
		    (dataArray.get(i+width)[0] - dataArray.get(0)[0]);
		newRow[2] = dataArray.get(i)[1];
	    }
	    else if( i >= dataArray.size() - width ){
		newRow[0] = dataArray.get(i)[0];
		newRow[1] = (dataArray.get(dataArray.size()-1)[1]-dataArray.get(i-width)[1]) /
		    (dataArray.get(dataArray.size()-1)[0] - dataArray.get(i-width)[0]);
		newRow[2] = dataArray.get(i)[1];
	    }else{
		newRow[0] = dataArray.get(i)[0];
		newRow[1] = (dataArray.get(i+width)[1]-dataArray.get(i-width)[1]) /
		    (dataArray.get(i+width)[0] - dataArray.get(i-width)[0]);
		newRow[2] = dataArray.get(i)[1];
	    }
	    if(true){
		velocityArray.add(newRow);
	    }
	}
	return velocityArray;
    }
    
    public static ArrayList<double[]> basicAccelerationArray(ArrayList<double[]> dataArray){
	int width = 500;
	ArrayList<double[]> accelerationArray = new ArrayList<double[]>();
	for(int i = 0; i < dataArray.size();i++){
	    double[] newRow = new double[2];
	    if( i < width){
		newRow[0] = dataArray.get(i)[0];
		newRow[1] = (dataArray.get(i+width)[1]-dataArray.get(0)[1]) /
		    (dataArray.get(i+width)[0] - dataArray.get(0)[0]);

	    }
	    else if(i >= dataArray.size() - width){
		newRow[0] = dataArray.get(i)[0];
		newRow[1] = (dataArray.get(dataArray.size()-1)[1]-dataArray.get(i-width)[1]) /
		    (dataArray.get(dataArray.size()-1)[0] - dataArray.get(i-width)[0]);

	    }else{
		newRow[0] = dataArray.get(i)[0];
		newRow[1] = (dataArray.get(i+width)[1]-dataArray.get(i-width)[1]) /
		    (dataArray.get(i+width)[0] - dataArray.get(i-width)[0]);
	    }
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


    //takes the updated CSV array and returns the curves in [position,vel,acc,motor_curve] form
    public static ArrayList<ArrayList<double[]>> basicCurveArray(ArrayList<double[]> data){
	double torque = 0.091106;
	double rotationalInertia=0.0001;
	ArrayList<ArrayList<double[]>> ans = new ArrayList<ArrayList<double[]>>();
	//ArrayList<double[]> positionArray = filters.movingAverageFilter(createPositionArray(data),100);
	ans.add(filters.movingAverageFilter(createPositionArray(data),100));
	ans.add(filters.movingAverageFilter(basicVelocityArray(ans.get(0)),1000));
	ans.add(filters.movingAverageFilter(basicAccelerationArray(ans.get(1)),100));
	ans.add(generateMotorCurve(ans.get(1),
				   ans.get(2),
				   torque,rotationalInertia));
	return ans;
    }
    
    public static ArrayList<ArrayList<double[]>> trueCurveArray(ArrayList<double[]> data){
	double torque = 0.091106;
	double rotationalInertia=0.0001;
	ArrayList<ArrayList<double[]>> ans = new ArrayList<ArrayList<double[]>>();
	//ArrayList<double[]> positionArray = filters.movingAverageFilter(createPositionArray(data),100);
	ans.add(filters.movingAverageFilter(createTruePositionArray(data),100));
	ans.add(filters.movingAverageFilter(basicVelocityArray(ans.get(0)),1000));
	ans.add(filters.movingAverageFilter(basicAccelerationArray(ans.get(1)),100));
	ans.add(generateMotorCurve(ans.get(1),
				   ans.get(2),
				   torque,rotationalInertia));
	return ans;
    }
    
    public static ArrayList<double[]> kalmanPosition(ArrayList<double[]> data){
	System.out.println("data size: " + data.size());
	ArrayList<double[]> vholder = basicVelocityArray(data);
	System.out.println("vholder size: " + vholder.size());
	ArrayList<double[]> velocity = filters.movingAverageFilter(vholder,1000);
	System.out.println("velocity size: " + velocity.size());

	//fix this shit
	double[][] X = new double[1][1];
	X[0][0] = data.get(1000)[1];
	double[][] P = new double[1][1];
	P[0][0] = 0;
	double[][] Q = new double[1][1];
	Q[0][0] = .11;
	double[][] H = new double[1][1];
	H[0][0] = 2*Math.PI/4096;

	double[][] newPredictionMatrix = new double[1][1];
	double[][] sensorStateVector = new double[1][1];
	double[][] sensorCovariance = new double[1][1];

        double timeStep;
	myKalmanFilter myFilter = new myKalmanFilter(X,P,Q,H);
	
	ArrayList<double[]> ans = new ArrayList<double[]>();
	for(int i = 1000; i < data.size()-1000; i++){
	    
	    double[] newRow = new double[2];
	    newRow[0] = data.get(i)[0];
	    newRow[1] = myFilter.getState().get(0,0);
	    timeStep = data.get(i+1)[0] - newRow[0]; 
	    newPredictionMatrix[0][0] = velocity.get(i)[1];
	    myFilter.updatePredictionMatrix(newPredictionMatrix);
	    
	    sensorStateVector[0][0] = data.get(i)[1];
	    sensorCovariance[0][0] = 0;
	    myFilter.update(sensorStateVector,sensorCovariance);
	    //System.out.printf("%f\n",newRow[1]);
	    ans.add(newRow);
	    myFilter.printFilterState();
	}
	System.out.println("ans size: " + ans.size());
	return ans;
    }

    
    
    public static ArrayList<ArrayList<double[]>> generateKalmanArray(ArrayList<double[]> data){
	double torque = 0.091106;
	double rotationalInertia=0.0001;
	ArrayList<ArrayList<double[]>> ans = new ArrayList<ArrayList<double[]>>();
	ArrayList<double[]> positionArray = kalmanPosition(data);
	ans.add(positionArray);
	ans.add(filters.movingAverageFilter(basicVelocityArray(ans.get(0)),1000));
	ans.add(filters.movingAverageFilter(basicAccelerationArray(ans.get(1)),100));
	ans.add(generateMotorCurve(ans.get(1),
				   ans.get(2),
				   torque,rotationalInertia));
	return ans;
    }
    
    public static void displayCharts(ArrayList<ArrayList<double[]>> curves,ArrayList<ArrayList<double[]>> curves2){
	analyzer chart = new analyzer(
				      "Time(s)",
				      "Position(rad)" ,
				      curves.get(0),curves2.get(0));
	
	chart.pack( );
	chart.setVisible( true ); 
	
	analyzer chart2 = new analyzer(
				       "Time(s)",
				       "Angular Velocity(rad/s)" ,
				       curves.get(1),curves2.get(1));
	
	chart2.pack( );
	chart2.setLocation(630,0);
	chart2.setVisible( true );
	analyzer chart3 = new analyzer(
				       "Time(s)",
				       "Angular Acceleration(rad/s^2)" ,
				       curves.get(2),curves2.get(2));
	
	chart3.pack( );
	chart3.setLocation(0,427);
	chart3.setVisible( true ); 
	analyzer chart4 = new analyzer(
				       "Torque(N*M)" ,
				       "Angular Velocity(Rad/S)",
				       curves.get(3),curves2.get(3));
	
	chart4.pack( );
	chart4.setLocation(630,427);
	chart4.setVisible( true );	
    }
    
    public static void displayCharts(ArrayList<ArrayList<double[]>> curves){
	analyzer chart = new analyzer(
				      "Time(s)",
				      "Position(rad)" ,
				      curves.get(0));
	
	chart.pack( );
	chart.setVisible( true ); 
	
	analyzer chart2 = new analyzer(
				       "Time(s)",
				       "Angular Velocity(rad/s)" ,
				       curves.get(1));
	
	chart2.pack( );
	chart2.setLocation(630,0);
	chart2.setVisible( true );
	analyzer chart3 = new analyzer(
				       "Time(s)",
				       "Angular Acceleration(rad/s^2)" ,
				       curves.get(2));
	
	chart3.pack( );
	chart3.setLocation(0,427);
	chart3.setVisible( true ); 
	analyzer chart4 = new analyzer(
				       "Torque(N*M)" ,
				       "Angular Velocity(Rad/S)",
				       curves.get(3));
	
	chart4.pack( );
	chart4.setLocation(630,427);
	chart4.setVisible( true );	
    }
    
    public static void main(String[] args){
	ArrayList<double[]> data = readCSV("model.csv");
	update(data);

	//ArrayList<ArrayList<double[]>> kalmanBS = basicCurveArray(data);//generateKalmanArray(data);
	ArrayList<ArrayList<double[]>> kalmanBS = generateKalmanArray(data);
	ArrayList<ArrayList<double[]>> basicBS = trueCurveArray(data);
	//printArray(basicBS.get(0));
	//displayCharts(kalmanBS,basicBS);//,kalmanBS);
	displayCharts(kalmanBS);
    }
}
