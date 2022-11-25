package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
/*
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera;
import org.openftc.easyopencv.OpenCvPipeline;
//import org.openftc.easyopencv.OpenCvWebcam;
*/
//import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;






@Autonomous(name= "Test Autonomous")
//@Disabled
public class TestAutonomous extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime();

    //OpenCvWebcam robotWebcam;
    //OpenCV.SkystoneDeterminationPipeline pipeline;

    private DcMotor BackRight;
    private Servo budsterupanddown;
    private Servo ElliottispotatoClaw;
    private Servo LiftRight;
    private Servo LiftLeft;
    private DcMotor BackLeft;
    private DcMotor FrontLeft;
    private DcMotor FrontRight;

    double NewLiftPos;
    double LiftHeight;
    double FbFHeight;
    boolean LiftUpButtonIsPressed;
    boolean LiftDownButtonPressed;
    double MaxLiftHeight;
    double minLiftHeight;
    int NumLiftStops;
    double LiftLeftOffset;

    BNO055IMU imu;                // Additional Gyro device
    Orientation angles;

    private final static double ServoPosition = 0.5;
    private final static double ServoSpeed = 0.1;

    static final double DRIVE_SPEED = 0.45;     // Nominal speed for better accuracy.
    static final double TURN_SPEED = 0.40;     // Nominal half speed for better accuracy.

    static final double HEADING_THRESHOLD = 1;      // As tight as we can make it with an integer gyro
    static final double P_TURN_COEFF = 0.025;     // Larger is more responsive, but also less stable
    static final double P_DRIVE_COEFF = 0.007;     // Larger is more responsive, but also less stable

