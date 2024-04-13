package beam.rocketcar;

import java.io.Serializable;

public class CarPos implements Serializable {
    long id;
    int row;
    int column;

    public CarPos(long id, int row, int col) {
        this.id = id;
        this.row = row;
        this.column = col;
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
