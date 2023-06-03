
import utils.LoadDataDBLP;
import utils.MetaPath;
import utils.MetaStructPath;
import utils.graphUtils.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class GraphevaluateDBLP {

    public static void main(String[] args) throws IOException {

        //Load the dataset, the first thing to load is the original graph
        System.out.println("---------DBLP dataset Tcsh----------");
        String topicPath = "./data/dblp_data/dblp_UserId_Topic.txt";
        String edgePath = "./data/dblp_data/dplp_edgeType.txt";
        String vertexPath = "./data/dblp_data/dplp_vertexType.txt";
        String graphPath = "./data/dblp_data/dblp_graph.txt";

        LoadDataDBLP loadDataDBLP = new LoadDataDBLP(graphPath,vertexPath,edgePath,topicPath);
        MetaStructPath queryStructPath = new MetaStructPath(new int[][]{{0}, {1}, {2, 3}, {1}, {0}}, new int[][]{{1}, {2, 3}, {2, 3}, {1}});
        GraphRefactor graphRefactor = new GraphRefactor(loadDataDBLP.getGraph(), loadDataDBLP.getVertexType(), loadDataDBLP.getEdgeType(), queryStructPath);



        int[][] graph= graphRefactor.getNewGraph();
        int[] vertexType=graphRefactor.getNewVertexType();
        int[] edgeType=graphRefactor.getNewEdgeType();
        MetaPath queryMetaPath=graphRefactor.getChangedMetaPath();

        System.out.println("Graph constrated finished");

        File file_community = new File("./communityAnswer/DBLP/");
        File[] Filearray = file_community.listFiles();
        assert Filearray != null;
        for (File value : Filearray) {
            String fileName=value.getName().split("\\.")[0];
            String[] parments=fileName.split("-");
            int queryId=Integer.parseInt(parments[1]);
            int queryK=Integer.parseInt(parments[2]);
            if (queryK<=2 || queryK>=5) continue;
            double theta=0;

            //Read communities found by community search
            Set<Integer> curSet=new HashSet<>();
            In communityFile=new In("./communityAnswer/DBLP/community-"+queryId+"-"+queryK+".txt");
            String searchTime ="";
            String line = communityFile.readLine();
            line=line.replaceAll("\r\n", "");
            String[] lines = line.split(" ");
            searchTime=lines[0];
            for (int i = 4; i < lines.length; i++) {
                curSet.add(Integer.parseInt(lines[i]));
            }
            communityFile.close();
            System.out.println(value);
            AvgDegree avg = new AvgDegree(graph,vertexType, edgeType,queryMetaPath,curSet);
            //Everything in after is in response to the communities we looked up above and some of the evaluation indicators
            avg.query(queryId,queryK,theta);
            avg.calculateDensity(curSet);

            // filename is the subgraph we obtained by calculation
            String filename="./resultData/DBLP_tcsh/queryGraph-"+queryId+"-"+queryK+"-"+(int) (theta*100)+".txt";
            String delimiter=" ";


            String content="";
            content=content+searchTime+","+queryId+","+queryK+","+(int) (theta*100)+",";
            Graph evaluateGraph = new Graph(filename, delimiter);
            StdOut.printf("number of vertices     = %7d\n", evaluateGraph.V());     content=content+evaluateGraph.V()+",";
            StdOut.printf("number of edges        = %7d\n", evaluateGraph.E());     content=content+evaluateGraph.E()+",";
            StdOut.printf("average degree         = %7.3f\n", averageDegree(evaluateGraph));    content=content+averageDegree(evaluateGraph)+",";
            StdOut.printf("maximum degree         = %7d\n",   maxDegree(evaluateGraph));        content=content+maxDegree(evaluateGraph)+",";
            if (diameter(evaluateGraph)-1.0<=0.0001){
                StdOut.printf("diameter of graph      ",  ""+1.00);          content=content+""+1.00+",";
                StdOut.printf("average path length    ", ""+1.00);         content=content+""+1.00+",";
                StdOut.printf("clustering coefficient  ", ""+1.00);         content=content+""+1.00+",";
            }else {
                double dia=diameter(evaluateGraph);
                double apl=averagePathLength(evaluateGraph);
                double clu=clusteringCoefficient(evaluateGraph);
                StdOut.printf("diameter of graph"     +  dia);          content=content+dia+",";
                StdOut.printf("average path length"   + apl);         content=content+apl+",";
                StdOut.printf("clustering coefficient " + clu);         content=content+clu+",";
            }

            //finally, Save some of our evaluation indicators in a .txt file, save them all in one file
            String path="/home/lyq/TBCSH_TEST/src/communityEvaluation/dblp_merge_out.txt";
            File file = new File(path);
            FileOutputStream fileOutputStream = new FileOutputStream(file,true);
            fileOutputStream.write((content+"\n").getBytes(StandardCharsets.UTF_8));
            fileOutputStream.close();
        }

    }





    public static double averageDegree(Graph G) {
        return (double) 2 * G.E() / G.V();
    }

    public static double averagePathLength(Graph G) {
        int sum = 0;
        for (String v : G.vertices()) {
            PathFinder pf = new PathFinder(G, v);
            for (String w : G.vertices())
                sum += pf.distanceTo(w);
        }
        return (double) sum / (G.V() * (G.V() - 1));
    }

    public static double clusteringCoefficient(Graph G) {
        double total = 0.0;
        for (String v : G.vertices()) {
            int possible = G.degree(v) * (G.degree(v) - 1);
            int actual = 0;
            for (String u : G.adjacentTo(v)) {
                for (String w : G.adjacentTo(v)) {
                    if (G.hasEdge(u, w))
                        actual++;
                }
            }
            if (possible > 0) {
                total += 1.0 * actual / possible;
            }
        }
        return total / G.V();
    }

    public static int maxDegree(Graph G) {
        int max = 0;
        for (String v : G.vertices()) {
            if (G.degree(v) > max)
                max = G.degree(v);
        }
        return max;
    }

    public static int diameter(Graph G){
        int best=-1;
        for (String s : G.vertices()){
            PathFinder finder=new PathFinder(G,s);
            for (String v:G.vertices()){
                if (finder.hasPathTo(v) && finder.distanceTo(v) > best){
                    best=finder.distanceTo(v);
                }
            }
        }
        return best;
    }


    public static class AvgDegree {
        private  int[][] graph = null;//data graph, including vertex IDs, edge IDs, and their link relationships
        private  int[] vertexType = null;//vertex -> type
        private  int[] edgeType = null;//edge -> type
        private Set<Integer> community=null;
        public Map<Integer,Set<Integer>> graphMap=new HashMap<>(); 

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
            // check if the type of the query vertices is the same as our starting path, and if so create a connected subgraph
            graphMap=buildGraph();
        }

        public Set<Integer> query(int queryId,int queryK,double theta){
            this.queryId=queryId;
            this.queryK=queryK;
            this.theta=theta;
            if (queryMPath.vertex[0]!=vertexType[queryId]) return null;
            return null;
        }


        //find the pnb of all query vertices under the given meta-path and save it in the graphMap
        //This is too complicated, we modify this and just find the connection between these vertices searched
        private Map<Integer, Set<Integer>> buildGraph() {
            long count = 0;
            Map<Integer, Set<Integer>> graphMap = new HashMap<Integer, Set<Integer>>();
            for(int curId : community) {
                if(vertexType[curId] == queryMPath.vertex[0]) {
                    List<Set<Integer>> visitList = new ArrayList<Set<Integer>>();
                    for(int i = 0;i <=queryMPath.pathLen;i ++)   visitList.add(new HashSet<Integer>());
                    Set<Integer> pnbSet = new HashSet<Integer>();
                    findPNeighbors(curId, curId, 0, visitList, pnbSet);    
                    count += pnbSet.size();
                    graphMap.put(curId, pnbSet);
                }
            }
            System.out.println("In the evaluation, for the entire graph, pnb is connected to the construction is completed");
            System.out.println("The average degree of the whole graph is: " + count * 1.0 / graphMap.size());  
            return graphMap;
        }

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

        // Calculated by counting the neighbours of the graphs we looked up,
        // counting the vertices, edges and densities of the graphs we looked up
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

            String path="./resultData/DBLP_tcsh/queryGraph-"+queryId+"-"+queryK+"-"+(int) (theta*100)+".txt";

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

}


