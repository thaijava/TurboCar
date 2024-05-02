package beam.turbocar;

import java.io.Serializable;

public class Command implements Serializable {
    public static final String COMMAND_REQ_MAP = "REQ_MAP:";
    public static final String COMMAND_REG_CAR = "REG_CAR:";
    public static final String COMMAND_MOVE_TO = "MOVE_TO:";
    public static final String COMMAND_GET_FRIENDS = "GET_FRIENDS:";
    public static final String COMMAND_GET_ALL_FRIENDS = "GET_ALL_FRIENDS:";
    public static final String COMMAND_BYE_BYE = "BYE_BYE";
    public static final String COMMAND_START_GAME = "START_GAME";

    String command;
    Object p1, p2, p3, p4;
    public Command(String command, Object p1, Object p2, Object p3) {
        this.command = command;
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;

    }

}
