

import utils.MetaPath;
import utils.MetaStructPath;

import java.util.*;

/**
 * @author LYQ
 * @Description The aim is to merge the meta-structures in the graph into a virtual node and add flow-constrained vertices and flow-constrained edges,
 * transform the meta-structures into meta-paths, construct a new graph with new node and edge IDs,
 * and record the mapping relationships between the new and old graphs using Map.
 */

public class GraphRefactor {

    //the old graph
    public int[][] graph=null;
    public int[] vertexType=null;
    public int[] edgeType=null;
    MetaStructPath queryMetaStructPath=null;

    //the new graph
    public int[][] newGraph=null;
    public int[] newVertexType=null;
    public int[] newEdgeType=null;
    public MetaPath changedMetaPath=null;

    //Correspondence between old and new vertices
    public Map<Integer,Integer> newOldVertexIdMap=new HashMap<>();
    public Map<Integer,Integer> oldNewVertexIdMap=new HashMap<>();
    public Map<Integer,Integer> newOldEdgeIdMap=new HashMap<>();
    public Map<Integer,Integer> oldNewEdgeIdMap=new HashMap<>();

    //Metastructural neighbours of a vertex
    public Map<Integer, ArrayList<String>> structNbrMap=new HashMap<>();
    public Map<Integer,Map<Integer,ArrayList<String>>> vidEidMap=new HashMap<>();


    public Map<Integer,Set<Integer>> vertexGrainMap=new HashMap<>();
    // The vertexGrainMap is the Id of the vertex connected at the time of bifurcation
    public Map<Integer,Set<Integer>> branchTypesIdMap=new HashMap<>();
    public Map<Integer,Integer> virtualVertexType=new HashMap<>();
    public Map<Integer,Integer> virtualEdgeType=new HashMap<>();

    public GraphRefactor(int[][] graph, int[] vertexType, int[] edgeType, MetaStructPath queryMetaStructPath){
        this.graph=graph;
        this.vertexType=vertexType;
        this.edgeType=edgeType;
        this.queryMetaStructPath=queryMetaStructPath;

        convertQueryPath(queryMetaStructPath);
        graphRefactor();
        System.out.println("grapgRefactor map is finished");
        creatNewGraph();
    }

    public Set<Integer> getSet(int queryVertexType){
        Set<Integer> keepSet=new HashSet<>();
        for (int i = 0; i < vertexType.length; i++) {
            if (vertexType[i]==queryVertexType){
                keepSet.add(i);
            }
        }
        return keepSet;
    }

    // iterate through, saving all the vertices in the original graph that match this structure, saving the neighbours of the vertices connected by the bifurcation
    //The final result is structNbrMap, which records the vertexId : nbrVId_edgeType If there are multiple instances, it means that it is repeated several times inside the list
    // Once we encounter a small bifurcation, we consider merging the bifurcation and adding a new node



    public void graphRefactor(){
    /*
    The function is to get the new neighbours of the vertices that match the bifurcation and the type of the corresponding edges, which are present in structNbrMap
     */
        for (int i = 0; i < queryMetaStructPath.structPathLen; i++) {
            if (queryMetaStructPath.structVLayerLen[i]<queryMetaStructPath.structVLayerLen[i+1]){ //判断是否会出现分岔
                int edgeType1=-1;
                int tempVertexType=queryMetaStructPath.vertex[i][0];
                Set<Integer> temSet=getSet(tempVertexType);

                int[][] queryStructV=new int[2][];
                queryStructV[0]=queryMetaStructPath.vertex[i+1];
                queryStructV[1]=queryMetaStructPath.vertex[i+2];

                int[][] queryStructE=new int[2][];
                queryStructE[0]=queryMetaStructPath.edge[i];
                queryStructE[1]=queryMetaStructPath.edge[i+1];

                for (int vertexId: temSet){
                    ArrayList<Integer> temVertexNbr=searchStructNbSet(vertexId,queryStructV,queryStructE);
                    if (temVertexNbr.size()==0) continue;  //Skip if there is no structNbr
                    while (temVertexNbr.contains(vertexId)) {
                        temVertexNbr.remove(vertexId);
                       // Delete itself, the neighbour must not have it's own, this is a very important line of code because our edge will come back on it's own

                    }
                    ArrayList<String> temVertexTypeNbr=new ArrayList<>();
                    for (int vertexIdNbr:temVertexNbr) {
                        temVertexTypeNbr.add(vertexIdNbr+"_"+edgeType1);
                    }
                    structNbrMap.put(vertexId,temVertexTypeNbr);
                }
                i++;
                edgeType1--;
            }
        }
        System.out.println("The number of structNbr vertices is："+structNbrMap.size());
    }

