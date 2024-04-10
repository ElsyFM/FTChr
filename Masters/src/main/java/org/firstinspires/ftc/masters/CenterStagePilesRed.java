//package org.firstinspires.ftc.masters;
//
//import com.acmerobotics.dashboard.FtcDashboard;
//import com.acmerobotics.dashboard.config.Config;
//import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
//import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
//import com.acmerobotics.roadrunner.geometry.Pose2d;
//import com.acmerobotics.roadrunner.geometry.Vector2d;
//import com.qualcomm.hardware.lynx.LynxModule;
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.util.ElapsedTime;
//
//import org.firstinspires.ftc.masters.drive.SampleMecanumDrive;
//import org.firstinspires.ftc.masters.trajectorySequence.TrajectorySequence;
//import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
//import org.openftc.easyopencv.OpenCvCamera;
//import org.openftc.easyopencv.OpenCvCameraFactory;
//import org.openftc.easyopencv.OpenCvCameraRotation;
//
//import java.util.Date;
//import java.util.List;
//
//@Config
//@Autonomous(name = "Center Stage Stack Side Red", group = "competition")
//public class CenterStagePilesRed extends LinearOpMode {
//    private OpenCvCamera webcam;
//
//    private static final int CAMERA_WIDTH = 640; // width  of wanted camera resolution
//    private static final int CAMERA_HEIGHT = 360; // height of wanted camera resolution
//
//    enum State {
//        PURPLE_DEPOSIT_PATH,
//        PURPLE_DEPOSIT,
//        RETRACT_SLIDE,
//        DRIVE_TO_STACK,
//        PICK_UP_FROM_STACK,
//        DRIVE_TO_BACKBOARD,
//        DROP_WHITE,
//        YELLOW_DEPOSIT,
//        DROP_YELLOW,
//        TO_STACK_CYCLE,
//
//        BACK,
//        SPIN,
//        PARK,
//    }
//
//    ElapsedTime depositTime = new ElapsedTime();
//    int resetInt = 0;
//
//    SampleMecanumDrive drive;
//
//    TelemetryPacket packet = new TelemetryPacket();
//    int cycle = 0;
//
//    @Override
//    public void runOpMode() throws InterruptedException {
//
//        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
//        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "frontWebcam"), cameraMonitorViewId);
//        PropFindLeft myPipeline;
//        webcam.setPipeline(myPipeline = new PropFindLeft(telemetry, packet));
//        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
//            @Override
//            public void onOpened() {
//                webcam.startStreaming(CAMERA_WIDTH, CAMERA_HEIGHT, OpenCvCameraRotation.UPRIGHT);
//            }
//
//            @Override
//            public void onError(int errorCode) {
//                /*
//                 * This will be called if the camera could not be opened
//                 */
//            }
//        });
//
//        List<LynxModule> allHubs = hardwareMap.getAll(LynxModule.class);
//
//        for (LynxModule hub : allHubs) {
//            hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
//        }
//        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
//
//        //CenterStageComputerVisionPipelines CV = new CenterStageComputerVisionPipelines(hardwareMap, telemetry);
//        PropFindLeft.pos propPos = null;
//
//        drive = new SampleMecanumDrive(hardwareMap, telemetry);
//        Pose2d startPose = new Pose2d(new Vector2d(-35, -58.5), Math.toRadians(90)); //Start position for roadrunner
//        drive.setPoseEstimate(startPose);
//
//        State currentState;
//
//
//        TrajectorySequence rightPurple = drive.trajectorySequenceBuilder(startPose)
//                .lineToSplineHeading(new Pose2d(-38, -55, Math.toRadians(90-35)))
//                .build();
//
//        TrajectorySequence leftPurple = drive.trajectorySequenceBuilder(startPose)
//                .lineToSplineHeading(new Pose2d(-40, -50, Math.toRadians(90+11)))
//                .build();
//
//        TrajectorySequence centerPurple = drive.trajectorySequenceBuilder(startPose)
//                .lineToSplineHeading(new Pose2d(-40, -50, Math.toRadians(90-22)))
//                .build();
//
//        TrajectorySequence leftPurpleToStack = drive.trajectorySequenceBuilder(leftPurple.end().plus(new Pose2d(0, 0, Math.toRadians(-11))))
//                .strafeRight(10)
//                .build();
//
//        TrajectorySequence leftPurpleToStack1 = drive.trajectorySequenceBuilder(leftPurpleToStack.end())
//
//                .lineToSplineHeading(new Pose2d(-36, -12, Math.toRadians(90)))
//                .splineToLinearHeading(new Pose2d(-40, -9, Math.toRadians(180)), Math.toRadians(180))
//                .build();
//
//        TrajectorySequence rightPurpleToStack = drive.trajectorySequenceBuilder(rightPurple.end())
//                .lineToSplineHeading(new Pose2d(-40, -6, Math.toRadians(180)))
//                .build();
//
//        TrajectorySequence centerPurpleToStack = drive.trajectorySequenceBuilder(centerPurple.end())
//                .lineToSplineHeading(new Pose2d(-45, -6, Math.toRadians(180)))
//                .build();
//
//        TrajectorySequence straightToBackBoard = drive.trajectorySequenceBuilder(rightPurpleToStack.end())
//                .lineToLinearHeading(new Pose2d(30, -10, Math.toRadians(180)))
//                .build();
//
//        TrajectorySequence strafeToBoardLeft = drive.trajectorySequenceBuilder(straightToBackBoard.end())
//                .lineToConstantHeading(new Vector2d(55, -28))
//                .build();
//
//        TrajectorySequence strafeToBoardCenter = drive.trajectorySequenceBuilder(straightToBackBoard.end())
//                .strafeTo(new Vector2d(55, -28))
//                .build();
//
//        TrajectorySequence strafeToBoardRight = drive.trajectorySequenceBuilder(straightToBackBoard.end())
//                .strafeTo(new Vector2d(55, -34))
//                .build();
//
//        TrajectorySequence strafeToYellowLeft = drive.trajectorySequenceBuilder(strafeToBoardLeft.end())
//                .strafeTo(new Vector2d(55, -23))
//                .build();
//
//        TrajectorySequence strafeToYellowRight= drive.trajectorySequenceBuilder(strafeToBoardRight.end())
//                .strafeTo(new Vector2d(55, -38))
//                .build();
//
//        TrajectorySequence strafeToYellowCenter = drive.trajectorySequenceBuilder(strafeToBoardRight.end())
//                .strafeTo(new Vector2d(55, -32.5))
//                .build();
//
//
//        TrajectorySequence toStackFromRight = drive.trajectorySequenceBuilder(strafeToBoardRight.end())
//                .splineToConstantHeading(new Vector2d(48, -5), Math.toRadians(180))
//                .build();
//
//        TrajectorySequence toStackFromCenter = drive.trajectorySequenceBuilder(strafeToBoardCenter.end())
//                .splineToConstantHeading(new Vector2d(48, -5), Math.toRadians(180))
//                .build();
//
//        TrajectorySequence toStackFromLeft = drive.trajectorySequenceBuilder(strafeToBoardLeft.end())
//                .splineToConstantHeading(new Vector2d(48, -5), Math.toRadians(180))
//                .build();
//
//
//
//
//        TrajectorySequence park;
//        int backSlidesTarget = 0;
//        int intakeTarget = 0;
//        boolean hasClawExtended = false;
//        boolean clawClose = false;
//        boolean toBackboard = false;
//        boolean openClaw = false;
//        boolean closeHook = false;
//
//        drive.closeClaw();
//        drive.openLargeHook();
//        drive.closeSmallHook();
//
//        propPos = myPipeline.position;
//
//        telemetry.addData("Position", propPos);
//        telemetry.update();
//
//        ElapsedTime waitToPickUpTime= null;
//
//        waitForStart();
//
//        drive.closeClaw();
//        int previousIntakeTarget = 0;
//        ElapsedTime elapsedTime = new ElapsedTime();
//        ElapsedTime extendSlidesTime= null;
//
//
//        long startTime = new Date().getTime();
//        long time = 0;
//
//        while (time < 50 && opModeIsActive()) {
//            time = new Date().getTime() - startTime;
//            propPos = myPipeline.position;
//            telemetry.addData("Position", propPos);
//        }
//
//        currentState = State.PURPLE_DEPOSIT_PATH;
//
//        if (propPos == PropFindLeft.pos.LEFT) {
//            drive.followTrajectorySequenceAsync(leftPurple);
//            drive.intakeToGround();
//        } else if (propPos == PropFindLeft.pos.RIGHT) {
//            drive.followTrajectorySequenceAsync(rightPurple);
//            drive.intakeToGround();
//        } else {
//            drive.followTrajectorySequenceAsync(centerPurple);
//            drive.intakeToGround();
//        }
//
//        while (opModeIsActive() && !isStopRequested()) {
//            drive.update();
//            drive.backSlidesMove(backSlidesTarget);
//            drive.intakeSlidesMove(intakeTarget);
//
//            telemetry.addData("intake slides", drive.getIntakeSlides().getCurrentPosition());
//            telemetry.addData("current state", currentState.name());
////            telemetry.addData("elapsed time", elapsedTime.milliseconds());
////            telemetry.addData("previous target", previousIntakeTarget);
//            telemetry.addData("x", drive.getPoseEstimate().getX());
//            telemetry.addData("y", drive.getPoseEstimate().getY());
//            telemetry.addData("heading", drive.getPoseEstimate().getHeading());
//
//            switch (currentState) {
//
//                case PURPLE_DEPOSIT_PATH:
//
//                    if (!drive.isBusy()) {
//                        if (propPos == PropFindLeft.pos.LEFT) {
//                            intakeTarget = CSCons.redRightIntakeExtension;
//                        } else if (propPos == PropFindLeft.pos.RIGHT) {
//                            intakeTarget = CSCons.leftIntakeExtension;
//                        } else {
//                            intakeTarget = CSCons.centerIntakeExtension;
//                        }
//                        currentState = State.PURPLE_DEPOSIT;
//                    }
//                    break;
//
//
//                case PURPLE_DEPOSIT:
//
//                    if (!hasClawExtended && drive.getIntakeSlides().getCurrentPosition() > intakeTarget - 20 && drive.getIntakeSlides().getCurrentPosition() < intakeTarget + 20) {
//                        drive.openClaw();
//                        elapsedTime = new ElapsedTime();
//                        hasClawExtended = true;
//                    }
//                    if (hasClawExtended && elapsedTime.milliseconds() > 200 && previousIntakeTarget == 0) {
//                        previousIntakeTarget = intakeTarget;
//                        intakeTarget = 0;
//                    }
//                    if (hasClawExtended && elapsedTime.milliseconds() > 300 && drive.getIntakeSlides().getCurrentPosition() < previousIntakeTarget - 100) {
//                        drive.intakeToTransfer();
//                        currentState = State.RETRACT_SLIDE;
//                    }
//
//                    break;
//                case RETRACT_SLIDE:
//                    if (drive.getIntakeSlides().getCurrentPosition() < 100) {
//                        if (propPos == PropFindLeft.pos.RIGHT) {
//                            drive.followTrajectorySequenceAsync(rightPurpleToStack);
//                            currentState = State.DRIVE_TO_STACK;
//                        } else if (propPos == PropFindLeft.pos.LEFT) {
//                            drive.followTrajectorySequenceAsync(leftPurpleToStack);
//                            currentState = State.SPIN;
//                        } else {
//                            drive.followTrajectorySequenceAsync(centerPurpleToStack);
//                            currentState = State.DRIVE_TO_STACK;
//                        }
//                        drive.intakeToTopStack();
//
//                    }
//
//                    break;
//                case SPIN:
//                    if (!drive.isBusy()) {
//
//                        drive.followTrajectorySequenceAsync(leftPurpleToStack1);
//                        currentState = State.DRIVE_TO_STACK;
//                    }
//
//                    break;
//
//                case DRIVE_TO_STACK:
//                    if (!drive.isBusy()) {
//                        drive.intakeToTopStack();
//                        if (propPos== PropFindLeft.pos.LEFT){
//                            intakeTarget=900;
//                        } else if (propPos == PropFindLeft.pos.MID) {
//                            intakeTarget = 220;
//                        } else {
//                            intakeTarget = 780;
//                        }
//                        extendSlidesTime= new ElapsedTime();
//                        currentState = State.PICK_UP_FROM_STACK;
//                        clawClose = false;
//                    }
//
//                    break;
//                case PICK_UP_FROM_STACK:
//                    if (!clawClose && ((drive.getIntakeSlides().getCurrentPosition() > intakeTarget-10 || extendSlidesTime.milliseconds()>2000) ) && (detectPixel() ||(waitToPickUpTime!=null && waitToPickUpTime.milliseconds()>500) )) {
//                        drive.closeClaw();
//                        drive.getIntakeSlides().setPower(0);
//                        intakeTarget = drive.getIntakeSlides().getCurrentPosition();
//                        elapsedTime = new ElapsedTime();
//                        clawClose = true;
//                        waitToPickUpTime=null;
//                    }
//                    if (clawClose && elapsedTime.milliseconds() > 700) {
//                        intakeTarget = 0;
//
//                        drive.followTrajectorySequenceAsync(straightToBackBoard);
//                        currentState = State.DRIVE_TO_BACKBOARD;
//                        toBackboard = false;
//                        elapsedTime = new ElapsedTime();
//                    }
//                    if ((drive.getIntakeSlides().getCurrentPosition()>intakeTarget-10 ||  extendSlidesTime.milliseconds()>2000)  && waitToPickUpTime==null && !clawClose ){
//                        waitToPickUpTime= new ElapsedTime();
//                    }
//
//                    break;
//
//                case DRIVE_TO_BACKBOARD:
//                    if (!drive.isBusy() && !toBackboard) {
//
//                        if (propPos == PropFindLeft.pos.RIGHT || propPos == PropFindLeft.pos.MID) {
//                            drive.followTrajectorySequenceAsync(strafeToBoardLeft); //to drop white
//                        } else {
//                            drive.followTrajectorySequenceAsync(strafeToBoardRight);
//                        }
//                        toBackboard = true;
//                    }
//
//                    if (!drive.isBusy() && toBackboard) {
//                        currentState = State.DROP_WHITE;
//                        closeHook = true;
//                    }
//
//                    if (drive.getIntakeSlides().getCurrentPosition()<50){
//                        drive.intakeToTransfer();
//                    }
//
//                    if (elapsedTime.milliseconds() > 1200 && !openClaw) {
//                        drive.transferClaw();
//                        openClaw = true;
//                    }
//                    if (elapsedTime.milliseconds() > 1900 && !closeHook) {
//                        drive.closeHook();
//                        closeHook = true;
//                    }
//
//                    if (drive.getPoseEstimate().getX() > 15 && closeHook) {
//                        backSlidesTarget = CSCons.OuttakePosition.AUTO.getTarget();
//                        drive.outtakeToBackdrop();
//                    }
//
//                    break;
//                case DROP_WHITE:
//                    if (closeHook) {
//                        if (cycle==0) {
//                            drive.openLargeHook();
//                        } else {
//                            drive.openLargeHook();
//                            drive.openSmallHook();
//                        }
//                        elapsedTime = new ElapsedTime();
//                        closeHook = false;
//                    }
//
//                    if (!closeHook && elapsedTime.milliseconds() > 200) {
//                        backSlidesTarget = 1400;
//                        if (drive.getBackSlides().getCurrentPosition() > 1350) {
//                            if (cycle==0) {
//                                if (propPos == PropFindLeft.pos.RIGHT) {
//                                    drive.followTrajectorySequenceAsync(strafeToYellowRight);
//                                } else if (propPos == PropFindLeft.pos.MID) {
//                                    drive.followTrajectorySequenceAsync(strafeToYellowCenter); //to drop white
//                                } else {
//                                    drive.followTrajectorySequenceAsync(strafeToYellowLeft);
//                                }
//
//                                currentState = State.YELLOW_DEPOSIT;
//                            } else if (cycle ==1 ){
//                                park=  drive.trajectorySequenceBuilder(strafeToBoardLeft.end())
//                                        .back(10)
//                                        .build();
//                                drive.followTrajectorySequenceAsync(park);
//                                currentState = State.PARK;
//                            }
//                            closeHook = true;
//
//                        }
//                    }
//                    break;
//                case YELLOW_DEPOSIT:
//                    if (!drive.isBusy()) {
//                        backSlidesTarget = 900;
//
//                        //wait till slides are down and open hook
//                        if (drive.getBackSlides().getCurrentPosition()<920 && closeHook) {
//                            drive.openSmallHook();
//                            closeHook = false;
//                            elapsedTime = new ElapsedTime();
//                        }
//                        //wait a bit so hook is open and pixel drops
//                        //raise slides
//                        //when slides are up drive back to stack
//                        telemetry.addData("YELLOW", "WAIT");
//                        if (!closeHook && elapsedTime.milliseconds()>400){
//                            telemetry.addData("YELLOW", "LIFT");
//                            backSlidesTarget = 1800;
//                            if (drive.getBackSlides().getCurrentPosition() > 1750) {
//                                if (propPos == PropFindLeft.pos.RIGHT) {
//                                    drive.followTrajectorySequenceAsync(toStackFromRight);
//                                } else if (propPos == PropFindLeft.pos.MID) {
//                                    drive.followTrajectorySequenceAsync(toStackFromCenter);
//                                } else {
//                                    drive.followTrajectorySequenceAsync(toStackFromLeft);
//                                }
//                                currentState = State.PARK;
//                                closeHook = true;
//
//                            }
//                        }
//                    }
//                    break;
//
//                case TO_STACK_CYCLE:
////                    backSlidesTarget = 0;
////                    drive.outtakeToTransfer();
////                    drive.openClaw();
////                    drive.intakeToPosition3();
////                    clawClose = false;
//////                    if (drive.getPoseEstimate().getX()<0) {
//////                        intakeTarget = 500;
//////                    }
////
////                    if (!drive.isBusy()){
////                        currentState= State.PICK_UP_FROM_STACK;
////                        cycle++;
////                        intakeTarget = 500;
////                    }
//                    break;
//
//
//
//                case PARK:
//                    if (!drive.isBusy()) {
//                       drive.outtakeToTransfer();
//                       backSlidesTarget=0;
//                    }
//                    break;
////                case STOP:
////                    break;
//            }
//        }
//    }
//
//    protected boolean detectPixel() {
//        if (drive.getColorSensor().getRawLightDetected() > CSCons.pixelDetectThreshold) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//
//
//}