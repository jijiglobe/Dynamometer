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

class myKalmanFilter{
    private SimpleMatrix x,P,F,Q;
    // x : current state vector
    // P : current covariance state matrix
    // F : prediction matrix
    // Q : noise from unaccounted factors
    private SimpleMatrix mu,H,Sigma,K,Z,R;
    // mu : sensor state vector
    // H : sensor transformation matrix
    // Sigma : Sensor covariance matrix
    // K : Kalman Gain
    public void setState(DMatrixRMaj x, DMatrixRMaj P){
	this.x = new SimpleMatrix(x);
	this.P = new SimpleMatrix(P);
    }

    public void updatePredictionMatrix(DMatrixRMaj F){
	this.F = new SimpleMatrix(F);
    }

    public void predict(){
	x = F.mult(x);
	P = F.mult(P).mult(F.transpose()).plus(Q);
    }

    public void update(DMatrixRMaj _Z, DMatrixRMaj _R){
	// Z : actual sensor reading vector
	// R : Sensor Covariance Matrix
	Z = new SimpleMatrix(_Z);
	R = new SimpleMatrix(_R);
	
	mu = H.mult(x);
	
	Sigma = H.mult(P).mult(H.transpose());
	//K = Sigma.mult(R.plus(Sigma).invert());

	
	K = P.mult(H.transpose()).mult(Sigma.plus(R).invert());

	x = x.plus(K.mult(Z.minus(mu)));

	P = P.minus(K.mult(H).mult(P));

    }

    public myKalmanFilter(DMatrixRMaj x, DMatrixRMaj P, DMatrixRMaj Q){
	setState(x,P);
	this.Q = new SimpleMatrix(Q);
    }
    
}
