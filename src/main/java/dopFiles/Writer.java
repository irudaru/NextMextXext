package dopFiles;

import java.io.Serializable;
import java.util.LinkedList;


public class Writer implements Serializable {
    LinkedList<BoolString> toWrite = new LinkedList<>();

    public static void write(Object s) {
        System.out.print(s);
    }

    public static void writeln(Object s) {
        System.out.println(s);
    }

    public void addToList(boolean isLn, Object o) {
        toWrite.add(new BoolString(isLn, o.toString()));
    }

    public void writeAll() {
        boolean isLn;
        String s;
        for (BoolString boolString : toWrite) {
            isLn = boolString.getBool();
            s = boolString.getString();

            if (isLn)
                writeln(s);
            else
                write(s);
        }
    }

    public void clearList() {
        toWrite.clear();
    }
}

class BoolString implements Serializable {
    boolean bool;
    String string;

    BoolString(boolean bool, String string) {
        this.bool = bool;
        this.string = string;
    }

    public boolean getBool() {
        return bool;
    }

    public void setBool(boolean bool) {
        this.bool = bool;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }
}
