
import java.io.File;
import java.io.RandomAccessFile;
import java.util.Arrays;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author David
 */
public class init {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        BotHTTPClient listener = new BotHTTPClient();
        RandomAccessFile offsetFile = new RandomAccessFile(new File("offset.txt"), "rw");

        listener.setOFFSET(38664566);
        //System.out.println(s);
        while (true) {
            String s = listener.getNextUpdate();
            if (!s.equals(NULL_REQUEST)) {
                System.out.println(s);
                Update[] updates = JSONParser.parse(s);
                if (updates != null && updates.length > 0) {
                    listener.setOFFSET(updates[updates.length - 1].update_id + 1);
                    System.out.println(Arrays.toString(updates));
                    for (Update update : updates) {
                    System.out.println("Recieved message from " + update.message.chat.first_name);
                        System.out.println("\tmessage # " + update.message.message_id);
                        listener.sendMessage(update.message.chat.id, "You said: " + update.message.text == null ? "No text" : update.message.text);
                    }

                } else {
                    System.out.println("null");
                }
                System.out.println("offset = " + listener.getOFFSET());
                Thread.sleep(500);
            }
        }
    }
    private static final String NULL_REQUEST = "{\"ok\":true,\"result\":[]}";

}
