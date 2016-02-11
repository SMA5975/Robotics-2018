package org.usfirst.frc.team5975.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
	VictorSP liftMotor;
	Ultrasonic distanceSensor;
	// RoboRio mapping
	int leftMotorChannel=1;
	int rightMotorChannel=2;
	int liftMotorChannel = 4;
	
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
	int turnValue= 200;
	
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
    	liftMotor = new VictorSP(liftMotorChannel);
    	leftMotor.setInverted(false);
    	distanceSensor = new Ultrasonic(pingSensorPin,echoSensorPin);
    	upperLimit = new DigitalInput(upperLimitPin);
    	lowerLimit = new DigitalInput(lowerLimitPin);
    	
    	
    	stick = new Joystick(joyPort);
        //test mode
    	LiveWindow.addSensor("driveSystem", "distanceSensor", distanceSensor);
    	SmartDashboard.putNumber("DistanceSensor", distanceSensor.getRangeInches());
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
    		myRobot.arcadeDrive(0.0, -1.0);
    		leftLoopCounter++;
    	}
    	//keep moving until you hit 2 inches from the wall
    	
    	while (distanceSensor.getRangeInches()>inchesFromWall)
     
		{
			myRobot.arcadeDrive(0.5, 0.0); 	// drive forwards half speed
		} 
    	
		myRobot.drive(0.0, 0.0); 	// stop robot
			
    	//turn 90 degrees right
		//you look fabulous today
    	while (rightLoopCounter < turnValue)	
    	{
    		myRobot.arcadeDrive(0.0, 1.0);
    		rightLoopCounter++;
    	}
    	
    	while (moveCounter < 100)
    	{
    		myRobot.drive(0.5, 0.0);
    	}
    	myRobot.drive(0.0, 0.0); 	// stop robot
		
    }
    //Hi hi
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
    	xAxis = limitAxis(xAxis);
    	xAxis = -xAxis;
    	xAxis=Math.pow(xAxis, 3.0);
    	
    	double yAxis = stick.getY ();
    	yAxis = limitAxis(yAxis);
    	yAxis = -yAxis;
    	yAxis=Math.pow(yAxis, 3.0);
    	myRobot.arcadeDrive(yAxis,  xAxis, true);
        moveLift();
        
       
    }
   //limits joystick axis to range -1.0 to 1.0
    private double limitAxis (double axis) {
    	if (axis > 1.0)
    	    axis = 1.0;
    	else if (axis < -1.0)
    		axis = -1.0;
    	return axis;
    	
    	
    }
     public void moveLift() {
    	
    	 // read axis value
 
    	 double myLAxis =  stick.getRawAxis(lTrigger);
    	 double myRAxis =  -stick.getRawAxis(rTrigger);
    	 double myAxis = myLAxis + myRAxis;
    	 myAxis = limitAxis (myAxis);
//    	 if(myAxis > 0 && lowerLimit.get() == false)
//    	 {
//    		 eleMotor.set(myAxis);
//    		 //if less than zero, going up
//    		 //greater than zero, going down
//    	 }
//    	 else if(myAxis < 0 && upperLimit.get() == false)
//    	 {
//    	 // move motor
//    	 eleMotor.set(myAxis);
//    	 }
    	 liftMotor.set (myAxis);
   
     }
     
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    	LiveWindow.run();
    }
    //hello
    //it's me
    //i was wondering if after all these years you'd like to meet
    //to go over everything
    //they say that times supposed to heal yeah
    //but i ain't done much healing
    //hello?
    //can you hear me?
    //I'm in California dreaming 'bout who we used to be
    //when we were younger and free
    //I've forgotten how it felt before the world fell at our feet
}
