package utils.graphUtils;

import online.MetaPath;
import online.basic.FastBCore;

import java.util.*;

public class PathSimBaselineFloat {
    private int[][] graph = null;//data graph, including vertice IDs, edge IDs, and their link relationships
    private int[] vertexType = null;//vertex -> type
    private int[] edgeType = null;//edge -> type
    private MetaPath queryMPath = null;
    private String filename = null;
    private String delimeter = null;
    private double queryIdSim=0.0;
    private double avergeSim=0.0;

    public PathSimBaselineFloat(int[][] graph, int[] vertexType, int[] edgeType, MetaPath queryMPath, String filename) {
        this.graph = graph;
        this.vertexType = vertexType;
        this.edgeType = edgeType;
        this.queryMPath = queryMPath;
        this.filename = filename;
        this.delimeter = " ";
    }

    public Map<Integer, Map<Integer, Float>> query(int queryId, MetaPath queryMPath, int queryK, double theta) {

        //我们之前计算出了查询的社区，现在是读取查询到的社区
        Graph community = new Graph(filename, delimeter);
        Set<Integer> bcoreSet = new HashSet<>();
        for (String v : community.vertices()) bcoreSet.add(Integer.parseInt(v));

        //为了计算顶点的相似度，要回到原图中计算
        AnalyzePathSim psimAna = new AnalyzePathSim(graph, vertexType, edgeType);
        Map<Integer, Map<Integer, Integer>> pGraph = psimAna.batchBuildForPathSim(bcoreSet, queryMPath); // pGrpah 用来记录每一个顶点对的路径数
        Map<Integer, Map<Integer, Float>> wGraph = new HashMap<Integer, Map<Integer, Float>>();   // 初始化 wGraph用来存储每个顶点对pSim
        List<Float> psimList = new ArrayList<>();
        for (int vid : pGraph.keySet()) wGraph.put(vid, new HashMap<Integer, Float>());
        for (int vid : pGraph.keySet()) {
            Map<Integer, Integer> nbMap = pGraph.get(vid);
            for (int nbVid : nbMap.keySet()) {
                if (nbVid <= vid) continue;   //此行代码是为了避免重复计算，因为是成对出现的，我们少算一半
                float curPSimV = (float) (pGraph.get(vid).get(nbVid) + pGraph.get(nbVid).get(vid)) / (float) (pGraph.get(vid).get(vid) + pGraph.get(nbVid).get(nbVid));
                //curPsimV就是计算的两个顶点之间的相似度
                wGraph.get(vid).put(nbVid, curPSimV);
                wGraph.get(nbVid).put(vid, curPSimV);
                psimList.add(curPSimV);
            }
        }
        //输出的指标有如下这么几个：
        //1、我们查询的顶点与其它顶点的相似度的平均值
        //2、整个社区的所有顶点对的相似度的平均值
        System.out.println("queryId, queryK,topic theta is " + queryId + ", " + queryK + ", " + theta);
        Map<Integer, Float> queryIdNbrSim = wGraph.get(queryId);
        double sum = 0;
        for (double sim : queryIdNbrSim.values()) sum += sim;
        queryIdSim=sum / queryIdNbrSim.values().size();

        //计算整个图的平均值
        sum = 0;
        for (double sim : psimList) sum += sim;
        avergeSim=sum / psimList.size();
        return wGraph;
    }

    public double getQueryIdSim(){
        return queryIdSim;
    }

    public double getAvergeSim(){
        return avergeSim;
    }
}