from pyspark import SparkConf, SparkContext
from sklearn.tree import DecisionTreeRegressor
from sklearn.base import copy
import sys
import pandas as pd
import numpy as np
import json
import time

input_file = sys.argv[1]
output_file = input_file[6:-18]+'_importances.json'

def getSparkContext():
    """
    Gets the Spark Context
    """
    conf = (SparkConf()
         .setMaster('spark://master:7077')
	 .setAppName("Feature Importance") # Name of App
	 .set('spark.akka.frameSize',"550")
	 .set("spark.executor.memory", "4g"))
    	
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

###Finishing Function
def finish(model):
	attributes = model.feature_importances_
	return attributes

def print_op(msg, fn, *args, **kwargs):
        print '%s...' % msg,
        t = time.time()
        res = fn(*args, **kwargs)
        print 'done. [%.6f seconds elapsed]' % (time.time() - t)

        return res

def main():
	###Loading data from sources
        # ex: data will be like [{'train_data': numpy_array, 'train_labels': numpy_array}]
	data = [print_op('preprocessing', preprocess, input_file)]

	#get spark context
	sc = getSparkContext()

	###Parallelize compute
        # creates RDD, distributes data across workers (data to this point has been in driver)
	decision_tree = print_op('loading data', sc.parallelize, data)

        # We know that decision_tree is an RDD that is a single-element array just like depicted in ln. 84; 'train'
        # function should take the longest time of any of these operations to perform and yet it returns quite quickly; 
        # Sam and mdye think this could be the root of the problem: we might need to better distribute the data to
        # operate on so that spark can parallelize that operation 
	processed = print_op('training machine', decision_tree.map, train)

        # not quite sure the structure of 'processed' data structure (need to determine what a call to
        # DecisionTreeRegressor.fit() will return...
        # Also, it could be that the tree is really unbalanced and that could cause parallelization problems
        finished = print_op('finishing', processed.map, finish)

        #
	good_columns = print_op('extracting good columns', extract, input_file)
        print 'good columns: %s' % good_columns

        collected_data = print_op('--------------------------------\n(this is probably gonna blow):\ncollecting dataset on driver', finished.collect())

	### Calculate feature weights
	feature_weights = print_op('calculating feature weights', np.ravel, collected_data[0])

	sorted_indexes=np.argsort(feature_weights)[::-1]
	sorted_features=good_columns[sorted_indexes]
	sorted_feature_weights=feature_weights[sorted_indexes]
	
	zipped_vals=zip(sorted_features,sorted_feature_weights)
	print zipped_vals
	with open(output_file,'w') as f:
		f.write(json.dumps(zipped_vals))

        print 'finished, exiting.\n'

if __name__=='__main__':
	main()
