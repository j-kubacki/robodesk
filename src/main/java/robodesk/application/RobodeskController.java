package robodesk.application;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RobodeskController {

    @RequestMapping("/")
    public String main(){
        return "index";
    }

    @RequestMapping("/goleft")
    public String goLeft(){
        System.out.println();
        return "index";
    }

    @RequestMapping("/goright")
    public String goRight(){
        System.out.println();
        return "index";
    }
}
