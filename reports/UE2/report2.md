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

## Association Rule Learning (2 P.)

> *Open the dataset `weather.nominal` in `Weka`, go to the Associate tab and choose the `Apriori` algorithm. Customize `Apriori` by setting `outputItemSets` to `True` and `numRules` to a sufficiently high number (e.g. `999999`) to obtain all itemsets and rules in the output. For the first run of the `Apriori` algorithm, set `lowerBoundMinSupport` (minimum support) to `0.1` and `minMetric` (minimum confidence) to `1`. The output first lists all frequent itemsets with their frequency and then all built associated rules sorted by confidence.*

> *1. How many frequent k-tuples exist for each k?*

```
Size of set of large itemsets L(1): 12
Size of set of large itemsets L(2): 54
Size of set of large itemsets L(3): 96
Size of set of large itemsets L(4): 64
Size of set of large itemsets L(5): 14
```

> *1. In this special case, when a minimal support of just one instance is needed, how do you interpret these sets with minimum and maximum *k* and their size?*

For `k=1` the size of the itemsets corresponds to the sum of the  number of unique values that have been assumed for each column (=attribute).

For `k=n_col` the size of the itemsets corresponds to the cardinality of the set containing all instances (=records). 

\newpage

> *2. Test different parameter combinations by increasing the support and decreasing the confidence. Compare the total number of itemsets and association rules and justify which configuration(s) is/are the most suitable.*

For the default parametrization, we have `336` rules overall. 

---

Increasing the number of `lowerBoundMinSupport` serves as "trimming" mechanic. For this dataset, however, (14 instances only) values, we believe that at least 2 records need to agree to count as a rule. Just 1 record seems unreasonable since we have to assume the data contains some noise. 

For example the rule

```
outlook=rainy temperature=cool windy=FALSE 1 ==> humidity=normal play=yes 1
    <conf:(1)> lift:(2.33) lev:(0.04) [0] conv:(0.57)
```

probably does not hold for every case and therefore we should try to select fewer rules over all. 

\>2 is probably too high, since we have so few instances. Thus we used `lowerBoundMinSupport=2/14`

---

For `minMetric` we observe a similar pattern; high values are prone to over-fitting behavior.

Arguably, a classifier should be at least `>50%` sure for very low dimensional categorical data. Only permitting rules with a certainty of `=100%`, however, would in turn lead to a loss of generalisation capabilities. Thus, we chose `minMetric=0.75` 

---

The new parametrization (`lowerBoundMinSupport=2/14`, `minMetric=0.75`) results in `67` rules. 

> *2. What are the best rules with respect to confidence, lift and leverage?*

