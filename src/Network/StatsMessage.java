package Network;

import java.io.Serializable;
import java.util.HashMap;

/**
 * StatsMessage object
 * @author Christiaan
 */
public class StatsMessage implements Serializable {
    public long containers_incoming;
    public long containers_outgoing;
    public HashMap<String, Integer> areas = new HashMap<String, Integer>();
}
