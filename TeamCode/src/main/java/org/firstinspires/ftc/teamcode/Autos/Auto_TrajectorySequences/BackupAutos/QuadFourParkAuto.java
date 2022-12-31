package org.firstinspires.ftc.teamcode.Autos.Auto_TrajectorySequences.BackupAutos;

import com.acmerobotics.roadrunner.geometry.Pose2d;

import org.firstinspires.ftc.teamcode.TeleOps.AprilTags.PowerPlay_AprilTagDetection;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;


public class QuadFourParkAuto extends PowerPlay_AprilTagDetection {
    public void initilize(){
        super.runOpMode();
    }
    @Override
    public void runOpMode() {
        initilize();
        Pose2d startPose = new Pose2d(-35, 62, Math.toRadians(270));
        SampleMecanumDrive bot = new SampleMecanumDrive(hardwareMap);
        bot.setPoseEstimate(startPose);

        if (aprilTag_ID == 1) {
            TrajectorySequence sussyBaka = bot.trajectorySequenceBuilder(startPose)
                    .lineToLinearHeading(new Pose2d(-35,99,Math.toRadians(0)))
                    .back(32)
                    .build();

            bot.followTrajectorySequence(sussyBaka);
            telemetry.addData("Chris Pratt", "Is Currently In The Mushroom Kingdom");
        } else if (aprilTag_ID == 2) {
            TrajectorySequence JuicyJay = bot.trajectorySequenceBuilder(startPose)
                    .lineToLinearHeading(new Pose2d(-35,99,Math.toRadians(0)))
                    .build();

            bot.followTrajectorySequence(JuicyJay);
            telemetry.addData("Walter White", "Currently Has No Pants");
        } else {
            TrajectorySequence jacobIsCute = bot.trajectorySequenceBuilder(startPose)
                    .lineToLinearHeading(new Pose2d(-35,99,Math.toRadians(0)))
                    .forward(32)
                    .build();

            bot.followTrajectorySequence(jacobIsCute);
            telemetry.addData("Bohan and Abhilash", " = Very Cute");
        }
    }
}
