# Exercise Part 1 of 3 - KV Computational Data Analytics (351.008)
Summer term 2020 - Prof. Dr. Johannes Fürnkranz, Florian Beck

Deadline: May 3, 23:59

**Group Members**

| Student ID    | First Name  | Last Name      |
| --------------|-------------|----------------|
| 01555580      | Peter       | Chalupar       |
| 01556188      | Alexander   | Raschl         |
| TODO      | TODO       | TODO     |

> *The submission should be in the form of a self-explanatory presentation (e.g. PDF, OpenOffice or PowerPoint) with a focus on interpretation or analysis.*

## 1 Rule Learning: Application and Interpretation (2 P.)

> *In this exercise you will compare JRip, JRip without pruning (by setting usePruning=False) and ConjunctiveRule on different datasets. Therefore, choose ten datasets and try to have as much variance in the number of instances, the number of attributes and the attribute data types as possible.*

**Datasets:**

| Dataset | # Instances | # Attributes¹ | # Continuous¹ | # Discrete | # Classes |
| ------- |------------ | ------------- | ------------- | ---------- | --------- |
| labor | 75 | 16 | 8 | 8 | 2 |
| iris | 150 | 4 | 4 | 0 | 3 |
| vote | 435 | 16 | 0 | 16 (all boolean) | 2 |
| wine | 178 | 13 | 13 | 0 | 3 |
| diabetes | 768 | 8 | 8 | 0 | 2 |
| breast-cancer | 286 | 10 | 0 | 10 | 2 |
| credit-g | 1000 | 21 | 7 | 13 | 2 |
| car | 1728 | 6 | 0 | 6 | 4 |
| contact-lenses | 24 | 4 | 0 | 4 | 3 |
| hypothyroid | 3772 | 29 | 7 | 22 | 4 |

***¹**Without Class Attribute*

> *1. Compare the number of rules, conditions and predicted classes of the resulting rule sets with respect to:*
> - *the datasets*
> - *the rule classifiers*

**Number of Rules per Classifier and Dataset (including default rule):**
| Dataset         | JRip            | JRip noPruning  | ConjunctiveRule |
| --------------- | --------------- | --------------- | --------------- |
| labor-neg-data  | 4               | 4               | 1               |
| iris            | 3               | 5               | 1               |
| vote            | 4               | 10              | 1               |
| wine            | 5               | 4               | 1               |
| pima_diabetes   | 4               | 11              | 1               |
| breast-cancer   | 3               | 5               | 1               |
| german_credit   | 3               | 13              | 1               |
| car             | 49              | 97              | 1               |
| contact-lenses  | 3               | 4               | 1               |
| hypothyroid     | 5               | 11              | 1               |

**Average number of Conditions er Classifier and Dataset (including default rule):**
| Dataset         | JRip            | JRip noPruning  | ConjunctiveRule |
| --------------- | --------------- | --------------- | --------------- |
| labor-neg-data  | 1,00            | 1,00            | 1               |
| iris            | 1,33            | 1,33            | 1               |
| vote            | 1,50            | 1,50            | 1               |
| wine            | 1,40            | 1,40            | 2               |
| pima_diabetes   | 2,25            | 2,25            | 1               |
| breast-cancer   | 1,33            | 1,33            | 0               |
| german_credit   | 1,67            | 1,67            | 0               |
| car             | 3,98            | 3,98            | 0               |
| contact-lenses  | 1,00            | 1,00            | 0               |
| hypothyroid     | 2,20            | 2,20            | 3               |

> *2. Is there a default rule for all algorithms? If so:*
> - *Which class is usually chosen as default rule?*
> - *How do you interpret the quality of the default rule?*

> *3. On the basis of the previous subtasks, can you make a statement which of the datasets is the easiest or best to learn?*

> *4. Perform a Friedman-Nemenyi test on the results and check whether there is a significant difference between the performance of the classifiers.*

