package org.usfirst.frc.team5975.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	RobotDrive myRobot;
	VictorSP leftMotor ;
	VictorSP rightMotor ;
	VictorSP eleMotor;
	Ultrasonic distanceSensor;
	// RoboRio mapping
	int leftMotorChannel=1;
	int rightMotorChannel=2;
	int eleMotorChannel = 3;
	
	//IO pin mapping
	int echoSensorPin=3;
	int pingSensorPin=4;
	int upperLimitPin=1;
	int lowerLimitPin=2;
	
	// Driver Station / controller mapping
	int joyPort=0;
	int lTrigger = 2;
	int rTrigger = 3;
	Joystick stick;
	
	// maximum values adjustments need to be made
	double inchesFromWall = 8.0;
	int turnValue= 13;
	
	int leftLoopCounter;
	int rightLoopCounter;
	int moveCounter;
	
	// Declaring XBox buttons
	
	//digital inputs
	DigitalInput upperLimit;
	DigitalInput lowerLimit;
	
	
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
    	leftMotor=new VictorSP(leftMotorChannel);
    	rightMotor=new VictorSP(rightMotorChannel);
    	myRobot =new RobotDrive(leftMotor,rightMotor);
    	eleMotor = new VictorSP(eleMotorChannel);
    	leftMotor.setInverted(false);
    	distanceSensor = new Ultrasonic(pingSensorPin,echoSensorPin);
    	upperLimit = new DigitalInput(upperLimitPin);
    	lowerLimit = new DigitalInput(lowerLimitPin);
    	
    	
    	stick = new Joystick(joyPort);
        
    }
    
    /**
     * This function is run once each time the robot enters autonomous mode
     */
    public void autonomousInit() {
    	leftLoopCounter = 0;
    	rightLoopCounter = 0;
    	moveCounter = 0;
    }

    /**
     * This function is called periodically during autonomous
     * turn 90 degrees left
     * go forward until wall 2 inches away
     * turn 90 degrees right
     * go forward through low bar 12 inches
     */
    
    public void autonomousPeriodic() {
    	//start by turning left
    	
    	while (leftLoopCounter < turnValue)
    	{
    		myRobot.tankDrive(1.0, -1.0);
    		leftLoopCounter++;
    	}
    	//keep moving until you hit 2 inches from the wall
    	
    	while (distanceSensor.getRangeInches()>inchesFromWall)
    
		{
			myRobot.drive(-0.5, 0.0); 	// drive forwards half speed
		} 
    	
		myRobot.drive(0.0, 0.0); 	// stop robot
			
    	//turn 90 degrees right
		//you look fabulous today
    	while (rightLoopCounter < turnValue)	
    	{
    		myRobot.tankDrive(-1.0, 1.0);
    		rightLoopCounter++;
    	}
    	
    	while (moveCounter < 100)
    	{
    		myRobot.drive(-0.5, 0.0);
    	}
    	myRobot.drive(0.0, 0.0); 	// stop robot
		
    }
    //hi hi
    /**
     * This function is called once each time the robot enters tele-operated mode
     */
    public void teleopInit(){
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
    	// went left instead of right
    	double xAxis = stick.getX ();
    	xAxis = -xAxis;
    	myRobot.arcadeDrive(stick.getY(), xAxis, false);
        moveElevator();
       
    }
     public void moveElevator() {
    	
    	 // read axis value
 
    	 double myLAxis =  stick.getRawAxis(lTrigger);
    	 double myRAxis =  stick.getRawAxis(rTrigger);
    	 double myAxis = myLAxis + myRAxis;
    	 if(myAxis > 0 && upperLimit.get() == false)
    	 {
    		 eleMotor.set(myAxis);
    		 
    	 }
    	 if(myAxis < 0 && lowerLimit.get() == false)
    	 {
    	 // move motor
    	 eleMotor.set(myAxis);
    	 }
    
     }
     
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    	LiveWindow.run();
    }
    //hello
    //it's me
}
