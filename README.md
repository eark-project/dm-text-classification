# dm-text-classification

This works, but is still work in progress.

## TODOs

* verfiy that it works on the "real" cluster (currently tested on earkdev) 
** mainly the communication between Python and Java/MapReduce Jobs
** also find an easy way to provide the Python libraries (see below)
* change paths in the Mapper (-> py script locations)

## Usage

Launch the MapReduce Job with

    ./start.sh

Adapt the script if neccessary.

## General Information:

The provied model was trained on german/austrian newspaper articles, expect bad performance on out-of-domain data.

However, feel free to train your own models, according to the [scikit-learn documentation] (http://scikit-learn.org/stable/documentation.html).

An exemple script that was used to generate the newspaper model can be found in the /pyscript subfolder.

## Requirements

To run the Python script, the following packages need to be installed: `numpy`, `scipy` and `scikit-learn`:

Requirements:

    sudo apt-get install gfortran
    sudo apt-get install liblapack-dev

Packages:

    pip install numpy
    pip install scipy
    pip install -U scikit-learn

## Other:

Create a folder:

    /tmp/clfin

and set rights (while on the user that will execute the hadoop command):

    sudo chmod 1777 /tmp/clfin

The Python script and the model need to be placed according to the path inside the Mapper (need to adapt it), the script needs execute permissions:

    chmod 755 classifier.py