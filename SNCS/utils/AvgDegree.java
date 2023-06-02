package utils;

import online.MetaPath;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/*
  根据查询顶点和元结构路径来计算查询到的社区的平均度数
  buildGraph 是找到在给定的元路径下所有查询顶点的pnb，保存在graphMap中
 */
public class AvgDegree {
    private  int[][] graph = null;//data graph, including vertex IDs, edge IDs, and their link relationships
    private  int[] vertexType = null;//vertex -> type
    private  int[] edgeType = null;//edge -> type
    private Set<Integer> community=null;
    public  Map<Integer,Set<Integer>> graphMap=new HashMap<>();  //存储按照元路径搜索的子图

    private int queryId = -1;//the query vertex id
    private MetaPath queryMPath = null;//the query meta-path
    private int queryK = -1;//the threshold k
    private double theta; //the topic threshold

    public AvgDegree(int[][] graph, int[] vertexType, int[] edgeType , MetaPath queryMPath, Set<Integer> community) {
        this.graph = graph;
        this.vertexType = vertexType;
        this.edgeType = edgeType;
        this.queryMPath=queryMPath;
        this.community=community;
        //检查查询的顶点的类型是否和我们的起始路径一样,一样的话建立连通子图
        graphMap=buildGraph();

    }

    public Set<Integer> query(int queryId,int queryK,double theta){
        this.queryId=queryId;
        this.queryK=queryK;
        this.theta=theta;
        if (queryMPath.vertex[0]!=vertexType[queryId]) return null;
        return null;
    }


    //找到在给定的元路径下所有查询顶点的pnb，保存在graphMap中
    //这样做太复杂，我们对此进行修改，只是找一下搜索到的这些顶点之间的连接关系
    private Map<Integer, Set<Integer>> buildGraph() {
        long count = 0;
        Map<Integer, Set<Integer>> graphMap = new HashMap<Integer, Set<Integer>>();
        for(int curId : community) {
            if(vertexType[curId] == queryMPath.vertex[0]) {
                List<Set<Integer>> visitList = new ArrayList<Set<Integer>>();
                for(int i = 0;i <=queryMPath.pathLen;i ++)   visitList.add(new HashSet<Integer>());
                Set<Integer> pnbSet = new HashSet<Integer>();
                findPNeighbors(curId, curId, 0, visitList, pnbSet);    //找到curId的所有pnb，pnbSet也可以认为是边的数目
//                System.out.println(pnbSet.size());
                count += pnbSet.size();
                graphMap.put(curId, pnbSet);
            }
        }
        System.out.println("In the evaluation, for the entire graph, pnb is connected to the construction is completed");
        System.out.println("The average degree of the whole graph is: " + count * 1.0 / graphMap.size());  //输出的整个图的平均度
        return graphMap;
    }

    //一个深度优先搜索来寻找它的p邻居
    private void findPNeighbors(int startID, int curId, int index, List<Set<Integer>> visitList, Set<Integer> pnbSet) {
        int targetVType = queryMPath.vertex[index + 1], targetEType = queryMPath.edge[index];
        int[] nbArr = graph[curId];
        for(int i = 0;i < nbArr.length;i += 2) {
            int nbVertexID = nbArr[i], nbEdgeID = nbArr[i + 1];
            Set<Integer> visitSet = visitList.get(index + 1);
            if(!visitSet.contains(nbVertexID) && targetVType == vertexType[nbVertexID] && targetEType == edgeType[nbEdgeID]) {
                if(index + 1 < queryMPath.pathLen) {
                    findPNeighbors(startID, nbVertexID, index + 1, visitList, pnbSet);
                    visitSet.add(nbVertexID);//mark this vertex (and its branches) as visited
                }else {//a meta-path has been found
                    if(nbVertexID != startID && community.contains(nbVertexID))   {
//                        System.out.println(pnbSet.size());
                        pnbSet.add(nbVertexID);
                    }
                    visitSet.add(nbVertexID);//mark this vertex (and its branches) as visited
                }
            }
        }
    }

    //计算的是我们查到的图的邻居，统计我们查到的图的顶点、边、密度
    public void calculateDensity(Set<Integer> querySet) throws IOException {
        Map<Integer,Set<Integer>> queryGraph=new HashMap<>();
        for (int id:querySet){
            queryGraph.put(id,new HashSet<Integer>());
        }
        for (int id:querySet){
            Set<Integer> pNbSet=graphMap.get(id);
            for (int pnb:pNbSet){
                if (querySet.contains(pnb)){
                    queryGraph.get(id).add(pnb);
                }
            }
        }

        int count=0;
        for (int id:queryGraph.keySet())  count+=queryGraph.get(id).size();

//        System.out.println("查询的顶点是："+queryId+"查询的k是："+queryK+"查询得到的图的密度是："+count*1.0/queryGraph.size());

        //我们把queryGraph存下来看看，来画以后的图
        String path="/home/lyq/TBCSH_TEST/src/resultData/queryGraph-"+queryId+"-"+queryK+"-"+(int) (theta*100)+".txt"; //为什么theta*100再转为int，是因为这样在文件操作中好表示
        //做分布的话需要画图可以从queryGraph里面读取数据画图

        File file = new File(path);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        for (int id:queryGraph.keySet()){
            StringBuilder content= new StringBuilder(Integer.toString(id));
            for (int nb:queryGraph.get(id))   content.append(" ").append(nb);
            fileOutputStream.write((content+"\n").getBytes(StandardCharsets.UTF_8));
        }
        fileOutputStream.close();
    }
}
