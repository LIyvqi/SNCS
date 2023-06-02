from octis.dataset.dataset import Dataset
dataset=Dataset()

import os
import string
from octis.preprocessing.preprocessing import Preprocessing
os.chdir(os.path.pardir)

# Initialize preprocessing
preprocessor = Preprocessing(vocabulary=None, max_features=None,
                             remove_punctuation=True, punctuation=string.punctuation,
                             lemmatize=True, stopword_list='english',
                             min_chars=1, min_words_docs=0)

index=[]
with open(r'..\TopicModel\data\Movies_and_TV_textInfo\indexes.txt','r') as indexFile:
    lines=indexFile.readlines()
    for line in lines:
        index.append(eval(line.strip('\n')))

index_corpus=[]
with open(r'..\TopicModel\data\Movies_and_TV_textInfo.tsv','r') as f:
    lines=f.readlines()
    for line in lines:
        index_corpus.append(line.split('\t')[0])

with open(r'..\TopicModel\data\Movies_and_TV_textInfo\ID_Index.txt','w') as f:
    for i in index:
        f.write(str({index_corpus[i//2]:i}))
        f.write('\n')