import java.util.*;
import java.io.*;

class motor{
    private double stall, free, torquePerSpeed, speedPerTorque;
    public motor(double stallTorque, double freeSpeed){
	//torque should be in N * M
	//free speed should be in RPM
	//system will automatically convert to rad/s
	stall = stallTorque;
	free = freeSpeed * 2 * Math.PI / 60.0;
	torquePerSpeed = stall/free;
	speedPerTorque = free/stall;
    }

    public double getSpeed(double torque){
	if(torque > 0 && torque < stall)
	    return free - (torque * speedPerTorque);
	else if(torque <= 0)
	    return free;
	else
	    return 0.0;
	
    }

    public double getTorque(double speed){
	if(speed > 0 && speed < free){
	    //System.out.println("STALL: "+stall);
	    return stall - (speed * torquePerSpeed);
	}else if(speed <= 0)
	    return stall;
	else
	    return 0.0;
    }
    
}

class inertialDisk{
    private double inertia, velocity, angle;
    public inertialDisk(double rotationalInertia){
	inertia = rotationalInertia;
	velocity = 0.0;
	angle = 0.0;
    }

    public double accelerate(double torque,double time){
	double acceleration = torque / inertia;
	double newVelocity = velocity + (acceleration * time);
	double averageVelocity = (newVelocity + velocity)/2;
	velocity = newVelocity;
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

class spring{
    private double springConstant;

    public spring(double constant){
	springConstant = constant;
	//System.out.println("CONST: "+springConstant);
    }
    public double getTorque(double angle){
	//System.out.printf("\n\n const: %52f",springConstant);
	return springConstant * angle;
	
    }
}

class model{
    private motor myMotor;
    private inertialDisk myDisk;
    private spring mySpring;
    private double increment,time;
    private boolean isVerbose;
    
    public model(motor myMotor,inertialDisk myDisk, spring mySpring,
		 double timeResolution ){
	this.myMotor = myMotor;
	this.myDisk = myDisk;
	this.mySpring = mySpring;
	increment = timeResolution;
	time = 0.0;
    }

    public model(motor myMotor,inertialDisk myDisk, spring mySpring,
		 double timeResolution,boolean verbose ){
	this(myMotor,myDisk,mySpring,timeResolution);
	isVerbose = true;
    }
    
    public double step(){
	time += increment;
	double speed = myDisk.getVelocity();
	double motorTorque = myMotor.getTorque(speed);
	double springTorque = mySpring.getTorque(myDisk.getAngle());
	if(isVerbose)
	    System.out.printf("Motor Torque: %5.2f, Spring Torque: %5.2f\n",
			      motorTorque,springTorque);
	myDisk.accelerate(motorTorque-springTorque,increment);
	return speed;
    }

    public void run(){
	System.out.println("Beginning Simulation");
	double previousSpeed = 0.0;
	double currentSpeed = 0.0;
	while(currentSpeed >= previousSpeed ||
	      !(currentSpeed < 0.1 && previousSpeed >=0.1)){
	    //System.out.println("stepping...");
	    previousSpeed = currentSpeed;
	    currentSpeed = step();
	    System.out.printf("\nTime: %5.2f\nPosition: %5.2f, Angular Velocity: %5.2f\n",
			      time,myDisk.getAngle(),currentSpeed);
	}
	
    }
    
}

public class motorModel{

    public static void main(String[] args){
	motor myMotor = new motor(2.41, 5330);
	inertialDisk myDisk = new inertialDisk(0.1);
	spring mySpring = new spring(.5); 
	model myModel = new model(myMotor,myDisk,mySpring,0.01,false);
	myModel.run();
    }
    
}
