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

        Graph community = new Graph(filename, delimeter);
        Set<Integer> bcoreSet = new HashSet<>();
        for (String v : community.vertices()) bcoreSet.add(Integer.parseInt(v));

        AnalyzePathSim psimAna = new AnalyzePathSim(graph, vertexType, edgeType);
        Map<Integer, Map<Integer, Integer>> pGraph = psimAna.batchBuildForPathSim(bcoreSet, queryMPath); 
        Map<Integer, Map<Integer, Float>> wGraph = new HashMap<Integer, Map<Integer, Float>>();  
        List<Float> psimList = new ArrayList<>();
        for (int vid : pGraph.keySet()) wGraph.put(vid, new HashMap<Integer, Float>());
        for (int vid : pGraph.keySet()) {
            Map<Integer, Integer> nbMap = pGraph.get(vid);
            for (int nbVid : nbMap.keySet()) {
                if (nbVid <= vid) continue;   
                float curPSimV = (float) (pGraph.get(vid).get(nbVid) + pGraph.get(nbVid).get(vid)) / (float) (pGraph.get(vid).get(vid) + pGraph.get(nbVid).get(nbVid));
                wGraph.get(vid).put(nbVid, curPSimV);
                wGraph.get(nbVid).put(vid, curPSimV);
                psimList.add(curPSimV);
            }
        }

        System.out.println("queryId, queryK,topic theta is " + queryId + ", " + queryK + ", " + theta);
        Map<Integer, Float> queryIdNbrSim = wGraph.get(queryId);
        double sum = 0;
        for (double sim : queryIdNbrSim.values()) sum += sim;
        queryIdSim=sum / queryIdNbrSim.values().size();

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
