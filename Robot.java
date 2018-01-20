package org.usfirst.frc.team5975.robot;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
	RobotDrive myRobot;
	VictorSP leftMotor;
	VictorSP rightMotor;
	
	
	// RoboRio mapping
	int leftMotorChannel=1;
	int rightMotorChannel=2;
	
	// Driver Station / controller mapping
	int joyPort1=0; //driver xbox controller
	int joyPort2=1; //lift xbox controller
	
	int lTriggerID = 2;
	int rTriggerID = 3; 
	int leftStickID = 1;
	int rightStickID = 5;
	int antiTurboButtonID = 6;
	int ifThisIsTooFastForYouPushThisButtonID = 5;
	Joystick youDriveMeCrazzy;
	Joystick youManipulateMyHeart;
	//verify that stick1 and stick2 correspond to the left and right joysticks on the controller
	//6 is right, 5 is left
	 
	
	//global variables
	int moveCounter;
	int moveLimit;
	double driveLeft;
	double driveRight;
	int rotationCounter;
	int turnLimit;
	int moveToPegLimit;
	
	//digital inputs
	double speedLimit = 0.8;
	double extremeSpeedLimit = 0.5;
	double normalMode = 0.75;

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
    	
    	leftMotor = new VictorSP(leftMotorChannel);
    	rightMotor = new VictorSP(rightMotorChannel);
   
    	myRobot = new RobotDrive(leftMotor,rightMotor);
    	leftMotor.setInverted(false);
   
    	
    	
    	youDriveMeCrazzy  = new Joystick(joyPort1);
    	youManipulateMyHeart = new Joystick(joyPort2);
    }
    
    /**
     * This function is run once each time the robot enters autonomous mode
     */
    public void autonomousInit() {
    	moveCounter = 0;
    	moveLimit = 270;
   
    	driveLeft = 0.9;

    	driveRight = 0.9;
    	
    	
    }
    
    /**
     * This function is called periodically during autonomous
     */

    public void autonomousPeriodic() {
    	System.out.println(moveCounter);
    		
    	if (moveCounter < moveLimit)
        {
    		moveCounter++;
    		//bigger the number for curve (second parameter) the more left it will turn
    		myRobot.drive(0.3, -0.00002);
  
    	}
    }
    
    /**
     * This function is called once each time the robot enters tele-operated mode
     */
    	
    public void teleopInit(){
    	rotationCounter = 0;
    }

    /**
     * This function is called periodically during operator control
     */
    
    public void teleopPeriodic() {
    	// went left instead of right
    	
    	double rightAxis = -youDriveMeCrazzy.getRawAxis(rightStickID);
    	double leftAxis = -youDriveMeCrazzy.getRawAxis(leftStickID);
    
    	leftAxis = limitAxis(leftAxis);
    	rightAxis = limitAxis(rightAxis);
    	
    	leftAxis = limitSpeed(leftAxis);
    	rightAxis = limitSpeed(rightAxis);
    
    	myRobot.tankDrive(leftAxis, rightAxis);
    }
    	
    private double limitAxis (double axis) {
    	
    	if (axis > 1.0)
    	    axis = 1.0;
    	else if (axis < -1.0)
    		axis = -1.0;
     
    	
    	return axis;
    		
    }
    
    //drive
    private double limitSpeed (double axis){
    	
    	axis = axis * axis * axis;
    	
    	if(youDriveMeCrazzy.getRawButton(antiTurboButtonID) == true){
    		axis = axis * extremeSpeedLimit;
    		System.out.println("Extremely Slow mode");
    	} //this is the right button
    	
    	else if(youDriveMeCrazzy.getRawButton(ifThisIsTooFastForYouPushThisButtonID) == true){
    		axis = axis * speedLimit;
    		System.out.println("ifThisIsTooFastForYouPushThisButton mode");
    	} //this is the left button
    	 else {
    		 axis = axis * normalMode;
    	}
    	
    	
    	return axis;
    }
    
}

