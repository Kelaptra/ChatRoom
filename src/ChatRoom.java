import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ChatRoom {
    private ArrayList<Socket> users = new ArrayList<Socket>();
    private Map<Socket,String> users_names = new LinkedHashMap<>();
    private ServerSocket serverSocket;
    ChatRoom() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Input port to listen on:");
        try {
            serverSocket = new ServerSocket(scanner.nextInt());
            serverSocket.setReuseAddress(true);
        }
        catch(IOException e){
            System.out.println("Listen failed");
        }
    }

    public void acceptSockets(){
        while (true) {
            Socket user = null;
            try{
                user = serverSocket.accept();
            }
            catch(IOException | NullPointerException e){
                System.out.println("Connection error");
            }
            if(user == null) continue;

            users.add(user);
            users_names.put(user, user.getInetAddress().getHostAddress());

            System.out.println("New user connected" + user.getInetAddress().getHostAddress());

            ChatUserHandler userSocket = new ChatUserHandler(users, users_names, user);
            new Thread(userSocket).start();
        }
    }


    public static void main(String[] args) {
        new ChatRoom().acceptSockets();

    }


}