<pre>
Dataset         JRip            JRip noPruning  ConjunctiveRule
labor-neg-data  85,964912 (2)   89,473684 (1)   75,438596 (3)
iris            93,333333 (1)   93,333333 (1)   66,666667 (3)
vote            95,172414 (2)   95,172414 (2)   95,402299 (1)
wine            89,887640 (2)   91,573034 (1)   63,483146 (3)
pima_diabetes   75,651042 (1)   72,265625 (2)   69,401042 (3)
breast-cancer   73,426573 (1)   73,426573 (1)   71,678322 (3)
german_credit   71,500000 (2)   72,900000 (1)   70,000000 (3)
car             86,111111 (2)   88,194444 (1)   70,023148 (3)
contact-lenses  79,166667 (1)   70,833333 (2)   54,166667 (3)
hypothyroid     99,443266 (1)   99,178155 (2)   97,083775 (3)
---------------------------------------------------------------
AVG RANK        1,500000        1,400000        2,800000
</pre>

<pre>
Perform the Friedman statistics test:
E.g. k = 3, N = 10
Chi²F = 12N / k(k+1)  *  ( SUM(avgRanks²) - k(k+1)² / 4 )
Chi²F = 12*10 / 3*4  *  ( SUM(avgRanks²) - 3*4 / 4 )
Sum(avgRanks²) = 12,050000 (from table above)
Chi²F = 8.0
Chi²(0.95,2) = 5.991   -> see https://people.richland.edu/james/lecture/m170/tbl-chi.html

χ²(0.95;2) = 5,991000 < 8,000000 = χ²F
Null hypotheses successfully rejected with p = 0.95!
</pre>

<pre>
Perform the Nemenyi post-hoc test: 
(which can be performed because the null hypothesis of the Friedman is rejected)
q_alpha_0.05_#c3 = 2.343
Critical Distance between pairs of avgRanks:
CD = q_alpha * Math.sqrt(k * (k + 1) / (6 * n))
CD = 1,047821
</pre>

With a CD of ~1.05 we can show that both JRip Classifiers are significantly better than ConjunctiveRule. As 2.8 > 1.5 + 1.05 (or 1.04). However JRip with pruning is NOT significant better than JRip without pruning.

*Java Code, which we used to calculate the Friedman statistics and CD:*
```java
private static void performFriedmanNemenyiTests(final double[] avgRanks, final double n) {
  System.out.println("Perform the Friedman statistics test:");
  final double k = avgRanks.length;
  final double sumAvgRanks2 = DoubleStream.of(avgRanks).map(a -> a * a).sum();
  System.out.println(String.format("Sum(avgRanks²) = %f", sumAvgRanks2));
  final double chi2F = 12 * n / k * (k + 1) * (sumAvgRanks2 - k * (k + 1) * (k + 1) / 4);
  final double chi2 = 5.991; // Chi²(0.95,k-1)
  if (chi2 < chi2F) {
    System.out.println(String.format("χ²(0.95;2) = %f < %f = χ²F", chi2, chi2F));
    System.out.println("Null hypotheses successfully rejected with p = 0.95!");

    System.out.println("Perform the Nemenyi post-hoc test: (which can be performed because the null hypothesis of the Friedman is rejected)");
    System.out.println("q_alpha_0.05_#c3 = 2.343 ");
    final double q_alpha = 2.343;
    final double CD = q_alpha * Math.sqrt(k * (k + 1) / (6 * n)); // Critical Distance between pairs of avgRanks
    System.out.println(String.format("CD = %f", CD));
  } else {
    System.out.println(String.format("χ²(0.95;2) = %f >= %f = χ²F", chi2, chi2F));
    System.out.println("Null hypotheses could NOT be rejected with p = 0.95!");
  }
}
```





## 3 Evaluation Methods (2 P.)

> *In this task different evaluation methods using Weka are to be applied and their results discussed. Apply the rule classifier JRip to five datasets (e.g. those from task 1) by first dividing each dataset into two equal stratified parts, a training set and a validation set.*  

 We chose the 5 Datasets which had the most samples among the datasets provided by Weka by default.

