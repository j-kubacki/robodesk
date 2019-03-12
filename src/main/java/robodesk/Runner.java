package robodesk;

import com.pi4j.io.gpio.RaspiPin;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import robodesk.application.RobodeskEasyDriverController;

@Component
class Runner implements CommandLineRunner {


    @Override
    public void run(String...args) throws Exception {
        System.out.println("START !");
        RobodeskEasyDriverController cont = new RobodeskEasyDriverController(RaspiPin.GPIO_24,RaspiPin.GPIO_23,null,null,null,null,null);

        cont.move(1,2,3);
        cont.shutdown();
        System.out.println("END !");
    }
}