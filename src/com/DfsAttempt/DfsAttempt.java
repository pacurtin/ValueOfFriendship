package com.DfsAttempt;

import java.io.*;
import java.math.*;
import java.util.*;

//i get a stack overflow with this method... pity I suspect it would have been faster than BFS.
//unfinished

class Node{
    int name = 0;
    boolean stillALoner=true;
    HashSet<Integer> neighbours = new HashSet<>();

    void addNeighbour(int neighbour){
        neighbours.add(neighbour);
    }

    boolean isLoner(){
        return stillALoner;
    }

    void isNoLongerLoner(){
        this.stillALoner=false;
    }

    Node(int name){
        this.name=name;
    }
}

public class DfsAttempt {

    static HashMap<Integer,Node> nodes = new HashMap<>(); //putting this here to allow static dfs method change
    static int localMeaningfulFriendships=0;
    static int meaningfulFriendships = 0;
    static List<Integer> localMeaningfulFriendshipsList = new ArrayList<>();

    //Correct BigInput answer = 194991399930000

    static public void dfs(Node root) {
        //Avoid infinite loops
        if(root == null) return;

        System.out.println(root.name);
        root.isNoLongerLoner();
        localMeaningfulFriendships++;
        meaningfulFriendships++;

        //for every child
        for(int neighbour :root.neighbours) {
            Node child = nodes.get(neighbour);
            //if childs state is not visited then recurse
            if (child.isLoner()) {
                child.isNoLongerLoner();
                localMeaningfulFriendships++;
                meaningfulFriendships++;
                dfs(child);
            }
        }
    }

    public static void main(String[] args) {
        //Scanner in = new Scanner(System.in);
        try {
            Scanner in = new Scanner(new File("resources/smallInput.txt"));
            int queries = in.nextInt();

            for (int i = 0; i < queries; i++) {
                int numVertices = in.nextInt();
                int edges = in.nextInt();

                for (int j = 0; j < edges; j++) {
                    int x = in.nextInt();
                    int y = in.nextInt();
                    if (nodes.get(x) == null) {
                        nodes.put(x, new Node(x));
                    }
                    nodes.get(x).addNeighbour(y);
                    if (nodes.get(y) == null) {
                        nodes.put(y, new Node(y));
                    }
                    nodes.get(y).addNeighbour(x);
                }

                while (1 == 1) {
                    long whileLoopStartTime = System.nanoTime();
                    //to maximise partial sum we start at node with most direct friends who is still a loner.
                    Node startNode = null;
                    int highestEdgeCount = 0;
                    for (Node node : nodes.values()) {
                        if (node.neighbours.size() > highestEdgeCount && node.isLoner()) {
                            highestEdgeCount = node.neighbours.size();
                            startNode = node;
                        }
                    }

                    long findStartTime = System.nanoTime();
                    System.out.println("Found start node. " + ((findStartTime - whileLoopStartTime) / 1000000) + " milliseconds");

                    if (highestEdgeCount == 0) {
                        break;  //this means we have found all the groups. No more meaningful relationships.
                    }

                    //to keep the partial sum maximised we are always looking for loners to join the current group of friends.
                    dfs(startNode);
                }

                Collections.sort(localMeaningfulFriendshipsList);//ordered the wrong way... reverse is expensive?

                BigInteger finalMeaningfulTally = BigInteger.ZERO;
                BigInteger total = BigInteger.ZERO;

                for (int group = localMeaningfulFriendshipsList.size() - 1; group >= 0; group--) {
                    BigInteger localMeaningfulFriendships = BigInteger.valueOf(localMeaningfulFriendshipsList.get(group));
                    BigInteger groupScore = calcGroupScore(localMeaningfulFriendships);
                    BigInteger totByLocMe = total.multiply(localMeaningfulFriendships);
                    finalMeaningfulTally = finalMeaningfulTally.add(groupScore.add(totByLocMe));
                    total = total.add(calcSubTotal(localMeaningfulFriendships));
                }

                System.out.println(finalMeaningfulTally.add(total.multiply(BigInteger.valueOf(edges - meaningfulFriendships))));

                nodes = new HashMap<>();        //Have to wipe all these clean for next query.
                localMeaningfulFriendships = 0;
                meaningfulFriendships = 0;
                localMeaningfulFriendshipsList = new ArrayList<>();

            }//end of query
        }catch(FileNotFoundException ex){
            System.out.println(ex.toString());
        }
    }

    static BigInteger calcGroupScore(BigInteger m){
        BigInteger score= BigInteger.valueOf(0);
        for (BigInteger i= m; i.compareTo(BigInteger.ZERO)>0; i = i.subtract(BigInteger.ONE)){
            score = score.add(calcSubTotal(i));
        }
        return score;
    }

    static BigInteger calcSubTotal(BigInteger m){
        return m.multiply(m.add(BigInteger.valueOf(1)));
    }

}
