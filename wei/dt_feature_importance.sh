#!/bin/bash
for filename in *.list; do
        python dt_feature_importance.py "$filename"
done

