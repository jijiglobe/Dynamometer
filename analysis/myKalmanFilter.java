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
    
    public void setState(DMatrixRMaj x, DMatrixRMaj P){
	this.x = new SimpleMatrix(x);
	this.P = new SimpleMatrix(P);
    }

    public SimpleMatrix getState(){
	return this.x;
    }
    
    public void setState(double[][] x, double[][] P){
	DMatrixRMaj matX = new DMatrixRMaj(x);
	DMatrixRMaj matP = new DMatrixRMaj(P);
	setState(matX,matP);
    }
    
    public void updatePredictionMatrix(DMatrixRMaj F){
	this.F = new SimpleMatrix(F);
    }

    public void updatePredictionMatrix(double[][] F){
	DMatrixRMaj matF = new DMatrixRMaj(F);
	updatePredictionMatrix(matF);
    }

    public void predict(){
	x = F.mult(x);
	P = F.mult(P).mult(F.transpose()).plus(Q);
    }

    public void update(double[][] Z, double[][] R){
	DMatrixRMaj _Z = new DMatrixRMaj(Z);
	DMatrixRMaj _R = new DMatrixRMaj(R);
	update(_Z,_R);
    }
    
    public void update(DMatrixRMaj _Z, DMatrixRMaj _R){
	// Z : actual sensor reading vector
	// R : Sensor Covariance Matrix
	/*H = SimpleMatrix.wrap(_Z);
	R = SimpleMatrix.wrap(_R);
	predict();
	
	//mu = H.mult(this.x);
	//Sigma = H.mult(P).mult(H.transpose());
	//K = Sigma.mult(R.plus(Sigma).invert());

	
	K = P.mult(H.transpose()).mult(P.plus(R).invert());

	x = x.plus(K.mult(H.minus(x)));

	P = P.minus(K.mult(H).mult(P));
	*/
	   // a fast way to make the matrices usable by SimpleMatrix
	predict();

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

    public myKalmanFilter(DMatrixRMaj x, DMatrixRMaj P, DMatrixRMaj Q, DMatrixRMaj H){
	setState(x,P);
	this.Q = new SimpleMatrix(Q);
	this.H = new SimpleMatrix(H);
	/*double[][] holder = new double[1][1];
	DMatrixRMaj matHolder = new DMatrixRMaj(holder);
	this.mu = new SimpleMatrix(matHolder);*/
    }

    public myKalmanFilter(double[][] x, double[][] P, double[][] Q, double[][] H){
	this(new DMatrixRMaj(x),new DMatrixRMaj(P),new DMatrixRMaj(Q), new DMatrixRMaj(H));
    }

}
