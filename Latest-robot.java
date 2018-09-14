package org.usfirst.frc.team5975.robot;

import edu.wpi.first.wpilibj.IterativeRobot;

import javax.swing.GrayFilter;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.CameraServer;

public class Robot extends IterativeRobot {
	RobotDrive myRobot;
	RobotDrive intakeMotor;
	VictorSP liftMotor;
	VictorSP leftMotor;
	VictorSP rightMotor;
	VictorSP rightIntakeMotor;
	VictorSP leftIntakeMotor;

	// RoboRio mapping
	int leftMotorChannel=2;
	int rightMotorChannel=1;
	int liftMotorChannel=3;
	int rightIntakeChannel=4;
	int leftIntakeChannel=5;
	DriverStation ds = DriverStation.getInstance();

	
	// Driver Station / controller mapping
	int joyPort1=0; //driver xbox controller
	int joyPort2=1; //manipulator xbox controller
	
	//Driver Controls
	int lTriggerID = 2;
	int rTriggerID = 3;
	int leftStickID = 1; //left and right sticks are joysticks (axis)
	int rightStickID = 5;
	int halfSpeedButtonID = 5; //half-speed button
	int turboButtonID = 6; //turbo button
	Joystick youDriveMeCrazzy;
	Joystick youManipulateMyHeart;
	
	//Manipulator Controls
	int youLiftMeUpID = 1; //A button
	int dontBringMeDownID = 2; //B button
	int intakeCubeID = 2; //left Trigger
	int ejectCubeID = 3; //right Trigger
	int descendLiftID = 5; //left Bumper
	int ascendLiftID = 6; //right Bumper
	
	//Driver stations
	int positionNumber = 2;
	int station1 = 1; //leftmost station
	int station2 = 2; //center station
	int station3 = 3; //rightmost station
	
	//verify that stick1 and stick2 correspond to the left and right joysticks on the controller
	//6 is right, 5 is left
	 
	//global variables
	int forwardLimit;
	int rightTurnLimit;
	int leftTurnLimit;
	int moveCounter;
	int moveLimit;
	double driveLeft;
	double driveRight;
	int rotationCounter;
	int turnLimit;
	int elevatorLiftLimit;
	double midAutoLimit;
	boolean ejectionLimit;
	boolean scaleGamePiece;
	boolean closeSwitchPiece;
	boolean farSwitchPiece;
	String gameData;
	
	
	//for field pieces, right is true, left is false
	
	//digital inputs
	double turboSpeedLimit = 0.9;
	double halfSpeedLimit = 0.7;
	double normalMode = 0.9;
	double liftSpeed = 1.0;
	double steadySpeed = 0.4;
	
	//IO pin mapping (copied from 2015 code)
	int upperLimitPin=1;
	int lowerLimitPin=2;
	
	//Digital inputs (copied from 2015 code)
	DigitalInput upperLimit;
	DigitalInput lowerLimit;

	boolean upperBtnState = false;
	boolean lowerBtnState = false;

	double speedLimitFactor = 0.8;

	boolean upperFlag = false;
	boolean lowerFlag = false;

	int autoStage = 0;
	
	I2C gyroI2C;
	byte[] gyroSendBuffer = {0};
	byte[] gyroRecvBuffer = {0,0};
	int youAreMyAngle;
	
	public int getGyroHeading()
	{
		gyroSendBuffer[0] = 0x06;
		boolean a = this.gyroI2C.transaction(gyroSendBuffer, 1, gyroRecvBuffer, 2);
		short heading = (short)((gyroRecvBuffer[0] & 0xff) | (gyroRecvBuffer[1] << 8));
		System.out.println(heading);
		System.out.println(a);
		return heading;
	}
	
	public void reset_gyro()
	{
		System.out.println("gyro resetting");
		gyroSendBuffer[0] = 0x03;
		System.out.println(this.gyroI2C.write(0x03, 0x52));
		//gyroSendBuffer[0] = 0x52;
		//System.out.println(this.gyroI2C.transaction(gyroSendBuffer, 1, gyroRecvBuffer, 1));
		
		return;
	}
	
