# After processing the corpus, the topic model is trained and stored in the TopicOut.txt file
# Stored as a dictionary, the output is the item number (type str) plus a topic vector representation of the item (type list)
from octis.dataset.dataset import Dataset
dataset = Dataset()
dataset.load_custom_dataset_from_folder(r"../TopicModel/data/Movies_and_TV_textInfo/")
from octis.models.LDA import LDA
model = LDA(num_topics=30)  # Create model
model_output = model.train_model(dataset) # Train the model

index=[]
with open(r"../TopicModel/data/Movies_and_TV_textInfo/indexes.txt",'r') as indexFile:
    lines=indexFile.readlines()
    for line in lines:
        index.append(eval(line.strip('\n')))

index_corpus=[]
with open(r"../TopicModel/data/Movies_and_TV_textInfo.tsv",'r') as f:
    lines=f.readlines()
    for line in lines:
        index_corpus.append(line.split('\t')[0])

index_topic_dict={}
with open(r"../TopicModel/data/Movies_and_TV_textInfo/TopicOut.txt",'w') as f:
    for i in range(len(model_output['topic-document-matrix'][0])):
        f.write(str({index_corpus[index[i]//2]: model_output['topic-document-matrix'][:, i].tolist()}))
        f.write('\n')