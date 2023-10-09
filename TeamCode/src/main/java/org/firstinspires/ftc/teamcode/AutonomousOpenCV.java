package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

@Autonomous(name="Autonomous - Generic", group="Linear Opmode")
@Disabled
public class AutonomousOpenCV extends LinearOpMode {
    private OpenCvWebcam webcam;
    private CircleDetection circleDetection;
    private DrivingFunctions df;

    Servo servo;
    protected boolean detectionRed = false; // whether to detect a red ball (if false detects blue)
    protected boolean runBallDetectionTest = false;
    protected boolean runEncoderTest = false;
    static final double DRIVE_SPEED = 0.3;
    static final double TURN_SPEED = 0.35;

    private void RunBallDetectionTest() {
        while (opModeIsActive()) {
            sleep(100);
            UpdateCircleDetectionTelemetry();
        }
    }

    private void DetectBallPosition(int timeoutInSeconds) {
        int tries = 0;
        while (opModeIsActive() && !circleDetection.CircleFound() && tries < timeoutInSeconds * 10) {
            sleep(100);
            tries++;
            UpdateCircleDetectionTelemetry();
        }
        if (!circleDetection.CircleFound())
            circleDetection.SetBallPosition(CircleDetection.BallPosition.LEFT); // Ball not found, makes a guess to the left
    }

    private void Initialize() {
        df = new DrivingFunctions(this);
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        circleDetection = new CircleDetection(detectionRed);
        webcam.setPipeline(circleDetection);
        webcam.setMillisecondsPermissionTimeout(5000); // Timeout for obtaining permission is configurable. Set before opening.
        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                webcam.startStreaming(1280, 720, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) {

            }
        });
    }

    @Override
    public void runOpMode() {

        Initialize();
        waitForStart();


        if (runBallDetectionTest) {
            RunBallDetectionTest();
            return;
        }
        if (runEncoderTest) {
            RunEncoderTest();
            return;
        }

        DetectBallPosition(5);

        if(circleDetection.GetBallPosition() == CircleDetection.BallPosition.LEFT)
        {
            PushPixelLeft();
        }
        if(circleDetection.GetBallPosition() == CircleDetection.BallPosition.CENTER)
        {
            PushPixelCentert();
        }
        if(circleDetection.GetBallPosition() == CircleDetection.BallPosition.RIGHT)
        {
            PushPixelRight();
        }
    }

    private void PushPixelRight()
    {
        df.driveStraight(DRIVE_SPEED, 35, 0);
        df.turnToHeading(TURN_SPEED, -45); // Negative angles turn to the right
        df.driveStraight(DRIVE_SPEED, 28, -45);
        df.driveStraight(DRIVE_SPEED, -28, -45);
        df.turnToHeading(TURN_SPEED, 0);
        df.driveStraight(DRIVE_SPEED, -30, 0);
    }

    private void PushPixelLeft()
    {
        df.driveStraight(DRIVE_SPEED, 35, 0);
        df.turnToHeading(TURN_SPEED, 45); // Positive angles turn to the left
        df.driveStraight(DRIVE_SPEED, 28, 45);
        df.driveStraight(DRIVE_SPEED, -28, 45);
        df.turnToHeading(TURN_SPEED, 0);
        df.driveStraight(DRIVE_SPEED, -30, 0);

    }
    private void PushPixelCentert()
    {
        df.driveStraight(DRIVE_SPEED, 38, 0);
        df.driveStraight(DRIVE_SPEED, -30, 0);
    }

    private void RunEncoderTest()
    {
        StopStreaming();
        df.TestEncoders();
    }
    protected void finalize()
    {
        StopStreaming();
    }
    private void StopStreaming()
    {
        webcam.stopStreaming();
        webcam.closeCameraDevice();
    }
    private void UpdateCircleDetectionTelemetry()
    {
        if(!runEncoderTest) {
            telemetry.addData("Frame Count", webcam.getFrameCount());
            telemetry.addData("FPS", String.format("%.2f", webcam.getFps()));
            telemetry.addData("Total frame time ms", webcam.getTotalFrameTimeMs());
            telemetry.addData("Pipeline time ms", webcam.getPipelineTimeMs());
            telemetry.addData("Overhead time ms", webcam.getOverheadTimeMs());
            telemetry.addData("Theoretical max FPS", webcam.getCurrentPipelineMaxFps());
            telemetry.addData("Circles detected: ", "%d", circleDetection.NumCirclesFound());
            telemetry.addData("Circle center = ", "%4.0f, %4.0f", circleDetection.CircleCenter().x, circleDetection.CircleCenter().y);
            telemetry.addData("Ball Position: ", "%s", circleDetection.GetBallPosition());
        }
        telemetry.update();
    }

}