    //Build a new graph from the original graph
    public void creatNewGraph(){
        Set<Integer> targetVertexTypes=new HashSet<>();
        //1. The first thing is to save the bifurcation and add a new type of vertex
        int vertexType1=-10;
        for (int i = 0; i < queryMetaStructPath.vertex.length; i++) {
            if (queryMetaStructPath.vertex[i].length==1) {
                targetVertexTypes.add(queryMetaStructPath.vertex[i][0]);
            }else{
                targetVertexTypes.add(vertexType1);
                vertexType1--;
            }
        }

        //2. For vertex Id's the old and new conversion is done
        // Added the vertices in the original structure that were not of the bifurcated type and gave them new Id's again
        int vertexIndex=0;
        for (int i = 0; i < graph.length; i++) {
            if (targetVertexTypes.contains(vertexType[i]) && !oldNewVertexIdMap.containsKey(i)){
                newOldVertexIdMap.put(vertexIndex,i);
                oldNewVertexIdMap.put(i,vertexIndex);
                vertexIndex++;
            }
        }
        //vertexIndex is the number of vertices added to the original eligible one and the new index coordinate of the next one
        // for edgeId to convert old to new
        int edgeIndex=0;
        // The first thing that is updated is the pre-existing edges, establishing a new relationship between the vertex Id and the edge Id
        //The final result is the VidEidMap, which is specified as follows.
        for (int oldVertexId:oldNewVertexIdMap.keySet()){
            int[] nb=graph[oldVertexId];
            //singleNbr is a Map, under the determined oldVertexId, to the edges of its neighbouring vertices and the type of the edge Vertex newId: edge newId_type
            Map<Integer,ArrayList<String>> singleNbr=new HashMap<>();
            for (int k = 0; k < nb.length; k+=2) {
                int nbVertexId = nb[k], nbEdgeId = nb[k + 1];
                if (targetVertexTypes.contains(vertexType[nbVertexId])) {   // If the neighbour is not a removed vertex, then it can be stored in
                    if (!oldNewEdgeIdMap.containsKey(nbEdgeId)) {
                        newOldEdgeIdMap.put(edgeIndex, nbEdgeId);
                        oldNewEdgeIdMap.put(nbEdgeId, edgeIndex);
                        edgeIndex++;
                    }

                    int nbnewVertexId=oldNewVertexIdMap.get(nbVertexId);
                    int nbnewEdgeId = oldNewEdgeIdMap.get(nbEdgeId);
                    if (!singleNbr.containsKey(nbnewVertexId)){
                        singleNbr.put(nbnewVertexId,new ArrayList<String>(Collections.singletonList("" + nbnewEdgeId + "_" + edgeType[nbEdgeId])));
                    }else {
                        singleNbr.get(nbnewVertexId).add(""+nbnewEdgeId+"_"+edgeType[nbEdgeId]);
                    }
                }
            }
            int newVertexId=oldNewVertexIdMap.get(oldVertexId);
            singleNbr.remove(newVertexId);
            vidEidMap.put(newVertexId,singleNbr);
            // The VidEidMap stores a graph: new vertex ID: (new vertex ID of a neighbour: new edge ID_edge type)
        }


        // All the neighbour relations in the old graph exist in VidEidMap
        // Add virtual combination vertices and flow constraint vertices

        int[] vertexEdgeNum=addVirtualVertex(vertexIndex,edgeIndex);

        System.out.println("Adding virtual vertices is end"+ Arrays.toString(vertexEdgeNum));


        // vidEidMap is a dictionary that stores a directed graph, now convert this dictionary to an array
        int vertexNum=vertexEdgeNum[0];
        int edgeNum=vertexEdgeNum[1];
        newGraph=new int[vertexNum][];
        newVertexType=new int[vertexNum];
        newEdgeType=new int[edgeNum];
        //First, we need to organize the diagram, because the connection between the vertices is not completely symmetrical yet
        for (int vId:vidEidMap.keySet()){
            Map<Integer, ArrayList<String>> vIdNbrMap=vidEidMap.get(vId);
            for (int nbVid:vIdNbrMap.keySet()){
                if (!vidEidMap.get(nbVid).containsKey(vId)) {
                    vidEidMap.get(nbVid).put(vId,vIdNbrMap.get(nbVid));
                }
            }
        }

        // converting the previous Map format of neighbourhood relationships between vertices into an array representation
        for (int i = 0; i < vertexNum; i++) {
            Map<Integer,ArrayList<String>> nbMap=vidEidMap.get(i);
            if (nbMap!=null){
                int nbNum=0;
                for (ArrayList<String> arr: nbMap.values()) {
                    nbNum+=arr.size();
                }
                int[] nb=new int[nbNum*2];
                int nbIndex=0;
                for (int nbVId:nbMap.keySet()){
                    ArrayList<String> nbArr=nbMap.get(nbVId);
                    for (String edgeId_type:nbArr){
                        String[] edgeId_types=edgeId_type.split("_");
                        nb[nbIndex++]=nbVId;
                        nb[nbIndex++]=Integer.parseInt(edgeId_types[0]);
                        newEdgeType[Integer.parseInt(edgeId_types[0])]=Integer.parseInt(edgeId_types[1]);
                    }
                }
                newGraph[i]=nb;
            }else {
                newGraph[i]= new int[]{};
            }
        }

        for (int newVid:newOldVertexIdMap.keySet()){
            newVertexType[newVid]=vertexType[newOldVertexIdMap.get(newVid)];
        }
        for (int newVid:virtualVertexType.keySet()){
            newVertexType[newVid]=virtualVertexType.get(newVid);
        }
    }

