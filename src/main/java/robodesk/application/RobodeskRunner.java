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
                GPIO.provisionDigitalMultipurposePin(RaspiPin.GPIO_04, PinMode.ANALOG_OUTPUT),
                GPIO.provisionDigitalMultipurposePin(RaspiPin.GPIO_17, PinMode.ANALOG_OUTPUT),
                GPIO.provisionDigitalMultipurposePin(RaspiPin.GPIO_27, PinMode.ANALOG_OUTPUT),
                GPIO.provisionDigitalMultipurposePin(RaspiPin.GPIO_22, PinMode.ANALOG_OUTPUT)
        };
        final GpioPinDigitalOutput[] CONTROL_PINS_B = {
                GPIO.provisionDigitalMultipurposePin(RaspiPin.GPIO_12, PinMode.ANALOG_OUTPUT),
                GPIO.provisionDigitalMultipurposePin(RaspiPin.GPIO_16, PinMode.ANALOG_OUTPUT),
                GPIO.provisionDigitalMultipurposePin(RaspiPin.GPIO_20, PinMode.ANALOG_OUTPUT),
                GPIO.provisionDigitalMultipurposePin(RaspiPin.GPIO_21, PinMode.ANALOG_OUTPUT)
        };

        for (GpioPinDigitalOutput pin : CONTROL_PINS_A){
            pin.setState(PinState.LOW);
        }
        for (GpioPinDigitalOutput pin : CONTROL_PINS_B){
            pin.setState(PinState.LOW);
        }

        for (byte[] halfstep : HALFSTEP_SEQ){
            IntStream.range(0,4).forEach(
                    n -> {
                        if (halfstep[n] == 0){
                            CONTROL_PINS_A[n].setMode(PinMode.ANALOG_INPUT);
                            CONTROL_PINS_B[n].setMode(PinMode.ANALOG_INPUT);
                        } else {
                            CONTROL_PINS_A[n].setMode(PinMode.ANALOG_OUTPUT);
                            CONTROL_PINS_A[n].setState(PinState.HIGH);
                            CONTROL_PINS_B[n].setMode(PinMode.ANALOG_OUTPUT);
                            CONTROL_PINS_B[n].setState(PinState.HIGH);
                        }
                    }
            );
            Thread.sleep(3);
        }

        for (GpioPinDigitalOutput pin : CONTROL_PINS_A){
            pin.setMode(PinMode.ANALOG_OUTPUT);
            pin.setState(PinState.HIGH);
        }
        for (GpioPinDigitalOutput pin : CONTROL_PINS_B){
            pin.setMode(PinMode.ANALOG_OUTPUT);
            pin.setState(PinState.HIGH);
        }

        GPIO.shutdown();

        System.out.println("Exiting ControlGpioExample");
    }
}
