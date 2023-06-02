package utils;

import java.util.Arrays;

public class MetaStructPath {
    public int[][] vertex;
    public int[][] edge;
    public int structPathLen;
    public int[] structVLayerLen;
    public int[] structELayerLen;

    public MetaStructPath(int[][] vertex, int[][] edge) {
        //an example of meta structure
        //vertex：[[A],[P],[T,V],[P],[A]]
        //edge:[[1],[2,3],[2,3],[1]]
        //vertexLayerLen:[1,1,2,1,1]
        //edgeLayerLen:[1,2,2,1]
        //structPathLen=4
        this.vertex=vertex;
        this.edge=edge;
        this.structPathLen=edge.length;
        if (vertex.length!=edge.length+1) System.out.println("the meta-struct-path is incorrect");
        structVLayerLen=new int[structPathLen+1];
        structELayerLen=new int[structPathLen];
        for (int i = 0; i < structPathLen; i++) {
            structVLayerLen[i]=vertex[i].length;
            structELayerLen[i]= edge[i].length;
        }
        structVLayerLen[structPathLen]= vertex[structPathLen].length;
    }

    public String printToString(){
        String str="";
        for (int i=0;i<structPathLen;i++){
            str+= Arrays.toString(vertex[i]);
            str+="----";
            str+=Arrays.toString(edge[i]);
            str+="|";
        }
        str+=Arrays.toString(vertex[structPathLen]);
        return str;
    }
}