    public void robotInit() {
    	
    	//CameraServer.getInstance().startAutomaticCapture();
    	
    	leftMotor = new VictorSP(leftMotorChannel);
    	rightMotor = new VictorSP(rightMotorChannel);
    	leftMotor.setInverted(false);
    	
    	leftIntakeMotor = new VictorSP(leftIntakeChannel);
    	rightIntakeMotor = new VictorSP(rightIntakeChannel);
    	leftIntakeMotor.setInverted(false);
    	intakeMotor = new RobotDrive(leftIntakeMotor, rightIntakeMotor);
   
    	myRobot = new RobotDrive(leftMotor,rightMotor);
   
    	youDriveMeCrazzy  = new Joystick(joyPort1);
    	youManipulateMyHeart = new Joystick(joyPort2);
    	
    	liftMotor = new VictorSP(liftMotorChannel);
    	
    	//copied from 2015 code
    	upperLimit = new DigitalInput(upperLimitPin);
    	lowerLimit = new DigitalInput(lowerLimitPin);
    	
    	gyroI2C = new I2C(I2C.Port.kOnboard,0x10);
    	this.reset_gyro();
    	
    }
    
    public void autonomousInit() {
    	moveCounter = 0;
    	autoStage = 0;
    	moveLimit = 20; //change back to 270
        forwardLimit = 20; //	change back to 100
        rightTurnLimit = -90;
        leftTurnLimit = 90;
        elevatorLiftLimit = 35; //this should be close
        ejectionLimit = false;
        
    	driveLeft = 0.9;
    	driveRight = 0.9;
    	
    	positionNumber = station1; //this is the line to change egg
    	
		gameData = DriverStation.getInstance().getGameSpecificMessage();

		if(gameData.charAt(0) == 'L') {//switch closest to our robot, determining whether our team color is on the left or right
			closeSwitchPiece = false;//Left
		} else { 
			closeSwitchPiece = true;//Right
		}
		
		if(gameData.charAt(1) == 'L') {//telling which color our side is on the big scale
			scaleGamePiece = false;//Left
		} else {
			scaleGamePiece = true;//Right
		}
		
		if(gameData.charAt(2) == 'L') {//switch farthest from our robot, determining whether our team color is on the left or right
			farSwitchPiece = false;//Left
		} else {
			farSwitchPiece = true;//Right
		}
		//DriverStation ds =
		//DriverStation.getInstance();
		//ds.someMethod();
		
		//DriverStation.getinstance.someMethod();
		this.reset_gyro();
    }

    public void goStraight() {
    	myRobot.drive(0.4, 0.01 * youAreMyAngle);
    }
    
