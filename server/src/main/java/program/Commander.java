package program;

import dopFiles.*;
import exceptions.EndOfFileException;
import exceptions.FailedCheckException;
import exceptions.IncorrectFileNameException;

import java.io.FileNotFoundException;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Comparator;

/**
 * Класс - обработчик команд с консоли
 */

public class Commander {
    public static Checker<Boolean> boolCheck = (Boolean B) -> {
        if (B != null) return B;
        else throw new FailedCheckException();
    };

    /**
     * Обработка команд, вводимых с консоли
     */
    public static boolean switcher(AbstractReader reader, Collection c, String s1, String s2) throws EndOfFileException {
        switch (s1) {
            case ("help"):
                help();
                break;
            case ("info"):
                info(c);
                break;
            case ("show"):
                show(c);
                break;
            case ("add"):
                add(reader, c, s2);
                break;
            case ("update"):
                update(reader, c, s2);
                break;
            case ("remove_by_id"):
                removeById(reader, c, s2);
                break;
            case ("clear"):
                clear(c);
                break;
            case ("save"):
                save(c);
                break;
            case ("execute_script"):
                return executeScript(c, s2);
            case ("exit"):
                return false;
            case ("add_if_min"):
                addIfMin(reader, c, s2);
                break;
            case ("remove_greater"):
                removeGreater(reader, c, s2);
                break;
            case ("remove_lower"):
                removeLower(reader, c, s2);
                break;
            case ("average_of_distance"):
                averageOfDistance(c);
                break;
            case ("min_by_creation_date"):
                minByCreationDate(c);
                break;
            case ("print_field_ascending_distance"):
                printFieldAscendingDistance(c);
                break;
            default:
                Writer.writeln("Такой команды нет");
        }
        return true;
    }

    /**
     * Показывает информацию по всем возможным командам
     */
    public static void help() {
        Writer.writeln(
                "help : вывести справку по доступным командам\n" +
                        "info : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)\n" +
                        "show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении\n" +
                        "add {element} : добавить новый элемент в коллекцию\n" +
                        "update id {element} : обновить значение элемента коллекции, id которого равен заданному\n" +
                        "remove_by_id id : удалить элемент из коллекции по его id\n" +
                        "clear : очистить коллекцию\n" +
                        "save : сохранить коллекцию в файл\n" +
                        "execute_script file_name : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.\n" +
                        "exit : завершить программу (без сохранения в файл)\n" +
                        "add_if_min {element} : добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции\n" +
                        "remove_greater {element} : удалить из коллекции все элементы, превышающие заданный\n" +
                        "remove_lower {element} : удалить из коллекции все элементы, меньшие, чем заданный\n" +
                        "average_of_distance : вывести среднее значение поля distance для всех элементов коллекции\n" +
                        "min_by_creation_date : вывести любой объект из коллекции, значение поля creationDate которого является минимальным\n" +
                        "print_field_ascending_distance : вывести значения поля distance в порядке возрастания"
        );
    }

    /**
     * Показывает информацию о коллекции
     */
    public static void info(Collection collection) {
        Writer.writeln("Тип коллекции: " + collection.list.getClass().getName());
        Writer.writeln("Колличество элементов: " + collection.list.size());
        Writer.writeln("Коллеция создана: " + collection.getDate());
    }

    /**
     * Выводит значения поля distance в порядке возрастания
     */
    public static void printFieldAscendingDistance(Collection c) {
        if (c.list.size() > 0)
            c.list.stream().filter(r -> r.getDistance() != null).map(Route::getDistance).sorted().forEach(Writer::writeln);
        else
            Writer.writeln("В коллекции нет элементов");
    }

    /**
     * выводит объект из коллекции, значение поля creationDate которого является минимальным
     */
    public static void minByCreationDate(Collection c) {
        if (c.list.size() > 0)
            Writer.writeln(c.list.stream().min(Comparator.comparing(Route::getCreationDate)).get());
        else
            Writer.writeln("В коллекции нет элементов");
    }

    /**
     * Выводит среднее значение поля distance
     */
    public static void averageOfDistance(Collection c) {
        if (c.list.size() > 0)
            Writer.writeln("Среднее значение distance: " + c.list.stream().filter(r -> r.getDistance() != null).mapToDouble(Route::getDistance).average().orElse(Double.NaN));
        else
            Writer.writeln("В коллекции нет элементов");
    }

    /**
     * Удаляет все элементы коллекции, которые меньше чем заданный
     */
    public static void removeLower(AbstractReader reader, Collection c, String s) throws EndOfFileException {
        int id = c.getRandId();
        Route newRoute = toAdd(reader, id, s);
        c.list.stream().filter(route -> route.compareTo(newRoute) < 0).forEach(route -> Writer.writeln("Удален элемент с id: " + route.getId()));
        c.list.removeIf(route -> route.compareTo(newRoute) < 0);
        Collections.sort(c.list);
    }

    /**
     * Удаляет все элементы коллекции, которые больше чем заданный
     */
    public static void removeGreater(AbstractReader reader, Collection c, String s) throws EndOfFileException {
        int id = c.getRandId();
        Route newRoute = toAdd(reader, id, s);
        c.list.stream().filter(route -> route.compareTo(newRoute) > 0).forEach(route -> Writer.writeln("Удален элемент с id: " + route.getId()));
        c.list.removeIf(route -> route.compareTo(newRoute) > 0);
        Collections.sort(c.list);
    }

    /**
     * Добавляет новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции
     */
    public static void addIfMin(AbstractReader reader, Collection c, String s) throws EndOfFileException {
        int id = c.getRandId();
        Route newRoute = toAdd(reader, id, s);
        if (newRoute.compareTo(c.list.getFirst()) < 0) {
            c.list.add(newRoute);
        } else Writer.writeln("Элемент не является минимальным в списке");
        Collections.sort(c.list);
    }

