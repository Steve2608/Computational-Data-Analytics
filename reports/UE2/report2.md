---
title: "Computational Data Analytics, UE2"
author: [Stefan Brandl, Peter Chalupar, Alexander Raschl]
date: \today{}
header-includes: 
	- \usepackage[all]{nowidow}
---

# Exercise Part 1 of 3 - KV Computational Data Analytics (351.008)
Summer term 2020 - Prof. Dr. Johannes Fürnkranz, Florian Beck

Deadline: May 3, 23:59

**Group Members**

| Student ID    | First Name  | Last Name      |
| --------------|-------------|----------------|
| 01555580      | Peter       | Chalupar       |
| 01556188      | Alexander   | Raschl         |
| 01555842      | Stefan      | Brandl         |

> *The submission should be in the form of a self-explanatory presentation (e.g. PDF, OpenOffice or PowerPoint) with a focus on interpretation or analysis.*

\newpage

## Pre-processing: Discretization (2 P.)

> *Choose among the datasets one with at least five numeric attributes. Create a discretized version
   of the dataset by applying the filter weka.filters.supervised.attribute.Discretize
   with standard parameters. Use 10-fold cross-validation with the same random seed for all
   experiments.*

We chose the glass dataset provided in 'glass.arff' to serve as our dataset of choice. It contains 9 numerical attributes and 214 samples.
 


> *1. To how many different bins are the previously numeric variables distributed?*

* **Standard Discretizer**  
The 9 attribues were split into a total of 22 bins:  
```
	Attribute RI now has 3 bins:
		'(-inf-1.517335]'
		'(1.517335-1.517985]'
		'(1.517985-inf)'

	Attribute Na now has 2 bins:
		'(-inf-14.065]'
		'(14.065-inf)'

	Attribute Mg now has 2 bins:
		'(-inf-2.695]'
		'(2.695-inf)'

	Attribute Al now has 3 bins:
		'(-inf-1.39]'
		'(1.39-1.775]'
		'(1.775-inf)'

	Attribute Si now has 1 bins:
		'All'

	Attribute K now has 4 bins:
		'(-inf-0.055]'
		'(0.055-0.615]'
		'(0.615-0.745]'
		'(0.745-inf)'

	Attribute Ca now has 4 bins:
		'(-inf-7.02]'
		'(7.02-8.315]'
		'(8.315-10.075]'
		'(10.075-inf)'

	Attribute Ba now has 2 bins:
		'(-inf-0.335]'
		'(0.335-inf)'

	Attribute Fe now has 1 bins:
		'All'
```

* **Binary Discretizer** 
The 9 attribues were split into a total of 16 bins: 
```
    	Attribute RI now has 2 bins:
    		'(-inf-1.517335]'
    		'(1.517335-inf)'
    
    	Attribute Na now has 2 bins:
    		'(-inf-14.065]'
    		'(14.065-inf)'
    
    	Attribute Mg now has 2 bins:
    		'(-inf-2.695]'
    		'(2.695-inf)'
    
    	Attribute Al now has 2 bins:
    		'(-inf-1.39]'
    		'(1.39-inf)'
    
    	Attribute Si now has 1 bins:
    		'All'
    
    	Attribute K now has 2 bins:
    		'(-inf-0.055]'
    		'(0.055-inf)'
    
    	Attribute Ca now has 2 bins:
    		'(-inf-7.02]'
    		'(7.02-inf)'
    
    	Attribute Ba now has 2 bins:
    		'(-inf-0.335]'
    		'(0.335-inf)'
    
    	Attribute Fe now has 1 bins:
    		'All'
```


> *2. Compare the accuracy of J48 on the original and the discretized dataset.*
* **Standard Discretizer**  
After applying a 10 Fold Cross Validation we end up with the following results: 
```
J48: Accuracy= 67,757
J48_Discretized_Data: Accuracy= 76,168
```
* **Binary Discretizer** 
After applying a 10 Fold Cross Validation we end up with the following results: 
```
J48: Accuracy= 67,757
J48_Discretized: Accuracy= 72,430
```

> *3. The meta-classifier FilteredClassifier combines a pre-processing method with a classifier
      to a new classifier which executes the pre-processing during the training process.
      Compare the accuracy of the combination Discretize and J48 on the original dataset
      with the previous results. How do you interpret the quality of the accuracies and the size
      of the learned trees?*  
>
Overall Tree Comparison:

| MakeBinary  |  Classifier   |  Data         |  Size   | Leaves  | Accuracy (10-CV)|
|-------------|---------------|---------------|---------|---------|---------------|
|   False     |      J48      | original      |   59    |   30    |  67,757  |
|   False     |      J48      | discretized   |   59    |   30    |**76,168**|
|   False     |  FilteredClf  | original      |   42    |   29    |  71,963  |
|   True      |      J48      | original      |   59    |   30    |  67,757  |
|   True      |      J48      | discretized   |   59    |   30    |  72,430  |
|   True      |  FilteredClf  | original      | **33**  | **17**  |  71,495  |

