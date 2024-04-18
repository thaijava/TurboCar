package beam.turbocar;

public class BlockOfChar extends Block{
    String charString ;
    public BlockOfChar(String charString, int row, int column) {
        super();
        this.row = row;
        this.column = column;
        this.charString = charString;
    }

    public String toString() {
        return  charString + "," + row + "," + column;
    }
}
