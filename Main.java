public class Main {
    public static void main(String[] args) {
        PID pid = new PID(1, 0.1, 0.2);
        new PIDFrame(pid);
    }
}