Looking at the table it can be observed that the discretized version achieve a major buff regarding the accuracy estimates. However, it needs to be noted that the discretizer has been applied to the whole Dataset, before splitting the dataset into folds during 10-Fold Cross validation. 
 
As the discretizer as a supervised approach uses the label/class information of the data samples this should be generally avoided, as the classifier that is later on applied to the training dataset has indirect knowledge of the samples in the test set. 
Thus, the resulting estimates for the discretized approach with latter 10 Fold Cross validation are probably slightly too high.  

The recommended way of estimating the performance of a pipeline including discretization as preprocessing step and a classifier is to split the dataset into folds at first, according to the 10-fold CV, and afterwards apply the discretization and train the classifier on the training folds only, whilst later on testing on the test fold, which is completely separated from the classification pipeline from the beginning. 
This of course, results in slightly lower accuracy estimates, as shown in the table above, but suggests more accurate estimate. 

The actual size of the learned trees pretty much remains the same, the only exceptions are the trees resulting from learning the Filtered Classification Pipeline. Without binary discretization we get a tree-size of 42 and with a tree-size of only 33 with only 17 leaves, which is quite few compared to the other trained models. Trees learned from binary discretized date tend to have slightly less learned rules simply because the number of splits to choose from is reduced by the binary discretization. As the number of splits cannot be chosen at the same granular level as it would be possible using the normal discretization approach, the estimated accuracy can suffer a little bit.

The discretization approach could even be considered as a anti-overfitting approach for decision trees, which could be applied instead of error-pruning after the tree has been built as it leads to simpler trees during training.  
```
Classifier Model
J48 pruned tree
------------------

Ba_1 = '(-inf-0.335]'
|   Mg_1 = '(-inf-2.695]'
|   |   K_1 = '(-inf-0.055]'
|   |   |   Na_1 = '(-inf-14.065]'
|   |   |   |   Ca_3 = '(-inf-10.075]': tableware (2.0)
|   |   |   |   Ca_3 = '(10.075-inf)': build wind non-float (3.0)
|   |   |   Na_1 = '(14.065-inf)': tableware (7.0)
|   |   K_1 = '(0.055-inf)'
|   |   |   Al_1 = '(-inf-1.39]': build wind non-float (6.0/1.0)
|   |   |   Al_1 = '(1.39-inf)'
|   |   |   |   Ca_3 = '(-inf-10.075]'
|   |   |   |   |   Al_2 = '(-inf-1.775]': build wind non-float (2.0)
|   |   |   |   |   Al_2 = '(1.775-inf)': containers (4.0/1.0)
|   |   |   |   Ca_3 = '(10.075-inf)': containers (11.0/2.0)
|   Mg_1 = '(2.695-inf)'
|   |   Ca_2 = '(-inf-8.315]'
|   |   |   RI_1 = '(-inf-1.517335]': build wind non-float (38.0/4.0)
|   |   |   RI_1 = '(1.517335-inf)'
|   |   |   |   RI_2 = '(-inf-1.517985]': build wind float (7.0)
|   |   |   |   RI_2 = '(1.517985-inf)': build wind non-float (7.0/1.0)
|   |   Ca_2 = '(8.315-inf)'
|   |   |   RI_1 = '(-inf-1.517335]'
|   |   |   |   Al_1 = '(-inf-1.39]': vehic wind float (13.0/6.0)
|   |   |   |   Al_1 = '(1.39-inf)': build wind non-float (5.0/1.0)
|   |   |   RI_1 = '(1.517335-inf)'
|   |   |   |   RI_2 = '(-inf-1.517985]': build wind float (32.0/6.0)
|   |   |   |   RI_2 = '(1.517985-inf)'
|   |   |   |   |   K_2 = '(-inf-0.615]'
|   |   |   |   |   |   Al_1 = '(-inf-1.39]': build wind float (41.0/14.0)
|   |   |   |   |   |   Al_1 = '(1.39-inf)': build wind non-float (4.0/1.0)
|   |   |   |   |   K_2 = '(0.615-inf)': build wind non-float (3.0)
Ba_1 = '(0.335-inf)': headlamps (29.0/3.0)

Number of Leaves  : 	17

Size of the tree : 	33
```


> *4. To obtain ordered bins, change the configuration of Discretize by setting makeBinary to
      True. Repeat the experiments and compare the results.*

Answers to *4.* already included in the answers of the points before respectively.