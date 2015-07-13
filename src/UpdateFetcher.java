
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author David
 */
public class UpdateFetcher implements Runnable {

    private ArrayList<Update> updates;
    private boolean lock;
    private long time;
    private boolean stop = false;
    @Override
    public void run() {
        BotHTTPClient listener = new BotHTTPClient();
        updates = new ArrayList<>();
        while (!stop) {
            while (lock);
            lock = true;
            try {
                String newUpdates = listener.getAllUpdates();
                if (!newUpdates.equals(NULL_REQUEST)) {
                    Update[] z = JSONParser.parse(newUpdates);
                    listener.setOFFSET(z[z.length - 1].update_id + 1);
                    updates.addAll(Arrays.asList(z));
                }
            } catch (Exception ex) {
            }
            lock = false;
            try {
                Thread.sleep(time);

            } catch (InterruptedException e) {

            }
        }
    }
    private static final String NULL_REQUEST = "{\"ok\":true,\"result\":[]}";

    public boolean hasUpdates() {
        return updates.size() > 0;
    }

    public Update getUpdate() {
        if (!hasUpdates()) {
            return null;
        }
        while (lock);
        lock = true;
        Update z = updates.remove(0);
        lock = false;
        return z;
        
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
  

}
