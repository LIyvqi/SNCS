"""
Processing ASN raw data sets
"""

category=dict() #to store paper_index and the publication in which the paper was published
paperyear=dict() #to store the year of publication of paper_index, we use the last 5 years, last 10 years, last 20 years and 20 years ago as nodes for discrete time periods
information=dict() # used to store the abstract of the paper and the paper title, as text information we extract the topic
authorPaper=dict() # to store the author's published papers
paperindex=""


with open(r"../TopicModel/data/AMiner-Paper.txt",'r',encoding="utf-8") as f:
    lines=f.readlines()
    for i in range(len(lines)):
        line=lines[i]
        if line==r"\n": continue
        if line[0:6]=="#index":
            paperindex = line.strip('\n').split(' ')[1]
            nextline=lines[i+3]
            if nextline[0:2]=="#t":
                year=nextline.strip('\n').split(' ')[1]
                if not year.isdigit(): continue
                year_int = eval(year)
                if year_int<2013:
                    paperindex=""

        if paperindex=="": continue
        if line[0:2] == "#*":
            title=line.strip('\n')[3:]
            information.update({paperindex:title})

        if line[0:2] == "#@":
            authors=line.strip('\n')[3:].strip(' ').split(';')[0:]
            for author in authors:
                if not authorPaper.get(author):
                    authorPaper.update({author:set()})
                    authorPaper.get(author).add(paperindex)
                else:authorPaper.get(author).add(paperindex)

        # 用来得到文章的发表年份
        if line[0:2] == "#t":
            year=line.strip('\n').split(" ")[1]
            if not year.isdigit(): continue
            year_int=eval(year)
            year_logo="the"+year+"year"
            paperyear.update({paperindex:year_logo})

        if line[0:2] == "#c":
            cat=line.strip('\n').split(' ')[1]
            if "'" in cat:
                cat=cat.split("'")
                cat=cat[0]
            if "CV" in cat:
                cat="CV"
            if "NLP" in cat:
                cat="NLP"
            category.update({paperindex:cat})

        if line[0:2] == "#!":
            abstract=line.strip('\n')[3:]
            information[paperindex]=information.get(paperindex)+" "+abstract

index=0
authorIndexDict=dict()
for author in authorPaper.keys():
    authorIndexDict.update({author:index})
    index=index+1

paperIndexDict=dict()
for paper in category.keys():
    if paper not in paperIndexDict.keys():
        paperIndexDict.update({paper: index})
        index = index + 1

categoryIndexDict=dict()
for cat in category.values():
    if cat not in categoryIndexDict.keys():
        categoryIndexDict.update({cat:index})
        index=index+1

yearIndexDict=dict()
for i in range(2013,2015,1):
    yearIndexDict.update({"the"+str(i)+"year":index})
    index=index+1


graphAuthor=dict()
graphPaper=dict()
graphYear=dict()
graphCategory=dict()
edgeIndex=0
graphEdge=dict()
graphEdgeType=dict()
graphVertexType=dict()

categoryTypeIndex=dict()
typeIndex=3
for cat in categoryIndexDict.keys():
    categoryTypeIndex.update({categoryIndexDict.get(cat):typeIndex})

typeIndex=typeIndex+1
yearTypeIndex=dict()
for year in yearIndexDict.keys():
    yearTypeIndex.update({yearIndexDict.get(year):typeIndex})

print("categoryTypeIndex: ",categoryTypeIndex)
print("yearTypeIndex: ",yearTypeIndex)


for author in authorPaper:
    papers=authorPaper.get(author)
    papersEdgeIndex=list()
    edgeint=edgeIndex
    authorIndex = authorIndexDict.get(author)
    for paper in papers:
        paperindex=paperIndexDict.get(paper)
        papersEdgeIndex.append(paperindex)
        edge1=str(paperindex)+"_"+str(authorIndex)
        edge2=str(authorIndex)+"_"+str(paperindex)
        if edge1 in graphEdge.keys():
            edgeint=graphEdge.get(edge1)
        else:
            graphEdge.update({edge1:edgeIndex})
            graphEdge.update({edge2:edgeIndex})
            edgeint=edgeIndex
            edgeIndex=edgeIndex+1
        papersEdgeIndex.append(edgeint)
        graphEdgeType.update({edgeint:1})

        if paperindex in graphPaper.keys():
            graphPaper[paperindex].append(authorIndex)
            graphPaper[paperindex].append(edgeint)
        else:
            graphPaper.update({paperindex:list()})
            graphPaper[paperindex].append(authorIndex)
            graphPaper[paperindex].append(edgeint)

    graphAuthor.update({authorIndex:papersEdgeIndex})


