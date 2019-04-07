import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Node implements Runnable {

    private Thread thread;
    private boolean initiate;
    private int id, round;
    private Link parentLink;
    private Map<Integer, Link> linkMap;
    private Map<Integer, Integer> messageTimer;
    private String name, path;
    private int distance;
    int count;

    Node(int id, List<Link> links) {
        this.id = id;
        round = 0;
        initiate = true;
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
        count = 0;
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
        if (initiate) {
            sendMessagesToLinks();
            initiate = false;
        } else {

            count += linkMap.size();
            for (Map.Entry<Integer, Link> e : linkMap.entrySet()) {
                Message msg = e.getValue().readMsg(id);
                if (msg.getSrc() < 0) continue;
                int src = msg.getSrc(), dist = msg.getDist();
                int tmp = ParseInput.adjacencyMatrix[src][id];
                if (tmp != 0 && dist != Integer.MAX_VALUE && tmp + dist <= distance && id != ParseInput.rootProcess) {
                    path = msg.getPath() + " " + src;
                    distance = tmp + dist;
                    parentLink = e.getValue();
                }
            }

            sendMessagesToLinks();
        }
    }

    void sendMessagesToLinks() {

        Random random = new Random();
        if (messageTimer.size() == 0) {
            for (Map.Entry<Integer, Link> e : linkMap.entrySet())
                messageTimer.put(e.getKey(), random.nextInt(Master.MaxRoundGap));
        }

        for (Map.Entry<Integer, Link> e : linkMap.entrySet()) {
            Message msg;
            if (messageTimer.get(e.getKey()) == round) {
                msg = new Message(path, id, distance);
                messageTimer.put(e.getKey(), random.nextInt(Master.MaxRoundGap));
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

}
