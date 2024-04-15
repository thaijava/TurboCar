package beam.turbocar;

import java.io.Serializable;

public class CarPos implements Serializable {
    long id;
    int row;
    int column;

    int headAngle;

    public CarPos(long id, int row, int col, int headAngle) {
        this.id = id;
        this.row = row;
        this.column = col;
        this.headAngle = headAngle;
    }

    public long getId() {
        return id;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public String toString() {
        return "id:" + id + " row:" + row + " col:" + column;
    }
}