for paper_str in category.keys():
    paper_index=paperIndexDict.get(paper_str)
    if paper_index in graphPaper.keys():
        categoryIndex=categoryIndexDict.get(category.get(paper_str))

        edgeint=edgeIndex
        edge1=str(paper_index)+"_"+str(categoryIndex)
        edge2=str(categoryIndex)+"_"+str(paper_index)
        if edge1 in graphEdge.keys():
            edgeint = graphEdge.get(edge1)
        else:
            graphEdge.update({edge1: edgeIndex})
            graphEdge.update({edge2: edgeIndex})
            edgeint = edgeIndex
            graphEdgeType.update({edgeint:categoryTypeIndex.get(categoryIndex)})
            edgeIndex = edgeIndex + 1
        graphPaper[paper_index].append(categoryIndex)
        graphPaper[paper_index].append(edgeint)

        if categoryIndex in graphCategory.keys():
            graphCategory[categoryIndex].append(paper_index)
            graphCategory[categoryIndex].append(edgeint)
        else:
            graphCategory.update({categoryIndex:list()})
            graphCategory[categoryIndex].append(paper_index)
            graphCategory[categoryIndex].append(edgeint)

for paper_str in paperyear.keys():
    paper_index=paperIndexDict.get(paper_str)
    if paper_index in graphPaper.keys():
        yearIndex=yearIndexDict.get(paperyear.get(paper_str))

        edgeint=edgeIndex
        edge1=str(paper_index)+"_"+str(yearIndex)
        edge2=str(yearIndex)+"_"+str(paper_index)
        if edge1 in graphEdge.keys():
            edgeint = graphEdge.get(edge1)
        else:
            graphEdge.update({edge1: edgeIndex})
            graphEdge.update({edge2: edgeIndex})
            edgeint=edgeIndex
            graphEdgeType.update({edgeint: yearTypeIndex.get(yearIndex)})
            edgeIndex = edgeIndex + 1

        graphPaper[paper_index].append(yearIndex)
        graphPaper[paper_index].append(edgeint)

        if yearIndex in graphYear.keys():
            graphYear[yearIndex].append(paper_index)
            graphYear[yearIndex].append(edgeint)
        else:
            graphYear.update({yearIndex:list()})
            graphYear[yearIndex].append(paper_index)
            graphYear[yearIndex].append(edgeint)

for vertex in graphAuthor.keys():
    graphVertexType.update({vertex:1})

for vertex in graphPaper.keys():
    graphVertexType.update({vertex:2})

for vertex in graphCategory.keys():
    graphVertexType.update({vertex:categoryTypeIndex.get(vertex)})

for vertex in graphYear.keys():
    graphVertexType.update({vertex:yearTypeIndex.get(vertex)})


with open(r"../TopicModel/data/AMiner-Paper-graph.txt",'w',encoding="utf-8") as f:
    for key in graphAuthor.keys():
        f.write(str(key))
        f.write(" ")
        for i in graphAuthor.get(key):
            f.write(str(i))
            f.write(" ")
        f.write('\n')

    for key in graphPaper.keys():
        f.write(str(key))
        f.write(" ")
        for i in graphPaper.get(key):
            f.write(str(i))
            f.write(" ")
        f.write('\n')

    for key in graphYear.keys():
        f.write(str(key))
        f.write(" ")
        for i in graphYear.get(key):
            f.write(str(i))
            f.write(" ")
        f.write('\n')

    for key in graphCategory.keys():
        f.write(str(key))
        f.write(" ")
        for i in graphCategory.get(key):
            f.write(str(i))
            f.write(" ")
        f.write('\n')

with open(r"../TopicModel/data/AMiner-Paper-graphVertexType.txt",'w',encoding="utf-8") as f:
    for key in graphVertexType.keys():
        f.write(str(key))
        f.write(" ")
        f.write(str(graphVertexType.get(key)))
        f.write('\n')

with open(r"../TopicModel/data/AMiner-Paper-graphEdgeType.txt",'w',encoding="utf-8") as f:
    for key in graphEdgeType.keys():
        f.write(str(key))
        f.write(" ")
        f.write(str(graphEdgeType.get(key)))
        f.write('\n')


with open(r"../TopicModel/data/AMiner-Paper-Author-Index.txt",'w',encoding="utf-8") as f:
    for key in authorIndexDict.keys():
        f.write(str(key))
        f.write(" ")
        f.write(str(authorIndexDict.get(key)))
        f.write('\n')

with open(r"../TopicModel/data/AMiner-Paper-oldIndex-textInfo.tsv",'w',encoding="utf-8") as f:
    for key in information.keys():
        f.write(str(key))
        f.write('\t')
        f.write(str(information.get(key)))
        f.write('\n')

with open(r"../TopicModel/data/AMiner-Paper-textInfo.tsv",'w',encoding="utf-8") as f:
    for key in information.keys():
        f.write(str(information.get(key)))
        f.write('\n')

with open(r"../TopicModel/data/AMiner-Paper-paperOld-newIndex-textInfo.tsv",'w',encoding="utf-8") as f:
    for key in paperIndexDict.keys():
        f.write(str(key))
        f.write(' ')
        f.write(str(paperIndexDict.get(key)))
        f.write('\n')

with open(r"../TopicModel/data/AMiner-Paper-newIndex-textInfo.tsv",'w',encoding="utf-8") as f:
    for key in paperIndexDict.keys():
        f.write(str(paperIndexDict.get(key)))
        f.write('\t')
        f.write(str(information.get(key)))
        f.write('\n')