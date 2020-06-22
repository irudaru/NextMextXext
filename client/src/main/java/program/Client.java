package program;

import command.Command;
import command.Commands;
import dopFiles.AbstractReader;
import exceptions.EndOfFileException;
import dopFiles.Writer;


import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Client {

    public static void main(String[] args)
    {
        try {
            InetSocketAddress addr = new InetSocketAddress(InetAddress.getByName("localhost"), 1);
            Selector selector = Selector.open();
            SocketChannel sc = SocketChannel.open();
            sc.configureBlocking(false);
            sc.connect(addr);
            sc.register(selector, SelectionKey.OP_CONNECT /*| SelectionKey.OP_READ | SelectionKey.OP_WRITE*/);
            try {
                while (true) {
                    if (selector.select() > 0) {
                        Boolean doneStatus = process(selector);
                        if (doneStatus) {
                            break;
                        }
                    }
                    //Writer.writeln("3");
                }
            } catch ( EndOfFileException e) {
                Writer.writeln("\u001B[31m" + "Неожиданное завершение работы консоли" + "\u001B[0m");//ctrl-d
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            Writer.writeln("Клиент был закрыт...");
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Boolean process(Selector selector) throws IOException, EndOfFileException, ClassNotFoundException {
        Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

        while (iterator.hasNext()) {
            SelectionKey key = iterator.next();
            iterator.remove();

            if (key.isConnectable()) {
                Boolean connected = processConnect(selector, key);
                if (!connected) {
                    return true;
                }
            }
            if (key.isReadable()) {
                SocketChannel sc = (SocketChannel) key.channel();
                ByteBuffer bb = ByteBuffer.allocate(1024*1024);
                bb.clear();
                sc.read(bb);

                ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bb.array()));

                Writer w = (Writer) objectInputStream.readObject();
                if (w != null)
                    w.writeAll();
                else
                    Writer.writeln("Что-то не так");
                key.interestOps(SelectionKey.OP_WRITE);
            }
            if (key.isWritable()) {

                Writer.write("\u001B[33m" + "Ожидание ввода команды: " + "\u001B[0m");
                String[] com = AbstractReader.splitter(Console.console.read());
                Command command = CommanderClient.switcher(Console.console, com[0], com[1]);

                if (command == null)
                    return false;
                else if (command.getCurrent() == Commands.EXIT)
                    return true;

                SocketChannel sc = (SocketChannel) key.channel();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                objectOutputStream.writeObject(command);
                objectOutputStream.flush();
                //конец
                ByteBuffer bb = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
                sc.write(bb);
                bb.clear();

                key.interestOps(SelectionKey.OP_READ);
            }
        }
        return false;
    }
    public static Boolean processConnect(Selector selector, SelectionKey key) {
        SocketChannel sc = (SocketChannel) key.channel();
        try {
            while (sc.isConnectionPending()) {
                sc.finishConnect();
                //key.interestOps(SelectionKey.OP_WRITE);
            }
            sc.configureBlocking(false);
            sc.register(selector, SelectionKey.OP_WRITE);
        } catch (IOException e) {
            key.cancel();
            e.printStackTrace();
            return false;
        }
        return true;
    }
    /*private static Socket clientSocket; //сокет для общения
    private static DataOutputStream dOut;
    private static DataInputStream dIn;

    public static void main(String[] args) {
        try {
            try {
                clientSocket = new Socket("localhost", 1);

                dOut = new DataOutputStream(clientSocket.getOutputStream());
                dIn = new DataInputStream(clientSocket.getInputStream());

                try {
                    String[] com;
                    while (true) {
                        Writer.write("\u001B[33m" + "Ожидание ввода команды: " + "\u001B[0m");
                        com = AbstractReader.splitter(Console.console.read());
                        Command command = CommanderClient.switcher(Console.console, com[0], com[1]);

                        if (command == null)
                            continue;
                        else if (command.getCurrent() == Commands.EXIT)
                            break;

                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                        objectOutputStream.writeObject(command);
                        objectOutputStream.flush();

                        dOut.writeInt(byteArrayOutputStream.size());
                        dOut.write(byteArrayOutputStream.toByteArray());

                        int length = dIn.readInt();
                        if (length > 0) {
                            byte[] message = new byte[length];
                            dIn.readFully(message, 0, message.length); // read the message
                            ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(message));
                            Writer w = (Writer) objectInputStream.readObject();
                            w.writeAll();
                            objectInputStream.close();
                        }
                    }

                } catch (EndOfFileException e) {
                    Writer.writeln("\u001B[31m" + "Неожиданное завершение работы консоли" + "\u001B[0m");//ctrl-d
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            } finally {
                Writer.writeln("Клиент был закрыт...");
                clientSocket.close();
                dOut.close();
                dIn.close();
            }
        } catch (IOException e) {
            System.err.println(e);
        }

    }*/
}