    /**
     * Считывает и исполняет скрипт из указанного файла.
     * В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме
     */
    public static boolean executeScript(Collection c, String s) {
        boolean programIsWorking = true;
        //program.Reader reader;
        try (Reader reader = new Reader(s)) {
            if (RecursionHandler.isContains(s)) {
                RecursionHandler.addToFiles(s);
                String[] com;
                Writer.write("\u001B[33m" + "Чтение команды в файле " + s + ": " + "\u001B[0m");
                String line = reader.read();
                while (line != null && programIsWorking) {
                    com = AbstractReader.splitter(line);
                    programIsWorking = Commander.switcher(reader, c, com[0], com[1]);
                    Writer.write("\u001B[33m" + "Чтение команды в файле " + s + ": " + "\u001B[0m");
                    line = reader.read();
                }
                RecursionHandler.removeLast();
            } else
                Writer.writeln("\u001B[31m" + "Найдено повторение" + "\u001B[0m");

        } catch (IncorrectFileNameException e) {
            Writer.writeln("\u001B[31m" + "Неверное имя файла" + "\u001B[0m");
        } catch (EndOfFileException e) {
            Writer.writeln("\u001B[31m" + "Неожиданный конец файла " + s + "\u001B[0m");
            RecursionHandler.removeLast();
        } catch (FileNotFoundException e) {
            Writer.writeln("\u001B[31m" + "Файл не найден" + "\u001B[0m");
        }
        return programIsWorking;
    }

    /**
     * Сохраняет коллекцию в фаил
     */
    public static void save(Collection c) {
        SaveManagement.saveToFile(c);
    }

    /**
     * Удаляет все элементы из коллекции
     */
    public static void clear(Collection c) {
        c.list.clear();
    }

    /**
     * Удаляет элемент по его id
     */
    public static void removeById(AbstractReader reader, Collection c, String s) throws EndOfFileException {
        int id;
        try {
            id = Route.idCheck.checker(Integer.parseInt(s));
        } catch (NumberFormatException | FailedCheckException e) {
            id = reader.handlerI("Введите int id: ", Route.idCheck);
        }
        Route r = c.searchById(id);
        if (r == null) {
            Writer.writeln("Такого элемента нет");
            return;
        }
        c.list.remove(r);
        Collections.sort(c.list);

    }

    /**
     * Перезаписывает элемент списка с указанным id
     */
    public static void update(AbstractReader reader, Collection c, String s) throws EndOfFileException {

        int id;
        try {
            id = Route.idCheck.checker(Integer.parseInt(s));
        } catch (NumberFormatException | FailedCheckException e) {
            id = reader.handlerI("Введите int id: ", Route.idCheck);
        }
        Route r = c.searchById(id);
        if (r == null) {
            Writer.writeln("Такого элемента нет");
            return;
        }
        String name = reader.handlerS("Введите String: name", Route.nameCheck);
        c.list.set(c.list.indexOf(r), toAdd(reader, id, name));
        Collections.sort(c.list);
    }

    /**
     * Выводит все элементы списка
     */
    public static void show(Collection c) {
        if (c.list.isEmpty())
            Writer.writeln("В коллекции нет элементов");
        else
            c.list.forEach(Writer::writeln);

    }

    /**
     * Добавляет элемент в список
     */
    public static void add(AbstractReader reader, Collection c, String s) throws EndOfFileException {
        int id = c.getRandId();
        c.list.add(toAdd(reader, id, s));
        Collections.sort(c.list);
    }

    public static Route toAdd(AbstractReader reader, int id, String s) throws EndOfFileException {

        Route route = new Route();
        route.setId(id);

        try {
            Route.nameCheck.checker(s);
            Writer.writeln("Поле name: " + s);
        } catch (FailedCheckException e) {
            s = reader.handlerS("Введите String name, диной больше 0: ", Route.nameCheck);
        }
        route.setName(s);

        Writer.writeln("Ввoд полей program.Coordinates");
        int cx = reader.handlerI("      Введите int x, не null: ", Coordinates.xCheck);
        Long cy = reader.handlerL("     Введите Long y, величиной больше -765: ", Coordinates.yCheck);
        route.setCoordinates(new Coordinates(cx, cy));

        ZonedDateTime creationTime = ZonedDateTime.now();
        route.setCreationDate(creationTime);

        Writer.writeln("Ввoд полей program.Location to");
        Long x = reader.handlerL("     Введите Long x, не null: ", Location.xyzCheck);
        long y = reader.handlerL("     Введите long y, не null: ", Location.xyzCheck);
        long z = reader.handlerL("     Введите long z, не null: ", Location.xyzCheck);
        String name = reader.handlerS("     Введите поле name, длиной меньше 867: ", Location.nameCheck);
        route.setTo(new Location(x, y, z, name));

        Writer.writeln("Является ли From null'ом?");
        if (!reader.handlerB("     Введите Bool: ", boolCheck)) {
            Writer.writeln("Ввoд полей program.Location from");
            x = reader.handlerL("     Введите Long x, не null: ", Location.xyzCheck);
            y = reader.handlerL("     Введите long y, не null: ", Location.xyzCheck);
            z = reader.handlerL("     Введите long z, не null: ", Location.xyzCheck);
            name = reader.handlerS("     Введите поле name, длиной меньше 867: ", Location.nameCheck);
            route.setFrom(new Location(x, y, z, name));
        } else
            route.setFrom(null);


        Long distance = reader.handlerL("Введите Long distance, величиной больше 1:", Route.distanceCheck);
        route.setDistance(distance);

        return route;
    }
}
