/*
This is a sample of the data format, the format of the meta-path
 */
import utils.MetaPath;
import utils.MetaStructPath;

public class Sample {
    public int[][] graph=new int[12][];
    public int[] vertexType=new int[12];
    public int[] edgeType=new int[20];
    public double[][] topicVector=new double[5][];

    public MetaPath qeuryMetaPath=null;
    public MetaStructPath queryMetaStructPath=null;

    public Sample() {
        setGraph();
    }

    public void setGraph() {
        graph[0]=new int[]{5,0,6,1,7,2,8,3,9,4};
        graph[1]=new int[]{5,5,6,6,8,7};
        graph[2]=new int[]{6,8,9,9};
        graph[3]=new int[]{7,10,8,11,9,12};
        graph[4]=new int[]{5,13,6,14,9,15};

        graph[5]=new int[]{0,0,1,5,4,13,10,16,11,17};
        graph[6]=new int[]{0,1,1,6,2,8,4,14,10,18,11,19};
        graph[7]=new int[]{0,2,3,10};
        graph[8]=new int[]{0,3,1,7,3,11};
        graph[9]=new int[]{0,4,2,9,3,12,4,15};

        graph[10]=new int[]{5,16,6,18};
        graph[11]=new int[]{5,17,6,19};

        vertexType=new int[]{1,1,1,1,1,2,2,2,3,3,4,4};
        edgeType=new int[]{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,2,2,2};

        int[] vertexType=new int[]{1,3,1};
        int[] edgeType=new int[]{1,1};
        qeuryMetaPath=new MetaPath(vertexType,edgeType);

        int[][] vertexTypeS=new int[][]{{0},{1},{2,3},{1},{0}};
        int[][] edgeTypeS=new int[][]{{1},{2,3},{2,3},{1}};
        queryMetaStructPath=new MetaStructPath(vertexTypeS,edgeTypeS);

        topicVector[0]= new double[]{1,1};
        topicVector[1]=new double[]{0.1,1.0};
        topicVector[2]=new double[]{0.9,0.5};
        topicVector[3]=new double[]{0.8,0.6};
        topicVector[4]=new double[]{0.95,0.7};
    }
}