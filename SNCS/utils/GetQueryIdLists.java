package utils;

import online.MetaPath;

import java.util.*;

public class GetQueryIdLists {

    public static List<Integer> getQueryIdLists(int[][] graph, int[] vertexType, int[] edgeType , MetaPath queryMetaPath) {
        List<Integer> queryIdList=new ArrayList<>();
        Map<Integer,Integer> map=new TreeMap<Integer, Integer>(
                new Comparator<Integer>(){
                    public int compare(Integer obj1,Integer obj2){
                        //按照降序排列的
                        return obj2.compareTo(obj1);
                    }
                });

        int mid=queryMetaPath.pathLen/2;
        Map<Integer, Integer> paths=new HashMap<>();
        for (int i = 0; i < graph.length; i++) {
            Map<Integer, Integer> nbrNumMap=new HashMap<>();
            if (vertexType[i]==queryMetaPath.vertex[mid]){
                Queue<Integer> queue=new LinkedList<>();
                queue.add(i);
                nbrNumMap.put(i,1);
                for (int j = mid; j < queryMetaPath.pathLen ; j++) {
                    int targetV=queryMetaPath.vertex[j+1],targetE=queryMetaPath.edge[j];
                    int layerLen=queue.size();

                    for (int k = 0; k < layerLen; k++) {
                        int vertexId=queue.poll();
                        int pathNum = nbrNumMap.get(vertexId);
                        nbrNumMap.remove(vertexId);
                        int[] nb=graph[vertexId];
                        for (int l = 0; l < nb.length; l=l+2) {
                            int nbv=nb[l],nbe=nb[l+1];
                            if (vertexType[nbv]==targetV && edgeType[nbe]==targetE){
                                if (!queue.contains(nbv)) queue.add(nbv);
                                nbrNumMap.put(nbv,nbrNumMap.getOrDefault(nbv,0)+pathNum);
                            }
                        }
                    }
                }
            }
            for (int vertex: nbrNumMap.keySet()){
                paths.put(vertex,nbrNumMap.get(vertex)+paths.getOrDefault(vertex,0));
            }
        }

        Iterator<Integer> iterator = paths.keySet().iterator();
        for (int vid: paths.keySet()) map.put(vid,paths.get(vid));
        while (iterator.hasNext() && queryIdList.size()<=100){
            queryIdList.add(iterator.next());
        }
        return queryIdList;
    }

    //  编写了一个数据集用来测试上述代码
//    public static void main(String[] args) {
//        int[][] graph=new int[8][];
//        graph[0]=new int[]{2,0,3,1};
//        graph[1]=new int[]{3,2,4,3,5,4};
//        graph[2]=new int[]{0,0,6,5};
//        graph[3]=new int[]{0,1,1,2,6,6,7,9};
//        graph[4]=new int[]{1,3,6,7};
//        graph[5]=new int[]{1,4,6,8,7,10};
//        graph[6]=new int[]{2,5,3,6,4,7,5,8};
//        graph[7]=new int[]{3,9,5,10};
//
//        int[] vertexType=new int[]{1,1,2,2,2,2,3,3};
//        int[] edgeType=new int[]{1,1,1,1,1,1,1,1,1,1,1};
//
//        MetaPath metaPath=new MetaPath(new int[]{1,2,3,2,1},new int[]{1,1,1,1});
//    }
}
