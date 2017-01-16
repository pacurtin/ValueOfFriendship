package com.BfsAttempt;

import java.io.*;
import java.math.*;
import java.util.*;

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

public class ValueOfFriendship {
    //Correct BigInput answer = 194991399930000
    static long totalTime=0;//for use in SpeedTest

    public static void main(String[] args) {
        //Scanner in = new Scanner(System.in);
        try{
            long startTime = System.nanoTime();
            Scanner in = new Scanner(new File("resources/MultiInput.txt"));
            int queries = in.nextInt();

            for(int i = 0; i < queries; i++){
                int numVertices = in.nextInt();
                int edges = in.nextInt();
                int meaningfulFriendships = 0;
                List<Integer> localMeaningfulFriendshipsList = new ArrayList<>();
                HashMap<Integer,Node> nodes = new HashMap<>();

                for(int j = 0; j < edges; j++){
                    int x = in.nextInt();
                    int y = in.nextInt();

                    if(nodes.get(x)==null){
                        nodes.put(x,new Node(x));
                    }
                    nodes.get(x).addNeighbour(y);
                    if(nodes.get(y)==null){
                        nodes.put(y,new Node(y));
                    }
                    nodes.get(y).addNeighbour(x);
                }

                long filledAdjacencyGraphTime = System.nanoTime();
                //System.out.println("After storage Before search. Time taken "+ ((filledAdjacencyGraphTime - startTime)/1000000)+ " milliseconds");

                while(1==1){
                    long whileLoopStartTime = System.nanoTime();
                    int localMeaningfulFriendships=0;
                    //to maximise partial sum we start at node with most direct friends who is still a loner.
                    Node startNode= null;
                    int highestEdgeCount=0;
                    for(Node node:nodes.values()){
                        if(node.neighbours.size()>highestEdgeCount && node.isLoner()){
                            highestEdgeCount=node.neighbours.size();
                            startNode = node;
                        }
                    }

                    long findStartTime = System.nanoTime();
                    //System.out.println("Found start node. "+ ((findStartTime - whileLoopStartTime)/1000000)+ " milliseconds");

                    if(highestEdgeCount==0){
                        break;  //this means we have found all the groups. No more meaningful relationships.
                    }

                    //to keep the partial sum maximised we are always looking for loners to join the current group of friends.

                    List<Node> currentFriends = new ArrayList<>();
                    currentFriends.add(startNode);
                    startNode.isNoLongerLoner();

                    for(int curr=0; curr<currentFriends.size();curr++){
                        Node currentFriend = currentFriends.get(curr);
                        for(int neighbour :currentFriend.neighbours){
                            if(nodes.get(neighbour).isLoner()){
                                Node newFriend = nodes.get(neighbour);
                                meaningfulFriendships++;
                                localMeaningfulFriendships++;
                                currentFriends.add(newFriend);
                                newFriend.isNoLongerLoner();
                                newFriend.neighbours.remove(currentFriend.name);    //this should speed things up.
                            }
                            nodes.remove(nodes.get(neighbour)); //does this optimise searching for new node?
                        }
                    }
                    long searchTime = System.nanoTime();
                    //System.out.println("BFS Search. "+ ((searchTime - findStartTime)/1000000)+ " milliseconds");
                    localMeaningfulFriendshipsList.add(localMeaningfulFriendships);
                }

                long endOfSearch = System.nanoTime();
                //System.out.println("Completed BFS while loop. Time since start of query: "+ ((endOfSearch - startTime)/1000000)+ " milliseconds");
                Collections.sort(localMeaningfulFriendshipsList);//ordered the wrong way... reverse is expensive?
                //System.out.println(localMeaningfulFriendshipsList);

                BigInteger finalMeaningfulTally =  BigInteger.ZERO;
                BigInteger total = BigInteger.ZERO;

                for(int group=localMeaningfulFriendshipsList.size()-1;group>=0;group--){
                    BigInteger localMeaningfulFriendships= BigInteger.valueOf(localMeaningfulFriendshipsList.get(group));
                    BigInteger groupScore = calcGroupScore(localMeaningfulFriendships);
                    BigInteger totByLocMe = total.multiply(localMeaningfulFriendships);
                    finalMeaningfulTally = finalMeaningfulTally.add(groupScore.add(totByLocMe));
                    total=total.add(calcSubTotal(localMeaningfulFriendships));
                }

                System.out.println(finalMeaningfulTally.add(total.multiply(BigInteger.valueOf(edges - meaningfulFriendships))));

                long endTime = System.nanoTime();
                System.out.println("BFS Finished. Time taken "+ ((endTime - startTime)/1000000)+ " milliseconds");
                totalTime+=(endTime - startTime)/1000000;
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
