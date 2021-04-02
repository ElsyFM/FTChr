package org.firstinspires.ftc.teamcode.opmodes.RedSideAutos;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.UpliftRobot;
import org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.FlickerSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.ShooterSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.TransferSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.WobbleSubsystem;
import org.firstinspires.ftc.teamcode.toolkit.background.AutoTimeout;
import org.firstinspires.ftc.teamcode.toolkit.background.Odometry;
import org.firstinspires.ftc.teamcode.toolkit.core.UpliftAuto;
import org.firstinspires.ftc.teamcode.toolkit.misc.Utils;
import org.firstinspires.ftc.teamcode.toolkit.opencvtoolkit.RingDetector;

@Autonomous(name = "Meet 5 Powershot Auto", group = "opModes")
public class Meet5PowershotAuto extends UpliftAuto {
    UpliftRobot robot;
    WobbleSubsystem wobbleSub;
    DriveSubsystem driveSub;
    IntakeSubsystem intakeSub;
    ShooterSubsystem shooterSub;
    TransferSubsystem transferSub;
    FlickerSubsystem flickerSub;
    Odometry odom;
    AutoTimeout autoTimeout;
    int stack;
    RingDetector detector;

    @Override
    public void initHardware() {
        robot = new UpliftRobot(this);
        wobbleSub = robot.wobbleSub;
        driveSub = robot.driveSub;
        shooterSub = robot.shooterSub;
        odom = robot.odometry;
        intakeSub = robot.intakeSub;
        transferSub = robot.transferSub;
        flickerSub = robot.flickerSub;
        detector = robot.ringDetector;
        autoTimeout = new AutoTimeout(robot);
        autoTimeout.enable();
    }

    @Override
    public void initAction() {
        intakeSub.initStick();
        transferSub.initTransferPos();
        intakeSub.initRoller();
        wobbleSub.closeWobble();
        wobbleSub.highWobble();
        intakeSub.initSweeper();
        flickerSub.setFlickerPos(0);
    }

