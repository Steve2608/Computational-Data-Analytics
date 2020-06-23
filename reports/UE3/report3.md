---
title: "Computational Data Analytics, UE3"
author: [Stefan Brandl, Peter Chalupar, Alexander Raschl]
date: \today{}
header-includes: 
	- \usepackage[all]{nowidow}
---

# Exercise Part 3 of 3 - KV Computational Data Analytics (351.008)
Summer term 2020 - Prof. Dr. Johannes Fürnkranz, Florian Beck

Deadline: July 12, 23:59

**Group Members**

| Student ID    | First Name  | Last Name      |
| --------------|-------------|----------------|
| 01555580      | Peter       | Chalupar       |
| 01556188      | Alexander   | Raschl         |
| 01555842      | Stefan      | Brandl         |

> *The submission should be in the form of a self-explanatory presentation (e.g. PDF, OpenOffice or PowerPoint) with a focus on interpretation or analysis.*

\newpage

## Stream Mining: Hoeffding Trees (1.5 P.)

> *In this exercise you will compare the decision tree classifiers J48 and HoeffdingTree which
   uses an incremental approach suitable for stream mining. Create a new random dataset by choosing Generate in the main tab. Choose the generator RandomRBF and run it with the standard settings. Compare the classification accuracies, and the time taken to build model of J48 and HoeffdingTree on the generated dataset. Repeat the classification for more generated datasets of bigger size by increasing -n (numExamples) (for example,100, 250, 500, 1,000, ..., 100,000). For the three biggest datasets, also compare the size of the trees, and the total execution time (in seconds; you can find the start and end time in the log). What conclusions about the classifiers can be drawn from the performances?*

The table below comprises the overall results, obtained during the requested tests.  
A full example console output can be found in _'src/EX6/example_output_log.txt'_, as it was simply far too big for including it in this report. 

| N_Samples            | J48_Acc (%)          | Hoeffding_Acc (%)    | J48_N_Leaves         | Hoeffding_N_Leaves   | J48_Time (s)         | Hoeffding_Time (s)  |
|----------------------|----------------------|----------------------|----------------------|----------------------|----------------------|---------------------|
| 100                  | 64,000               | 58,000               | 13                   | 1                    | 0,054                | 0,036               |
| 250                  | 74,800               | 67,600               | 33                   | 1                    | 0,064                | 0,032               |
| 500                  | 77,400               | 64,000               | 79                   | 1                    | 0,088                | 0,038               |
| 1000                 | 80,200               | 62,500               | 115                  | 1                    | 0,200                | 0,061               |
| 10000                | 89,360               | 74,440               | 707                  | 7                    | 2,459                | 0,700               |
| 50000                | 91,858               | 86,214               | 2375                 | 20                   | 28,205               | 1,564               |
| 100000               | 92,145               | 88,450               | 3833                 | 44                   | 91,481               | 3,171               |
 
A few general trends can be observed for all dataset sizes:
- The achieved accuracy (10-Fold-CV) is always higher for the _J48_ compared to the _Hoeffding Tree_
- The number of leaves of _J48_ is always (far) larger.
- Especially for large datasets the _Hoeffding Tree_ is far faster to fit than the _J48_
- The overall achieved accuracy increases with the dataset size for both classifiers (more data, better estimates)

__Conclusions__  
Interestingly, the number of leaves for _Hoeffding Trees_ up to size 1000 is only 1. The predictions are solely made through a single instance of adaptive _Naive Bayes_ _(standard params)_.

As the _Hoeffding Tree_ is specifically designed for handling large datasets/streams it is not surprising that its training times outperform the training times of the _J48_.  
However, this comes with the cost of loosing some of the accuracy, the _J48_ provides.  
Thus, one could argue that the _Hoeffding Tree_ trades some accuracy for better performance and simpler/lightweight models. 




## Distance-based Methods (1.5 P.)

> *Weka provides the classifier IBk which has implemented different nearest neighbor approaches
   discussed in the lecture. To test them, create a new random dataset by choosing Generate in the
   main tab. Choose the generator RandomRBF and set -c (numClasses) to 10 and -n (numExamples)
   to 25,000.*

> *1. Compare the classification results and the execution times with LinearNNSearch, KDTree
      and BallTree as nearestNeighbourSearchAlgorithm.*

TODO

> *1. In this special case, when a minimal support of just one instance is needed, how do you interpret these sets with minimum and maximum *k* and their size?*

TODO

> *2. For the fastest search algorithm in the previous task, compare the accuracies for different
       numbers of nearest neighbors (in Weka: -K (KNN)). For the best three values for k, does a
       distance weight method further improve the accuracy?*


### Clustering (2 P.)

> *The dataset 'cities_greece' contains the latitudes and longitudes of 9,882 cities in Greece 3. In
   this exercise you will apply the algorithms SimpleKMeans and DBSCAN to cluster the cities. The
   latter must first be installed via the package manager. To do this, open it in the start menu via
   Tools > Package manager, search for optics_dbScan and install the package. If the clusterer
   DBSCAN does not appear as an option when you choose the clusterer, you might have to restart
   Weka before using it.
   Load the dataset into Weka, go to the Cluster tab and retain the standard evaluation settings for
   all experiments (Cluster mode = Use training set).*

> *1. Apply SimpleKMeans with k = 2 (in Weka: -N (numClusters)). Have a look at the computed
      clusters by right clicking in the result list and selecting Visualize cluster assignments
      (with longitude as x-axis and latitude as y-axis). Repeat the clustering with different
      values for k, compare the results and find an appropriate value for k. For this k, apply
      the cluster with three more different seeds. Does the result change? If yes, how much?*
>
TODO

> *2. Now use DBSCAN to cluster the cities. If you use the standard settings of DBSCAN you will
      just get a single cluster. Adjust the parameters -E (epsilon) and -M (minPoints) to get a
      more appropriate outcome (Hint: You have to decrease epsilon). Compare the number
      of clusters and the number of unclustered instances. What is the best configuration in
      your opinion? Compute for the "best" number of clusters the outcome of SimpleKMeans
      and compare the results.*

TODO
