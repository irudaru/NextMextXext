package dopFiles;

import exceptions.EndOfFileException;
import exceptions.FailedCheckException;

import java.util.Scanner;

//ирир

/**
 * Абстрактный класс, предназначенный для считаывания команд с консоли и считывания команд из файла
 */

public abstract class AbstractReader implements AutoCloseable {
    /**
     * Поле типа Scanner, предназначенное для считывания строки либо из файла, либо из консоли
     */
    protected Scanner scan;

    /**
     * protected конструктор
     */
    protected AbstractReader() {
    }

    public static String[] splitter(String line) {
        String[] s = line.split(" ", 2);
        if (s.length == 2) {
            s[1] = s[1].trim();
            return s;
        } else
            return new String[]{s[0], ""};
    }

    public static Boolean parseBoolean(String s) {
        if (s.equalsIgnoreCase("true") || s.equalsIgnoreCase("t") || s.equals("1") || s.equalsIgnoreCase("y"))
            return true;
        else if (s.equalsIgnoreCase("false") || s.equalsIgnoreCase("f") || s.equals("0") || s.equalsIgnoreCase("n"))
            return false;
        throw new NumberFormatException();
    }

    /**
     * @return Возвращает последнюю строку
     */
    public abstract String read() throws EndOfFileException;

    public void close() {
        scan.close();
    }

    /**
     * Метод для парсинга Integer
     */
    public Integer handlerI(String s, Checker<Integer> c) throws EndOfFileException {
        String line;

        while (true) {
            try {
                Writer.write(s);
                line = this.read();
                if (line == null)
                    throw new EndOfFileException("Преждевременный конец файла!");
                else if (line.equals(""))
                    return c.checker(null);
                return c.checker(Integer.parseInt(line));
            } catch (NumberFormatException e) {
                Writer.writeln("\u001B[31m" + "Ошибка ввода, попробуйте еще раз" + "\u001B[0m");
            } catch (FailedCheckException e) {
                Writer.writeln("\u001B[31m" + "Условия не соблюдены, попробуйте еще раз" + "\u001B[0m");
            }
        }
    }

    /**
     * Метод для парсинга Long
     */
    public Long handlerL(String s, Checker<Long> c) throws EndOfFileException {
        String line;

        while (true) {
            try {
                Writer.write(s);
                line = this.read();
                if (line == null)
                    throw new EndOfFileException("Преждевременный конец файла!");
                else if (line.equals(""))
                    return c.checker(null);
                return c.checker(Long.parseLong(line));
            } catch (NumberFormatException e) {
                Writer.writeln("\u001B[31m" + "Ошибка ввода, попробуйте еще раз" + "\u001B[0m");
            } catch (FailedCheckException e) {
                Writer.writeln("\u001B[31m" + "Условия не соблюдены, попробуйте еще раз" + "\u001B[0m");
            }
        }
    }

    /**
     * Метод для парсинга String
     */
    public String handlerS(String s, Checker<String> c) throws EndOfFileException {
        String line;
        while (true) {
            try {
                Writer.write(s);
                line = this.read();
                if (line == null)
                    throw new EndOfFileException("Преждевременный конец файла!");
                else if (line.equals(""))
                    return c.checker(null);
                return c.checker(line);
            } catch (FailedCheckException e) {
                Writer.writeln("\u001B[31m" + "Условия не соблюдены, попробуйте еще раз" + "\u001B[0m");
            }
        }
    }

    /**
     * Метод для парсинга Boolean
     */
    public Boolean handlerB(String s, Checker<Boolean> c) throws EndOfFileException {
        String line;

        while (true) {
            try {
                Writer.write(s);
                line = this.read();
                if (line == null)
                    throw new EndOfFileException("Преждевременный конец файла!");
                else if (line.equals(""))
                    return c.checker(null);
                return c.checker(parseBoolean(line));
            } catch (NumberFormatException e) {
                Writer.writeln("\u001B[31m" + "Ошибка ввода, попробуйте еще раз" + "\u001B[0m");
            } catch (FailedCheckException e) {
                Writer.writeln("\u001B[31m" + "Условия не соблюдены, попробуйте еще раз" + "\u001B[0m");
            }
        }
    }
}



