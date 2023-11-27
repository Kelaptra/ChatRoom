import java.io.*;
import java.net.Socket;
import java.util.Scanner;
public class ChatUser {
    private Socket socket;
    private DataInputStream inputStream = null;
    private DataOutputStream outputStream = null;
    private Thread reading;
    private Thread writing;
    ChatUser(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Input Address: ");
        String Address = scanner.next();
        System.out.println("Input port: ");
        int port = scanner.nextInt();
        try {
            socket = new Socket(Address, port);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            setReading();
            setWriting();
            reading.start();
            writing.start();
            reading.join();
            writing.join();

        }
        catch(IOException | InterruptedException e) {
            System.out.println("Failed to connect to the server");
            scanner.close();
        }
    }

    public boolean leaveDemand(String message) throws IOException {
        if(!message.equals("cm.leave")){
            return false;
        }
        inputStream.close();
        outputStream.close();
        socket.close();
        reading.stop();
        return true;
    }

    public void setWriting() {
        Scanner scanner = new Scanner(System.in);
        writing = new Thread(() -> {
            String message;
            while (true) {
                try {
                    message = scanner.nextLine();
                    if(leaveDemand(message)){
                        return;
                    }
                    outputStream.writeUTF(message);
                    outputStream.flush();
                }
                catch (IOException e) {
                    System.out.println("writing error");
                }
            }
        });
    }
    public void setReading(){
        reading = new Thread(() -> {
            String message;
            while (true) {
                try {
                    message = inputStream.readUTF();
                    System.out.println(message);
                }
                catch (IOException e) {
                    System.out.println("reading error");
                }
            }
        });
    }

    public static void main(String[] args) {
        new ChatUser();
    }
}
