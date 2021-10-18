package org.firstinspires.ftc.teamcode.tools;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DcMotor;
import androidx.annotation.NonNull;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Lift {

    /**
     *
     * @param map local hardwareMap instance
     * @param telemetry local telemetry instance
     * @param toolGamepad instance of FtcLib GamepadX
     */
    public Lift(@NonNull HardwareMap map, Telemetry telemetry, GamepadEx toolGamepad) {
        this.liftMotor = map.get(DcMotor.class,"liftMotor");
        this.bottomSensor = map.get(DigitalChannel.class,"bottomSensor");
        this.topSensor = map.get(DigitalChannel.class,"topSensor");
        this.telemetry = telemetry;
        this.stick = toolGamepad;
    }

    private final Telemetry telemetry;
    private final DcMotor liftMotor;
    private final DigitalChannel bottomSensor;
    private final DigitalChannel topSensor;
    private final GamepadEx stick;

    public void update() {
        final double afloatValue = 0.05;
        final double stickValue = stick.getLeftY();
        telemetry.addData("left stick",stickValue);
        telemetry.addData("lift motor power", liftMotor.getPower());
        telemetry.addData("bottomSensor",bottomSensor.getState());
        telemetry.addData("topSensor", topSensor.getState());
        telemetry.addData("motor encoder",liftMotor.getCurrentPosition());
        if ((stickValue >= 0.05 && !topSensor.getState()) || (stickValue <= -0.05 && !bottomSensor.getState())) {
            liftMotor.setPower(stickValue);
        } else if (bottomSensor.getState()) {
            liftMotor.setPower(0);
        } else {
            liftMotor.setPower(afloatValue);
        }
    }
}
