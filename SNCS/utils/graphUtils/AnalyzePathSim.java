package utils.graphUtils;

import online.MetaPath;

import java.util.*;

public class AnalyzePathSim {
    private int[][] graph=null;
    private int[] vertexType=null;
    private int[] edgeType=null;

    public AnalyzePathSim(int[][] graph,int[] vertexType,int[] edgeType){
        this.graph=graph;
        this.vertexType=vertexType;
        this.edgeType=edgeType;
    }

    public Map<Integer,Map<Integer, Integer>> batchBuildForPathSim(Set<Integer> bcoreSet, MetaPath queryMPath){
        Map<Integer,Map<Integer,Integer>> pGraph=new HashMap<>();
        for (int vid:bcoreSet){
            Map<Integer, Integer> vnbPath = searchVnbPath(vid,queryMPath);
            Set<Integer> set=new HashSet<>();
            for (int v:vnbPath.keySet()) if (!bcoreSet.contains(v)) set.add(v);
            for (int v:set) vnbPath.remove(v);
            pGraph.put(vid,vnbPath);
        }
        return pGraph;
    }


    private Map<Integer, Integer> searchVnbPath(int vid,MetaPath queryMPath) {
        Map<Integer, Integer> pathNum=new HashMap<>();
        Queue<Integer> queue=new LinkedList<>();
        queue.add(vid);
        pathNum.put(vid,1);
        Map<Integer, Integer> vnbPath=new HashMap<>();

        for (int i = 0; i < queryMPath.pathLen; i++) {
            int targetV=queryMPath.vertex[i+1],targetE=queryMPath.edge[i];
            int layerLen=queue.size();

            for (int j = 0; j < layerLen; j++) {
                int vertexId=queue.poll();
                int paths=pathNum.get(vertexId);
                pathNum.remove(vertexId);
                int[] nb=graph[vertexId];
                for (int k = 0; k < nb.length; k=k+2) {
                    int nbv=nb[k],nbe=nb[k+1];
                    if (vertexType[nbv]==targetV && edgeType[nbe]==targetE){
                        if (!queue.contains(nbv))  queue.add(nbv);
                        pathNum.put(nbv,pathNum.getOrDefault(nbv,0)+paths);
                    }
                }
            }
        }

        for (int v:pathNum.keySet()){
            if (vertexType[v]==vertexType[vid]) vnbPath.put(v,pathNum.get(v));
        }
        return vnbPath;
    }

}