    //add virtual vertices and flow constraint vertices
    //first determine the combination of bifurcations
    public List<Set<Integer>> getTypeVertexCom(Map<Integer,Set<Integer>> subBranchTypesIdMap){
        int virtualVertexNums=1;
        int num=0;
        List<Set<Integer>> comList=new ArrayList<>();
        int comListLen=0;
        for (int type:subBranchTypesIdMap.keySet()){
            virtualVertexNums*=subBranchTypesIdMap.get(type).size();
            if (num==0){
                for (int id:subBranchTypesIdMap.get(type)) {
                    comList.add(new HashSet<>(Collections.singletonList(id)));
                }
                num++;
                comListLen=comList.size();
                continue;
            }

            for (int i = 0; i < comListLen; i++) {
                Set<Integer> set0=comList.get(0);
                for (int id:subBranchTypesIdMap.get(type)){
                    Set<Integer> temSet=new HashSet<>(set0);
                    temSet.add(id);
                    comList.add(temSet);
                }
                comList.remove(0);
            }
            comListLen=comList.size();
        }
        return comList;
    }

    //add new virtual vertices
    //This function is used to add stream constrained vertices and combinatorial vertices and their connectivity in the graph, updated in the vidEidMap
    //This function returns the number of vertices and edges after the virtual vertices and edges have been added
    public int[] addVirtualVertex(int vertexBeginIndex,int edgeBeginIndex) {
        Map<Integer,Integer> mulVirtuleFildVertex=new HashMap<>();
        Map<Integer, Integer> oldVertexFildVMap = new HashMap<>();
        int vertexIndex = vertexBeginIndex;
        int edgeIndex = edgeBeginIndex;
        int virtualCombVertexT = -10, virtualfildVertexT = -111, virtualFildEdgeT = -121, virtualComEdgeT = -131;
        Map<Integer, Set<Integer>> virtualVIdComMap = new HashMap<>();
        List<Set<Integer>> comList = getTypeVertexCom(branchTypesIdMap);
        for (Set<Integer> temSet : comList) {
            virtualVIdComMap.put(vertexIndex, temSet);
            virtualVertexType.put(vertexIndex, virtualCombVertexT);
            vertexIndex++;
        }
        for (int oldVertexId : vertexGrainMap.keySet()) {
            Map<Integer, Set<Integer>> oldVertexIdGrain = new HashMap<>();
            int newVertexId = oldNewVertexIdMap.get(oldVertexId);
            for (int branchId : vertexGrainMap.get(oldVertexId)) {
                if (!oldVertexIdGrain.containsKey(vertexType[branchId])) {
                    oldVertexIdGrain.put(vertexType[branchId], new HashSet<>(Collections.singletonList(branchId)));
                } else {
                    oldVertexIdGrain.get(vertexType[branchId]).add(branchId);
                }
            }
            // Calculate the number of instances that can be concatenated
            int minInstance = Integer.MAX_VALUE;
            for (int type : oldVertexIdGrain.keySet()) {
                minInstance = Math.min(minInstance, oldVertexIdGrain.get(type).size());
            }
            // Add edges to flow constraint vertices based on the number of instances
            if (vidEidMap.containsKey(newVertexId)) {
                virtualVertexType.put(vertexIndex, virtualfildVertexT);
                oldVertexFildVMap.put(oldVertexId, vertexIndex);
                //Adding a new virtual vertex relationship
                int virtualFildVId = oldVertexFildVMap.get(oldVertexId);

                Map<Integer, ArrayList<String>> singleNbrMap = vidEidMap.get(newVertexId);
                ArrayList<String> fildEdgeList = new ArrayList<String>();
                Map<Integer, ArrayList<String>> fildVSinglNbr = new HashMap<>(); // Neighbours of the newly added stream constraint vertices

                for (int i = 0; i < minInstance; i++) {
                    fildEdgeList.add("" + edgeIndex + "_" + virtualFildEdgeT);
                    virtualEdgeType.put(edgeIndex, virtualFildEdgeT);
                    edgeIndex++;
                }

                if (minInstance>1) mulVirtuleFildVertex.put(newVertexId,minInstance);

                fildVSinglNbr.put(newVertexId, fildEdgeList);
                singleNbrMap.put(vertexIndex, fildEdgeList);
                vidEidMap.put(virtualFildVId, fildVSinglNbr);
                vertexIndex++;
            } else {
                continue;
            }

            // Once the flow constraint edges have been added, we relate the new flow constraint vertices to the combined vertices
            List<Set<Integer>> vertexComb = getTypeVertexCom(oldVertexIdGrain);
            int virtualFildVId = oldVertexFildVMap.get(oldVertexId);
            Map<Integer, ArrayList<String>> virtualFildVNbr = vidEidMap.get(virtualFildVId);
            for (Set<Integer> temSet : vertexComb) {
                for (int virtualComId : virtualVIdComMap.keySet()) {
                    if (temSet.equals(virtualVIdComMap.get(virtualComId))) {
                        virtualFildVNbr.put(virtualComId, new ArrayList<>(Collections.singletonList("" + edgeIndex + "_" + virtualComEdgeT)));
                        if (vidEidMap.containsKey(virtualComId)) {
                            vidEidMap.get(virtualComId).put(virtualFildVId, new ArrayList<>(Collections.singletonList("" + edgeIndex + "_" + virtualComEdgeT)));
                        } else {
                            Map<Integer, ArrayList<String>> singNbr = new HashMap<>();
                            singNbr.put(virtualFildVId, new ArrayList<String>(Collections.singletonList("" + edgeIndex + "_" + virtualComEdgeT)));
                            vidEidMap.put(virtualComId, singNbr);
                        }
                        edgeIndex++;
                    }
                }
            }
        }
        return new int[]{vertexIndex,edgeIndex};
    }

