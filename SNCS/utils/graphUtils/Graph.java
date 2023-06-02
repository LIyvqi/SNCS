package utils.graphUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Graph {

    private Map<String, Set<String>> st;   //数字字符串，用map来表示图
    private int E;

    public Graph() {
        st=new HashMap<>();
    }

    /**
     * 使用指定的分隔符从指定的文件中得到我们需要的图
     * @param filename 文件名路径
     * @param delimiter key和value的分隔符
     */
    public Graph(String filename, String delimiter) {
        st = new HashMap<String, Set<String>>();
        In in = new In(filename);       //In是一个在算法书上找的文件读取的类
        while (in.hasNextLine()) {
            String line = in.readLine();
            String[] names = line.split(delimiter);
            for (int i = 1; i < names.length; i++) {
                addEdge(names[0], names[i]);
            }
        }
        in.close();
    }

    /**
     * 返回这个图的顶点的数目
     * @return the number of vertices in this graph
     */
    public int V() {
        return st.size();
    }

    /**
     * 返回这个图的边的数目
     * @return the number of edges in this graph
     */
    public int E() {
        return E;
    }

    // throw an exception if v is not a vertex
    private void validateVertex(String v) {
        if (!hasVertex(v)) throw new IllegalArgumentException(v + " is not a vertex");
    }

    /**
     * 返回指定的V的度数
     * @param  v the vertex
     * @return the degree of {@code v} in this graph
     * @throws IllegalArgumentException if {@code v} is not a vertex in this graph
     */
    public int degree(String v) {
        validateVertex(v);
        return st.get(v).size();
    }

    /**
     * 在图中添加顶点v-w的边，如果不存在顶点把顶点也加上，如果存在就只加边
     * @param  v one vertex in the edge
     * @param  w the other vertex in the edge
     */
    public void addEdge(String v, String w) {
        if (!hasVertex(v)) addVertex(v);
        if (!hasVertex(w)) addVertex(w);
        if (!hasEdge(v, w)) E++;
        st.get(v).add(w);
        st.get(w).add(v);
    }

    /**
     * 如果顶点v不存在图中，向图添加顶点v
     * @param  v the vertex
     */
    public void addVertex(String v) {
        if (!hasVertex(v)) st.put(v, new HashSet<String>());
    }


    /**
     * 返回 图中顶点的集合
     * @return the set of vertices in this graph
     */
    public Set<String> vertices() {
        return st.keySet();
    }

    /**
     * 返回顶点v的邻居
     * @param  v the vertex
     * @return the set of vertices adjacent to vertex {@code v} in this graph
     * @throws IllegalArgumentException if {@code v} is not a vertex in this graph
     */
    public Set<String> adjacentTo(String v) {
        validateVertex(v);
        return st.get(v);
    }

    /**
     * 返回图中是否包含顶点v
     * @param  v the vertex
     * @return {@code true} if {@code v} is a vertex in this graph,
     *         {@code false} otherwise
     */
    public boolean hasVertex(String v){
        return st.containsKey(v);
    }

    /**
     * 判断 v-w是否有边相连
     * @param  v one vertex in the edge
     * @param  w the other vertex in the edge
     * @return {@code true} if {@code v-w} is a vertex in this graph,
     *         {@code false} otherwise
     * @throws IllegalArgumentException if either {@code v} or {@code w}
     *         is not a vertex in this graph
     */
    public boolean hasEdge(String v, String w) {
        validateVertex(v);
        validateVertex(w);
        return st.get(v).contains(w);
    }

    /**
     * 返回图的字符串表示
     * @return string representation of this graph
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (String v : st.keySet()) {
            s.append(v).append(": ");
            for (String w : st.get(v)) {
                s.append(w).append(" ");
            }
            s.append('\n');
        }
        return s.toString();
    }

    /**
     * Unit tests the {@code Graph} data type.
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        //输出测试一下图
        Graph graph = new Graph("D:\\JavaProject\\TBCSH_TEST\\src\\resultData\\queryGraph-4-1.txt"," ");

        System.out.println(graph);

        for (String v : graph.vertices()) {
            System.out.print(v + ": ");
            for (String w : graph.adjacentTo(v)) {
                System.out.print(w + " ");
            }
            System.out.println();
        }
    }
}