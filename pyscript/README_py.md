## How-To: create text classification models

This is a short introduction, for more in-depth information please refer to the [scikit-learn] (http://scikit-learn.org/stable/#) site.

### Training Data

* You need training data, which _must_ be in plain text (usually .txt) format.
** You can of course extract it from other sources, like HTML, XML or PDF.
* Define a set of categories, that fit the training data. Assign categories to the text files
in the following way:

    <root folder>
        |-- <category A>
            |--- <file 1>
            |--- <file ...>
            |--- <file n>
        |-- <category B>
            |--- <file 1>
            |--- <file ...>
            |--- <file n>

    The folder names <category> equal the names of the categories. Every file in a folder is assigned to this category. Filenames do not matter.

The number of files you need depends mostly on the number of categories and the algorithm used for classification.

* Never less than 50 samples (files).

The algorithm used here is [LinearSVC] (http://scikit-learn.org/stable/modules/generated/sklearn.svm.LinearSVC.html#sklearn.svm.LinearSVC),
which should scale well to very large datasets. 
Since it is a [SVM (support vector machine)] (http://scikit-learn.org/stable/modules/svm.html#classification), 
it should also work when the number of dimensions is higher than the number of samples.

* Ideally the number of categories is lower than the number of sample files.
* The more sample files and the bigger the difference between the samples, the better.

### Create the model

I trained using newspaper articles (hence the file name), but anything should work.

In the file `newspaper_model.py`, edit the following:

    data_folder = 'path/to/trainingdata/root/folder'

Running the script with only these changes will train a model and display the confusion matrix and the classifiers performance.

To save the model to disk, uncomment the following lines:

    clf.fit(dataset.data, dataset.target)
    joblib.dump(clf, 'newspapers.pkl')

Also `print dataset.target_names`. Copy the output of this; you need it later for

    classifier.py
    self.categories = []

so that the script returns the category names correctly.