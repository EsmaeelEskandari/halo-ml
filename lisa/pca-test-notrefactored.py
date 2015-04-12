#!/usr/bin/python

import scipy as sp
from sklearn.decomposition import PCA
from sklearn.base import copy
from sklearn.cross_validation import train_test_split
import sys
import pandas as pd
import numpy as np

### Load data from sources ###
input_file=sys.argv[1]
dataframe=pd.read_csv(input_file,sep=' ')
dataframe=dataframe.drop(['id(1)','desc_scale(2)','num_prog(4)','phantom(8)',\
   'Unnamed: 62','Orig_halo_ID(30)','A[x](45)',\
   'b_to_a(43)', 'c_to_a(500c)(49)' ,'A[z](500c)(52)' ,\
   'b_to_a(500c)(48)','A[z](47)' ,'A[y](46)',\
   'A[x](500c)(50)','#scale(0)','A[y](500c)(51)'\
   ],axis=1)

### Switch columns so shape is last ###
cols=dataframe.columns.tolist()
switch_column=cols[-1]
cols[cols.index('c_to_a(44)')]=switch_column
cols[-1]='c_to_a(44)'
dataframe=dataframe[cols]

train_data,train_labels=np.hsplit(np.array(dataframe),[-1])
train_labels=np.ravel(train_labels).astype(float,casting='unsafe')
dataframe=dataframe.drop(['c_to_a(44)'],axis=1)
#print("data shape: {0}, data labels shape: {1}").format(data.shape, data_labels.shape)
#print("data labels: {0}").format(data_labels)

### Get the feature names ###
feature_names=dataframe.columns.tolist()
### Just checking values while debugging ###
#print("feature names:")
#print("{0}").format(feature_names)


### Finding how many components are needed to explain 99% of the explained variance ###

def getExplainedVariance(k_values):
  for k in k_values:
    pca=PCA(n_components=k)
    X_reduced=pca.fit_transform(train_data)
    print 'Explained variance ratio(s) (first {0} components): {1}'.format(k, pca.explained_variance_ratio_)
    print 'The shape of X reduced: {0}'.format(X_reduced.shape)
    print 'The fraction of total variance explained by the first {0} components: {1}'.format(k, sum(pca.explained_variance_ratio_))
    
k_values=range(1,6)  # for testing - we actually have 47 features
getExplainedVariance(k_values)
