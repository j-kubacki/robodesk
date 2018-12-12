package robodesk.application;

import com.pi4j.io.gpio.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.stream.IntStream;

@Component
@Scope("singleton")
public class RobodeskRunner implements Runnable{

    private final byte[][] LEFTWHEEL_HALFSTEP_SEQ = {
            {0,1,1,1},
            {0,0,1,1},
            {1,0,1,1},
            {1,0,0,1},
            {1,1,0,1},
            {1,1,0,0},
            {1,1,1,0},
            {0,1,1,0}
    };

    private final byte[][] RIGHTWHEEL_HALFSTEP_SEQ = {
            {0,1,1,0},
            {1,1,1,0},
            {1,1,0,0},
            {1,1,0,1},
            {1,0,0,1},
            {1,0,1,1},
            {0,0,1,1},
            {0,1,1,1}
    };

    private final byte[] STOP_SEQ = {1,1,1,1};

    private enum Direction{
        FORWARD, BACKWARD, STOP
    }

    private Direction LEFT_WHEEL = Direction.STOP;
    private Direction RIGHT_WHEEL = Direction.STOP;
    
    public void goForward(){
        start();
        LEFT_WHEEL = Direction.FORWARD;
        RIGHT_WHEEL = Direction.FORWARD;
    }

    public void goLeft(){
        start();
        LEFT_WHEEL = Direction.FORWARD;
        RIGHT_WHEEL = Direction.STOP;
    }

    public void goRight(){
        start();
        LEFT_WHEEL = Direction.STOP;
        RIGHT_WHEEL = Direction.FORWARD;
    }

    public void goBackward(){
        start();
        LEFT_WHEEL = Direction.BACKWARD;
        RIGHT_WHEEL = Direction.BACKWARD;
    }

    public void stop(){
        start();
        LEFT_WHEEL = Direction.STOP;
        RIGHT_WHEEL = Direction.STOP;
    }

    private GpioController GPIO;
    private GpioPinDigitalOutput[] CONTROL_PINS_A;
    private GpioPinDigitalOutput[] CONTROL_PINS_B;

    public void setup(){
        System.out.println("<--Pi4J--> GPIO Control Example ... started.");

        GPIO = GpioFactory.getInstance();
        CONTROL_PINS_A = new  GpioPinDigitalOutput[]{
                GPIO.provisionDigitalMultipurposePin(RaspiPin.GPIO_07, PinMode.DIGITAL_OUTPUT),
                GPIO.provisionDigitalMultipurposePin(RaspiPin.GPIO_00, PinMode.DIGITAL_OUTPUT),
                GPIO.provisionDigitalMultipurposePin(RaspiPin.GPIO_02, PinMode.DIGITAL_OUTPUT),
                GPIO.provisionDigitalMultipurposePin(RaspiPin.GPIO_03, PinMode.DIGITAL_OUTPUT)
        };
        CONTROL_PINS_B = new GpioPinDigitalOutput[]{
                GPIO.provisionDigitalMultipurposePin(RaspiPin.GPIO_26, PinMode.DIGITAL_OUTPUT),
                GPIO.provisionDigitalMultipurposePin(RaspiPin.GPIO_27, PinMode.DIGITAL_OUTPUT),
                GPIO.provisionDigitalMultipurposePin(RaspiPin.GPIO_28, PinMode.DIGITAL_OUTPUT),
                GPIO.provisionDigitalMultipurposePin(RaspiPin.GPIO_29, PinMode.DIGITAL_OUTPUT)
        };

        for (GpioPinDigitalOutput pin : CONTROL_PINS_A){
            pin.low();
        }
        for (GpioPinDigitalOutput pin : CONTROL_PINS_B){
            pin.low();
        }
    }

    public void teardown(){
        for (GpioPinDigitalOutput pin : CONTROL_PINS_A){
            pin.setMode(PinMode.DIGITAL_OUTPUT);
            pin.high();
        }
        for (GpioPinDigitalOutput pin : CONTROL_PINS_B){
            pin.setMode(PinMode.DIGITAL_OUTPUT);
            pin.high();
        }

        GPIO.shutdown();

        System.out.println("Exiting ControlGpioExample");
    }

    private boolean working = false;

    private void start(){
        if (!working){
            working = true;
            new Thread(this).start();
        }
    }

    public void run(){
        do {
                for (int k = 0, i = 0; k < 7 || i < 7; k++, i++) {
                    byte[] halfstepLeftWheel = LEFTWHEEL_HALFSTEP_SEQ[k];
                    byte[] halfstepRightWheel = RIGHTWHEEL_HALFSTEP_SEQ[i];
                    IntStream.range(0, 4).forEach(
                            n -> {
                                if (halfstepLeftWheel[n] == 0) {
                                    CONTROL_PINS_A[n].setMode(PinMode.DIGITAL_INPUT);
                                } else {
                                    CONTROL_PINS_A[n].setMode(PinMode.DIGITAL_OUTPUT);
                                    CONTROL_PINS_A[n].high();
                                }

                                if (halfstepRightWheel[n] == 0) {
                                    CONTROL_PINS_B[n].setMode(PinMode.DIGITAL_INPUT);
                                } else {
                                    CONTROL_PINS_B[n].setMode(PinMode.DIGITAL_OUTPUT);
                                    CONTROL_PINS_B[n].high();
                                }
                            }
                    );
                    switch (LEFT_WHEEL){
                        case FORWARD:
                            k++;
                            break;
                        case BACKWARD:
                            break;
                        case STOP:
                            break;
                    }
                    switch (RIGHT_WHEEL){
                        case FORWARD:
                            i++;
                            break;
                        case BACKWARD:
                            break;
                        case STOP:
                            break;
                    }

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                        //discard
                    }
                }
        } while (working);
    }
}
