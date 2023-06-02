
from octis.dataset.dataset import Dataset
dataset = Dataset()
dataset.load_custom_dataset_from_folder(r"../TopicModel/data/dblp-4-area/")
from octis.models.LDA import LDA
model = LDA(num_topics=10)  # Create model
model_output = model.train_model(dataset) # Train the model

print(len(model_output['topic-document-matrix'][0]) )

with open(r"../TopicModel/data/dblp-4-area/Dblp_paper_TopicOut.txt",'w') as f:
    for i in range(len(model_output['topic-document-matrix'][0])):
        f.write(str(model_output['topic-document-matrix'][:, i].tolist()))
        f.write('\n')


import numpy as np

paperIdIndex=[]
with open(r"../TopicModel/dblp_data_processed/dplp_paper_index.txt",'r') as f:
    lines=f.readlines()
    for line in lines:
        paperNewIndex=line.strip('\n')
        paperIdIndex.append(paperNewIndex)

topics=[]
with open(r"../TopicModel/data/dblp-4-area/Dblp_paper_TopicOut.txt",'r') as f:
    lines=f.readlines()
    for line in lines:
        topic=line.strip('\n')
        topics.append(eval(topic))

index=[]
with open(r"../TopicModel/data/dblp-4-area/indexes.txt",'r') as f:
    lines=f.readlines()
    for line in lines:
        corpusIndex=line.strip('\n')
        index.append(corpusIndex)

print(len(paperIdIndex))
print(len(topics))
print(len(index))

with open(r"../TopicModel/data/Aminer-paper/Aminer_PaperId_Topic.txt",'w') as f:
    for i in range(len(index)):
        f.write(paperIdIndex[eval(index[i])])
        f.write(' ')
        for j in topics[i]:
            f.write(str(j))
            f.write(' ')
        f.write('\n')

userId=[]
with open(r"../TopicModel/dblp_data_processed/dplp_vertexType.txt",'r') as f:
    lines=f.readlines()
    for line in lines:
        line=line.strip('\n').split(' ')
        if line[1]=='0':
            userId.append(eval(line[0]))

print(userId)

vertexType=dict()
vertexNum=0
with open(r"../TopicModel/dblp_data_processed/dplp_vertexType.txt",'r') as f:
    lines=f.readlines()
    for line in lines:
        line=line.strip('\n').split(' ')
        if (line[1]=='0'):
            vertexNum=vertexNum+1
        vertexType.update({eval(line[0]):eval(line[1])})

graph=dict()
with open(r"../TopicModel/dblp_data_processed/dblp_graph.txt",'r') as f:
    lines=f.readlines()
    for i in range(vertexNum):
        line=lines[i]
        line=line.strip('\n').strip(' ').split(' ')
        authorId=i
        graph.update({authorId:list()})
        for j in range(0,len(line),2):
            graph[authorId].append(eval(line[j]))

print(graph.get(3))

topicsDict=dict()
for i in range(len(index)):
    topicsDict.update({eval(paperIdIndex[eval(index[i])]):np.array(topics[i])})

def softmax(X):
    s=np.exp(X)/sum(np.exp(X))
    return s

userTopic=dict()
for user in userId:
    userTopic.update({user:np.zeros(10)})
    print(user)
    print(graph.get(user))
    for paperid in graph.get(user):
        if paperid in topicsDict.keys():
            userTopic[user]=userTopic[user]+topicsDict.get(paperid)
    userTopic[user]=softmax(userTopic[user])

with open(r"../TopicModel/data/dblp-4-area/dblp_UserId_Topic.txt",'w') as f:
    for key in userTopic.keys():
        f.write(str(key))
        f.write(' ')
        for i in userTopic.get(key):
            f.write(str(i))
            f.write(' ')
        f.write('\n')