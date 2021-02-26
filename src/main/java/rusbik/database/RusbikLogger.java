package rusbik.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RusbikLogger extends Thread {
    private final List<RusbikBlockActionPerformLog> blockActionPerformLogs = new ArrayList<>();
    public boolean running;

    /**
     * Logger thread constructor and initializer
     */
    public RusbikLogger(String name) {
        super(name);
        this.running = true;
        this.start();
    }
    
    /**
     * Add log to the log list.
     * @param log with the information of the action carried out on the block
     */
    public synchronized void log(RusbikBlockActionPerformLog log) {
         this.blockActionPerformLogs.add(log);
    }

     /**
      * Thread of writing logs in the database.
      */
    @Override
     public void run() {
         while(this.running) {  // Write first list log in database and remove it from the list
             if(!this.blockActionPerformLogs.isEmpty()) {
                try {
                    RusbikDatabase.blockLogging(this.blockActionPerformLogs.get(0));
                } catch (SQLException e) {
                     e.printStackTrace();
                }
                this.blockActionPerformLogs.remove(0);
             } else {
                 try {  // Sleep Thread execution 500 ms
                     Thread.sleep(500);
                 } catch (InterruptedException e) {
                     e.printStackTrace();
                 }
             }
         }

        // Safe logger thread stop
        if(!this.blockActionPerformLogs.isEmpty()) { // Just make sure all logs are in database
            blockActionPerformLogs.forEach(log -> {
                try {
                    RusbikDatabase.blockLogging(log);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }
     }
}
