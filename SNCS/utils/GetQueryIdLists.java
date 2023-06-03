package utils;

import online.MetaPath;

import java.util.*;

public class GetQueryIdLists {

    public static List<Integer> getQueryIdLists(int[][] graph, int[] vertexType, int[] edgeType , MetaPath queryMetaPath) {
        List<Integer> queryIdList=new ArrayList<>();
        Map<Integer,Integer> map=new TreeMap<Integer, Integer>(
                new Comparator<Integer>(){
                    public int compare(Integer obj1,Integer obj2){
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

}
