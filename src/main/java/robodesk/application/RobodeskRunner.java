package robodesk.application;

import com.pi4j.io.gpio.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.util.stream.IntStream;

@Service
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
                GPIO.provisionDigitalMultipurposePin(RaspiPin.GPIO_04, PinMode.DIGITAL_OUTPUT),
                GPIO.provisionDigitalMultipurposePin(RaspiPin.GPIO_17, PinMode.DIGITAL_OUTPUT),
                GPIO.provisionDigitalMultipurposePin(RaspiPin.GPIO_27, PinMode.DIGITAL_OUTPUT),
                GPIO.provisionDigitalMultipurposePin(RaspiPin.GPIO_22, PinMode.DIGITAL_OUTPUT)
        };
        final GpioPinDigitalOutput[] CONTROL_PINS_B = {
                GPIO.provisionDigitalMultipurposePin(RaspiPin.GPIO_12, PinMode.DIGITAL_OUTPUT),
                GPIO.provisionDigitalMultipurposePin(RaspiPin.GPIO_16, PinMode.DIGITAL_OUTPUT),
                GPIO.provisionDigitalMultipurposePin(RaspiPin.GPIO_20, PinMode.DIGITAL_OUTPUT),
                GPIO.provisionDigitalMultipurposePin(RaspiPin.GPIO_21, PinMode.DIGITAL_OUTPUT)
        };

        for (GpioPinDigitalOutput pin : CONTROL_PINS_A){
            //pin.setState(PinState.LOW);
            pin.low();
        }
        for (GpioPinDigitalOutput pin : CONTROL_PINS_B){
            //pin.setState(PinState.LOW);
            pin.low();
        }

        IntStream.range(0, 360).forEach(
                i -> {
                    for (byte[] halfstep : HALFSTEP_SEQ){
                        IntStream.range(0,4).forEach(
                                n -> {
                                    if (halfstep[n] == 0){
                                        CONTROL_PINS_A[n].setMode(PinMode.DIGITAL_INPUT);
                                        CONTROL_PINS_A[n].low();
                                        CONTROL_PINS_B[n].setMode(PinMode.DIGITAL_INPUT);
                                        CONTROL_PINS_B[n].low();
                                    } else {
                                        CONTROL_PINS_A[n].setMode(PinMode.DIGITAL_OUTPUT);
                                        //CONTROL_PINS_A[n].setState(PinState.HIGH);
                                        CONTROL_PINS_A[n].high();
                                        CONTROL_PINS_B[n].setMode(PinMode.DIGITAL_OUTPUT);
                                        //CONTROL_PINS_B[n].setState(PinState.HIGH);
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
            //pin.setState(PinState.HIGH);
            pin.high();
        }
        for (GpioPinDigitalOutput pin : CONTROL_PINS_B){
            pin.setMode(PinMode.DIGITAL_OUTPUT);
            //pin.setState(PinState.HIGH);
            pin.high();
        }

        GPIO.shutdown();

        System.out.println("Exiting ControlGpioExample");
    }
}
