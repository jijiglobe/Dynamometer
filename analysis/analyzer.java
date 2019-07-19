import java.util.*;
import java.io.*;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

class analyzer extends ApplicationFrame {
    //---------------- DATA VISUALIZATION CODE BELOW------------------
    public analyzer( String applicationTitle , String chartTitle,
		     ArrayList<double[]> dataArray) {
	super(applicationTitle);
	JFreeChart lineChart = ChartFactory.createLineChart(
							    chartTitle,
							    "Torque","Speed",
							    createDataset(dataArray),
							    PlotOrientation.VERTICAL,
							    true,true,false);
         
	ChartPanel chartPanel = new ChartPanel( lineChart );
	chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
	setContentPane( chartPanel );
    }

    private DefaultCategoryDataset createDataset(ArrayList<double[]> dataArray) {
	DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
	for(double[] row : dataArray){
	    dataset.addValue((Number)row[1], "Speed" , row[0] );
	}
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
    
    public static ArrayList<double[]> basicVelocityArray(ArrayList<double[]> dataArray,
							 double springCoefficient){
	ArrayList<double[]> velocityArray = new ArrayList<double[]>();
	for(int i = 30; i < dataArray.size()-30;i++){
	    double[] newRow = new double[2];
	    newRow[0] = dataArray.get(i)[5]*springCoefficient;
	    newRow[1] = (dataArray.get(i+30)[5]-dataArray.get(i-30)[5]) /
		(dataArray.get(i+30)[0] - dataArray.get(i-30)[0]);
	    velocityArray.add(newRow);
	}
	return velocityArray;
    }
    
    public static void main(String[] args){
	ArrayList<double[]> data = readCSV("model.csv");
	update(data);
	ArrayList<double[]> dumbVelocityArray = basicVelocityArray(data,0.091106);
	printArray(dumbVelocityArray);
	analyzer chart = new analyzer(
				      "Torque Vs. Speed" ,
				      "Torque Vs. Speed",
				      dumbVelocityArray);

	chart.pack( );
	RefineryUtilities.centerFrameOnScreen( chart );
	chart.setVisible( true );
    }
}
