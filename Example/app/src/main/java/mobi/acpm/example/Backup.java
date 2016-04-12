package mobi.acpm.example;

import java.io.Serializable;

/**
 * Created by acpm on 06/04/16.
 */
public class Backup implements Serializable {

    public String User;
    public StringBuilder entries = new StringBuilder();

    public void addEntries(StringBuilder ent){
        entries = ent;
    }

    public String getEntries(){
        return entries.toString();
    }
}
