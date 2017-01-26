package org.usfirst.frc.team5975.robot;

//import edu.wpi.first.wpilibj.DigitalInput;
//import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.SensorBase;
import edu.wpi.first.wpilibj.InterruptableSensorBase;
import edu.wpi.first.wpilibj.DigitalSource;
import edu.wpi.first.wpilibj.DigitalInput;

public class Robot extends IterativeRobot {
	RobotDrive myRobot;
	VictorSP leftMotor;
	VictorSP rightMotor;
	Ultrasonic distanceSensor;
	
	// RoboRio mapping
	int leftMotorChannel=1;
	int rightMotorChannel=2;
	
	//IO pin mapping
	int echoSensorPin=6;
	int pingSensorPin=7;
	
	// Driver Station / controller mapping
	int joyPort1=0; //driver xbox controller
	int joyPort2=1; //lift xbox controller
	
	int lTriggerID = 2;
	int rTriggerID = 3;
	int leftStickID = 1;
	int rightStickID = 5;
	int turboButtonID = 6;
	Joystick youDriveMeCrazzy;
	Joystick youManipulateMyHeart;
	//verify that stick1 and stick2 correspond to the left and right joysticks on the controller
	 
	// maximum values adjustments need to be made
	double inchesFromWall = 8.0;
	
	//global variables
	int moveCounter;
	int moveLimit;
	double driveLeft;
	double driveRight;
	boolean ultraTrigger;
	
	//digital inputs
	double speedLimitFactor = 0.8;

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
    	leftMotor=new VictorSP(leftMotorChannel);
    	rightMotor=new VictorSP(rightMotorChannel);
    	myRobot =new RobotDrive(leftMotor,rightMotor);
    	leftMotor.setInverted(false);
    	distanceSensor = new Ultrasonic(pingSensorPin,echoSensorPin);
    	distanceSensor.setAutomaticMode(true);
    	
    	youDriveMeCrazzy  = new Joystick(joyPort1);
    	youManipulateMyHeart = new Joystick(joyPort2);
    	
        //test mode
    	LiveWindow.addSensor("driveSystem", "distanceSensor", distanceSensor);
    	SmartDashboard.putNumber("DistanceSensor", distanceSensor.getRangeInches());
    }
    
    /**
     * This function is run once each time the robot enters autonomous mode
     */
    public void autonomousInit() {
    	moveCounter = 0;
    	moveLimit = 213;
    	driveLeft = 0.5;
    	driveRight = 0.5;
    }
    
    /**
     * This function is called periodically during autonomous
     */
	
    public void autonomousPeriodic() {
    	System.out.println(moveCounter);
    	if (moveCounter < moveLimit)
    	{
    		myRobot.tankDrive(driveLeft, driveRight);
    		moveCounter++;
    	}
    	else {
    		myRobot.drive(0.0, 0.0); 	// stop robot
    	}
    }
    
    /**
     * This function is called once each time the robot enters tele-operated mode
     */
    public void teleopInit(){
    	ultraTrigger = false;
    }

    /**
     * This function is called periodically during operator control
     */
    
    public void teleopPeriodic() {
    	// went left instead of right
    	
    	double leftAxis = -youDriveMeCrazzy.getRawAxis(leftStickID);
    	double rightAxis = -youDriveMeCrazzy.getRawAxis(rightStickID);
    
    	leftAxis = limitAxis(leftAxis);
    	rightAxis = limitAxis(rightAxis);
    	
    	leftAxis = limitSpeed(leftAxis);
    	rightAxis = limitSpeed(rightAxis);
    
    	myRobot.tankDrive(leftAxis, rightAxis);
        
    	double myLAxis =  youDriveMeCrazzy.getRawAxis(lTriggerID); 
    		//System.out.println(myLAxis);
    	if (myLAxis > 0 && ultraTrigger == false){
    		System.out.println(distanceSensor.getRangeInches());
    		ultraTrigger = true;
    	}
    	if (myLAxis == 0){
    		ultraTrigger = false;
    	}
    }
    
   //limits joystick axis to range -1.0 to 1.0
    private double limitAxis (double axis) {
    	
    	if (axis > 1.0)
    	    axis = 1.0;
    	else if (axis < -1.0)
    		axis = -1.0;
    	// if not turbo mode slow down robot to 50% 
    	
    	return axis;
    		
    }
    
    private double limitSpeed (double axis){
    	
    	axis = axis * axis * axis;
    	
    	if(youDriveMeCrazzy.getRawButton(turboButtonID) == false){
    		axis = axis * speedLimitFactor;
    	} else {
    		System.out.println("Turbo mode!!");
    	}
    	
    	return axis;
    }

    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    	LiveWindow.run();
    }
    
}