See [rasbt.github.io](https://rasbt.github.io/mlxtend/user_guide/frequent_patterns/association_rules/) for explanation of terms *confidence*, *lift*, *leverage*. 

- *confidence*: There are `58/67` rules with `confidence=1`, so there is no clear "best" rule
- *lift*: 
```
38. temperature=hot play=no 2 ==> outlook=sunny humidity=high 2
    <conf:(1)> lift:(4.67) lev:(0.11) [1] conv:(1.57)
```
Rule 15 has the highest *lift* meaning that `temperature=hot play=no 2 ==> outlook=sunny humidity=high 2` is `3.5` times more likely than if `temperature=hot play=no 2` and `outlook=sunny humidity=high 2` were statistically independent. 

- *leverage*: 
```
2. temperature=cool 4 ==> humidity=normal 4
    <conf:(1)> lift:(2) lev:(0.14) [2] conv:(2)
```
Rule 2 has the highest *leverage*.

> *2. Which three rules do you find the most interesting and surprising?*

```
5. outlook=sunny humidity=high 3 ==> play=no 3
    <conf:(1)> lift:(2.8) lev:(0.14) [1] conv:(1.93)
```

Rule 5 shows that in 100% of the cases, there was no play `play=no`, when it was sunny and very humid (`outlook=sunny`, `humidity=high`). 

```
7. outlook=rainy windy=FALSE 3 ==> play=yes 3
    <conf:(1)> lift:(1.56) lev:(0.08) [1] conv:(1.07)
```

Rule 7 was surprising to me, as even if it was rainy (`outlook=rainy`) but not windy (`windy=FALSE`) there would be a play.

```
17. temperature=hot play=yes 2 ==> outlook=overcast 2
    <conf:(1)> lift:(3.5) lev:(0.1) [1] conv:(1.43)
```

Rule 17 is interesting, as it shows that whenever there was a play while it is hot (`play=yes`, `temperature=hot`) it was overcast as well (`outlook=overcast`). 

> *2. Also list three rules that receive a high evaluation, but do not seem to be interesting*

```
37. outlook=sunny temperature=hot humidity=high 2 ==> play=no 2
    <conf:(1)> lift:(2.8) lev:(0.09) [1] conv:(1.29)
```

Rule 37 for example is pretty much not interesting, since it is just a more specific version of rule 5

```
1. outlook=overcast 4 ==> play=yes 4
    <conf:(1)> lift:(1.56) lev:(0.1) [1] conv:(1.43)
```

Arguably, the best rule is not all that interesting, since it has a comparatively low *lift* score (does not provide that much insight) and might not generalise that well.

```
16. outlook=overcast temperature=hot 2 ==> windy=FALSE 2
    <conf:(1)> lift:(1.75) lev:(0.06) [0] conv:(0.86)
```

Rule 16 is just generally uninteresting.

\newpage

> *3. Repeat the previous subtask with the dataset `supermarket` and collect again the best and/or most interesting rules you obtained.*

The supermarket dataset consists of 4,627 instances, thus there will be no/few rules with `conf=1`. Also, since it consists of 216 (binary!) attributes, so there will be a lot of rules. 

We try to limit the number of rules by adjusting `lowerBoundMinSupport=0.25`.

---

In total this gives us `105` rules. 

> *3. What are the best rules with respect to confidence, lift and leverage?*

- *confidence*: 
```
1. biscuits=t fruit=t vegetables=t 1404 ==> bread and cake=t 1216
    <conf:(0.87)> lift:(1.2) lev:(0.04) [205] conv:(2.08)
```

Rule 1 has the highest *confidence* at 87%.

- *lift*: 
```
103. total=high 1679 ==> frozen foods=t 1273
    <conf:(0.76)> lift:(1.29) lev:(0.06) [287] conv:(1.7)
```
Rule 103 has the highest *lift*.

- *leverage*: 
```
81. bread and cake=t vegetables=t 2298 ==> fruit=t 1791
    <conf:(0.78)> lift:(1.22) lev:(0.07) [319] conv:(1.63)
```
Rule 81 has the highest *leverage*

> *3. Which three rules do you find the most interesting and surprising?*

```
103. total=high 1679 ==> frozen foods=t 1273
    <conf:(0.76)> lift:(1.29) lev:(0.06) [287] conv:(1.7)
```

Rule 103 is interesting because it states, that the overall spending are high (`total=high`), when frozen foods get bought (`frozen food=t`). 

```
51. bread and cake=t frozen foods=t vegetables=t 1548 ==> fruit=t 1242
    <conf:(0.8)> lift:(1.25) lev:(0.05) [251] conv:(1.81)

52. bread and cake=t frozen foods=t fruit=t 1548 ==> vegetables=t 1242
    <conf:(0.8)> lift:(1.25) lev:(0.05) [251] conv:(1.82)
```

Rules 51 and 52 also show, how `bread and cake`, `frozen foods`, `vegetables` and `fruit` get bought together a lot.

> *3. Which three rules do you find the most interesting and surprising?*

```
1. biscuits=t fruit=t vegetables=t 1404 ==> bread and cake=t 1216
    <conf:(0.87)> lift:(1.2) lev:(0.04) [205] conv:(2.08)
2. frozen foods=t fruit=t vegetables=t 1451 ==> bread and cake=t 1242
    <conf:(0.86)> lift:(1.19) lev:(0.04) [197] conv:(1.94)
3. baking needs=t milk-cream=t fruit=t 1365 ==> bread and cake=t 1161
    <conf:(0.85)> lift:(1.18) lev:(0.04) [178] conv:(1.87)
4. margarine=t fruit=t 1538 ==> bread and cake=t 1301
    <conf:(0.85)> lift:(1.18) lev:(0.04) [194] conv:(1.81)
5. biscuits=t vegetables=t 1764 ==> bread and cake=t 1487
    <conf:(0.84)> lift:(1.17) lev:(0.05) [217] conv:(1.78)
6. baking needs=t fruit=t vegetables=t 1489 ==> bread and cake=t 1255
    <conf:(0.84)> lift:(1.17) lev:(0.04) [183] conv:(1.78)
7. tissues-paper prd=t milk-cream=t 1514 ==> bread and cake=t 1275
    <conf:(0.84)> lift:(1.17) lev:(0.04) [185] conv:(1.77)
8. total=high 1679 ==> bread and cake=t 1413
    <conf:(0.84)> lift:(1.17) lev:(0.04) [204] conv:(1.76)
9. biscuits=t milk-cream=t 1767 ==> bread and cake=t 1485
    <conf:(0.84)> lift:(1.17) lev:(0.05) [213] conv:(1.75)
10. baking needs=t milk-cream=t vegetables=t 1392 ==> bread and cake=t 1169
    <conf:(0.84)> lift:(1.17) lev:(0.04) [167] conv:(1.74)
11. biscuits=t fruit=t 1837 ==> bread and cake=t 1541
    <conf:(0.84)> lift:(1.17) lev:(0.05) [218] conv:(1.73)
12. milk-cream=t margarine=t 1549 ==> bread and cake=t 1299
    <conf:(0.84)> lift:(1.17) lev:(0.04) [184] conv:(1.73)
13. biscuits=t margarine=t 1493 ==> bread and cake=t 1251
    <conf:(0.84)> lift:(1.16) lev:(0.04) [176] conv:(1.72)
14. milk-cream=t fruit=t vegetables=t 1571 ==> bread and cake=t 1311
    <conf:(0.83)> lift:(1.16) lev:(0.04) [180] conv:(1.69)
15. biscuits=t frozen foods=t 1810 ==> bread and cake=t 1510
    <conf:(0.83)> lift:(1.16) lev:(0.04) [207] conv:(1.69)
16. margarine=t vegetables=t 1587 ==> bread and cake=t 1322
    <conf:(0.83)> lift:(1.16) lev:(0.04) [179] conv:(1.67)
17. biscuits=t tissues-paper prd=t 1453 ==> bread and cake=t 1209
    <conf:(0.83)> lift:(1.16) lev:(0.04) [163] conv:(1.66)
18. frozen foods=t fruit=t 1861 ==> bread and cake=t 1548
    <conf:(0.83)> lift:(1.16) lev:(0.05) [208] conv:(1.66)
19. frozen foods=t milk-cream=t 1826 ==> bread and cake=t 1516
    <conf:(0.83)> lift:(1.15) lev:(0.04) [201] conv:(1.65)
20. party snack foods=t fruit=t 1592 ==> bread and cake=t 1321
    <conf:(0.83)> lift:(1.15) lev:(0.04) [175] conv:(1.64)
21. tissues-paper prd=t vegetables=t 1559 ==> bread and cake=t 1293
    <conf:(0.83)> lift:(1.15) lev:(0.04) [171] conv:(1.64)
22. baking needs=t milk-cream=t 1907 ==> bread and cake=t 1580
    <conf:(0.83)> lift:(1.15) lev:(0.04) [207] conv:(1.63)
23. tissues-paper prd=t fruit=t 1567 ==> bread and cake=t 1297
    <conf:(0.83)> lift:(1.15) lev:(0.04) [169] conv:(1.62)
24. party snack foods=t milk-cream=t 1541 ==> bread and cake=t 1275
    <conf:(0.83)> lift:(1.15) lev:(0.04) [165] conv:(1.62)
25. milk-cream=t fruit=t 2038 ==> bread and cake=t 1684
    <conf:(0.83)> lift:(1.15) lev:(0.05) [217] conv:(1.61)
26. baking needs=t margarine=t 1645 ==> bread and cake=t 1358
    <conf:(0.83)> lift:(1.15) lev:(0.04) [174] conv:(1.6)
27. baking needs=t biscuits=t 1764 ==> bread and cake=t 1456
    <conf:(0.83)> lift:(1.15) lev:(0.04) [186] conv:(1.6)
28. frozen foods=t tissues-paper prd=t 1505 ==> bread and cake=t 1239
    <conf:(0.82)> lift:(1.14) lev:(0.03) [155] conv:(1.58)
29. baking needs=t fruit=t 1900 ==> bread and cake=t 1564
    <conf:(0.82)> lift:(1.14) lev:(0.04) [196] conv:(1.58)
30. sauces-gravy-pkle=t fruit=t 1490 ==> bread and cake=t 1226
    <conf:(0.82)> lift:(1.14) lev:(0.03) [153] conv:(1.58)
31. baking needs=t tissues-paper prd=t 1573 ==> bread and cake=t 1294
    <conf:(0.82)> lift:(1.14) lev:(0.03) [161] conv:(1.57)
32. frozen foods=t vegetables=t 1882 ==> bread and cake=t 1548
    <conf:(0.82)> lift:(1.14) lev:(0.04) [193] conv:(1.57)
33. biscuits=t party snack foods=t 1592 ==> bread and cake=t 1306
    <conf:(0.82)> lift:(1.14) lev:(0.03) [160] conv:(1.55)
34. baking needs=t party snack foods=t 1530 ==> bread and cake=t 1254
    <conf:(0.82)> lift:(1.14) lev:(0.03) [152] conv:(1.55)
35. milk-cream=t vegetables=t 2025 ==> bread and cake=t 1658
    <conf:(0.82)> lift:(1.14) lev:(0.04) [200] conv:(1.54)
36. sauces-gravy-pkle=t milk-cream=t 1487 ==> bread and cake=t 1216
    <conf:(0.82)> lift:(1.14) lev:(0.03) [145] conv:(1.53)
37. bread and cake=t biscuits=t vegetables=t 1487 ==> fruit=t 1216
    <conf:(0.82)> lift:(1.28) lev:(0.06) [264] conv:(1.97)
38. party snack foods=t vegetables=t 1559 ==> bread and cake=t 1273
    <conf:(0.82)> lift:(1.13) lev:(0.03) [151] conv:(1.52)
39. juice-sat-cord-ms=t biscuits=t 1542 ==> bread and cake=t 1259
    <conf:(0.82)> lift:(1.13) lev:(0.03) [149] conv:(1.52)
40. frozen foods=t margarine=t 1531 ==> bread and cake=t 1249
    <conf:(0.82)> lift:(1.13) lev:(0.03) [147] conv:(1.52)
41. juice-sat-cord-ms=t milk-cream=t 1637 ==> bread and cake=t 1334
    <conf:(0.81)> lift:(1.13) lev:(0.03) [155] conv:(1.51)
42. sauces-gravy-pkle=t vegetables=t 1558 ==> bread and cake=t 1268
    <conf:(0.81)> lift:(1.13) lev:(0.03) [146] conv:(1.5)
43. baking needs=t vegetables=t 1949 ==> bread and cake=t 1586
    <conf:(0.81)> lift:(1.13) lev:(0.04) [183] conv:(1.5)
44. fruit=t vegetables=t 2207 ==> bread and cake=t 1791
    <conf:(0.81)> lift:(1.13) lev:(0.04) [202] conv:(1.48)
45. juice-sat-cord-ms=t fruit=t 1672 ==> bread and cake=t 1356
    <conf:(0.81)> lift:(1.13) lev:(0.03) [152] conv:(1.48)
46. frozen foods=t party snack foods=t 1582 ==> bread and cake=t 1283
    <conf:(0.81)> lift:(1.13) lev:(0.03) [144] conv:(1.48)
47. sauces-gravy-pkle=t frozen foods=t 1497 ==> bread and cake=t 1214
    <conf:(0.81)> lift:(1.13) lev:(0.03) [136] conv:(1.48)
48. baking needs=t frozen foods=t 1835 ==> bread and cake=t 1485
    <conf:(0.81)> lift:(1.12) lev:(0.04) [164] conv:(1.47)
49. baking needs=t sauces-gravy-pkle=t 1521 ==> bread and cake=t 1223
    <conf:(0.8)> lift:(1.12) lev:(0.03) [128] conv:(1.43)
```

Basically the first 49 rules are uninteresting, as they all are rules for `bread and cake=t`.

> *3. Compare and interpret the settings that you think are the most suitable for the two datasets.*

| Dataset     | lowerBoundMinSupport | minMetric |
|-------------|----------------------|-----------|
| WEATHER     | 0.142857             | 0.75      |
| SUPERMARKET | 0.25                 | 0.75      |

For the smaller dataset, we have chosen a lower `lowerBoundMinSupport` to even let it find more than a hand full rules. However, for bigger datasets we can increase this value to filter "meaningless" rules. 

Interestingly, we have also set `minMetric` to `0.75` for both datasets, which actually is "coincidental" since it works well for both datasets; also both datasets only have very few possible values per column (`SUPERMARKET` has only binary columns). 

For high dimensional datasets, where each column has a multitude of values to assume, `minMetric` would have to be reduced accordingly.