    // Find the next vertex by bifurcation, the result is returned as List<Integer> result,
    // the repetition represents the number of occurrences, the repetition n times means the number of instances is n
    public ArrayList<Integer> searchStructNbSet(int vertexId,int[][] queryStructV,int[][] queryStructE){
        int[] targetVType1 = queryStructV[0];
        int[] targetEtype1 = queryStructE[0];
        int targetVType2 = queryStructV[1][0];
        int[] targetEType2 = queryStructE[1];



        Map<Integer,Set<Integer>> mulV=new HashMap<>();

        Map<Integer, ArrayList<Integer>> finalVertexLabel = new HashMap<>();
        // This is a map, the key is the Id of the vertex found by this structure, the value is the label of the point found by which path

        int[] nb = graph[vertexId];
        for (int i=0;i<nb.length;i+=2){
            int nbVertexId=nb[i],nbEdgeId=nb[i+1];
            for (int j=0;j<targetVType1.length;j++){
                if (vertexType[nbVertexId]==targetVType1[j] && edgeType[nbEdgeId]==targetEtype1[j]){
                    if (mulV.get(vertexType[nbVertexId])==null){
                        mulV.put(vertexType[nbVertexId],new HashSet<>(Collections.singletonList(nbVertexId)));
                    }else {
                        mulV.get(vertexType[nbVertexId]).add(nbVertexId);
                    }
                }
            }
        }

        int typeIndex=-1;
        if (mulV.size()<targetVType1.length)  return new ArrayList<>();
        for (int vType1:targetVType1){
            typeIndex++;
            for (int nbVertexId : mulV.get(vType1)){
                int[] nb2 = graph[nbVertexId];
                for (int j = 0; j < nb2.length; j += 2) {
                    int nextNbVertexId = nb2[j], nextNbEdgeId = nb2[j + 1];
                    if (vertexType[nextNbVertexId] == targetVType2 && edgeType[nextNbEdgeId] == targetEType2[typeIndex]) {
                        if (finalVertexLabel.get(nextNbVertexId) == null) {
                            finalVertexLabel.put(nextNbVertexId, new ArrayList<Integer>(Collections.singletonList(vType1)));
                        } else {
                            finalVertexLabel.get(nextNbVertexId).add(vType1);
                        }

                        if (!branchTypesIdMap.containsKey(vertexType[nbVertexId])){
                            branchTypesIdMap.put(vertexType[nbVertexId],new HashSet<>(Collections.singletonList(nbVertexId)));
                        }else {
                            branchTypesIdMap.get(vertexType[nbVertexId]).add(nbVertexId);
                        }
                        if (!vertexGrainMap.containsKey(nextNbVertexId)){
                            vertexGrainMap.put(nextNbVertexId,new HashSet<>(Collections.singletonList(nbVertexId)));
                        }else {
                            vertexGrainMap.get(nextNbVertexId).add(nbVertexId);
                        }
                    }
                }
            }
        }

        ArrayList<Integer> result=new ArrayList<>();
        for (int vertexKey:finalVertexLabel.keySet()){
            ArrayList<Integer> vertexLabel=finalVertexLabel.get(vertexKey);
            //judge the labels inside the ArrayList to meet the distribution from 0 to targetVType1.length-1, find the smallest value
            int frequency=frequencyOfListElements(vertexLabel, targetVType1.length);
            for (int i = 0; i < frequency; i++) {
                result.add(vertexKey);
            }
        }
        while (result.contains(vertexId)){
            result.remove(result.indexOf(vertexId));
            //remove of a list removes the value at the specified index, not a specific value
        }
        return result;  //result is a neighbour whose vertexId is connected by a meta-structure bifurcation
    }

