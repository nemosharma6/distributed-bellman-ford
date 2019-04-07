import java.util.ArrayList;
import java.util.List;

public class Master {

    static final int MaxRoundGap = 15;

    public static void main(String[] args) {
        Log.init(args[1]);
        ParseInput.readFile(args[0]);
        List<Node> nodes = new ArrayList<>();

        ArrayList<Link> linkList = new ArrayList<>();
        for (int i = 0; i < ParseInput.processCount; i++) {
            for (int j = 0; j < ParseInput.processCount; j++) {
                if (ParseInput.adjacencyMatrix[i][j] > 0 && j > i)
                    linkList.add(new Link(i, j));
            }
        }

        for (int i = 0; i < ParseInput.processCount; i++) {
            ArrayList<Link> links = new ArrayList<>();
            for (Link e : linkList) {
                if (e.getNode1() == i || e.getNode2() == i)
                    links.add(e);
            }
            nodes.add(new Node(i, links));
        }

        int round = 0;

        try {
            while (round < ParseInput.processCount) {

                round++;
                while (true) {
                    for (Node node : nodes)
                        node.start();

                    for (Node node : nodes)
                        node.join();

                    boolean roundStatus = true;
                    for (Node node : nodes)
                        roundStatus = roundStatus && node.isReadyToGo();

                    if(roundStatus) {
                        for (Node node: nodes)
                            node.setNextAsTrue();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Log.write(Master.class.getName(), e.getMessage());
        }

        for (Node node : nodes) {
            Log.write(Master.class.getName(), "Node : " + node.getId() + ", Parent : " + node.parent() +
                    ", Path : " + node.getPath() + ", Distance : " + node.getDistance() + ", MCount :" + node.getCount());
            System.out.println(Master.class.getName() + " : " + node.getId() + ", Parent : " + node.parent() +
                    ", Path : {" + node.getPath() + " }, Distance : " + node.getDistance() + ", MCount :" + node.getCount());
        }
    }
}
