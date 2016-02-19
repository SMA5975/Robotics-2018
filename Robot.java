package org.usfirst.frc.team5975.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.GenericHID.Hand;
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
	int joyPort1=0; //driver xbox controller
	int joyPort2=1; //lift xbox controller
	
	int lTriggerID = 2;
	int rTriggerID = 3;
	int leftStickID = 1;
	int rightStickID = 5;
	Joystick youDriveMeCrazzy;
	Joystick youManipulateMyHeart;
	
	//verify that stick1 and stick2 correspond to the left and right joysticks on the controller
	 
	// maximum values adjustments need to be made
	double inchesFromWall = 8.0;
	int turnValue= 213;
	
	int leftLoopCounter;
	int rightLoopCounter;
	int moveCounter;
	
	// Declaring XBox buttons
	
	//digital inputs
	DigitalInput upperLimit;
	DigitalInput lowerLimit;
	boolean upperBtnState = false;
	boolean lowerBtnState = false;
	long debounceDelay = 50;
	double speedLimitFactor = 0.5;
	
	boolean upperFlag = false;
	boolean lowerFlag = false;
	int autoStage = 0;
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     * 
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
    	
    	youDriveMeCrazzy  = new Joystick(joyPort1);
    	youManipulateMyHeart = new Joystick(joyPort2);
    	
        //test mode
    	LiveWindow.addSensor("driveSystem", "distanceSensor", distanceSensor);
    	SmartDashboard.putNumber("DistanceSensor", distanceSensor.getRangeInches());
    	SmartDashboard.putBoolean("Upper Limit", upperBtnState);
    	SmartDashboard.putBoolean("Lower Limit", lowerBtnState);
    }
    
    /**
     * This function is run once each time the robot enters autonomous mode
     */
    public void autonomousInit() {
    	leftLoopCounter = 0;
    	rightLoopCounter = 0;
    	moveCounter = 0;
    }

    //you're the {CSS} to my <HTML> <3
    
    /**
     * This function is called periodically during autonomous
     * turn 90 degrees left
     * go forward until wall 2 inches away
     * turn 90 degrees right
     * go forward through low bar 12 inches
     */
    
    public void autonomousPeriodic2() {
    	//start by turning left
    	
    	 
    	while (leftLoopCounter < turnValue)
    	{	
    		autoLowerLift();
    		myRobot.arcadeDrive(0.0, -1.0);
    		leftLoopCounter++;		
    	}
    	// TBD ADD DELAY
    	
    	//keep moving until you hit 2 inches from the wall
    
    	/*while (distanceSensor.getRangeInches()>inchesFromWall)
    	{
    		autoLowerLift();
    		myRobot.arcadeDrive(0.5, 0.0); 	// drive forwards half speed
    	}	 
    	myRobot.drive(0.0, 0.0); // stop robot */	
    		 
    	//turn 90 degrees right
		//you look fabulous today
    	 
    	while (rightLoopCounter < turnValue)	
    	{
    		autoLowerLift();
    		myRobot.arcadeDrive(0.0, 1.0);
    		rightLoopCounter++;
    	}
    	
    	 
    	while (moveCounter < 100)
    	{ 
    		autoLowerLift();
    		myRobot.drive(0.5, 0.0);
    	}
    	myRobot.drive(0.0, 0.0); 	// stop robot
    	
    }
    //you're the {CSS} to my <HTML> 
	
	/**
	 * This function is called periodically during autonomous
	 * turn 90 degrees left
	 * go forward until wall 2 inches away
	 * turn 90 degrees right
	 * go forward through low bar 12 inches
	 */
	
	public void autonomousPeriodic() {
		if (autoStage == 0){
			//start by turning left
			if (leftLoopCounter < turnValue)
			{	
				autoLowerLift();
				myRobot.arcadeDrive(0.0, -1.0);
				leftLoopCounter++;		
			} else {
				autoStage = 1;
			}
		} else if (autoStage == 1) {
			//turn 90 degrees right
			//you look fabulous today
			 
			if (rightLoopCounter < turnValue)	
			{
				autoLowerLift();
				myRobot.arcadeDrive(0.0, 1.0);
				rightLoopCounter++;
			} else {
				autoStage = 2;
			}
		} else if (autoStage == 2) {
			if (moveCounter < 100)
			{ 
				autoLowerLift();
				myRobot.drive(0.5, 0.0);
				moveCounter++;
			}
			myRobot.drive(0.0, 0.0); 	// stop robot
		}
		
		//keep moving until you hit 2 inches from the wall
	
		/*while (distanceSensor.getRangeInches()>inchesFromWall)
		{
			autoLowerLift();
			myRobot.arcadeDrive(0.5, 0.0); 	// drive forwards half speed
		}	 
		myRobot.drive(0.0, 0.0); // stop robot */	
	}

	//during while loops if lift is not all the way down autoLowerLift lowers it
    private void autoLowerLift()  {
    	/*if (lowerLimit.get() {
    		liftMotor.set(0.1);
    	}	*/
    }
    //Hi hi hi
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
    	//double myLAxis =  xboxController.getRawAxis(lTriggerID);
    	
    	double leftAxis = -youDriveMeCrazzy.getRawAxis(leftStickID);
    	double rightAxis = -youDriveMeCrazzy.getRawAxis(rightStickID);
    
    	leftAxis = limitAxis(leftAxis);
    	rightAxis = limitAxis(rightAxis);
    	
    	leftAxis = limitSpeed(leftAxis);
    	rightAxis = limitSpeed(rightAxis);
    	
    	// instead of cube try quadratic scaling (joystick value * absolute value of joystick value)
    	
 
    	myRobot.tankDrive(leftAxis, rightAxis);//myRobot.tankDrive(Axis 1, Axis 2)-you need to define the two axes, I'm not sure what the button mappings for the axes are on your controller
    	
    	double liftAxis = getLiftAxis();
    	moveLift(liftAxis);

    	//Timer.delay(.002);// figure exactly how the timer.delay function works for an iterative robot-On a simple robot this function prevents the robot from doing anything for the given time interval, make sure this won't happen on your robot
       
    }
   //limits joystick axis to range -1.0 to 1.0
    private double limitAxis (double axis) {
    	
    	//clean up joystick reading
    	if (axis > 1.0)
    	    axis = 1.0;
    	else if (axis < -1.0)
    		axis = -1.0;
    	// if not turbo mode slow down robot to 50% 
    	
    	return axis;
    		
    }
    
    private double limitSpeed (double axis){
    	
    	axis = axis * axis * axis;
    	
    	if(youDriveMeCrazzy.getBumper(Hand.kRight) == false){
    		axis = axis * speedLimitFactor;
    	} else {
    		System.out.println("Turbo mode!!");
    	}
    	
    	return axis;
    }
