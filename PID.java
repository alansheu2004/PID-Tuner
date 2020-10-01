public class PID {
    private double kP;
    private double kI;
    private double kD;
    private double kF;

    private double lastError;
    private double lastTime;
    private double integral;

    private PIDFrame frame;

    public PID() {
        this(0,0,0,0);
    }

    public PID(double kP, double kI, double kD) {
        this(kP, kI, kD, 0);
    }

    public PID(double kP, double kI, double kD, double kF) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        this.kF = kF;
        
        this.integral = 0;
    }

    public double update(double actual, double setpoint) {
        double error = setpoint - actual;
        double currentTime = System.currentTimeMillis()/1000.0;

        double derivative = 0;
        
        if (lastTime != 0) {
            double interval = currentTime - lastTime;
		    integral += error * interval;
            derivative = (error - lastError) / interval;
        }

        if (frame != null) {
            frame.updateData(currentTime, actual, setpoint, integral, derivative);
        }

        lastError = error;
        lastTime = currentTime;

		return error*kP + integral*kI + derivative*kD + setpoint*kF;
    }

    public double getP() {
        return kP;
    }

    public double getI() {
        return kI;
    }

    public double getD() {
        return kD;
    }

    public double getF() {
        return kF;
    }

    public void setP(double kP) {
        this.kP = kP;
    }

    public void setI(double kI) {
        this.kI = kI;
    }

    public void setD(double kD) {
        this.kD = kD;
    }

    public void setF(double kF) {
        this.kF = kF;
    }

    public void setFrame(PIDFrame frame) {
        this.frame = frame;
    }
}