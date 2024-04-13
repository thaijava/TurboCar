package beam.rocketcar;

import java.io.Serializable;

public class Command implements Serializable {
    String command;
    Object p1, p2, p3;
    public Command(String command, Object p1, Object p2, Object p3) {
        this.command = command;
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;

    }
}