    @Override
    public void body() throws InterruptedException {

        // extra safety measure to ensure that program does not run if stopped initially
        if(isStopRequested() || !opModeIsActive()) {
            return;
        }

        // set the initial position, ring stack count, and prepare shooter/transfer/intake
        odom.setOdometryPosition(105.25, 8.5, 0);
        stack = robot.ringDetector.ringCount;
        Log.i("Rect Ratio", robot.ringDetector.rectRatio + "");
        Log.i("Ring Count", robot.ringDetector.ringCount + "");
//        stack = 1;
        shooterSub.setShooterVelocity(robot.autoHighGoalVelocity);
        transferSub.autoRaiseTransfer();

        intakeSub.dropRoller();

        if(stack == 4) {

            // shoot
            shooterSub.setShooterVelocity(robot.autoHighGoalVelocity);
            driveSub.passThroughPosition(109, 24, 1, 0);
            driveSub.driveToPosition(109, 48, 0.75, 0);
            driveSub.turnTo(0, 0);
            autoHighGoalShoot();
            driveSub.driveToPosition(109, 44, 0.75, 0);


            // intake four stack, if you were able to shoot all 3 initial rings
//            if(robot.shotCount >= 3) {
            while(robot.transferState != UpliftRobot.TransferState.DOWN && opModeIsActive()) {
                robot.safeSleep(5);
            }
            intakeSub.setIntakePower(1);
            driveSub.driveToPosition(109, 56, 0.35, 0);
            robot.safeSleep(2500);
            intakeSub.setIntakePower(0);
            intakeSub.liftRoller();

            // shoot second set of 3
            intakeSub.setIntakePower(-1);
            transferSub.autoRaiseTransfer();
            intakeSub.setIntakePower(0);
            shooterSub.setShooterVelocity(robot.autoHighGoalVelocity);
            driveSub.driveToPosition(109, 48, 0.7, 0);
            driveSub.turnTo(0, 0);
            autoHighGoalShoot();

//                // intake last ring
//                intakeSub.setIntakePower(1);
//                driveSub.driveToPosition(108, 28, 0.5, 0);
//                intakeSub.setIntakePower(0);
//
//                // shoot last ring
//                transferSub.autoRaiseTransfer();
//                driveSub.driveToPosition(105.25, 12, 0.5, 0);
//                flickerSub.flickRing();

//            }


            // drive to drop off first wobble
            intakeSub.setIntakePower(1);
            wobbleSub.setWobblePosition(0.2);
            driveSub.driveToPosition(136, 114, 1, 180);
            intakeSub.setIntakePower(0);
            wobbleSub.dropWobble();
            robot.safeSleep(500);
            wobbleSub.openWobble();

            // drive to pick up second wobble
//            driveSub.driveToPosition(115, 43, 0.7, 0, DriveSubsystem.COUNTER_CLOCKWISE);
//            driveSub.driveToPosition(115, 32, 0.5, 0.5, 0, 0);
//            wobbleSub.pickUp();
            getSecondWobble();

            // drop off second wobble
            driveSub.driveToPosition(128,116, 1, -135);
            wobbleSub.dropOff();
            driveSub.driveToPosition(88, 78, 1, -135);

            //park
            intakeSub.dropRoller();
            driveSub.driveToPosition(94, 84, 0.5, 0);

        } else if(stack == 1) {

            // shoot
            shooterSub.setShooterVelocity(robot.autoHighGoalVelocity);
            driveSub.driveToPosition(109, 48, 0.7, 0);
            driveSub.turnTo(0, 0);
            autoHighGoalShoot();

            // intake single stack
            while (robot.transferState != UpliftRobot.TransferState.DOWN && opModeIsActive()) {
                Utils.sleep(5);
            }
            robot.safeSleep(500);
            intakeSub.setIntakePower(1);
            driveSub.driveToPosition(110, 56, 0.25, 0);
            robot.safeSleep(1000);
            intakeSub.setIntakePower(0);
            intakeSub.liftRoller();

            // shoot the 1 ring
            transferSub.autoRaiseTransfer();
            shooterSub.setShooterVelocity(robot.autoHighGoalVelocity);
            driveSub.driveToPosition(110, 48, 0.5, 0);
            driveSub.turnTo(0, 0);
            flickerSub.flickRing();
            flickerSub.flickRing();
            shooterSub.setShooterPower(0);
            transferSub.autoDropTransfer();
            wobbleSub.setWobblePosition(0.2);

            // drop off first wobble
            driveSub.driveToPosition(116, 96, 1, 180);
            wobbleSub.dropOff();

            // pick up second wobble goal
//            driveSub.driveToPosition(115, 43, 0.7, 0, DriveSubsystem.COUNTER_CLOCKWISE);
//            driveSub.driveToPosition(115, 36, 0.5, 0.5, 0, 0);
//            wobbleSub.pickUp();
            getSecondWobble();

            // drop off the second wobble goal
            driveSub.driveToPosition(110, 90, 1, 180);
            wobbleSub.dropOff();

            // park
            park();
            intakeSub.dropRoller();

        } else {        // either 0 rings, or a problem with detection (-1)
            shooterSub.setShooterVelocity(robot.bounceBackVelocity);
            driveSub.driveToPosition(91, 60, 0.7, 1, 0, 0);
            driveSub.turnTo(0, 0);

            transferSub.autoRaiseTransfer();
            while (!robot.velocityData.isBounceBackReady() || robot.transferState != UpliftRobot.TransferState.UP) {
                robot.safeSleep(1);
            }
            flickerSub.flickRing();
            driveSub.driveToPosition(83.5, 60, 0.7, 1,0 ,0);
            flickerSub.flickRing();
            driveSub.driveToPosition(75, 60, 0.7, 1,0 ,0);
            flickerSub.flickRing();
            transferSub.autoDropTransfer();
            shooterSub.setShooterPower(0);
            intakeSub.setIntakePower(1);
            driveSub.driveToPosition(60, 127, 0.7, 0);
            driveSub.driveToPosition(60, 125, 0.5, 90);
            driveSub.driveToPosition(100, 125, 0.7, 90);
//            // drop off first wobble
            wobbleSub.setWobblePosition(0.2);
            driveSub.driveToPosition(130, 70, 1, 180, DriveSubsystem.CLOCKWISE);
            wobbleSub.dropOff();
            driveSub.driveToPosition(130, 66, 1, 180);
//
//            // go to pick up second wobble
//            getSecondWobble();
//
//            // go to drop off second wobble
//            driveSub.driveToPosition(130, 66, 1, -135, DriveSubsystem.CLOCKWISE);
//            wobbleSub.dropOff();
//            driveSub.driveToPosition(126, 62, 1,-135);
//
//            // park
//            park();
        }

    }

    @Override
    public void exit() throws InterruptedException {
        driveSub.stopMotors();
        robot.writePositionToFiles();
    }

    public void park() {
        wobbleSub.highWobble();
        driveSub.driveToPosition(94, 84, 1, 0);
        driveSub.stopMotors();
    }

    public void getSecondWobble() {
        driveSub.driveToPosition(113, 44, 0.7, 0, DriveSubsystem.COUNTER_CLOCKWISE);
        driveSub.turnTo(0, 0);
        driveSub.driveToPosition(113, 37, 0.4, 1, 0, 0);
        wobbleSub.pickUp();
    }

    public void autoHighGoalShoot() {
        while(robot.transferState != UpliftRobot.TransferState.UP && opModeIsActive()) {
            Utils.sleep(5);
        }
        robot.safeSleep(500);
        double initialTime = System.currentTimeMillis();
        for(int i = 0; i < 3; i++) {
            while(!robot.velocityData.isAutoHighGoalReady() && (System.currentTimeMillis() - initialTime) < 2000 && !robot.operatorCancel && opModeIsActive()) {
                robot.safeSleep(1);
            }
            flickerSub.flickRing();
        }
        shooterSub.setShooterPower(0);
        transferSub.autoDropTransfer();
    }

//    public void fourStackShoot() {
//        double initialTime = System.currentTimeMillis();
//        while(robot.shooter1Vel < 2300 && (System.currentTimeMillis() - initialTime) < 2300 && opModeIsActive()) {
//            robot.safeSleep(1);
//        }
//
//        flickerSub.flickRing();
//        robot.safeSleep(100);
//        flickerSub.flickRing();
//        robot.safeSleep(100);
//        flickerSub.flickRing();
//
//        transferSub.autoDropTransfer();
//    }

}
