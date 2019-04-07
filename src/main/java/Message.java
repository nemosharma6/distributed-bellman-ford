/*
store the message and the nodes communicating through the message
 */
public class Message {

    private String path;
    private int src;
    private String msg2Str;
    private int dist;

    Message(String p, int s, int d) {
        path = p;
        src = s;
        dist = d;
        msg2Str = "{ " + p + " : " + s + " -> " + d + " }";
    }

    int getSrc() {
        return src;
    }

    @Override
    public String toString() {
        return msg2Str;
    }

    int getDist() {
        return dist;
    }

    String getPath() {
        return path;
    }
}
