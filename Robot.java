package org.usfirst.frc.team5975.robot;

//import edu.wpi.first.wpilibj.DigitalInput;
//import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.CameraServer;
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
import edu.wpi.first.wpilibj.DigitalOutput;

public class Robot extends IterativeRobot {
	RobotDrive myRobot;
	VictorSP leftMotor;
	VictorSP rightMotor;
	VictorSP liftMotor;
	Ultrasonic distanceSensor;
	int weCanGoTheDistance;
	int weWentTheDistance;
	
	// RoboRio mapping
	int leftMotorChannel=1;
	int rightMotorChannel=2;
	int liftMotorChannel=3;
	
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
	int antiTurboButtonID = 6;
	int ifThisIsTooFastForYouPushThisButtonID = 5;
	Joystick youDriveMeCrazzy;
	Joystick youManipulateMyHeart;
	//verify that stick1 and stick2 correspond to the left and right joysticks on the controller
	//6 is right, 5 is left
	 
	// maximum values adjustments need to be made
	double inchesFromWall = 8.0;
	
	//global variables
	int moveCounter;
	int moveLimit;
	double driveLeft;
	double driveRight;
	boolean ultraTrigger;
	int rotationCounter;
	
	//digital inputs
	double speedLimit = 0.7;
	double extremeSpeedLimit = 0.4;
	double normalMode = 0.9;

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
    	CameraServer.getInstance().startAutomaticCapture();
    	leftMotor = new VictorSP(leftMotorChannel);
    	rightMotor = new VictorSP(rightMotorChannel);
    	liftMotor = new VictorSP(liftMotorChannel);
    	myRobot = new RobotDrive(leftMotor,rightMotor);
    	leftMotor.setInverted(false);
    	/*
    			weCanGoTheDistance = 3;
    	weWentTheDistance = 4;
    	distanceSensor = new Ultrasonic(weWentTheDistance, weCanGoTheDistance);
    	distanceSensor.setAutomaticMode(true);
    	if (distanceSensor.isEnabled()) {
    		System.out.println("Distance sensor is enabled");
    	} else {
    		System.out.println("Distance sensor is not enabled");
    	}
    	*/
    	
    	youDriveMeCrazzy  = new Joystick(joyPort1);
    	youManipulateMyHeart = new Joystick(joyPort2);
    	
        //test mode
    	//LiveWindow.addSensor("driveSystem", "distanceSensor", distanceSensor);
    	//SmartDashboard.putNumber("DistanceSensor", distanceSensor.getRangeInches());
    }
    
    /**
     * This function is run once each time the robot enters autonomous mode
     */
    public void autonomousInit() {
    	moveCounter = 0;
    	moveLimit = 270;
    	driveLeft = -0.5;
    	driveRight = -0.5;
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
    	rotationCounter = 0;
    }

    /**
     * This function is called periodically during operator control
     */
    
    public void teleopPeriodic() {
    	// went left instead of right
    	
    	double leftAxis = youDriveMeCrazzy.getRawAxis(rightStickID);
    	double rightAxis = youDriveMeCrazzy.getRawAxis(leftStickID);
    
    	leftAxis = limitAxis(leftAxis);
    	rightAxis = limitAxis(rightAxis);
    	
    	leftAxis = limitSpeed(leftAxis);
    
    	
    	
    	
    	rightAxis = limitSpeed(rightAxis);
    
    	myRobot.tankDrive(leftAxis, rightAxis);
        
    	/*double myLAxis =  youDriveMeCrazzy.getRawAxis(lTriggerID); 
    		//System.out.println(myLAxis);
    	if (myLAxis > 0 && ultraTrigger == false){
    		System.out.println(distanceSensor.getRangeInches());
    		ultraTrigger = true;
    	}
    	if (myLAxis == 0){
    		ultraTrigger = false;
    	} */
     	
    	//positive goes up, negative goes down
    	climbRope ();
    	
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

    public void climbRope()
    {
  
    	longClimb ();
    	shortClimb ();
    	 
    }
    
    //when left trigger pulled the motor rotates set amount
    
    public void longClimb() 
    {
    	double myLAxis =  youManipulateMyHeart.getRawAxis(lTriggerID); 
    	//System.out.println("myLAxis= ");
    	//System.out.println(myLAxis);
    	//System.out.printf("myLAxis=%f\n", myLAxis);
    	if (myLAxis != 0) {
    		liftMotor.set(-limitAxis(myLAxis));
    	 // } else {
    		//  liftMotor.set(-limitAxis(0.0));
    		  }
 
    	}
    
    
    //when right trigger pulled the motor has a set rotation allowed, 
    //no matter how many times the trigger is pulled and let go of
    
    public void shortClimb () 
    {
    	
    	double myRAxis =  youManipulateMyHeart.getRawAxis(rTriggerID); 
    	//System.out.println("myRAxis= ");
    	//System.out.println(myRAxis);
    	//System.out.printf("myRAxis=%f\n", myRAxis);
    	if (myRAxis != 0) {
    		liftMotor.set(-limitAxis(myRAxis));
    		  } else {
    		  liftMotor.set(-limitAxis(0.0));
    		  }
    		  
    		 // if (myRAxis == 0) {
    		 // liftMotor.set(0.0);
    		 // }
    		 
    		
    	}
    

    
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    	LiveWindow.run();
    }
    
}

/*
  Hey
  I was doing just fine before I met you
  I code too much and that's an issue
  But I'm OK
  Hey
  you tell hardware it was nice to meet them
  But I hope I never see them
  Again
  
  I know it breaks your heart
  Moved to the city with robot parts
  And 
  2 events, 2 awards
  Now we're looking pretty as young women with charts
  And I, I, I, I, I can't stop
  No, I, I, I, I, I can't stop
  
  So, robot, pull me closer
  In the trigger of your controller
  That I know you can't afford
  Bite that churro on your shoulder
  Pull the noodle right off the corner
  Of that bumpers that you stole
  From your roommate back in Boulder
  We ain't ever getting older

We ain't ever getting older
We ain't ever getting older

You look as good as the day I met you
I forget just why I left you,
I was insane
Stay and play that Blink-182 song
That we beat to death in Tucson,
OK

I know it breaks your heart
Moved to the city in a broke-down car
And four years, no call
Now I'm looking pretty in a hotel bar
And I, I, I, I, I can't stop
No, I, I, I, I, I can't stop

So, baby, pull me closer
In the back seat of your Rover
That I know you can't afford
Bite that tattoo on your shoulder
Pull the sheets right off the corner
Of that mattress that you stole
From your roommate back in Boulder
We ain't ever getting older

We ain't ever getting older
We ain't ever getting older

So, baby, pull me closer
In the back seat of your Rover
That I know you can't afford
Bite that tattoo on your shoulder
Pull the sheets right off the corner
Of that mattress that you stole
From your roommate back in Boulder
We ain't ever getting older

We ain't ever getting older
No, we ain't ever getting older
We ain't ever getting older
No, we ain't ever getting older
We ain't ever getting older
We ain't ever getting older
We ain't ever getting older
No, we ain't ever getting older

We ain't ever getting older
No, we ain't ever getting older
 */
