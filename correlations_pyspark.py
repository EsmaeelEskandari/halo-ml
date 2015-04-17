from pyspark import SparkConf, SparkContext
from sklearn.cross_validation import train_test_split, Bootstrap
from sklearn.tree import DecisionTreeRegressor
from pyspark.mllib.stat import Statistics
from sklearn.base import copy
import sys
import pandas as pd
import numpy as np
import json
from pyspark.mllib.linalg import Vectors
input_file = sys.argv[1]
output_file = input_file[6:-18]+'_importances.json'

def getSparkContext():
    """
    Gets the Spark Context
    """
    conf = (SparkConf()
         .setMaster('spark://master:7077')
	 .setAppName("Feature Importance") # Name of App
	 .set('spark.akka.frameSize',"50")
	 .set("spark.executor.memory", "2g")) # Set 1 gig of memory
    	
    sc = SparkContext(conf = conf) 
    return sc
### Preprocessing Function
def preprocess (hlist_filename):
	### Import data
	dataframe=pd.read_csv(hlist_filename,sep=' ')
	dataframe=dataframe.drop(['id(1)','desc_scale(2)','num_prog(4)','phantom(8)',\
							'Unnamed: 62','Orig_halo_ID(30)','A[x](45)',\
							'b_to_a(43)', 'c_to_a(500c)(49)' ,'A[z](500c)(52)' ,\
							'b_to_a(500c)(48)','A[z](47)' ,'A[y](46)',\
							'A[x](500c)(50)','#scale(0)','A[y](500c)(51)'\
							],axis=1)

	### Switch columns so shape is last as dependent variable
	cols = dataframe.columns.tolist()
	switch_column= cols[-1]
	cols[cols.index('c_to_a(44)')]=switch_column
	cols[-1]='c_to_a(44)'
	dataframe=dataframe[cols]

	train_data,train_labels=np.hsplit(np.array(dataframe),[-1])
	train_labels=np.ravel(train_labels).astype(float,casting='unsafe')
	dataframe=dataframe.drop(['c_to_a(44)'],axis=1)

	return dict(train_data=train_data, train_labels=train_labels)

###Extracting good_columns
def extract(sample):
	dataframe=pd.read_csv(sample,sep=' ')
    	dataframe=dataframe.drop(['id(1)','desc_scale(2)','num_prog(4)','phantom(8)',\
							'Unnamed: 62','Orig_halo_ID(30)','A[x](45)',\
							'b_to_a(43)','c_to_a(44)', 'c_to_a(500c)(49)' ,'A[z](500c)(52)' ,\
							'b_to_a(500c)(48)','A[z](47)' ,'A[y](46)',\
							'A[x](500c)(50)','#scale(0)','A[y](500c)(51)'\
							],axis=1)

	good_columns=np.array(dataframe.columns.values.tolist())
	return good_columns

###Training Function
def train(data):
	train_data = data['train_data']
	train_labels = data['train_labels']
	decision_tree=DecisionTreeRegressor().fit(train_data,train_labels)
	return decision_tree
        #attributes = decision_tree.feature_importances_
        #return attributes
###Finishing Function
def finish(model):
	attributes = model.feature_importances_
	return attributes
def main():
	###Loading data from sources
	print 'before  preprocess'
	data = [preprocess(input_file)]
	print 'after preprocess'
	#get spark context
	sc = getSparkContext()
	print 'before parallelize'
        data=np.hstack((data[0]['train_data'],data[0]['train_labels'].reshape((data[0]['train_labels'].shape[0],1)))) 
        data=[Vectors.dense(list(data[row,:])) for row in range(0,data.shape[0])]
        samples = sc.parallelize(data)
        #samples.persist()
        pearsonCorr = Statistics.corr(samples)
        print str(pearsonCorr).replace('nan', 'NaN')
        sys.exit()
        print Statistics.corr(data, method="pearson")
if __name__=='__main__':
	main()
