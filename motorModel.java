
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
	//returns speed in rad/s given torque in Nm
	if(torque > 0 && torque < stall)
	    return free - (torque * speedPerTorque);
	else if(torque <= 0)
	    return free;
	else
	    return 0.0;
	
    }

    public double getTorque(double speed){
	//returns torque in Nm given speed in rad/s
	if(speed > 0 && speed < free){
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
	//takes rotational Inertia in Kg/m^2
	inertia = rotationalInertia;
	velocity = 0.0;
	angle = 0.0;
    }

    public double accelerate(double torque,double time){
	//simulates a constant torque over time
	//torque in Nm, time in s
	double acceleration = torque / inertia;
	double newVelocity = velocity + (acceleration * time);
	double averageVelocity = (newVelocity + velocity)/2;
	velocity = newVelocity;
	angle += averageVelocity * time;
	return angle;
    }
    public double getVelocity(){
	//returns angular velocity of the system in rad/s
	return velocity;
    }
    public double getAngle(){
	//returns the total angular displacement of the disk in radians
	return angle;
    }
}

class spring{
    private double springConstant;

    public spring(double constant){
	//takes spring constant in Nm/rad
	springConstant = constant;
	
    }
    public double getTorque(double angle){
	//returns torque in Nm given angle in radians 
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
		 double timeResolution){
	//takes a properly constructed motor, disk and spring
	//time resolution determines how often the system updates
	//verbose determines if the system will print extra data when run
	this.myMotor = myMotor;
	this.myDisk = myDisk;
	this.mySpring = mySpring;
	increment = timeResolution;
	time = 0.0;
    }
    public model(motor myMotor,inertialDisk myDisk, spring mySpring,
		 double timeResolution, boolean verbose){
	this(myMotor,myDisk,mySpring,timeResolution);
	isVerbose = verbose;
    }
    public double step(){
	//increment the entire system by single step
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
	//repeatedly increments system
	//will fail if system never reaches back back below 0.1rad/s
	//   - note that this shouldn't be possible
	try{
	    BufferedWriter fileWriter = new BufferedWriter(new FileWriter("model.csv"));
	    String line = String.format("Time(s),Angle(rad),Angular Velocity(rad/s)");
	    fileWriter.write(line);
	    
	    double previousSpeed = 0.0;
	    double currentSpeed = 0.0;
	    
	    while(currentSpeed >= previousSpeed ||
		  !(currentSpeed < 0.1 && previousSpeed >=0.1)){
		previousSpeed = currentSpeed;
		currentSpeed = step();
		//System.out.printf("\nTime: %5.2f\nPosition: %5.2f, Angular Velocity: %5.2f\n",
		//		  time,myDisk.getAngle(),currentSpeed);
		line = String.format("\n%f,%f,%f",time,myDisk.getAngle(),currentSpeed);
		fileWriter.write(line);
		
	    }
	    fileWriter.close();
	    
	} catch(IOException erorr){
	    System.out.println("Encountered an IOException");
	}
	
    }
    
}

public class motorModel{

    public static void main(String[] args){
	motor myMotor = new motor(2.41, 5330);
	inertialDisk myDisk = new inertialDisk(0.1);
	spring mySpring = new spring(.5); 
	model myModel = new model(myMotor,myDisk,mySpring,0.01);
	myModel.run();
    }
    
}
