from sklearn.tree import DecisionTreeRegressor
import pandas as pd
import numpy as np
import json
### Import data
dataframe=pd.read_csv('hlist_0.18030_preprocessed.list',sep=' ')
dataframe=dataframe.drop(['id(1)','desc_scale(2)','num_prog(4)','phantom(8)',\
							'Unnamed: 62','Orig_halo_ID(30)','A[x](45)',\
							'b_to_a(43)', 'c_to_a(500c)(49)' ,'A[z](500c)(52)' ,\
							'b_to_a(500c)(48)','A[z](47)' ,'A[y](46)',\
							'A[x](500c)(50)','#scale(0)','A[y](500c)(51)'\
							],axis=1)

### Switch columns so shape is last 
cols = dataframe.columns.tolist()
switch_column= cols[-1]
cols[cols.index('c_to_a(44)')]=switch_column
cols[-1]='c_to_a(44)'
dataframe=dataframe[cols]




data,data_labels=np.hsplit(np.array(dataframe),[-1])
data_labels=np.ravel(data_labels).astype(float,casting='unsafe')
dataframe=dataframe.drop(['c_to_a(44)'],axis=1)

good_columns=np.array(dataframe.columns.values.tolist())

decision_tree=DecisionTreeRegressor()
decision_tree.fit(data,data_labels)


feature_weights=np.sort(decision_tree.feature_importances_)
sorted_indexes=np.argsort(feature_weights)[::-1]

sorted_features=good_columns[sorted_indexes]
sorted_feature_weights=feature_weights[sorted_indexes]

zipped_vals=zip(sorted_features,sorted_feature_weights)

with open('output.json','w') as f:
	f.write(json.dumps(zipped_vals))

