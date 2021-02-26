package rusbik.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RusbikLogger extends Thread{
    private volatile List<RusbikBlockActionPerformLog> blockAccitionPerformLogs = new ArrayList<>();

    public RusbikLogger() {
        this.start();
    }
    
    /**
     * Add log to the log list.
     * @param log with the information of the action carried out on the block
     */
    public synchronized void addBlockActionPerformLog( RusbikBlockActionPerformLog log){
         this.blockAccitionPerformLogs.add(log);
     }
     /**
      * Thread of writing logs in the database.
      */
    @Override
     public void run(){
         while(true){// Write first list log in database and remove it from the list
             if(!blockAccitionPerformLogs.isEmpty()){
                try {
                    RusbikDatabase.blockLogging(this.blockAccitionPerformLogs.get(0));
                } catch (SQLException e) {
                     e.printStackTrace();
                }
                this.blockAccitionPerformLogs.remove(0);
             }else{
                 try {// Sleep Thread execution 500 ms
                     Thread.sleep(500);
                 } catch (InterruptedException e) {
                     e.printStackTrace();
                 }
             }
         }
     }
     
}
