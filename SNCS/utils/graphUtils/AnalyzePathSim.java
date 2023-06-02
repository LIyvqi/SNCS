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

    //pGraph 用来计算图中任意两个顶点间的路径实例数
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

    //用来寻找vid到其它id的p-nbr路径实例
    //相似性是允许边重复的，无所谓，对我们元结构来说也适用这个算法
    private Map<Integer, Integer> searchVnbPath(int vid,MetaPath queryMPath) {
        Map<Integer, Integer> pathNum=new HashMap<>();
        Queue<Integer> queue=new LinkedList<>();
        queue.add(vid);
        pathNum.put(vid,1);
        Map<Integer, Integer> vnbPath=new HashMap<>();

        for (int i = 0; i < queryMPath.pathLen; i++) {
            int targetV=queryMPath.vertex[i+1],targetE=queryMPath.edge[i];
            int layerLen=queue.size();
            //通过广度优先搜索来批量的寻找从vid
            //其实这里面是一个广度优先加动态规划
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

//      用来测试计算顶点相似性的代码
//    public static void main(String[] args) {
//        Test2 test2=new Test2();
//        AnalyzePathSim analyzePathSim=new AnalyzePathSim(test2.graph, test2.vertexType, test2.edgeType);
//        Set<Integer> curSet=new HashSet<>();
//        curSet.add(0);
//        curSet.add(1);
//        curSet.add(2);
//        curSet.add(3);
//        System.out.println(analyzePathSim.batchBuildForPathSim(curSet, test2.qeuryMetaPath));
//
//        PathSimBaselineFloat pathSimBaselineFloat=new PathSimBaselineFloat(test2.graph, test2.vertexType, test2.edgeType, test2.qeuryMetaPath, "D:\\JavaProject\\TBCSH_TEST\\src\\resultData\\queryGraph-4-1.txt");
//        System.out.println(pathSimBaselineFloat.query(0, test2.qeuryMetaPath, 1,0.2));
//
//    }
}