package com.BfsAttempt;

import com.DisjointSetAttempt.DisjointSetAttempt;
import com.DisjointUnionAttempt.DisjointUnionAttempt;

/**
 * Created by padraig on 14/01/2017.
 */

//bfs 593ms
//disjoint 453ms
//disjoint with union

public class SpeedTest {
    public static void main(String[] args){
        int testRuns = 50;
        ValueOfFriendship bfs = new ValueOfFriendship();
        DisjointSetAttempt dSet = new DisjointSetAttempt();
        DisjointUnionAttempt dSetUnion = new DisjointUnionAttempt();
        for(int i = 0; i<testRuns; i++){
            dSetUnion.main(new String[0]);
        }
        for(int i = 0; i<testRuns; i++){
            bfs.main(new String[0]);
        }
        for(int i = 0; i<testRuns; i++){
            dSet.main(new String[0]);
        }
        System.out.println("Average time for bfs was "+ (ValueOfFriendship.totalTime)/testRuns);
        System.out.println("Average time for disjoint set was "+ (DisjointSetAttempt.totalTime)/testRuns);
        System.out.println("Average time for disjoint set with union was "+ (DisjointUnionAttempt.totalTime)/testRuns);
    }

}
