paper_author=dict()   #1
paper_venue=dict()    #2
paper_year=dict()     #3

author_paper=dict()  #0
venue_paper=dict()   #ok
year_paper=dict()    #ok

paper_content=dict()   # ok

# 处理论文和作者
with open("D:\pythonProject\CSHDataAnalysics\Dblp4core\dataset\paper_author.txt") as f:
    lines=f.readlines()
    for i in range(len(lines)):
        line=lines[i].strip('\n').split(',')
        paper_author.setdefault(i,list())
        for j in range(len(line)):
            paper_author.get(i).append(line[j])

# 处理论文和标题及内容
with open(r"D:\pythonProject\CSHDataAnalysics\Dblp4core\dataset\title_content.txt",encoding="utf-8") as f:
    lines=f.readlines()
    for i in range(len(lines)):
        line=lines[i].strip('\n').split('\t')
        paper_content.setdefault(i,list())
        for j in range(len(line)):
            if (len(line[j])!=0):
                paper_content.get(i).append(line[j])

# 处理论文和会议的关系
with open(r"D:\pythonProject\CSHDataAnalysics\Dblp4core\dataset\conference.txt") as f:
    lines=f.readlines()
    for i in range(len(lines)):
        line=lines[i].strip('\n')
        paper_venue.update({i:line})

# 处理论文和年月的关系
with open(r"D:\pythonProject\CSHDataAnalysics\Dblp4core\dataset\year.txt") as f:
    lines=f.readlines()
    for i in range(len(lines)):
        line=lines[i].strip('\n')
        paper_year.update({i:line})

# 完成了顶点Id的转化
vertex_index_map=dict()
index=0

for p,a in paper_author.items():
    for author in a:
        author_paper.setdefault(author,list())
        author_paper.get(author).append(p)

        if (vertex_index_map.get(author)==None):
            vertex_index_map.update({author:index})
            index=index+1

for p,y in paper_year.items():
    year_paper.setdefault(y,list())
    year_paper.get(y).append(p)
    if (vertex_index_map.get(p)==None):
        vertex_index_map.update({p:index})
        index=index+1

for p,v in paper_venue.items():
    venue_paper.setdefault(v,list())
    venue_paper.get(v).append(p)
    if (vertex_index_map.get(v)==None):
        vertex_index_map.update({v:index})
        index=index+1

for i in range(2012,2017,1):
    year=str(i)
    vertex_index_map.update({year:index})
    index=index+1

edgeType=dict()
edge_index_map=dict()
edgeIndex=0

for a,p in author_paper.items():
    for pp in p:
        s_a_pp=str(vertex_index_map.get(a))+"_"+str(vertex_index_map.get(pp))
        s_pp_a=str(vertex_index_map.get(pp))+"_"+str(vertex_index_map.get(a))
        if (edge_index_map.get(s_a_pp)==None):
            edgeType.update({edgeIndex:1})
            edge_index_map.update({s_a_pp:edgeIndex})
            edge_index_map.update({s_pp_a:edgeIndex})
            edgeIndex=edgeIndex+1

for p,v in paper_venue.items():
    s_p_v=str(vertex_index_map.get(p))+"_"+str(vertex_index_map.get(v))
    s_v_p=str(vertex_index_map.get(v))+"_"+str(vertex_index_map.get(p))
    if (edge_index_map.get(s_p_v)==None):
        edgeType.update({edgeIndex:2})
        edge_index_map.update({s_p_v:edgeIndex})
        edge_index_map.update({s_v_p:edgeIndex})
        edgeIndex=edgeIndex+1

for p,y in paper_year.items():
    s_p_y=str(vertex_index_map.get(p))+"_"+str(vertex_index_map.get(y))
    s_y_p=str(vertex_index_map.get(y))+"_"+str(vertex_index_map.get(p))
    if (vertex_index_map.get(s_p_y)==None):
        edgeType.update({edgeIndex:3})
        edge_index_map.update({s_p_y:edgeIndex})
        edge_index_map.update({s_y_p:edgeIndex})
        edgeIndex=edgeIndex+1

# 添加边以及类别，构建一个新图
graph=[[] for i in range(index)]
# 1、作者和论文的关系,   论文和作者的关系
for a,p in author_paper.items():
    a_index=vertex_index_map.get(a)
    for paper in p:
        p_index=vertex_index_map.get(paper)
        edge=str(a_index)+"_"+str(p_index)
        edge_index=edge_index_map.get(edge)
        graph[a_index].append(p_index)
        graph[a_index].append(edge_index)
        graph[p_index].append(a_index)
        graph[p_index].append(edge_index)

# 2、论文和会议的关系   会议和论文的关系
for p,v in paper_venue.items():
    p_index=vertex_index_map.get(p)
    v_index=vertex_index_map.get(v)
    edge_index=edge_index_map.get(str(p_index)+"_"+str(v_index))
    graph[p_index].append(v_index)
    graph[p_index].append(edge_index)
    graph[v_index].append(p_index)
    graph[v_index].append(edge_index)

# 3、论文和时间的关系   时间和论文的关系
for p,y in paper_year.items():
    p_index=vertex_index_map.get(p)
    y_index=vertex_index_map.get(y)
    edge_index=edge_index_map.get(str(p_index)+"_"+str(y_index))
    graph[p_index].append(y_index)
    graph[p_index].append(edge_index)
    graph[y_index].append(p_index)
    graph[y_index].append(edge_index)

vertexTypeMap=dict()
for a in author_paper.keys():
    vertexTypeMap.update({vertex_index_map.get(a):0})
for p in paper_author.keys():
    vertexTypeMap.update({vertex_index_map.get(p): 1})
for v in venue_paper.keys():
    vertexTypeMap.update({vertex_index_map.get(v):2})
for y in year_paper.keys():
    vertexTypeMap.update({vertex_index_map.get(y):3})


with open(r"D:\pythonProject\CSHDataAnalysics\Dblp4core\dblp_data_processed\dblp_graph.txt",'w') as f:
    for vid in range(len(graph)):
        f.write(str(vid))
        f.write(" ")
        l=graph[vid]
        for i in range(len(l)-1):
            f.write(str(l[i]))
            f.write(' ')
        f.write(str(l[len(l)-1]))
        f.write("\n")

with open(r"D:\pythonProject\CSHDataAnalysics\Dblp4core\dblp_data_processed\dplp_vertexType.txt",'w') as f:
    for v,t in vertexTypeMap.items():
        f.write(str(v))
        f.write(" ")
        f.write(str(t))
        f.write('\n')

with open(r"D:\pythonProject\CSHDataAnalysics\Dblp4core\dblp_data_processed\dplp_edgeType.txt",'w') as f:
    for e,t in edgeType.items():
        f.write(str(e))
        f.write(" ")
        f.write(str(t))
        f.write('\n')

with open(r"D:\pythonProject\CSHDataAnalysics\Dblp4core\dblp_data_processed\dplp_content.txt",'w',encoding='utf-8') as f:
    with open(r"D:\pythonProject\CSHDataAnalysics\Dblp4core\dblp_data_processed\dplp_paper_index.txt", 'w') as f2:
        for p,c in paper_content.items():
            for cc in c:
                f.write(cc)
            f.write('\n')
            f2.write(str(vertex_index_map.get(p)))
            f2.write('\n')


graphDict=dict()
for i in range(len(graph)):
    if i<6000:
        graphDict.update({i:len(graph[i])})

print(sorted(graphDict.items(),key= lambda kv:(kv[1],kv[0]),reverse=True))