> *1. Now train JRip on each of these training sets and evaluate the accuracy (percentage of
     correctly classified examples) of the resulting classifiers (without changing customized
     options like random seed) using:*
> - *1x5 cross-validation*
> - *1x10 cross-validation*
> - *1x20 cross-validation*
> - *leave-one-out*
> - *the training set itself*

The following table shows the achieved accuracies using the mentioned validation method:

<pre>
Dataset                  1x5 CV         1x10 CV         1x20 CV           LOOCV            Self
supermarket             74,330%         75,281%         75,756%         75,238%         83,967%
hypothyroid             99,046%         98,993%         98,993%         98,993%         99,205%
segment                 90,000%         90,533%         91,467%         90,533%         96,667%
german_credit           69,200%         72,400%         73,200%         72,200%         75,000%
unbalanced              98,598%         98,598%         98,598%         98,364%         98,598%
</pre>

> *How do you assess the quality of the accuracy estimates obtained?*  

At first, it can be observed that evaluating our model using the train set (*Self*) results in the highest obtained accuracies. 
This is not surprising as the model has already seen the train data during the training process, 
thus it is easier for the model to correctly classify seen data.  
As a result, using the train set for evaluation leads to overly optimistic results and is generally considered as bad-practice.
  
Second, the accuracies provided by 1x20 CV and leave-one-out-CV (*LOOCV*) are quite the same. As the train datasets, 
are fairly small (at most 2300 samples) a split into 20 folds should already be a good approximation to leave-one-out-CV. 
 
The 5 fold cross validation is more separated from the  other methods, which can be explained by the relatively low amount of folds. 
Thus, the results are more dependent on the random splits, as there is also more *unseen* data present on each cross validation iteration.  

Interestingly, the accuracies obtained using the *unbalanced* dataset are fairly the same, which can explained by the overall class distribution, 
which contains exactly 98.598% of negatives and only 1,4% positive examples. 
 
To sum this up, we would either pick the 1x20 CV or the leave-one-out-CV if we have enough computing resources as our evaluation method of choice.

> *2. Repeat the previous steps using:*
> - *10x10 cross-validation*
> - *5x2 cross-validation*
> - *Compare the accuracy estimates obtained in
    this way with the estimates from the previous task. In your opinion, does a smart selection
    of random seeds lead to a better estimation?*   

In the following, we see that the 5x2 CV results in large differences in accuracy compared to our preferred validation methods from before. 
Only the results for the unbalanced dataset are the same, 
as the learned ruleset is possibly exactly the same for all validation methods due to the highly skewed class distribution. 

<pre>
Dataset                  1x5 CV         1x10 CV         1x20 CV           LOOCV            Self        10x10 CV          5x2 CV
supermarket             74,330%         75,281%         75,756%         75,238%         83,967%         75,065%         73,526%
hypothyroid             99,046%         98,993%         98,993%         98,993%         99,205%         99,125%         99,024%
segment                 90,000%         90,533%         91,467%         90,533%         96,667%         89,987%         86,827%
german_credit           69,200%         72,400%         73,200%         72,200%         75,000%         71,540%         70,880%
unbalanced              98,598%         98,598%         98,598%         98,364%         98,598%         98,598%         98,598%
</pre>
 
The main reason for multiple cross validation computations is to reduce the impact of the initial split into *N* folds.  
Thus, if it happens that a "bad" split is made when using only 1x10 CV, this can result in slightly misleading results.   
If using 10x10 CV, 10 separate splits are made and thus a single "bad" split would not have a major impact.  
The mentioned differences of the 5x2 CV are caused by only using 2 folds for each split, which can result in highly biased folds. 
As a result, 10x10 cross validation should definitely be preferred over 5x2 cross validation.   

Furthermore, we think that a _smart_ selection of random seeds should actually not lead to better estimation results, 
if a proper random sampling is made using this seed. 