    // count the number of occurrences of each element in the List, we return when each element is present and return the smallest number of occurrences,
    // otherwise 0, which is the number of instances at the bifurcation of our meta-structure
    public int frequencyOfListElements(ArrayList<Integer> arr,int targetVType1Len){
        if (arr==null || arr.size()<targetVType1Len) {
            return 0;
        }
        Map<Integer,Integer> map=new HashMap<>();
        for(int temp:arr){
            map.put(temp,map.getOrDefault(temp,0)+1);
        }
        int min=Integer.MAX_VALUE;
        if (map.size()!=targetVType1Len) {
            return 0;
        }
        for (int value:map.values()){
            min=Math.min(min,value);
        }
        return min;
    }



    public void convertQueryPath(MetaStructPath queryStructPath) {
        List<Integer> temvertexList = new ArrayList<>();
        List<Integer> temedgeList = new ArrayList<>();
        int vertexType = -10;
        int virtualfildVertexT = -111, virtualFildEdgeT = -121, virtualComEdgeT = -131;
        temvertexList.add(queryStructPath.vertex[0][0]);
        for (int i = 1; i <= queryStructPath.structPathLen; i++) {
            if (queryStructPath.vertex[i].length == 1) {
                temvertexList.add(queryStructPath.vertex[i][0]);
                if (queryStructPath.edge[i-1].length==1){
                    temedgeList.add(queryStructPath.edge[i-1][0]);
                }
            } else {
                temvertexList.add(virtualfildVertexT);
                temvertexList.add(vertexType);
                temvertexList.add(virtualfildVertexT);
                temedgeList.add(virtualFildEdgeT);
                temedgeList.add(virtualComEdgeT);
                temedgeList.add(virtualComEdgeT);
                temedgeList.add(virtualFildEdgeT);
                // update the type
                virtualfildVertexT--;
                vertexType--;
                virtualFildEdgeT--;
                virtualComEdgeT--;
            }
        }
        int[] vertex = new int[temvertexList.size()];
        int[] edge = new int[temedgeList.size()];
        for (int i = 0; i < temedgeList.size(); i++) {
            vertex[i] = temvertexList.get(i);
            edge[i] = temedgeList.get(i);
        }
        vertex[temvertexList.size() - 1] = temvertexList.get(temvertexList.size() - 1);
        changedMetaPath = new MetaPath(vertex, edge);
    }

    // return the four quantities needed, in a subsequent study, mainly the correspondence between the new graph,
    // the new node type, the new edge type, the new node ID and the old node ID under this meta-structure
    public int[][] getNewGraph(){
        return newGraph;
    }

    public int[] getNewVertexType(){
        return newVertexType;
    }

    public int[] getNewEdgeType(){
        return newEdgeType;
    }

    public Map<Integer,Integer> getNewOldVertexIdMap(){
        return newOldVertexIdMap;
    }

    public Map<Integer,Integer> getOldNewVertexIdMap(){
        return oldNewVertexIdMap;
    }

    public MetaPath getChangedMetaPath() {
        return changedMetaPath;
    }
}