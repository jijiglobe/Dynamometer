import java.util.Random;
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
import org.ejml.simple.*;
import org.ejml.data.DMatrixRMaj;

class main{
    public static void simulate(){
	parameters params = new parameters();
        motor myMotor = new motor(params.motorStall, params.motorFree);
        inertialDisk myDisk = new inertialDisk(params.diskInertia);
        spring mySpring = new spring(params.springCoeff);
        encoder myEncoder = new encoder(params.encoderEPR,params.encoderError);
        model myModel = new model(myMotor,myDisk,mySpring,myEncoder,params.timeResolution,params.decoderSampleRate,false);
        myModel.run();
    }
    public static void visualize(){
        ArrayList<double[]> data = analyzer.readCSV("model.csv");
        analyzer.update(data);

	ArrayList<ArrayList<double[]>> kalmanBS = analyzer.generateKalmanArray(data);
        ArrayList<ArrayList<double[]>> basicBS = analyzer.trueCurveArray(data);

        display.displayCharts(kalmanBS,basicBS);

    }
    
    public static void main(String[] args){
	System.out.println("Simulating...");
	simulate();	
	System.out.println("visualizing...");
	visualize();
    }
}

