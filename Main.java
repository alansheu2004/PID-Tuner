import java.util.*;

public class Main {
    public static void main(String[] args) {
        PID pid = new PID(1,2,3,4);
        new PIDFrame(pid);

        Scanner scanner = new Scanner(System.in);
        while(true) {
            pid.update(scanner.nextDouble(), scanner.nextDouble());
        }
    }
}