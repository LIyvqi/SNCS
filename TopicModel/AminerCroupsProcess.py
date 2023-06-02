import spacy
nlp=spacy.load('en_core_web_sm')


import os
import string
from octis.preprocessing.preprocessing import Preprocessing
os.chdir(os.path.pardir)

# Initialize preprocessing
preprocessor = Preprocessing(vocabulary=None, max_features=None,
                             remove_punctuation=True, punctuation=string.punctuation,
                             lemmatize=True, stopword_list='english',
                             min_chars=1, min_words_docs=0)

# preprocess
dataset = preprocessor.preprocess_dataset(documents_path=r"../TopicModel/data/AMiner-Paper-textInfo.tsv")
#
dataset.save(r"../TopicModel/data/Aminer-paper")