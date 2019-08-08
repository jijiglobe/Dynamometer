import org.ejml.simple.*;
import org.ejml.data.DMatrixRMaj;
import java.util.*;
import java.io.*;

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
    
    //use to manually update state using a DMatrixRMaj
    public void setState(DMatrixRMaj x, DMatrixRMaj P){
	this.x = new SimpleMatrix(x);
	this.P = new SimpleMatrix(P);
    }

    //use to manually update state using a double[][]
    public void setState(double[][] x, double[][] P){
	DMatrixRMaj matX = new DMatrixRMaj(x);
	DMatrixRMaj matP = new DMatrixRMaj(P);
	setState(matX,matP);
    }
    
    //use to get the state vector
    public SimpleMatrix getState(){
	return this.x;
    }

    //use to manually update prediction matrix (for nonlinear applications)
    //takes a DMatrixRMaj
    public void updatePredictionMatrix(DMatrixRMaj F){
	this.F = new SimpleMatrix(F);
    }

    //use to manually update prediction matrix (for nonlinear applications)
    //takes a double[][]
    public void updatePredictionMatrix(double[][] F){
	DMatrixRMaj matF = new DMatrixRMaj(F);
	updatePredictionMatrix(matF);
    }

    //does prediction step, not currently used.
    public void predict(){
	x = F.mult(x);
    }

    //Update step for filter: wrapper function takes double[][]s and calls the DMatrixRMaj version
    public void update(double[][] Z, double[][] R){
	DMatrixRMaj _Z = new DMatrixRMaj(Z);
	DMatrixRMaj _R = new DMatrixRMaj(R);
	update(_Z,_R);
    }

    //prints SimpleMatrix out in human readable form
    public void print2DArray(SimpleMatrix arr){
	System.out.printf("[[");
	for(int c = 0; c < arr.numCols();c++){
	    System.out.printf("%5.2f",arr.get(0,c));
	}
	System.out.printf("]");
	
	for(int i = 1; i < arr.numRows();i++){
	    System.out.printf("\n[");
	    for(int c = 1; c < arr.numCols();c++){
		System.out.printf(",%5.2f",arr.get(i,c));
	    }
	    System.out.printf("]");
	}
	System.out.printf("]\n");
    }

    //prints all the relevant state matrices
    public void printFilterState(){
	System.out.println("State Vector: ");
	print2DArray(this.x);
	System.out.println("");
	
	System.out.println("State Covariance: ");
	print2DArray(this.P);
	System.out.println("");

	System.out.println("Prediction: ");
	print2DArray(this.F);
	System.out.println("");
	
    }
    
    //calls the update step of the function
    public void update(DMatrixRMaj _Z, DMatrixRMaj _R){
	// Z : actual sensor reading vector
	// R : Sensor Covariance Matrix

	SimpleMatrix z = SimpleMatrix.wrap(_Z);
        SimpleMatrix R = SimpleMatrix.wrap(_R);
	
        // y = z - H x
        SimpleMatrix y = z.minus(H.mult(x));
	
        // S = H P H' + R
        SimpleMatrix S = H.mult(P).mult(H.transpose()).plus(R);
	
        // K = PH'S^(-1)
        SimpleMatrix K = P.mult(H.transpose().mult(S.invert()));
	// x = x + Ky
        x = x.plus(K.mult(y));

        // P = (I-kH)P = P - KHP
        P = P.minus(K.mult(H).mult(P));
    }

    //Constructor for the filter taking DMatrixRMajs
    // x : Initial state vector
    // P : Initial state Covariance
    // Q : Noise from un-modeled sources at each step
    // H : Transformation matrix for sensor state to state vector
    public myKalmanFilter(DMatrixRMaj x, DMatrixRMaj P, DMatrixRMaj Q, DMatrixRMaj H){
	setState(x,P);
	double[][] tempPrediction = new double[1][1];
	this.F = new SimpleMatrix(new DMatrixRMaj(tempPrediction));
	printFilterState();
	this.Q = new SimpleMatrix(Q);
	this.H = new SimpleMatrix(H);
    }

    //wrapper constructor that takes double[][]s
    // x : Initial state vector
    // P : Initial state Covariance
    // Q : Noise from un-modeled sources at each step
    // H : Transformation matrix for sensor state to state vector
    public myKalmanFilter(double[][] x, double[][] P, double[][] Q, double[][] H){
	this(new DMatrixRMaj(x),new DMatrixRMaj(P),new DMatrixRMaj(Q), new DMatrixRMaj(H));
    }

}