    public void autonomousPeriodic() {
    	
    	boolean status;
    	
		System.out.println(gameData + " angle = " + youAreMyAngle);
    	
    	//gyroI2C.addressOnly();
    
	    youAreMyAngle = this.getGyroHeading();

    	
    	System.out.println(moveCounter);
    		//positionNumber must be 1 or 3
    	 if (positionNumber == station2) {
    		if (autoStage == 0) { 
    			// go forward
    			if (moveCounter < forwardLimit) {
    				System.out.println("going forward ; stage 0");
    				this.goStraight(); 
    				moveCounter++;	
    			} else { 
    				autoStage = 1; 
    				moveCounter = 0;
    				this.reset_gyro();
    				System.out.println("hit else statement of stage 0");
    			} 
    		} else if (autoStage == 1) { 
    			// turn 90 degrees
    			System.out.println("gyro status: " + youAreMyAngle);
    			if (closeSwitchPiece == true) { //true is right
    				if (youAreMyAngle < rightTurnLimit) {
    					myRobot.drive(0.4, 0.5); //turns right (I changed this from 0.4)
    					System.out.println("turning right ; stage 1");
    				} else {
    					this.reset_gyro();
    	   				autoStage = 2; 
    	    			moveCounter = 0;
    	    			midAutoLimit = 30;
    				}
    			} else if(closeSwitchPiece == false) { //false is left
    				if (youAreMyAngle > leftTurnLimit) {
    					myRobot.drive(0.4, -0.5); //turns left
    					System.out.println("turning left ; stage 1");
    				} else { 
    					this.reset_gyro();
    					autoStage = 2; 
    					moveCounter = 0;
    					midAutoLimit = 60;
    				} 
    			}
    		} else if (autoStage == 2) { 
    			// go forward
    			System.out.println("hit stage 2");
    			if (moveCounter < 8) {  //change back 105 
    				//this.goStraight(); 
    				System.out.println("going straight ; stage 2");
    				moveCounter++; 
    			} else {
    				autoStage = 3;
    				moveCounter = 0;
    				this.reset_gyro();
    			}
    		} else if (autoStage == 3) { 
    			// turn 90 degrees left
    			System.out.println("hit stage 3");
    			System.out.println("gyro status: " + youAreMyAngle);
    			if (closeSwitchPiece == false) {
    				if (youAreMyAngle > rightTurnLimit) {
    					//myRobot.drive(0.5, -0.5); //turns right 
    					System.out.println("turning right ; stage 3");
    				} else {
    					this.reset_gyro();
    	   				autoStage = 4; 
    	    			moveCounter = 0;
    				}
    			} else if(closeSwitchPiece == true) {
    				if (youAreMyAngle < leftTurnLimit) {
    					//myRobot.drive(0.5, 0.5); //turns left
    					System.out.println("turning left ; stage 3");
    				} else { 
    					this.reset_gyro();
    					autoStage = 4; 
    					moveCounter = 0;
    				} 
    			}
    		} else if (autoStage == 4) { 
    			// go forward
    			System.out.println("hit stage 4");
    			if (moveCounter < 60) { //was 80
    				//this.goStraight(); 
    				moveCounter++; 
    				System.out.println("going forward ; stage 4");
    			} else {
    				autoStage = 5;
    				moveCounter = 0;
    			}
    		} else if (autoStage == 5) { 
    			System.out.println("hit stage 5");
    			if (moveCounter < elevatorLiftLimit) {
    				System.out.println("lifting ; stage 5");
    				//liftMotor.set(liftSpeed); 
    				moveCounter++;
    			} else {
    				autoStage = 6;
    				moveCounter = 0;
    				ejectionLimit = true;
    			}
    		} else if (autoStage == 6) {
    			System.out.println("hit stage 6");
    			if (moveCounter < 10) {
    				if (ejectionLimit == true) {
    					//intakeMotor.drive(0.5, 0.0);
    					//liftMotor.set(0);
    					moveCounter++;
    					System.out.println("ejecting ; stage 6");
    			}
    		}
    	}
    }
    	if ((positionNumber == station1 || positionNumber == station3)) {
    		if (autoStage == 0) { 
    			if (moveCounter < 150) {
    				moveCounter++;
    				//first number is speed
    				//bigger the number for curve (second parameter) the more left it will turn
    				this.goStraight();
    				System.out.println("station 1 check"); 
    			} else {
    				this.reset_gyro();
    				autoStage = 1;
    				moveCounter = 0;
    			}
    		}
    		if (((positionNumber == station1) && (closeSwitchPiece == true)) || ((positionNumber == station3)  && (closeSwitchPiece == false))){
    			if (autoStage == 1) { 
    				System.out.println("hit stage 1");
    				if (moveCounter < elevatorLiftLimit) {
    					//liftMotor.set(liftSpeed); 
    					moveCounter++;
    				} else {
    					autoStage = 2;
    					moveCounter = 0;
    				}
    			} else if (autoStage == 2) {
    				if (moveCounter < 25) { //25 is a random number
    					System.out.println("hit stage 2");
    					//intakeMotor.drive(-0.5, 0.0);
    					//liftMotor.set(steadySpeed);
    					moveCounter++;
 					} else {
 						moveCounter = 0;
    					autoStage = 3;
 					}
 				} else if (autoStage == 3) {
 					System.out.println("hit stage 3`");
 					//liftMotor.set(0);
 				}
 			}	
    	}
    }
 	   	
    public void teleopInit() {
    	rotationCounter = 0;
    }
    
    public void teleopPeriodic() {
    	
    	double rightAxis = -youDriveMeCrazzy.getRawAxis(rightStickID);
    	double leftAxis = -youDriveMeCrazzy.getRawAxis(leftStickID);
    
    	leftAxis = limitAxis(leftAxis);
    	rightAxis = limitAxis(rightAxis);
    	
    	leftAxis = limitSpeed(leftAxis);
    	rightAxis = limitSpeed(rightAxis);
    
    	myRobot.tankDrive(leftAxis, rightAxis);
    	
    	double liftAxis = getLiftValue();
    	moveLift(liftAxis);
    	
    	moveIntake();
    	theLowLife();
    	upAndAway();
    	
    	
    }
    	
