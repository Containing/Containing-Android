package Network;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map;

/**
 * StatsMessage object
 * @author Christiaan
 */
public class StatsMessage implements Serializable {
    public Date date = new Date();
    public long containers_incoming;
    public long containers_outgoing;
    public HashMap<String, Integer> areas = new HashMap<String, Integer>();
    public HashMap<String, Integer> vehicles = new HashMap<String, Integer>();
    
    /**
     * Helper method for getting the keys of areas
     * @return 
     */
    public String[] getAreaKeys() {
        return getKeys(areas);
    }
    
    /**
     * Helper method for getting the values of areas
     * @return 
     */
    public Integer[] getAreaValues() {
        return getValues(areas);
    }
    
    /**
     * Helper method for getting vehicle keys
     * @return 
     */
    public String[] getVehicleKeys() {
    	return getKeys(vehicles);
    }
    
    /**
     * Helper method for getting the values of vehicles
     * @return 
     */
    public Integer[] getVehicleValues() {
        return getValues(vehicles);
    }
    
    /**
     * Private helper method for getting keys from a HashMap
     * @param map
     * @return
     */
    private String[] getKeys(HashMap<String, Integer> map) {
        Set set = map.entrySet();
        Iterator it = set.iterator();
        String[] keys = new String[set.size()];
        int index = 0;
        while(it.hasNext()) {
            Map.Entry me = (Map.Entry)it.next();
            keys[index] = (String)me.getKey();
            index++;
        }
        return keys;
    }
    
    /**
     * Private helper method for getting values from a HashMap
     * @param map
     * @return
     */
    private Integer[] getValues(HashMap<String, Integer> map) {
        Set set = map.entrySet();
        Iterator it = set.iterator();
        Integer[] values = new Integer[set.size()];
        int index = 0;
        while(it.hasNext()) {
            Map.Entry me = (Map.Entry)it.next();
            values[index] = (Integer)me.getValue();
            index++;
        }
        return values;
    }
}
