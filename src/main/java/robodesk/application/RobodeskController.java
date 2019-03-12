//package robodesk.application;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//@Controller
//public class RobodeskController {
//
//    @Autowired
//    private RobodeskRunner runner;
//
//    @RequestMapping("/")
//    public String main(){
//        runner.setup();
//        return "index";
//    }
//
//    @RequestMapping("/goleft")
//    public String goLeft(){
//        System.out.println("LEFT!");
//        runner.goLeft();
//        return "index";
//    }
//
//    @RequestMapping("/goright")
//    public String goRight(){
//        System.out.println("RIGHT!");
//        runner.goRight();
//        return "index";
//    }
//
//    @RequestMapping("/goforward")
//    public String goForward(){
//        System.out.println("FORWARD!");
//        runner.goForward();
//        return "index";
//    }
//
//    @RequestMapping("/stop")
//    public String stop(){
//        System.out.println("STOP!");
//        runner.stop();
//        return "index";
//    }
//
//    @RequestMapping("/end")
//    public String end(){
//        runner.teardown();
//        return "index";
//    }
//}
