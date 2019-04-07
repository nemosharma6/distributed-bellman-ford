import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/*
Keep reading messages until you have received messages from all neighbouring processes.
Ignore dummy messages.
Send messages to neighbours as per the timer. Send dummy message if its not the time.
Once all messages have been sent and received, process the received messages and update local state.
 */
public class Node implements Runnable {

    private Thread thread;
    private int id, round;
    private Link parentLink;
    private Map<Integer, Link> linkMap;
    private Map<Integer, Integer> messageTimer;
    private String name, path;
    private int distance;
    private int count;
    private int sentCount;
    private boolean next, readyToGo;
    private Map<Integer, Message> valueMap;

    Node(int id, List<Link> links) {
        this.id = id;
        round = 0;
        linkMap = new HashMap<>();
        parentLink = null;
        name = "node-" + id;
        messageTimer = new HashMap<>();

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (Link l : links) {
            linkMap.put(l.linkedTo(id), l);
            sb.append(l.toString());
            sb.append(" ");
        }

        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        Log.write(name, " edges " + sb.toString());
        int tmp = ParseInput.adjacencyMatrix[id][ParseInput.rootProcess];
        distance = tmp == 0 ? ((id == ParseInput.rootProcess) ? 0 : Integer.MAX_VALUE) : tmp;
        path = tmp == 0 ? "" : String.valueOf(ParseInput.rootProcess);
        count = sentCount = 0;
        readyToGo = false;
        next = true;
        valueMap = new HashMap<>();
    }

    void start() {
        Log.write(name, " round :" + (round++));
        thread = new Thread(this, String.valueOf(id));
        thread.start();
    }

    void join() {
        try {
            thread.join();
            Log.write(name, " finish round :" + (round - 1));
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    String parent() {
        if (parentLink == null) return "Just an Orphan";
        else return String.valueOf(parentLink.linkedTo(id));
    }

    public void run() {
        if (next) {
            readyToGo = false;
            count += linkMap.size();
            for (Map.Entry<Integer, Link> e : linkMap.entrySet()) {
                if (e.getValue().peekMsg(id) == null) continue;
                Message msg = e.getValue().readMsg(id);
                if (msg.getSrc() < 0) continue;
                int src = msg.getSrc();
                valueMap.put(src, msg);
            }

            sendMessagesToLinks();

            if (sentCount == linkMap.size() && valueMap.size() == linkMap.size()) {
                for (Map.Entry<Integer, Message> e : valueMap.entrySet()) {
                    int tmp = ParseInput.adjacencyMatrix[e.getKey()][id];
                    int src = e.getValue().getSrc(), dist = e.getValue().getDist();
                    if (tmp != 0 && dist != Integer.MAX_VALUE && tmp + dist <= distance && id != ParseInput.rootProcess) {
                        path = e.getValue().getPath() + " " + src;
                        distance = tmp + dist;
                        parentLink = linkMap.get(src);
                    }
                }

                readyToGo = true;
                next = false;
                messageTimer.clear();
                sentCount = 0;
                valueMap.clear();
                round = 0;
            }
        }
    }

    void sendMessagesToLinks() {

        Random random = new Random();
        if (messageTimer.size() == 0 && sentCount == 0) {
            for (Map.Entry<Integer, Link> e : linkMap.entrySet())
                messageTimer.put(e.getKey(), random.nextInt(Master.MaxRoundGap));
        }

        for (Map.Entry<Integer, Link> e : linkMap.entrySet()) {
            Message msg;
            if (e.getValue().peekSelfMsg(id) != null) continue;
            if (messageTimer.containsKey(e.getKey()) && messageTimer.get(e.getKey()) <= round) {
                msg = new Message(path, id, distance);
                sentCount++;
                messageTimer.remove(e.getKey());
            } else
                msg = new Message("", -1, 0); // dummy message

            e.getValue().sendMsg(id, msg);
        }
    }

    int getId() {
        return id;
    }

    String getPath() {
        return path;
    }

    int getDistance() {
        return distance;
    }

    int getCount() {
        return count;
    }

    void setNextAsTrue() {
        next = true;
    }

    boolean isReadyToGo() {
        return readyToGo;
    }
}
