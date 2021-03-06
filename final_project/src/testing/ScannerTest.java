package testing;

import common.RangeFinderScan;
import common.Robot;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import localization.DirectionKalmanPoseProvider;

/**
 * Tests and visualizes the results of a RangeFinderScan. This feature did not
 * work in the end
 */

public class ScannerTest
{
    public static void main(String[] args) throws Exception
    {
        // ---- INIT

        // Create the robot with default parameters
        Robot robot = new Robot();

        // Pass in the pilot as a MoveProvider, and the Gyro
        DirectionKalmanPoseProvider gyro_pose = new DirectionKalmanPoseProvider(robot.pilot, robot.gyro.getAngleMode(),
                true);
        robot.setPoseProvider(gyro_pose);

        robot.pilot.setTravelSpeed(15);
        robot.pilot.setRotateSpeed(180 / 3);

        Button.ENTER.waitForPressAndRelease();

        boolean success = true;
        RangeFinderScan scan = RangeFinderScan.scan(robot, 160);
        success = success && (scan != null);

        if (success)
        {
            for (int x = 0; x < scan.scan_bandwidth; x++)
            {
                int scan_dist = (int) ((RangeFinderScan.MAX_SENSOR_RANGE - scan.normalized_spectrum[x]) * 10);
                for (int y = 0; y < scan_dist; y++)
                {
                    LCD.setPixel(x, y, 1);
                }
            }
        }

        if (success)
        {
            Button.ESCAPE.waitForPressAndRelease();
        }
    }
}
