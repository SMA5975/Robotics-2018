package org.usfirst.frc.team5975.robot;

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
	int distanceSensorPin1=3;
	int distanceSensorPin2=4;
	
	// Driver Station / controller mapping
	int joyPort=0;
	int lTrigger = 2;
	int rTrigger = 3;
	Joystick stick;
	int autoLoopCounter;
	int turnLoopCounter;
	
	// Declaring XBox buttons
	
	
	
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
    	distanceSensor = new Ultrasonic(distanceSensorPin1,distanceSensorPin2);
    	
    	stick = new Joystick(joyPort);
        
    }
    
    /**
     * This function is run once each time the robot enters autonomous mode
     */
    public void autonomousInit() {
    	autoLoopCounter = 0;
    	turnLoopCounter = 0;
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
    	
    	if (turnLoopCounter < 13.37)
    	{
    		myRobot.tankDrive(1.0, -1.0);
    		turnLoopCounter++;
    	}
    	//keep moving until you hit 2 inches from the wall
    	
    	if(distanceSensor.getRangeInches()>2)
    
		{
			myRobot.drive(-0.5, -0.25); 	// drive forwards half speed
			} else {
			myRobot.drive(0.0, 0.0); 	// stop robot
			}
    	//turn 90 degrees right
    	if (turnLoopCounter < 13.37)	
    	{
    		myRobot.tankDrive(-1.0, 1.0);
    		turnLoopCounter++;
    	}
    }
    //hi 
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
    	 boolean limitUpper= false;
    	 boolean limitLower= false;
    	 /*
    	  * read the limit switches and set the values
    	  * 
    	  * 
    	  */
    	 
    	 
    	 // read axis value
    	 double myLAxis =  stick.getRawAxis(lTrigger);
    	 double myRAxis =  stick.getRawAxis(rTrigger);
    	 double myAxis = myLAxis + myRAxis;
    	 if(myAxis > 0 && limitUpper == false)
    	 {
    		 eleMotor.set(myAxis);
    		 
    	 }
    	 if(myAxis < 0 && limitLower == false)
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
    
}
