package utils.graphUtils;

import java.util.*;

/*
 *  以s作为起点，通过广度优先算法来寻找从s到图上任意一个顶点的路径
 */
public class PathFinder {

    // prev[v] = previous vertex on shortest path from s to v
    // dist[v] = length of shortest path from s to v
    private Map<String, String>  prev = new HashMap<>();
    private Map<String, Integer> dist = new HashMap<>();

    // run BFS in graph G from given source vertex s
    public PathFinder(Graph G, String s) {

        // put source on the queue
        Queue<String> queue = new LinkedList<>();
        queue.add(s);
        dist.put(s, 0);

        // repeated remove next vertex v from queue and insert
        // all its neighbors, provided they haven't yet been visited
        while (!queue.isEmpty()) {
            String v = queue.poll();
            for (String w : G.adjacentTo(v)) {
                if (!dist.containsKey(w)) {
                    queue.add(w);
                    dist.put(w, 1 + dist.get(v));
                    prev.put(w, v);
                }
            }
        }
    }

    // is v reachable from the source s
    public boolean hasPathTo(String v) {
        return dist.containsKey(v);
    }

    // return the length of the shortest path from v to s
    public int distanceTo(String v) {
        if (!hasPathTo(v)) return Integer.MAX_VALUE;
        return dist.get(v);
    }

    // return the shortest path from v to s as an Iterable
    public Iterable<String> pathTo(String v) {
        Stack<String> path = new Stack<String>();
        while (v != null && dist.containsKey(v)) {
            path.push(v);
            v = prev.get(v);
        }
        return path;
    }


    public static void main(String[] args) {
        String filename  = "D:\\JavaProject\\TBCSH_TEST\\src\\resultData\\queryGraph-4-1.txt";
        String delimiter = " ";
        Graph G = new Graph(filename, delimiter);
        System.out.println(G);
        String s = "0";
        PathFinder pf = new PathFinder(G, s);
        System.out.println("图构造完毕");
        String t="1";
        for (String vid:pf.pathTo(t)) System.out.print(vid+"-");
        System.out.println();
        System.out.println(t+"到"+s+"的路径长度是："+pf.distanceTo(t));
    }
}