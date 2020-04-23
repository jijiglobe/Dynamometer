import java.util.Random;
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
class encoder{
    private int EPR,totalCounter;
    private double step, noise;
    public encoder(int edgesPerRevolution){
	EPR = edgesPerRevolution;
	step = 2*Math.PI/EPR;
	totalCounter = 0;
	noise = 0.0;
    }

    public encoder(int edgesPerRevolution, double noise){
	this(edgesPerRevolution);
	this.noise = noise;
    }
    
    public int readAndClear(double displacement){
	int newCounter = (int) (displacement / step);
	int countHolder = newCounter - totalCounter;
	totalCounter = newCounter;
	double randomNumber = Math.random();
	if(randomNumber < noise){
	    //System.out.println("plus one");
	    return countHolder+1;
	}
	if(randomNumber < noise * 2 && countHolder > 0){
	    //System.out.println("minus one");
	    return countHolder-1;
	}
	return countHolder;
    }
}

class model{
    private motor myMotor;
    private inertialDisk myDisk;
    private spring mySpring;
    private encoder myEncoder;
    private double increment,time,samplingPeriod,resetTime;
    private boolean isVerbose;
    
    public model(motor myMotor,inertialDisk myDisk, spring mySpring,
		 encoder myEncoder,int timeResolution,int sampleRate){
	//takes a properly constructed motor, disk and spring
	//time resolution determines how often the system updates
	//samples determines the period between samples of the decoder
	//verbose determines if the system will print extra data when run
	this.myMotor = myMotor;
	this.myDisk = myDisk;
	this.mySpring = mySpring;
	this.myEncoder = myEncoder;
	increment = 1/(double)timeResolution;
	samplingPeriod = 1/(double)sampleRate;
	resetTime = 0;
	time = 0.0;//the time since the system was initialized.
	isVerbose = false;
    }
    public model(motor myMotor,inertialDisk myDisk, spring mySpring,
		 encoder myEncoder,int timeResolution,int sampleRate,  boolean verbose){
	this(myMotor,myDisk,mySpring,myEncoder,timeResolution,sampleRate);
	isVerbose = verbose;
    }
    public double step(){
	//increment the entire system by single step
	time += increment;
	resetTime += increment;
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
	    String line = String.format("Time(s),edges,TrueAngle,TrueVelocity");
	    fileWriter.write(line);
	    
	    double previousSpeed = 0.0;
	    double currentSpeed = 0.0;
	    
	    while(currentSpeed >= previousSpeed ||
		  !(currentSpeed < 0.1 && previousSpeed >=0.1)){
		previousSpeed = currentSpeed;
		currentSpeed = step();
		//System.out.printf("\nTime: %5.2f\nPosition: %5.2f, Angular Velocity: %5.2f\n",
		//		  time,myDisk.getAngle(),currentSpeed);
		if(resetTime > samplingPeriod){
		    line = String.format("\n%f,%d,%f,%f",time,myEncoder.readAndClear(myDisk.getAngle()),myDisk.getAngle(),myDisk.getVelocity());
		    resetTime = 0;
		    fileWriter.write(line);
		}
		
	    }
	    fileWriter.close();
	    
	} catch(IOException erorr){
	    System.out.println("Encountered an IOException");
	}
	
    }
    
}
