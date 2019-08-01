import org.ejml.simple.*;
import org.ejml.data.DMatrixRMaj;
import java.util.*;
import java.io.*;

public class filters{

    public static ArrayList<double[]> movingAverageFilter(ArrayList<double[]> data,int filterWidth){
	ArrayList<double[]> ans = new ArrayList<double[]>();
	int filterStep = filterWidth/2;
	int rowLen = data.get(0).length;
	for(int i = 0; i < data.size(); i++){
	    double[] newRow = new double[rowLen];
	    newRow[0] = data.get(i)[0];
	    if( i < filterStep || i>=data.size()-filterStep){
		newRow[1] = data.get(i)[1];
	    }else{
		double average = 0;
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

