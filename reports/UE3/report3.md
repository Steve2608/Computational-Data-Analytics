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
   uses an incremental approach suitable for stream mining. Create a new random dataset by choosing Generate in the main tab. Choose the generator RandomRBF and run it with the standard settings. Compare the classification accuracies, and the time taken to build model of J48 and HoeffdingTree on the generated dataset. Repeat the classification for more generated datasets of bigger size by increasing -n (numExamples) (for example,100, 250, 500, 1,000, ..., 100,000). For the three biggest datasets, also compare the size of the trees, and the total execution time (in seconds; you can find the start and end time in the log).What conclusions about the classifiers can be drawn from the performances?*

TODO
 


> *1. What conclusions about the classifiers can be drawn from the performances?*

TODO

> *2. For the three biggest datasets, also compare the size of
      the trees and the total execution time (in seconds; you can find the start and end time in the log).*

TODO






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
