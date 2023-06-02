
import utils.LoadDataDBLP;
import utils.MetaPath;
import utils.MetaStructPath;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class TcshDblpMain {
    public static void main(String[] args) throws IOException {
        System.out.println("---------DBLP dataset----------");
        String topicPath = "./data/dblp_data/dblp_UserId_Topic.txt";
        String edgePath = "./data/dblp_data/dplp_edgeType.txt";
        String vertexPath = "./data/dblp_data/dplp_vertexType.txt";
        String graphPath = "./data/dblp_data/dblp_graph.txt";


        LoadDataDBLP loadDataDBLP = new LoadDataDBLP(graphPath,vertexPath,edgePath,topicPath);
        MetaStructPath queryStructPath=new MetaStructPath(new int[][]{{0},{1},{2,3},{1},{0}},new int[][]{{1},{2,3},{2,3},{1}});

        //Convert old graphs into new graphs by graph reconstruction algorithms under specified meta-structures
        long startTime=System.currentTimeMillis();
        GraphRefactor graphRefactorRepeated=new GraphRefactor(loadDataDBLP.getGraph(), loadDataDBLP.getVertexType(),loadDataDBLP.getEdgeType(), queryStructPath);
        System.out.println("load data finsh");
        System.out.println("graph refacted");
        int[][] graph=graphRefactorRepeated.getNewGraph();
        int[] vertexType=graphRefactorRepeated.getNewVertexType();
        int[] edgeType=graphRefactorRepeated.getNewEdgeType();
        double[][] topicVector=loadDataDBLP.getUserTopic();
        MetaPath queryMPath=graphRefactorRepeated.getChangedMetaPath();
        long refactorTime = System.currentTimeMillis();
        System.out.println("The time of graph refactor is "+(refactorTime-startTime));

        //Do community search on new graphs and converted meta path
        BatchECore batchECore = new BatchECore(graph,vertexType,edgeType);
        List<Integer> queryIdList = Arrays.asList(996,2298,1011,1369,2940,1366,972,964,845,81);  //Randomly selected vertices
        List<Integer> queryKList= Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12,13);
        List<Double> thetaList = Arrays.asList(0.4,0.5,0.6,0.7,0.8,0.85,0.9,0.95);

        long time0=System.currentTimeMillis();
        for (int queryId:queryIdList){
            for (double theta : thetaList){
                for (int queryK : queryKList){
                    System.out.println("search for"+queryId+"-"+queryK+"-"+(int) (theta*100));
                    Set<Integer> reSet1= batchECore.query(queryId,queryMPath,queryK,topicVector,theta);
                    long time1=System.currentTimeMillis();
                    File communityFile=new File("./communityAnswer/DBLP/community-"+queryId+"-"+queryK+"-"+(int) (theta*100)+".txt");

                    ////communityFile contains the ids of the communities found, separated by " ",
                    // which are: searchTime, queryId, queryK, theta, the ids of the communities searched, and definitely the ones containing queryId
                    double searchTime=time1-time0;

                    if (reSet1==null) continue;
                    String content="";
                    content=content+searchTime+" "+queryId+" "+queryK+" "+theta+" ";
                    System.out.println(reSet1.size());
                    for (int id:reSet1) {
                        content=content+id+" ";
                    }
                    FileOutputStream fos=null;
                    if (!communityFile.exists()){
                        communityFile.createNewFile();
                        fos=new FileOutputStream(communityFile);
                    }else {
                        fos=new FileOutputStream(communityFile);
                    }
                    OutputStreamWriter osw=new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                    osw.write(content+"\n");
                    osw.close();
                    time0=time1;
                }
            }
        }
    }
}
