public class Main {
    public static void main(String[] args) {
        PID pid = new PID(1,2,3,4);
        new PIDFrame(pid);

        for (int i=0; true; i++) {
            pid.update(10/(1+Math.exp(-0.2*(i-30))),10);

            try {
                Thread.sleep(50);
            } catch (Exception e) {
                
            }
        }
    }
}