//    OpenCvCamera phoneCam;

    @Override
    public void runOpMode() throws InterruptedException {






        BackRight = hardwareMap.get(DcMotor.class, "BackRight");
        budsterupanddown = hardwareMap.get(Servo.class, "budsterupanddown");
        ElliottispotatoClaw = hardwareMap.get(Servo.class, "ElliottispotatoClaw");
        LiftRight = hardwareMap.get(Servo.class, "LiftRight");
        LiftLeft = hardwareMap.get(Servo.class, "LiftLeft");
        BackLeft = hardwareMap.get(DcMotor.class, "BackLeft");
        FrontLeft = hardwareMap.get(DcMotor.class, "FrontLeft");
        FrontRight = hardwareMap.get(DcMotor.class, "FrontRight");

        FrontLeft.setDirection(DcMotor.Direction.FORWARD);
        BackLeft.setDirection(DcMotor.Direction.FORWARD);
        FrontRight.setDirection(DcMotor.Direction.REVERSE);
        BackRight.setDirection(DcMotor.Direction.REVERSE);

        budsterupanddown.setDirection(Servo.Direction.REVERSE);
        ElliottispotatoClaw.setDirection(Servo.Direction.REVERSE);
        LiftRight.setDirection(Servo.Direction.REVERSE);
        FbFHeight = 0.3;

        // Lift Variables
        LiftHeight = 0.15;
        LiftLeftOffset = -0.022;
        MaxLiftHeight = 0.6;
        minLiftHeight = 0.12;
        NumLiftStops = 4;
        LiftDownButtonPressed = false;
        LiftUpButtonIsPressed = false;
        budsterupanddown.setPosition(0.5000000001);
        LiftLeft.setPosition(LiftLeftOffset + LiftHeight);
        LiftRight.setPosition(LiftHeight);
        NewLiftPos = LiftRight.getPosition();

        //Set motor modes
        FrontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FrontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BackLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BackRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        //Setting motor mode regarding encoders
        FrontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        FrontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        BackRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        BackLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.mode = BNO055IMU.SensorMode.IMU;
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json";

        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);

        telemetry.addData("Mode", "working...");
        telemetry.addData("imu calib status", imu.getCalibrationStatus().toString());
        telemetry.update();

        telemetry.addData("Mode", "ready");
        telemetry.addData("imu calib status", imu.getCalibrationStatus().toString());
        telemetry.update();

        waitForStart();
        runtime.reset();

        if (isStopRequested()) return;

        while (opModeIsActive()) {

                gyroDrive(0.20, 1000, 0);

                /*
                gyroTurn(TURN_SPEED, -95);
                gyroHold(TURN_SPEED, -95, 0.5);

                gyroDrive(0.45, 230, -95);
                sleep(200);

                gyroTurn(TURN_SPEED, -130);
                gyroHold(TURN_SPEED, -130, 0.3);

                gyroDrive(0.45, -1000, -130);
                sleep(200);

                //leftLinkage.setPosition(LEFT_LINKAGE_UP);
                //rightLinkage.setPosition(RIGHT_LINKAGE_UP);
                //sleep(200);

                gyroTurn(TURN_SPEED, -130);
                gyroHold(TURN_SPEED, -130, 0.3);

                gyroDrive(DRIVE_SPEED, 700, -130); //180
                sleep(200);

                gyroTurn(TURN_SPEED, -45);
                gyroHold(TURN_SPEED, -45, 0.5);

                gyroDrive(0.45, 550, -45);
                sleep(200);

                 */


                sleep(100000);
        }

    }


    public void gyroDrive(double speed,
                          int distance,
                          double angle) {
        double max;
        double error;
        double steer;
        double leftSpeed;
        double rightSpeed;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            FrontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            FrontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            BackLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            BackRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

            FrontRight.setTargetPosition(distance);
            FrontLeft.setTargetPosition(distance);
            BackLeft.setTargetPosition(distance);
            BackRight.setTargetPosition(distance);

            FrontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            FrontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            BackRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            BackLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            FrontRight.setPower(speed);
            FrontLeft.setPower(speed);
            BackRight.setPower(speed);
            BackLeft.setPower(speed);

            // keep looping while we are still active, and BOTH motors are running.
            while (opModeIsActive() &&
                    (FrontRight.isBusy() && FrontLeft.isBusy() && BackLeft.isBusy() && BackRight.isBusy())) {

                // adjust relative speed based on heading error.
                error = getError(angle);
                steer = getSteer(error, P_DRIVE_COEFF);

                // if driving in reverse, the motor correction also needs to be reversed
                if (distance < 0)
                    steer *= -1.0;

                leftSpeed = speed - steer;
                rightSpeed = speed + steer;

                // Normalize speeds if either one exceeds +/- 1.0;
                max = Math.max(Math.abs(leftSpeed), Math.abs(rightSpeed));

                if (max > 1.0) {
                    leftSpeed /= max;
                    rightSpeed /= max;
                }

                FrontLeft.setPower(leftSpeed);
                BackLeft.setPower(leftSpeed);
                FrontRight.setPower(rightSpeed);
                BackRight.setPower(rightSpeed);

                // Display drive status for the driver.
                telemetry.addData("Err/St", "%5.1f/%5.1f", error, steer);
                telemetry.addData("Speed", "%5.2f:%5.2f", leftSpeed, rightSpeed);
                telemetry.update();
            }

            // Stop all motion;
            FrontRight.setPower(0);
            BackRight.setPower(0);
            FrontLeft.setPower(0);
            BackLeft.setPower(0);

            // Turn off RUN_TO_POSITION
            FrontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            BackRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            FrontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            BackLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }


    /**
     * Method to spin on central axis to point in a new direction.
     * Move will stop if either of these conditions occur:
     * 1) Move gets to the heading (angle)
     * 2) Driver stops the opmode running.
     *
     * @param speed Desired speed of turn.
     * @param angle Absolute Angle (in Degrees) relative to last gyro reset.
     *              0 = fwd. +ve is CCW from fwd. -ve is CW from forward.
     *              If a relative angle is required, add/subtract from current heading.
     */
    public void gyroTurn(double speed, double angle) {

        // keep looping while we are still active, and not on heading.
        while (opModeIsActive() && !onHeading(speed, angle, P_TURN_COEFF)) {
            // Update telemetry & Allow time for other processes to run.
            telemetry.update();
        }
    }

    public void Drive (int frontRightDistance, int frontLeftDistance, int rearRightDistance, int rearLeftDistance, double speed){


        FrontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        FrontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        BackRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        BackLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        FrontRight.setTargetPosition(frontRightDistance);
        FrontLeft.setTargetPosition(frontLeftDistance);
        BackRight.setTargetPosition(rearRightDistance);
        BackLeft.setTargetPosition(rearLeftDistance);

        FrontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        FrontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        BackRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        BackLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        FrontRight.setPower(speed);
        FrontLeft.setPower(speed);
        BackRight.setPower(speed);
        BackLeft.setPower(speed);


        while (FrontRight.isBusy() && FrontLeft.isBusy() && BackRight.isBusy() && BackLeft.isBusy() && opModeIsActive()) {
            telemetry.addData("FrontRightPosition", FrontRight.getCurrentPosition());
            telemetry.addData("FrontLeftPosition", FrontLeft.getCurrentPosition());
            telemetry.update();
        }

        FrontRight.setPower(0);
        FrontLeft.setPower(0);
        BackRight.setPower(0);
        BackLeft.setPower(0);

        FrontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        FrontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        BackRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        BackLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        sleep(500);
    }

    /**
     * Method to obtain & hold a heading for a finite amount of time
     * Move will stop once the requested time has elapsed
     *
     * @param speed    Desired speed of turn.
     * @param angle    Absolute Angle (in Degrees) relative to last gyro reset.
     *                 0 = fwd. +ve is CCW from fwd. -ve is CW from forward.
     *                 If a relative angle is required, add/subtract from current heading.
     * @param holdTime Length of time (in seconds) to hold the specified heading.
     */
    public void gyroHold(double speed, double angle, double holdTime) {

        ElapsedTime holdTimer = new ElapsedTime();

        // keep looping while we have time remaining.
        holdTimer.reset();
        while (opModeIsActive() && (holdTimer.time() < holdTime)) {
            // Update telemetry & Allow time for other processes to run.
            onHeading(speed, angle, P_TURN_COEFF);
            telemetry.update();
        }

        // Stop all motion;
        FrontRight.setPower(0);
        BackRight.setPower(0);
        FrontLeft.setPower(0);
        BackLeft.setPower(0);

    }

    /**
     * Perform one cycle of closed loop heading control.
     *
     * @param speed  Desired speed of turn.
     * @param angle  Absolute Angle (in Degrees) relative to last gyro reset.
     *               0 = fwd. +ve is CCW from fwd. -ve is CW from forward.
     *               If a relative angle is required, add/subtract from current heading.
     * @param PCoeff Proportional Gain coefficient
     * @return
     */
    boolean onHeading(double speed, double angle, double PCoeff) {

        double error;
        double steer;
        boolean onTarget = false;
        double leftSpeed;
        double rightSpeed;

        // determine turn power based on +/- error
        error = getError(angle);

        if (Math.abs(error) <= HEADING_THRESHOLD) {
            steer = 0.0;
            leftSpeed = 0.0;
            rightSpeed = 0.0;
            onTarget = true;
        } else {
            steer = getSteer(error, PCoeff);
            rightSpeed = speed * steer;
            leftSpeed = -rightSpeed;
        }

        // Send desired speeds to motors.
        FrontLeft.setPower(leftSpeed);
        BackLeft.setPower(leftSpeed);
        FrontRight.setPower(rightSpeed);
        BackRight.setPower(rightSpeed);

        // Display it for the driver.
        telemetry.addData("Target", "%5.2f", angle);
        telemetry.addData("Error", error);
        telemetry.addData("Speed.", "%5.2f:%5.2f", leftSpeed, rightSpeed);

        return onTarget;
    }

    /**
     * getError determines the error between the target angle and the robot's current heading
     *
     * @param targetAngle Desired angle (relative to global reference established at last Gyro Reset).
     * @return error angle: Degrees in the range +/- 180. Centered on the robot's frame of reference
     * +ve error means the robot should turn LEFT (CCW) to reduce error.
     */
    public double getError(double targetAngle) {

        double robotError;
        angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        this.imu.getPosition();
        // calculate error in -179 to +180 range  (
        robotError = angles.firstAngle - targetAngle;
        while (robotError > 180) robotError -= 360;
        while (robotError <= -180) robotError += 360;
        return robotError;
    }

    /**
     * returns desired steering force.  +/- 1 range.  +ve = steer left
     *
     * @param error  Error angle in robot relative degrees
     * @param PCoeff Proportional Gain Coefficient
     * @return
     */
    public double getSteer(double error, double PCoeff) {
        return Range.clip(error * PCoeff, -1, 1);
    }

}