    private double limitAxis (double axis) {
    	if (axis > 1.0)
    	    axis = 1.0;
    	else if (axis < -1.0)
    		axis = -1.0;

    	return axis;
    }
    
    //drive
    private double limitSpeed (double axis) {
    	
    	axis = axis * axis * axis;
    	
    	if(youDriveMeCrazzy.getRawButton(halfSpeedButtonID) == true) {
    		axis = axis * halfSpeedLimit;
    		System.out.println("Extremely Slow mode");
    	} //this is the right button
    	
    	else if(youDriveMeCrazzy.getRawButton(turboButtonID) == true) {
    		axis = axis * turboSpeedLimit;
    		System.out.println("Turbo mode");
    	} else { //this is the left button
    		 axis = axis * normalMode;
    	}
    	return axis;
    }
    
    public double getLiftValue() {  // read trigger buttons and create the axis value
    	
    	double myRAxis = 0;
    	if(youManipulateMyHeart.getRawButton(ascendLiftID) == true) {
    		//System.out.println("lift raising");
    		myRAxis = liftSpeed;
    		//System.out.println("lift is WORKING");
    	}
    	
    	double myLAxis = 0;
    	if(youManipulateMyHeart.getRawButton(descendLiftID) == true) {
    		//System.out.println("lift descend");
    		myLAxis = steadySpeed;
    		//myLAxis = 0.0;
    		
    	}
//    	if (youManipulateMyHeart.getRawButton(descendLiftID) == false && (youManipulateMyHeart.getRawButton(ascendLiftID) == false)){
//    		System.out.println("stay stable");
//    		myLAxis = steadySpeed; 
//    	}
    	//System.out.println("lift speed = " + (myLAxis + myRAxis));
    	return ( myLAxis + myRAxis);
    }
   
   // public void moveLift(double liftAxis) {
   //	liftMotor.set(liftAxis);
   //}
    
    public void moveLift(double liftAxis) {
    	 final int stopMotor = 0;

    	 //upperBtnState = upperLimit.get();
    	 upperBtnState = false;
    	 lowerBtnState = lowerLimit.get(); 
    	//System.out.println("Upper limit =" + upperBtnState);
    //	System.out.println("Lower limit =" + lowerBtnState);
    	
    	 if(liftAxis <= 0 && lowerBtnState == true) {
    		 liftMotor.set(stopMotor);
    		 lowerFlag = true;
    		 System.out.println("Hit lower limit");
    	 } else if(liftAxis > 0 && upperBtnState == true) {
    		 liftMotor.set(steadySpeed);
    		 upperFlag = true;
    		 System.out.println("Hit upper limit");
    	 } else if(lowerBtnState == true && upperBtnState == true) {
    		 // Both limit switches can't be on at the same time!
    		 // Since this should never happen, stop the motor!
    		 liftMotor.set(liftAxis);
    	 } else {
    		 liftMotor.set(liftAxis);
    		 if(lowerFlag == true && liftAxis > 0) {
    			 lowerFlag = false;
    		 } else if(upperFlag == true && liftAxis < 0){
    			 upperFlag = false;
    		 }
    	 }
    } 
 
    public void moveIntake() {
    	double myLAxis =  youManipulateMyHeart.getRawAxis(intakeCubeID); // reverse one axis to act as an eject button
    	double myRAxis =  -youManipulateMyHeart.getRawAxis(ejectCubeID);

    	double intakeValue = limitAxis(myLAxis + myRAxis);
    	intakeMotor.drive(intakeValue,0);
    }
    
    public void theLowLife() {
    	boolean lowerLimit = youManipulateMyHeart.getRawButton(dontBringMeDownID);
//    	boolean checkAscend = youManipulateMyHeart.getRawButton(ascendLiftID);
//    	boolean checkDescend = youManipulateMyHeart.getRawButton(descendLiftID);
////    	
//    	if (checkAscend == false && checkDescend == false) {
//    		if(checkKill == true) {
//    			System.out.println("lift kill check");
//        		liftMotor.set(0.0);
//        	}
//    	}	
    	if (lowerLimit == true) {
    		//System.out.println("Hit lower limit");
    	}
    }
    
    public void upAndAway() {
    	boolean upperLimit = youManipulateMyHeart.getRawButton(youLiftMeUpID);
	
    	if (upperLimit == true) {
    		System.out.println("Hit upper limit");
    	}
    }
    
}
