package program;

import command.Command;
import dopFiles.Writer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;


public class Server {

    private static Collection collection;
    public static void main(String[]args) {
        collection = Collection.startFromSave(args);
        try {
            Selector selector = Selector.open();
            ServerSocketChannel serverSocket = ServerSocketChannel.open();
            serverSocket.bind(new InetSocketAddress("localhost", 1));
            serverSocket.configureBlocking(false);
            serverSocket.register(selector, SelectionKey.OP_ACCEPT );
            ByteBuffer buffer = ByteBuffer.allocate(1024*1024);

            while (true) {
                if (selector.select() <= 0)
                    continue;

                Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                while (iter.hasNext()) {

                    SelectionKey key = iter.next();
                    iter.remove();

                    if (!key.isValid()) {
                        continue;
                    }

                    if (key.isAcceptable()) {
                        register(selector, key);
                    } else if (key.isWritable()) {
                        answer(buffer, key);
                    } else if (key.isReadable()) {
                        read(buffer, key);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void answer(ByteBuffer buffer, SelectionKey key) throws IOException, ClassNotFoundException {
        SocketChannel client = (SocketChannel) key.channel();

        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(buffer.array()));
        Command command = (Command) objectInputStream.readObject();
        objectInputStream.close();
        buffer.clear();
        Writer.writeln("Вызвана команада: " + command.getCurrent().toString());
        Writer w = CommanderServer.switcher(command, collection);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(w);

        buffer = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());

        client.write(buffer);
        if (buffer.hasRemaining())
            buffer.compact();
        else
            buffer.clear();
        objectOutputStream.flush();
        key.interestOps(SelectionKey.OP_READ);
    }

    private static void read(ByteBuffer buffer, SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();

        try {
            if (client.read(buffer) <= 0)
                throw new SocketException();
        } catch (SocketException e){
            client.close();
            buffer.clear();
            Writer.writeln("Connection closed...");
            Writer.writeln("Server will keep running. Try running another client to re-establish connection");
            return;
        }

        key.interestOps(SelectionKey.OP_WRITE);
    }

    private static void register(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel serverSocket = (ServerSocketChannel) key.channel();
        SocketChannel client = serverSocket.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
        System.out.println("Connection Accepted: " + client.getLocalAddress());
    }

    /*public static void main(String[] args)
    {
        Collection collection = Collection.startFromSave(args);
        try {
            Selector selector = Selector.open();
            ServerSocketChannel serverSocket = ServerSocketChannel.open();
            serverSocket.bind(new InetSocketAddress("localhost", 1));
            serverSocket.configureBlocking(false);
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);
            SelectionKey key;
            while (true) {
                if (selector.select() <= 0)
                    continue;
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();
                while (iterator.hasNext()) {
                    key = iterator.next();
                    iterator.remove();
                    if (key.isAcceptable()) {
                        SocketChannel sc = serverSocket.accept();
                        sc.configureBlocking(false);
                        sc.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                        Writer.writeln("Connection Accepted: " + sc.getLocalAddress() + "\n");
                    }
                    Command command = new Command(Commands.NON);
                    if (key.isReadable()) {
                        SocketChannel sc = (SocketChannel) key.channel();
                        ByteBuffer bb = ByteBuffer.allocate(1024*1024);
                        bb.clear();
                        sc.read(bb);

                        String result = new String(bb.array()).trim();
                        if (result.length() <= 0) {
                            sc.close();
                            Writer.writeln("Connection closed...");
                            Writer.writeln("Server will keep running. Try running another client to re-establish connection");
                            continue;
                        }

                        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bb.array()));
                        command = (Command) objectInputStream.readObject();
                        bb.clear();
                        objectInputStream.close();
                    }
                    if (key.isWritable()) {
                        SocketChannel sc = (SocketChannel) key.channel();
                        ByteBuffer bb = ByteBuffer.allocate(1024*1024);
                        bb.clear();
                        Writer w = CommanderServer.switcher(command, collection);

                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                        objectOutputStream.writeObject(w);
                        objectOutputStream.flush();

                        bb = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
                        bb.flip();
                        sc.write(bb);
                        bb.clear();
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }*/

    /*private static ServerSocket server;

    private static DataOutputStream dOut;
    private static DataInputStream dIn;

    public static void main(String[] args) {
        try {
            try {
                server = new ServerSocket(1);
                Collection collection = Collection.startFromSave(args);
                Writer.writeln("Сервер запущен!");
                try (Socket clientSocket = server.accept()) {
                    dOut = new DataOutputStream(clientSocket.getOutputStream());
                    dIn = new DataInputStream(clientSocket.getInputStream());

                    while (true) {
                        int length = dIn.readInt();
                        if (length > 0) {
                            byte[] message = new byte[length];
                            dIn.readFully(message, 0, message.length);

                            ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(message));

                            Command command = (Command) objectInputStream.readObject();
                            objectInputStream.close();

                            Writer w = CommanderServer.switcher(command, collection);

                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                            objectOutputStream.writeObject(w);
                            objectOutputStream.flush();

                            dOut.writeInt(byteArrayOutputStream.size());
                            dOut.write(byteArrayOutputStream.toByteArray());
                        }
                    }
                } finally {
                    SaveManagement.saveToFile(collection);
                    dIn.close();
                    dOut.close();
                }
            } finally {
                Writer.writeln("Сервер закрыт!");
                server.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println(e);
        }
    }*/
}