package launchers;

import common.MotorUtils;
import common.PIDController;
import common.SensorUtils;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;

public class AdjustablePID
{
    static float base_speed = 250;
    
    public static void main(String[] args) throws Exception
    {
        NXTRegulatedMotor left = Motor.A;
        NXTRegulatedMotor right = Motor.B;
        EV3ColorSensor color = new EV3ColorSensor(SensorPort.S1);
        
        float[] sensor_reading;
        int sampleSize = color.sampleSize();
        sensor_reading = new float[sampleSize];
        
        PIDController controller = new PIDController(2.00f, 9.00f, 0.05f);
        int adjust_index = 0;
        
        // INITIALIZE
        // --------------------
        //System.out.println("Press ENTER to start...");
        while(!Button.ENTER.isDown())
        {
            Thread.sleep(20);
        }        
       
        Thread.sleep(100);
        
        left.setSpeed(base_speed);
        right.setSpeed(base_speed);
        left.forward();
        right.forward();
        
        long t_last = System.nanoTime();
        
        System.out.println("");
        System.out.println("");
        
        // MAIN LOOP
        // --------------------
        while(!Button.ENTER.isDown())
        {
            // Adjust parameters
            if (Button.DOWN.isDown())
            {
                adjust_control_parameter(adjust_index, controller, -0.025f);
                controller.zero_integral();
                Thread.sleep(100);
            }
            else if (Button.UP.isDown())
            {
                adjust_control_parameter(adjust_index, controller, 0.025f);
                controller.zero_integral();
                Thread.sleep(100);
            }
            
            // Change which parameter we're tweaking
            if(Button.LEFT.isDown() && adjust_index > 0)
            {
                adjust_index--;
                Thread.sleep(100);
            }
            else if(Button.RIGHT.isDown() && adjust_index < 3)
            {
                adjust_index++;
                Thread.sleep(100);
            }
            
            if (Button.ESCAPE.isDown())
            {
                controller.zero_integral();
            }
            
            String vals = String.format("%2.2f %2.2f %3.1f%n", controller.get_proportional(), controller.get_integral() ,controller.get_derivative(), base_speed);
            String mod = String.format("CM: %d", adjust_index);
            
            // Print the current state of the control variables
            LCD.drawString(vals, 0, 0);
            LCD.drawString(mod, 0, 1);
            
            color.getRedMode().fetchSample(sensor_reading, 0);
            float val = sensor_reading[0];
            float error = SensorUtils.get_error(val);
            
            long t_curr = System.nanoTime();
            float correction = controller.step((t_curr - t_last)/(Math.pow(10, 9)), error);
            t_last = t_curr;
            
            float sleft = base_speed * (1.0f - correction);
            float sright = base_speed * (1.0f + correction);
            MotorUtils.setSpeeds(left, right, sleft, sright);
        }
    }
    
    public static void adjust_control_parameter(int index, PIDController controller, float factor)
    {
        switch(index)
        {
        case 0:
            controller.set_proportional(controller.get_proportional() + factor);
            break;
        case 1:
            controller.set_integral(controller.get_integral() + factor);
            break;
        case 2:
            controller.set_derivative(controller.get_derivative() + factor);
            break;
        case 3:
            base_speed *= factor;
            break;
        }
    }
}
