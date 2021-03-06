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

class analyzer {

    private static int cols = 6;
    //number of columns in processed data array


    //Reads dynamometer data from a CSV, converting it into an ArrayList<double[]>
    public static ArrayList<double[]> readCSV(String filename){
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

    //Does initial data processing. (summating quadrature edges and generating corresponding angles based on quadrature data
    public static void update(ArrayList<double[]> dataArray){
	int totalEdges = 0;
	double totalTime = 0;
	double previousPosition = 0.0;
	double previousTime = 0;
	double holder;
	for(double[] row : dataArray){
	    if(row[1] - previousPosition < -10){
		totalEdges += 256;
	    }
	    previousPosition = row[1];
	    holder = row[0];
	    if(row[0] < previousTime){
		totalTime += 256;
	    }
	    row[0] = (totalTime + row[0])/Math.pow(10,6);
	    previousTime = holder;
	    row[4] = totalEdges + row[1];
	    row[5] = 2*Math.PI*row[4]/4096;
	}
    }

    //prints out an ArrayList<double[]> in human-readable form (for debugging)
    public static void printArray(ArrayList<double[]> dataArray){
	for(double[] row : dataArray){
	    for(double data : row){
		System.out.printf("%f",data);
		System.out.printf(",");
	    }
	    System.out.printf("\n");
	}
    }


    //Takes dataArray post-update and reads the true position data from the model
    public static ArrayList<double[]> createTruePositionArray(ArrayList<double[]> dataArray){
	ArrayList<double[]> ans = new ArrayList<double[]>();
	double prev = 0;
	for(int i = 0; i < dataArray.size(); i++){
	    double[] row  = dataArray.get(i);
	    double[] newRow = new double[2];
	    newRow[0] = row[0];
	    newRow[1] = row[2];
	    ans.add(newRow);
	    prev = newRow[1];
	}
	return ans;

    }

    //creates a basic array of positions based on encoder readings
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

    //Calculates the average velocity based on a position array based on the total distance travelled over 100 time intervals 
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

    //Calculates the Velocity based on the change in velocity over 500 time intervals
    public static ArrayList<double[]> basicAccelerationArray(ArrayList<double[]> dataArray){
	int width = 500;
	ArrayList<double[]> accelerationArray = new ArrayList<double[]>();
	for(int i = 0; i < dataArray.size()-5000;i++){
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

    //uses the parameters of the system, and the acceleration and velocity to generate a Velocity vs. torque curve
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
	ans.add(filters.movingAverageFilter(createPositionArray(data),100));
	ans.add(filters.movingAverageFilter(basicVelocityArray(ans.get(0)),1000));
	ans.add(filters.movingAverageFilter(basicAccelerationArray(ans.get(1)),100));
	ans.add(generateMotorCurve(ans.get(1),
				   ans.get(2),
				   torque,rotationalInertia));
	return ans;
    }
    
    //takes the updated CSV and returns the curves based on the no-noise no-sampling-error curves
    //formatted as [position,velocity,acceleration,motor curve]
    public static ArrayList<ArrayList<double[]>> trueCurveArray(ArrayList<double[]> data,double torque, double rotationalInertia){
	ArrayList<ArrayList<double[]>> ans = new ArrayList<ArrayList<double[]>>();
	ans.add(filters.movingAverageFilter(createTruePositionArray(data),100));
	ans.add(filters.movingAverageFilter(basicVelocityArray(ans.get(0)),1000));
	ans.add(filters.movingAverageFilter(basicAccelerationArray(ans.get(1)),100));
	ans.add(generateMotorCurve(ans.get(1),
				   ans.get(2),
				   torque,rotationalInertia));
	return ans;
    }

    //Uses a kalman filter with a single state variable to calculate the position with less noise
    public static ArrayList<double[]> kalmanPosition(ArrayList<double[]> data){
	System.out.println("data size: " + data.size());
	ArrayList<double[]> vholder = basicVelocityArray(data);
	System.out.println("vholder size: " + vholder.size());
	ArrayList<double[]> velocity = filters.movingAverageFilter(vholder,1000);
	System.out.println("velocity size: " + velocity.size());

	
	double[][] X = new double[1][1];
	X[0][0] = data.get(1000)[1];
	double[][] P = new double[1][1];
	P[0][0] = 0.1;
	double[][] Q = new double[1][1];
	Q[0][0] = 0.11;
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
	    sensorCovariance[0][0] = 0.1;
	    myFilter.update(sensorStateVector,sensorCovariance);
	    
	    ans.add(newRow);
	    
	}
	System.out.println("ans size: " + ans.size());
	return ans;
    }

    
    //uses the basicVelocity and basicAcceleration to calculate all arrays based on Kalman position
    //formatted as [position,velocity,acceleration,motor curve]
    public static ArrayList<ArrayList<double[]>> generateKalmanArray(ArrayList<double[]> data,double torque,double rotationalInertia){
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

}
