# Leveraging Semantic Information for Enhanced Community Search in Heterogeneous Graphs

## Introduction
Our method contains two parts, one for topic extraction and the other for community search, the figure below shows the pipeline of the algorithm.

![pipeline](https://user-images.githubusercontent.com/49839855/143671797-bc55f3e9-da1d-439e-8a29-8281507d96e6.png)
The pipeline of SNCS. 
For the input heterogeneous graph (HG), we extract topics and reconstruct the graph in parallel. We aggregate topic vectors from the neighbors of vertices that \yq{have} the same type as the query vertex $q$. Following the given meta-structure, we reconstruct a new HG from the input HG and return the meta-paths that can represent this structure on the newly-reconstructed graph. Subsequently, we filter the topics to obtain a specific set of vertices, which is further used to search for communities  on the newly-reconstructed HG by the converted meta-paths e.g., conducting community search by the meta-path-based BatchEcoe[1] algorithm).


The TCS folder is the code for graph reconstruction method and community search, implemented by Java(JDK 1.8). We are using publicly available datasets that can be downloaded from the internet. To make it easier for the reader to understand, we have given an example with the dblp dataset. Note that the algorithm for community search by meta-path (BatchEcore)[1] was obtained from the author, so it is not publicly available, I added thematic constraints to the BatchEcore algorithm with modifications.


Topic extraction is a very common method and we have implemented it with the help of OCTIS(https://github.com/MIND-Lab/OCTIS). You can see the repository for details of it and we have also made public the code and sample datasets we have processed.

## Proof of Theorem 4.1

The proof is detailed in the "proof.pdf".

## Quality metrics and Implementation Details

In line with existing works about meta-structures, we focus on meta-structures with diameters at most four. We select meta-structures with more connected vertices as expert suggest, so as to ensure that our query is meaningful. Our dataset contains four vertex types that, coincidentally, constitute a meta-structure. To ensure the validity of our experiments, we randomly selected 20 vertices as the set of query vertices. The topic similarity threshold $\theta$ is set from 0.5 to 0.95, and the $k$ is set from 1 to 12. In the results reported in the following, each data point is the average result for these queries. From the parameter analysis section in paper, We have analyzed that a larger $k$ and $\theta$ mean denser topology. In order to identify intermediate-sized dense communities surrounding query vertices in the DBLP and ASN datasets, we determined appropriate parameter configurations to be $k=9$ and $\theta=0.60$, and $0.95$, respectively (the settings of **Table 3 in paper**).


For the parameters settings of **Table 4 in paper**, we utilized the same vertices set as query vertices and set $k$ equal to 9 for $k$-core. For $k\mathcal{K}\mathcal{P}$-core, we employed the exact keywords set that corresponded with the query vertices, while for SNCS, we used the topic vectors that had been extracted from these vertices. Topic constraints for $k\mathcal{K}\mathcal{P}$-core and SNCS were established at 0.08 and 0.60, respectively. Setting topic constraints for $k\mathcal{K}\mathcal{P}$-core that cover a certain proportion of the keyword set is challenging, so we increased $\theta$ starting from 0.01 in increments of 0.01 until the optimal community was achieved.

Implementation details are discussed in our document called "implementationDetails.pdf", which details the quality metrics, query vertices and meta-structure selection, and the settings for $k$ and $\theta$.	

## Requirements:
 Intel(R) Xeon(R) Silver 4110 CPU @ 2.10GHz and 64G of memory with Ubuntu installed.
 
 JDK 1.8
 
 All environment required for the OCTIS
 
 ## Input Farmat
The standard data formats for TCS-HINMS input are mainly:

graph, vertex type, edge type, topic vector, Meta-path, Meta-structure.

TCS/Sample.java is a sample for data format.


## References
[1] Y. Fang, Y. Yang, W. Zhang, X. Lin, and X. Cao, “Effective and efficientcommunity  search  over  large  heterogeneous  information  networks,”Proceedings of the VLDB Endowment, vol. 13, no. 6, pp. 854–867, 2020
