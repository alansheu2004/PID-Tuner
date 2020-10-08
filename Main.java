public class Main {
    public static double value;
    public static void main(String[] args) {
        PID pid = new PID(0.25, 0, 0, 0);
        value = 0;
        new PIDFrame(pid);

        for (int i=0; true; i++) {
            value += (pid.update(value,10) - 0.05)/5 + (Math.random()-0.5)/30;

            try {
                Thread.sleep(50);
            } catch (Exception e) {
                
            }
        }
    }
}