package com.DisjointSetAttempt;

/**
 * Created by padraig.curtin on 13/01/2017.
 */
import java.io.*;
import java.math.*;
import java.util.*;

class Node{
    private int name = 0;
    private Node representative = this; //could just use head of linkedlist for this?

    int getName(){
        return name;
    }

    Node getRepresentative(){
        return this.representative;
    }

    @Override
    public String toString() {
        return "Node "+name;
    }

    void setRepresentative(Node representative){
        this.representative=representative;
    }

    Node(int name){
        this.name=name;
    }
}

class ExtendedLinkedList<T> extends LinkedList<T>{
    //need a flag to show this set has already been merged into another set.
    private boolean visited = false;

    void setVisited(){
        this.visited=true;
    }

    boolean isVisited(){
        return this.visited;
    }
}

public class DisjointSetAttempt {
    //Correct BigInput answer = 194991399930000
    public static long totalTime = 0;//for use in SpeedTest
    static HashMap<Integer, ExtendedLinkedList<Node>> sets = new HashMap<>();

    static void createSet(int x){
        ExtendedLinkedList<Node> newList = new ExtendedLinkedList<>();//guy on stackoverflow says arraylist almost always faster than linkedlist?
        newList.add(new Node(x));
        sets.put(x,newList);
    }

    static void mergeSets(int x, int y){
        ExtendedLinkedList<Node> xSet = sets.get(findRepresentative(x).getName());//should be using representatives here.
        ExtendedLinkedList<Node> ySet = sets.get(findRepresentative(y).getName());
        //System.out.println("Merging "+ xSet + " and " + ySet +" with representatives "+ xSet.getFirst().getRepresentative() + " and " + ySet.getFirst().getRepresentative());
        if(xSet.size()>=ySet.size()){
            for(Node node: ySet){
                node.setRepresentative(xSet.getFirst().getRepresentative());
            }
            xSet.addAll(ySet);
            ySet.setVisited();
        }else{
            for(Node node: xSet){
                node.setRepresentative(ySet.getFirst().getRepresentative());
            }
            ySet.addAll(xSet);
            xSet.setVisited();
        }
    }

    static Node findRepresentative(int x){
        return sets.get(x).getFirst().getRepresentative();
    }

    public static void main(String[] args) {
        //Scanner in = new Scanner(System.in);
        try{
            long startTime = System.nanoTime();
            Scanner in = new Scanner(new File("resources/BigInput.txt"));
            int queries = in.nextInt();

            for(int i = 0; i < queries; i++){
                int numVertices = in.nextInt();
                for(int createdSet=1;createdSet<=numVertices;createdSet++){
                    createSet(createdSet);
                }

                int edges = in.nextInt();
                for(int j = 0; j < edges; j++){
                    int x = in.nextInt();
                    int y = in.nextInt();
                    if(!findRepresentative(x).equals(findRepresentative(y))) {
                        mergeSets(x, y);
                    }
                }

                long setsMerged = System.nanoTime();
                //System.out.println("Sets merged "+ ((setsMerged - startTime)/1000000)+ " milliseconds");


                List<Integer> localMeaningfulFriendshipsList = new ArrayList<>();
                int meaningfulFriendships = 0;
                for(ExtendedLinkedList set: sets.values()){
                    if(!set.isVisited()&&set.size()>1){
                        //System.out.println(set);
                        localMeaningfulFriendshipsList.add(set.size()-1);
                        meaningfulFriendships+=set.size()-1;
                    }
                }

                Collections.sort(localMeaningfulFriendshipsList);
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
                System.out.println("D set Finished. Time taken "+ ((endTime - startTime)/1000000)+ " milliseconds");
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
