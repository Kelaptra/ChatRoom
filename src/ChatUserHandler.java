import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ChatUserHandler implements Runnable {
    private static ArrayList<Socket> users;
    private Map<Socket,String> users_names = new LinkedHashMap<>();
    private final Socket userSocket;

    public ChatUserHandler(ArrayList<Socket> users, Map<Socket,String> users_names, Socket userSocket) {
        this.users = users;
        this.users_names = users_names;
        this.userSocket = userSocket;
    }

    public boolean leaveDemand(String message) {
        if(!message.equals("cm.leave")) {      //command leave
            return false;
        }
        users.remove(userSocket);
        users_names.remove(userSocket);
        return true;
    }

    public void changeNameDemand(String message){
        if(!message.contains("cm.change_name/ ")) return;
        String name = message.split("cm.change_name/ ")[0];
        users_names.replace(userSocket, name);
    }

    public void closer(DataInputStream in, DataOutputStream out) {
        try{
            in.close();
            out.close();
            userSocket.close();
        }
        catch(IOException e){
            System.out.println("Failed to close");
        }
    }
    public synchronized void sendToAll() {
        try {
            DataInputStream inputStream = new DataInputStream(userSocket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(userSocket.getOutputStream());;
            String message;
            while (true) {
                message = inputStream.readUTF();
                if(leaveDemand(message)){
                    System.out.println("shemovida1");
                    System.out.println(message);
                    closer(inputStream, outputStream);
                    System.out.println("shemovida2");
                    System.out.println(message);
                    return;
                }
                changeNameDemand(message);
                System.out.println(message);
                for (Socket user : users) {
                    if(user == userSocket) continue;
                    outputStream = new DataOutputStream(user.getOutputStream());
                    outputStream.writeUTF(users_names.get(user) + ": " + message);
                    outputStream.flush();
                }
            }

        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error reading/sending message");
        }
    }

    public void run() {
        sendToAll();
    }
}
