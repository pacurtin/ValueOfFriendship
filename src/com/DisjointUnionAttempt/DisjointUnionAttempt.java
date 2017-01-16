package com.DisjointUnionAttempt;

/**
 * Created by padraig.curtin on 13/01/2017.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.*;

class Node{
    private int name = 0;
    private int rank = 0;
    private Node parent = this;
    private ArrayList<Node> children = new ArrayList<>();
    private int numChildren=1; //counting itself as one for this question.
    //need a flag to show this set has already been merged into another set.
    private boolean visited = false;

    void addChild(Node child){
        children.add(child);
    }
    List<Node> getChildren(){
        return children;
    }

    public void setNumChildren(int numChildren) {
        this.numChildren = numChildren;
    }
    public int getNumChildren() {
        return numChildren;
    }

    void setVisited(){
        this.visited=true;
    }
    boolean isVisited(){
        return this.visited;
    }

    public Node getParent() {
        return parent;
    }
    public void setParent(Node parent) {
        this.parent = parent;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    int getName(){
        return name;
    }

    //Node getRepresentative(){
    //    return this.representative;
    //}

    @Override
    public String toString() {
        return "Node "+name;
    }

    //void setRepresentative(Node representative){
    //    this.representative=representative;
    //}

    Node(int name){
        this.name=name;
    }

    void printTreeAsList() {
        List<Node> treeMembers = new ArrayList<>();
        List<Node> currentFriends = new ArrayList<>();
        int count = 0;
        treeMembers.add(this);
        currentFriends.add(this);

        for (int curr = 0; curr < currentFriends.size(); curr++) {
            Node currentFriend = currentFriends.get(curr);
            count++;
            for (Node child : currentFriend.getChildren()) {
                Node newFriend = child;
                currentFriends.add(newFriend);
                treeMembers.add(newFriend);
            }
        }
        System.out.println("This tree has " + count + " members");
        System.out.println(treeMembers);
    }
}

public class DisjointUnionAttempt {
    //Correct BigInput answer = 194991399930000
    public static long totalTime = 0;//for use in SpeedTest
    static HashMap<Integer, Node> trees = new HashMap<>();

    static void createSet(int x){
        trees.put(x, new Node(x));
    }

    static void mergeSets(int x, int y){
        Node PX = trees.get(findRepresentative(x).getName());//should be using representatives here.
        Node PY = trees.get(findRepresentative(y).getName());
        //System.out.println("-----------------------------------------------------------------------------" );
        //System.out.println("Merging "+ PX + " and " + PY );
        //PX.printTreeAsList();
        //PY.printTreeAsList();
        if (PX.getRank()>PY.getRank()){
            PY.setParent(PX);
            PX.addChild(PY);
            PY.setVisited();
            PX.setNumChildren(PX.getNumChildren() + PY.getNumChildren());
            //PX.printTreeAsList();
        }else{
            PX.setParent(PY);
            PY.addChild(PX);
            PX.setVisited();
            PY.setNumChildren(PX.getNumChildren()+PY.getNumChildren());
            //PY.printTreeAsList();
        }
        if (PX.getRank() == PY.getRank()){
            PY.setRank(PY.getRank()+1);
        }
        //System.out.println("-----------------------------------------------------------------------------" );
    }

    static Node findRepresentative(int x){      //representitive is the root of the tree?
        if(trees.get(x)!=trees.get(x).getParent()){                                              //if node x's parent is not itself
            trees.get(x).setParent(findRepresentative(trees.get(x).getParent().getName()));     //set parent of x to findRepresentitive of parent of x
        }
        return trees.get(x).getParent();
    }

    public static void main(String[] args) {
        //Scanner in = new Scanner(System.in);
        try{
            long startTime = System.nanoTime();
            Scanner in = new Scanner(new File("resources/multiInput.txt"));
            int queries = in.nextInt();

            for(int i = 0; i < queries; i++){
                trees = new HashMap<>();//clear trees
                int numVertices = in.nextInt();
                int meaningfulFriendships = 0;
                List<Integer> localMeaningfulFriendshipsList = new ArrayList<>();
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

                for(Node node: trees.values()){
                    if(!node.isVisited()&&node.getNumChildren()>1){
                        //node.printTreeAsList();
                        //System.out.println(node.getNumChildren());
                        localMeaningfulFriendshipsList.add(node.getNumChildren()-1);
                        meaningfulFriendships+=node.getNumChildren()-1;
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
                System.out.println("D union Finished. Time taken "+ ((endTime - startTime)/1000000)+ " milliseconds");
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
