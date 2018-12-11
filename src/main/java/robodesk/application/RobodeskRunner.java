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
                GPIO.provisionDigitalOutputPin(RaspiPin.GPIO_04, PinState.HIGH),
                GPIO.provisionDigitalOutputPin(RaspiPin.GPIO_17, PinState.HIGH),
                GPIO.provisionDigitalOutputPin(RaspiPin.GPIO_27, PinState.HIGH),
                GPIO.provisionDigitalOutputPin(RaspiPin.GPIO_22, PinState.HIGH)
        };
        final GpioPinDigitalOutput[] CONTROL_PINS_B = {
                GPIO.provisionDigitalOutputPin(RaspiPin.GPIO_12, PinState.HIGH),
                GPIO.provisionDigitalOutputPin(RaspiPin.GPIO_16, PinState.HIGH),
                GPIO.provisionDigitalOutputPin(RaspiPin.GPIO_20, PinState.HIGH),
                GPIO.provisionDigitalOutputPin(RaspiPin.GPIO_21, PinState.HIGH)
        };

        for (byte[] halfstep : HALFSTEP_SEQ){
            IntStream.range(0,4).forEach(
                    n -> {
                        if (halfstep[n] == 0){
                            CONTROL_PINS_A[n].low();
                            CONTROL_PINS_B[n].low();
                        } else {
                            CONTROL_PINS_A[n].low();
                            CONTROL_PINS_B[n].low();
                        }
                    }
            );
        }

        GPIO.shutdown();

        System.out.println("Exiting ControlGpioExample");
    }
}
