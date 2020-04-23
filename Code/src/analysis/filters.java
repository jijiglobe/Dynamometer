import org.ejml.simple.*;
import org.ejml.data.DMatrixRMaj;
import java.util.*;
import java.io.*;

public class filters{

    //moving average filter smooths data by replacing each data value by its local average
    public static ArrayList<double[]> movingAverageFilter(ArrayList<double[]> data,int filterWidth){
	ArrayList<double[]> ans = new ArrayList<double[]>();
	int filterStep = filterWidth/2;
	int rowLen = data.get(0).length;
	double average;
	int counter;
	for(int i = filterStep; i < data.size(); i++){
	    double[] newRow = new double[rowLen];
	    newRow[0] = data.get(i)[0];
	    average = 0;
	    counter = 0;
	    if( i < filterStep || i>=data.size()-filterStep){
		//This segment is commented out due to an error where it's not possible to average data from the edges where the filter overlaps the edge of the array
		
	    }else{
		
		for(int c = i-filterStep; c <= i+filterStep;c++){
		    average+= data.get(c)[1];
		}
		newRow[1] = average/filterWidth;		
	    }
	    for(int c = 2; c< rowLen; c++){
		newRow[c] = data.get(i)[c];
	    }
	    ans.add(newRow);
	}
	return ans;
    }
}

