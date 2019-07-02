import java.util.*;
import java.io.*;

public class motorModel{
    private class motor{
	private double stall, free, torquePerSpeed, speedPerTorque;
	public motor(double stallTorque, double freeSpeed){
	    //torque should be in N * M
	    //free speed should be in RPM
	    //system will automatically convert to rad/s
	    stall = stallTorque;
	    free = freeSpeed;
	    torquePerSpeed = stall/free;
	    speedPerTorque = free/stall;
	}

	public double getSpeed(double torque){
	    return torque * speedPerTorque;
	}

	public double getTorque(double speed){
	    return speed * torquePerSpeed;
	}
    }

    private class inertialDisk{
	private double inertia, velocity, angle;
	public inertialDisk(double rotationalInertia){
	    inertia = rotationalInertia;
	    angle = 0.0;
	}

	public double accelerate(double torque,double time){
	    double acceleration = torque / inertia;
	    double newVelocity = velocity + (acceleration * time);
	    double averageVelocity = (newVelocity + velocity)/2;
	    angle += averageVelocity * time;
	    return angle;
	}
	public double getVelocity(){
	    return velocity;
	}
	public double getAngle(){
	    return angle;
	}
    }

    private class spring{
	private double springConstant;

	public spring(double constant){
	    constant = springConstant;
	}
	public double getTorque(double angle){
	    return springConstant * angle;
	}
    }

    public static void main(String[] args){
	
    }
}
