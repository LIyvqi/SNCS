package utils;

import java.io.*;

public class LoadDataDBLP {
    public String graphFile=null;
    public String vertexFile=null;
    public String edgeFile=null;
    public String topicFile=null;
    public int vertexNum=0;
    public int edgeNum=0;
    public int userNum=0;
    public int[][] graph;
    public int[] edgeType;
    public int[] vertexType;
    public double[][] userTopic;

    public int[][] getGraph(){
        return graph;
    }

    public int[] getVertexType(){
        return vertexType;
    }

    public int[] getEdgeType(){
        return edgeType;
    }

    public double[][] getUserTopic() {
        return userTopic;
    }


    public LoadDataDBLP(String graphFile, String vertexFile, String edgeFile, String topicFile) throws IOException {
        this.graphFile=graphFile;
        this.edgeFile=edgeFile;
        this.topicFile=topicFile;
        this.vertexFile=vertexFile;
        try{
            File test=new File(graphFile);
            long fileLength=test.length();
            LineNumberReader rf=new LineNumberReader(new FileReader(test));
            if (rf!=null){
                rf.skip(fileLength);
                vertexNum=rf.getLineNumber();
            }
            rf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        readGraph();
        readEdgeType();
        readVertexType();
        readUserTopic();
    }

    //load the graph
    public int[][] readGraph() throws IOException {
        int[][] graph=new int[vertexNum][];
        try{
            BufferedReader stdin=new BufferedReader(new FileReader(graphFile));
            String line=null;
            while ((line = stdin.readLine()) != null){
                String[] s=line.split(" ");
                int vertexId=Integer.parseInt(s[0]);
                int[] nb=new int[s.length-1];
                for (int i = 1; i < s.length; i++) {
                    nb[i-1]=Integer.parseInt(s[i]);
                }
                graph[vertexId]=nb;
                edgeNum+=nb.length/2;
            }
            stdin.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("The scale of graph is：" + " |V|=" + vertexNum + " |E|=" + edgeNum / 2); //the edge is bi-directional
        this.graph=graph;
        return graph;
    }

    //读取每个顶点的类别
    public int[] readVertexType(){
        int[] vertexType=new int[vertexNum];
        try {
            BufferedReader stdin = new BufferedReader(new FileReader(vertexFile));
            String line=null;
            while ((line=stdin.readLine())!=null){
                String[] s=line.split(" ");
                int id=Integer.parseInt(s[0]);
                int type=Integer.parseInt(s[1]);
                if (type==0) userNum++;   //the vertex type of user in dblp is 0
                vertexType[id]=type;
            }
            stdin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("The number of vertices is："+vertexType.length);
        this.vertexType=vertexType;
        return vertexType;
    }

    //读取每条边的类别
    public int[] readEdgeType(){
        int[] edgeType=new int[edgeNum];
        try{
            BufferedReader stdin = new BufferedReader(new FileReader(edgeFile));
            String line=null;
            while ((line=stdin.readLine())!=null){
                String[] s=line.split(" ");
                int eid=Integer.parseInt(s[0]);
                int etype=Integer.parseInt(s[1]);
                edgeType[eid]=etype;
            }
            stdin.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("The number of edge is："+edgeType.length);
        this.edgeType=edgeType;
        return edgeType;
    }

    //读取主题的信息
    public double[][] readUserTopic(){
        System.out.println("userNum is "+userNum);
        double[][] userTopic=new double[userNum][];
        try{
            BufferedReader stdin=new BufferedReader(new FileReader(topicFile));
            String line=null;
            while ((line=stdin.readLine())!=null){
                String[] s=line.split(" ");
                int uid=Integer.parseInt(s[0]);
                double[] topic=new double[10];  // The dimensionality of the topics in DBLP dataset is 10
                for (int i = 1; i <11 ; i++) {
                    topic[i-1]=Double.parseDouble(s[i]);
                }
                userTopic[uid]=topic;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("The number of users is :"+userTopic.length);
        this.userTopic=userTopic;
        return userTopic;
    }

}
