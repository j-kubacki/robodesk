package robodesk.application;

import com.pi4j.io.gpio.*;




public class RobodeskEasyDriverController {

    public static final int FORWARD = 0;
    public static final int BACKWARD = 1;
    public static final int FULL_STEP = 0;
    public static final int HALF_STEP = 1;
    public static final int ONE_FOURTH_STEP = 2;
    public static final int ONE_EIGHTH_STEP = 3;

    private int mDrivingMode;
    private GpioPinDigitalOutput mStepPin;
    private GpioPinDigitalOutput mDirPin;
    private GpioPinDigitalOutput mSleepPin;
    private GpioPinDigitalOutput mEnablePin;
    private GpioPinDigitalOutput mMs1Pin;
    private GpioPinDigitalOutput mMs2Pin;
    private GpioPinDigitalOutput mResetPin;


    public  RobodeskEasyDriverController(int drivingMode, Pin stepPin, Pin dirPin, Pin sleepPin,
                       Pin enablePin, Pin ms1Pin, Pin ms2Pin, Pin resetPin) {
        mDrivingMode = drivingMode;

        final GpioController gpio = GpioFactory.getInstance();

        // provision gpio pin #01 as an output pin and turn on
        mStepPin = gpio.provisionDigitalOutputPin(stepPin, "Step Pin", PinState.LOW);
        mDirPin = gpio.provisionDigitalOutputPin(dirPin, "Direction Pin", PinState.LOW);
        mSleepPin = gpio.provisionDigitalOutputPin(sleepPin, "Sleep Pin", PinState.HIGH);
        mEnablePin = gpio.provisionDigitalOutputPin(enablePin, "Enable Pin", PinState.LOW);
        mMs1Pin = gpio.provisionDigitalOutputPin(ms1Pin, "MS1 Pin", PinState.HIGH);
        mMs2Pin = gpio.provisionDigitalOutputPin(ms2Pin, "MS2 Pin", PinState.HIGH);
        mResetPin = gpio.provisionDigitalOutputPin(resetPin, "Reset Pin", PinState.HIGH);

        setDrivingMode(drivingMode);


        mStepPin.setShutdownOptions(true, PinState.LOW);
        mDirPin.setShutdownOptions(true, PinState.LOW);
        mSleepPin.setShutdownOptions(true, PinState.HIGH);
        mEnablePin.setShutdownOptions(true, PinState.LOW);
        mMs1Pin.setShutdownOptions(true, PinState.HIGH);
        mMs2Pin.setShutdownOptions(true, PinState.HIGH);
        mResetPin.setShutdownOptions(true, PinState.HIGH);
    }

    public  RobodeskEasyDriverController(Pin stepPin, Pin dirPin, Pin sleepPin,
                       Pin enablePin, Pin ms1Pin, Pin ms2Pin, Pin resetPin) {
        new RobodeskEasyDriverController(ONE_EIGHTH_STEP, stepPin, dirPin, sleepPin, enablePin, ms1Pin, ms2Pin, resetPin);
    }

    public  RobodeskEasyDriverController(Pin stepPin) {
        mDrivingMode = ONE_EIGHTH_STEP;

        final GpioController gpio = GpioFactory.getInstance();

        // provision gpio pin #01 as an output pin and turn on
        mStepPin = gpio.provisionDigitalOutputPin(stepPin, "MyLED", PinState.LOW);
    }

    public  void setDrivingMode(int drivingMode) {
        switch (drivingMode) {
            case FULL_STEP: {
                mMs1Pin.low();
                mMs2Pin.low();
                break;
            }
            case HALF_STEP: {
                mMs1Pin.high();
                mMs2Pin.low();
                break;
            }
            case ONE_FOURTH_STEP: {
                mMs1Pin.low();
                mMs2Pin.high();
                break;
            }
            case ONE_EIGHTH_STEP: {
                mMs1Pin.high();
                mMs2Pin.high();
                break;
            }
        }
    }

    public  void rotate(double degrees, int interval, int drivingMode) throws InterruptedException {
        move(getStepsFromDegrees(degrees, drivingMode), interval, drivingMode);
    }

    public  void rotate(double degrees, int interval) throws InterruptedException {
        rotate(degrees, interval, mDrivingMode);
    }

    public  void move(int distance, int interval, int drivingMode) throws InterruptedException {
        if (drivingMode != mDrivingMode) {
            setDrivingMode(drivingMode);
        }

        if (distance < 0) {
            setDirection(BACKWARD);
        } else {
            setDirection(FORWARD);
        }

        for (int i = 0; i < Math.abs(distance); i++) {
            mStepPin.high();
            Thread.sleep(interval);
            mStepPin.low();
            Thread.sleep(interval);
        }
    }

    public  void move(int steps, int interval) throws InterruptedException {
        move(steps, interval, mDrivingMode);
    }

    public  void sleep() {
        mSleepPin.low();
    }

    public  void wake() {
        mSleepPin.high();
    }

    public  void reset() {
        mResetPin.low();
    }

    public  void enable() {
        mEnablePin.low();
    }

    public  void disable() {
        mEnablePin.high();
    }

    public  void shutdown() {
        final GpioController gpio = GpioFactory.getInstance();
        gpio.shutdown();
    }

    public  void setDirection(int direction) {
        if (direction == FORWARD) {
            mDirPin.low();
        } else if (direction == BACKWARD) {
            mDirPin.high();
        }
    }

    public static  double getDegreesFromStep(int steps, int drivingMode) {
        switch (drivingMode) {
            case FULL_STEP:
                return steps * 1.8;
            case HALF_STEP:
                return steps * 0.9;
            case ONE_FOURTH_STEP:
                return steps * 0.45;
            case ONE_EIGHTH_STEP:
                return steps * 0.225;
            default:
                return 0.0;
        }
    }

    public static  int getStepsFromDegrees(double degrees, int drivingMode) {
        switch (drivingMode) {
            case FULL_STEP:
                return (int) (degrees / 1.8);
            case HALF_STEP:
                return (int) (degrees / 0.9);
            case ONE_FOURTH_STEP:
                return (int) (degrees / 0.45);
            case ONE_EIGHTH_STEP:
                return (int) (degrees / 0.225);
            default:
                return 0;
        }
    }

}
