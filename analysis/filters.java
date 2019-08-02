import org.ejml.simple.*;
import org.ejml.data.DMatrixRMaj;
import java.util.*;
import java.io.*;

public class filters{

    public static ArrayList<double[]> movingAverageFilter(ArrayList<double[]> data,int filterWidth){
	ArrayList<double[]> ans = new ArrayList<double[]>();
	int filterStep = filterWidth/2;
	int rowLen = data.get(0).length;
	double average;
	int counter;
	for(int i = 0; i < data.size(); i++){
	    double[] newRow = new double[rowLen];
	    newRow[0] = data.get(i)[0];
	    average = 0;
	    counter = 0;
	    if( i < filterStep){
		
		for(int c = 0; c <= i+filterStep;c++){
		    average+= data.get(c)[1];
		    counter++;
		}
		newRow[1] = average/counter;
	    }else if(i>=data.size()-filterStep){
		for(int c = i-filterStep; c <data.size();c++){
		    average+= data.get(c)[1];
		    counter++;
		}
		newRow[1] = average/counter;
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