//     public void moveLift() {
//    	
//    	 // read axis value
//    	 boolean upperLimitFlag = upperLimit.get() ;
//    	 boolean lowerLimitFlag = lowerLimit.get() ;
//    	 
//    	 
//    	 double myLAxis =  stick.getRawAxis(lTrigger);
//    	 double myRAxis =  -stick.getRawAxis(rTrigger);
//    	 double myAxis = myLAxis + myRAxis;
//    	 myAxis = limitAxis (myAxis);
//    	 if(myAxis > 0 && lowerLimit.get() == true )
//    	 {
// 		 liftMotor.set(myAxis);
//  		 //if less than zero, going up
//   		 //greater than zero, going down
// 		 }
//    		 else if(myAxis < 0 && upperLimit.get() == true )
//    		 {
//   	 // move motor
//   	 liftMotor.set(myAxis);
//    	 }
//    	// liftMotor.set (myAxis);
//   
//     }
     
     public void moveLift(double liftAxis)
     {
     	 final int stopMotor = 0;
     	 
     	 // Read the limit switches before moving the lift!
     	 //readSwitch(upperLimit, upperBtnState, lastUpperBtnState, lastUpperDebounceTime);
     	  //readSwitch(lowerLimit, lowerBtnState, lastLowerBtnState, lastLowerDebounceTime);
     	 
     	 upperBtnState = upperLimit.get();
     	 lowerBtnState = lowerLimit.get();
     	 
     	 if(liftAxis < 0 && upperBtnState == false)
     	 {
     		 liftMotor.set(stopMotor);
     		 lowerFlag = true;
     	 }
     	 else if(liftAxis > 0 && lowerBtnState == false)
     	 {
     		 liftMotor.set(stopMotor);
     		 upperFlag = true;
     	 }
     	 else if (lowerBtnState == false && upperBtnState == false)
     	 {
     		 // Both limit switches can't be on at the same time!
     		 // Since this should never happen, stop the motor!
     		 liftMotor.set(stopMotor);
     	 }
     	 else
     	 {
     		 liftMotor.set(liftAxis);
     		 
     		 if (lowerFlag == true && liftAxis > 0) {
     			 lowerFlag = false;
     		 } else if (upperFlag == true && liftAxis < 0){
     			 upperFlag = false;
     		 }
     	 }
     }

     public double getLiftAxis()
     {
    	 // read trigger buttons and create the axis value
     	 
    	 double myLAxis =  youManipulateMyHeart.getRawAxis(lTriggerID); // reverse one axis to act as a "down" button
    	 double myRAxis =  -youManipulateMyHeart.getRawAxis(rTriggerID);
    	 return limitAxis( myLAxis + myRAxis);

     }

     @SuppressWarnings("unused")
	private void readSwitch(DigitalInput mySwitch, boolean switchState, boolean lastState, long lastDebounceTime)
     {
    	 boolean switchReading = mySwitch.get();
    	 
    	 // check the switch has changed since the last
    	 // state. If so, reset the debouncing timer
    	 if ( switchReading != lastState )
    	 {
    		 lastDebounceTime = System.currentTimeMillis();
    	 }
    	 if ( (System.currentTimeMillis() - lastDebounceTime) > debounceDelay)
    	 {
    		 switchState = switchReading;
    	 }
    	 
    	 // Save the reading before exiting the function. Next 
    	 // time it will be the last (previous) state
    	 lastState = switchReading;
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
    //There's such a difference between us
    //And a million miles
    //Hello from the other side
    //I must have called a thousand times
    //to tell you I'm sorry for everything that ive done
    //but when i call you never seem to be home
    //Hello from the outside
    //at least i can say that I've tried
    //to tell you i'm sorry for breaking your heart
    //but it don't matter
    //it clearlyyyyyy
    //doesn't tear you apart
    //anymorreeeeeeeeeeeee
    //hi
    
   
  
    
}
