package robodesk.application;

import com.pi4j.io.gpio.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.util.stream.IntStream;

//@Service
public class RobodeskRunner implements ApplicationRunner {

    private final byte[][] HALFSTEP_SEQ = {
            {0,1,1,1},
            {0,0,1,1},
            {1,0,1,1},
            {1,0,0,1},
            {1,1,0,1},
            {1,1,0,0},
            {1,1,1,0},
            {0,1,1,0}
    };

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("<--Pi4J--> GPIO Control Example ... started.");

        final GpioController GPIO = GpioFactory.getInstance();
        final GpioPinDigitalOutput[] CONTROL_PINS_A = {
                GPIO.provisionDigitalMultipurposePin(RaspiPin.GPIO_07, PinMode.DIGITAL_OUTPUT),
                GPIO.provisionDigitalMultipurposePin(RaspiPin.GPIO_00, PinMode.DIGITAL_OUTPUT),
                GPIO.provisionDigitalMultipurposePin(RaspiPin.GPIO_02, PinMode.DIGITAL_OUTPUT),
                GPIO.provisionDigitalMultipurposePin(RaspiPin.GPIO_03, PinMode.DIGITAL_OUTPUT)
        };
        final GpioPinDigitalOutput[] CONTROL_PINS_B = {
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

        IntStream.range(0, 360).forEach(
                i -> {
                    for (int k = 0; k < 7; k++){
                        byte[] halfstepLeftWheel = HALFSTEP_SEQ[k];
                        byte[] halfstepRightWheel = HALFSTEP_SEQ[7-k];
                        IntStream.range(0,4).forEach(
                                n -> {
                                    if (halfstepLeftWheel[n] == 0){
                                        CONTROL_PINS_A[n].setMode(PinMode.DIGITAL_INPUT);
                                    } else {
                                        CONTROL_PINS_A[n].setMode(PinMode.DIGITAL_OUTPUT);
                                        CONTROL_PINS_A[n].high();
                                    }

                                    if (halfstepRightWheel[n] == 0){
                                        CONTROL_PINS_B[n].setMode(PinMode.DIGITAL_INPUT);
                                    } else {
                                        CONTROL_PINS_B[n].setMode(PinMode.DIGITAL_OUTPUT);
                                        CONTROL_PINS_B[n].high();
                                    }
                                }
                        );
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ex){
                            //discard
                        }
                    }
                }
        );

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
}
