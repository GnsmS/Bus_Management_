package projectsrc;

import java.util.ArrayList;
import java.util.Collections;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.NullPointerException;
import java.util.Scanner;

class PathCalculater {

    int maxId = 0;
    ArrayList<Stop> listOfStops;
    Graph mg;
    String transfersFile, stopTimesFile, stopsFile;
    double[] dists;

    /*
     * -Create ShortestPath object than call getPathSequence(start, end).
     * 
     * -Returns string with cost then sequence of stops each on a new line.
     * 
     * -If one of the stops is will return an appropriate statement.
     * 
     * -Returns appropriate statement if there is no path.
     * 
     * -Throws FileNotFoundException
     */

    public PathCalculater(String transfersFile, String stopTimesFile, String stopsFile) throws FileNotFoundException {
        this.transfersFile = transfersFile;
        this.stopTimesFile = stopTimesFile;
        this.stopsFile = stopsFile;
        listOfStops = new ArrayList<Stop>();

        getStops();
        setupEdges();
    }

    public void getStops() throws FileNotFoundException {
        File stops = new File(stopsFile);
        Scanner stopsScanner = new Scanner(stops);
        stopsScanner.nextLine();

        String stop;
        String[] stopInfo;
        int stopId;

        while (stopsScanner.hasNextLine()) {
            stop = stopsScanner.nextLine();
            stopInfo = stop.split(",");
            stopId = Integer.parseInt(stopInfo[0]);
            listOfStops.add(new Stop(stopId, stopInfo[2]));
            maxId = Math.max(stopId, maxId);
        }
        maxId++;
        stopsScanner.close();
    }

    void setupEdges() throws FileNotFoundException {
        File stopTimes = new File(stopTimesFile);
        File transfers = new File(transfersFile);

        Scanner info = new Scanner(transfers);
        info.nextLine();

        mg = new Graph(maxId);

        String transfer;
        String[] transferValues;

        while (info.hasNextLine()) {
            transfer = info.nextLine();
            transferValues = transfer.split(",");

            int from = Integer.parseInt(transferValues[0]);
            int to = Integer.parseInt(transferValues[1]);
            int type = Integer.parseInt(transferValues[2]);
            int cost = (type == 0) ? 2 : (Integer.parseInt(transferValues[3]) / 100);

            mg.addEdge(from, to, cost);
        }
        info.close();

        info = new Scanner(stopTimes);
        info.nextLine();

        String trip;
        String[] tripValues;
        int from = 1;
        int to, sequenceValue;
        boolean skipEdge;

        while (info.hasNextLine()) {
            trip = info.nextLine();
            tripValues = trip.split(",");
            sequenceValue = Integer.parseInt(tripValues[4]);

            skipEdge = false;

            if (sequenceValue != 1) {
                to = Integer.parseInt(tripValues[3]);
                for (Edge ed : mg.adj(from)) {
                    if (ed.to == to) {
                        skipEdge = true;
                        break;
                    }
                }
                if (!skipEdge) {
                    mg.addEdge(from, to, 1);
                }
            }

            from = Integer.parseInt(tripValues[3]);
        }
        info.close();
    }

    public void relax(double[] distTo, Edge[] edgeTo, int v) {
        for (Edge e : mg.adj(v)) {
            int w = e.to;
            if (distTo[w] > distTo[v] + e.weight) {
                distTo[w] = distTo[v] + e.weight;
                edgeTo[w] = e;
            }
        }
    }

    public Edge[] runDijkstra(int start) {
        double[] distTo = new double[maxId];
        boolean[] relaxed = new boolean[maxId];
        Edge[] edgeTo = new Edge[maxId];

        for (Stop stop : listOfStops) {
            distTo[stop.getId()] = Double.POSITIVE_INFINITY;
            relaxed[stop.getId()] = false;
            edgeTo[stop.getId()] = null;
        }
        distTo[start] = 0;

        int current = start;

        for (int i = 0; i < listOfStops.size(); i++) {
            relax(distTo, edgeTo, current);
            relaxed[current] = true;
            double min = Double.POSITIVE_INFINITY;
            for (Stop stop : listOfStops) {
                if (distTo[stop.getId()] < min && !relaxed[stop.getId()]) {
                    min = distTo[stop.getId()];
                    current = stop.getId();
                }
            }
        }
        dists = distTo;
        return edgeTo;
    }

    public String getPathSequence(int start, int end) throws FileNotFoundException {
        setupEdges();

        if (getStopById(start) == null) {
            return "Departure stop does not exist";
        }
        if (getStopById(end) == null) {
            return "Destination stop does not exist";
        }

        Edge[] edgeTo = runDijkstra(start);

        ArrayList<String> sequence = new ArrayList<String>();

        int currentStop = end;

        sequence.add(getStopById(end));
        do {
            try {
                currentStop = edgeTo[currentStop].from;
            } catch (NullPointerException e) {
                return "There is no path !";
            }
            sequence.add(getStopById(currentStop));
        } while (currentStop != start);
        Collections.reverse(sequence);

        sequence.add(0, dists[end] + "");

        String returnString = "";
        for (String string : sequence) {
            returnString += string + "\n";
        }
        return returnString;
    }

    public String getStopById(int id) {
        for (Stop s : listOfStops) {
            if (s.getId() == id) {
                return s.getName();
            }
        }
        return "Faulty Stop!";
    }

    class Edge {

        int from, to;
        double weight;

        Edge(int from, int to, double weight) {
            this.to = to;
            this.from = from;
            this.weight = weight;
        }

    }

    class Graph {

        ArrayList<ArrayList<Edge>> adjL = new ArrayList<ArrayList<Edge>>();

        Graph(int edges) {
            for (int i = 0; i < edges; i++) {
                adjL.add(new ArrayList<Edge>());
            }
        }

        ArrayList<Edge> adj(int vertex) {
            return adjL.get(vertex);
        }

        void addEdge(int from, int to, int weight) {
            adjL.get(from).add(new Edge(from, to, weight));
        }
    }

    class Stop {

        int id;
        String name;

        Stop(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }
    }
}