> *3. Determine the accuracy on the validation set by using it as a test set. Assuming that
      the validation set is a real use case, how do you assess the estimates of the evaluation
      methods from the previous two tasks?*  

TODO


<pre>
Dataset                  1x5 CV         1x10 CV         1x20 CV           LOOCV            Self        10x10 CV          5x2 CV      Validation
supermarket             74,330%         75,281%         75,756%         75,238%         83,967%         75,065%         73,526%         79,490%
hypothyroid             99,046%         98,993%         98,993%         98,993%         99,205%         99,125%         99,024%         99,099%
segment                 90,000%         90,533%         91,467%         90,533%         96,667%         89,987%         86,827%         94,000%
german_credit           69,200%         72,400%         73,200%         72,200%         75,000%         71,540%         70,880%         71,500%
unbalanced              98,598%         98,598%         98,598%         98,364%         98,598%         98,598%         98,598%         98,598%
</pre>


> *4. Select a sufficiently large dataset of a binary classification problem and compare the ROC
   curve and AUC for __J48__ and __NaiveBayes__.*

We selected the diabetes dataset which consists of 768 samples being 500 negative and 268 positive samples.
We also perform a 50% Stratified Split in order to remain consistent with the previous tasks.

- __J48__
<pre>
=== Summary ===

Correctly Classified Instances         285               74.2188 %
Incorrectly Classified Instances        99               25.7813 %
Kappa statistic                          0.3806
Mean absolute error                      0.3102
Root mean squared error                  0.4605
Relative absolute error                 68.1752 %
Root relative squared error             97.3324 %
Total Number of Instances              384     
</pre>

<pre>
=== Detailed Accuracy By Class ===

                 TP Rate  FP Rate  Precision  Recall   F-Measure  MCC      ROC Area  PRC Area  Class
                 0,875    0,519    0,769      0,875    0,818      0,390    0,698     0,767     tested_negative
                 0,481    0,125    0,660      0,481    0,556      0,390    0,698     0,542     tested_positive
Weighted Avg.    0,742    0,387    0,732      0,742    0,730      0,390    0,698     0,691     
</pre>

<pre>
=== Confusion Matrix ===

   a   b   <-- classified as
 223  32 |   a = tested_negative
  67  62 |   b = tested_positive
</pre>

![J48 ROC curve](./img/J48_ROC_diabetes.PNG "J48 ROC Curve")



- __NaiveBayes__

<pre>
=== Summary ===

Correctly Classified Instances         290               75.5208 %
Incorrectly Classified Instances        94               24.4792 %
Kappa statistic                          0.4406
Mean absolute error                      0.2906
Root mean squared error                  0.4064
Relative absolute error                 63.8822 %
Root relative squared error             85.9146 %
Total Number of Instances              384     
</pre>

<pre>
=== Detailed Accuracy By Class ===

                 TP Rate  FP Rate  Precision  Recall   F-Measure  MCC      ROC Area  PRC Area  Class
                 0,835    0,403    0,804      0,835    0,819      0,441    0,825     0,898     tested_negative
                 0,597    0,165    0,647      0,597    0,621      0,441    0,825     0,731     tested_positive
Weighted Avg.    0,755    0,323    0,751      0,755    0,753      0,441    0,825     0,842     
</pre>

<pre>
=== Confusion Matrix ===

   a   b   <-- classified as
 213  42 |   a = tested_negative
  52  77 |   b = tested_positive
</pre>

![Naive Bayes ROC curve](./img/NaiveBayes_ROC_diabetes.PNG "Naive Bayes ROC Curve")

| Dataset   |   Method   |   AUC   |
| ----------|------------|---------|
| diabetes  | NaiveBayes | 0.8247  |
| diabetes  | J48        | 0.6978  |



Generally, it can be observed that the AUC is considerably higher when using __NaiveBayes__ for classification than using the __J48__.  
__NaiveBayes__ assigned 5 more data samples to the correct class than __J48__. 