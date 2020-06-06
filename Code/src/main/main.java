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
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class main{
    public static void simulate(JTextField stall,
				JTextField free,
				JTextField inertia,
				JTextField coeff,
				JTextField EPR,
				JTextField Error,
				JTextField resolution,
				JTextField sampleRate){
	double motorStall = Double.parseDouble(stall.getText());
	double motorFree = Double.parseDouble(free.getText());
	double diskInertia = Double.parseDouble(inertia.getText());
	double springCoeff = Double.parseDouble(coeff.getText());
	int encoderEPR = (int) Double.parseDouble(EPR.getText());
	double encoderError = Double.parseDouble(Error.getText());
	int timeResolution = (int) Double.parseDouble(resolution.getText());
	int decoderSampleRate = (int) Double.parseDouble(sampleRate.getText());

        motor myMotor = new motor(motorStall, motorFree);
        inertialDisk myDisk = new inertialDisk(diskInertia);
        spring mySpring = new spring(springCoeff);
        encoder myEncoder = new encoder(encoderEPR,encoderError);
        model myModel = new model(myMotor,myDisk,mySpring,myEncoder,timeResolution,decoderSampleRate,false);
        myModel.run();
	System.out.println("Simulation Complete");
    }
    
    public static void visualize(JTextField inertia,
				JTextField coeff,
				 JTextField EPR,JTextField dataFile){

	double diskInertia = Double.parseDouble(inertia.getText());
	double springCoeff = Double.parseDouble(coeff.getText());
	int encoderEPR = (int) Double.parseDouble(EPR.getText());
	String dataFileName = dataFile.getText();
	
        ArrayList<double[]> data = analyzer.readCSV(dataFileName);
        analyzer.update(data);

	//uses torque and rotationalinertia
	ArrayList<ArrayList<double[]>> kalmanBS = analyzer.generateKalmanArray(data,springCoeff,diskInertia);
	//uses torque and rotationalinertia
        ArrayList<ArrayList<double[]>> basicBS = analyzer.trueCurveArray(data,springCoeff,diskInertia);

        display.displayCharts(kalmanBS,basicBS);

    }

    public static JTextField addTextBox(JPanel textPanel,String name,
					String defaultValue,int height){
	GridBagConstraints gbc = new GridBagConstraints();
	gbc.gridy = height;
	JLabel label = new JLabel(name);
	JTextField tf = new JTextField(20);
	tf.setText(defaultValue);
	textPanel.add(label,gbc);
	textPanel.add(tf,gbc);
	return tf;
    }
    
    public static JTextField addTextBox(JPanel textPanel,String name,
					double defaultValue,int height){
	String val = ""+defaultValue;
	return addTextBox(textPanel,name,val,height);
    }
    
    public static JButton addButton(JPanel textPanel, String name, int height){
	GridBagConstraints gbc = new GridBagConstraints();
	gbc.gridy = height;
	JButton button = new JButton(name);
	textPanel.add(button,gbc);
	return button;
    }
    
    public static void main(String[] args){
	JFrame frame = new JFrame("Dyno Gui");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setSize(600,300);

	JPanel panel = new JPanel();
	panel.setLayout(new GridBagLayout());
	
	parameters params = new parameters();	
	JTextField stall = addTextBox(panel,"Motor Stall",params.motorStall,0);
	JTextField free = addTextBox(panel,"Motor Free",params.motorFree,1);
	JTextField inertia = addTextBox(panel,"Disk Inertia",params.diskInertia,2);
	JTextField coeff = addTextBox(panel,"Spring Coefficient",params.springCoeff,3);
	JTextField EPR = addTextBox(panel,"Encoder EPR",params.encoderEPR,4);
	JTextField Error = addTextBox(panel,"Encoder Error",params.encoderError,5);
	JTextField resolution = addTextBox(panel,"Time Resolution",params.timeResolution,6);
	JTextField sampleRate = addTextBox(panel,"Decoder Sample Rate",params.decoderSampleRate,7);
	JTextField dataFile = addTextBox(panel,"Data CSV File","model.csv",8);
	
	JButton simButton = addButton(panel,"Run Simulation", 9);
	simButton.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e){
		    simulate(stall,free,inertia,coeff,EPR,Error,resolution,sampleRate);
		}
	    });
	
	JButton anButton = addButton(panel,"Run Analysis",9);
	anButton.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e){
		    visualize(inertia,coeff,EPR,dataFile);
		}
	    });
	
	frame.getContentPane().add(panel);
	
	frame.setVisible(true);
	return;
	/*simulate();	
	  visualize();*/
    }
}

