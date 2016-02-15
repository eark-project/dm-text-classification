import sys
import uuid
import os

try:
    from sklearn.externals import joblib
except Exception, e:
    sys.exit(e)

'''
This script takes the input from sys.argv and performs text categorization.
'''

class TextClassifier(object):
    def __init__(self, model):
        # TODO: maybe load the categories dynamically from an external file? -> third/fourth cmd line parameter?
        self.categories = ['AutoMobil', 'Bildung', 'Etat', 'Familie', 'Finanzen', 'Gesundheit', 'Greenlife',
                           'Immobilien', 'Inland', 'International', 'Karriere', 'Kultur', 'Lifestyle', 'Meinung',
                           'Panorama', 'Politik', 'Reise', 'Sport', 'Stil', 'Technik', 'Web', 'Wirtschaft', 'Wissenschaft']
        try:
            self.clf = joblib.load(model)
        except Exception, e:
            sys.exit('[ERROR] Error when loading model: %s' % e)

    def categorize(self, tmpFile):
        try:
            with open(tmpFile, 'r') as clfInput:
                confidence = self.clf.decision_function(clfInput.readlines())
        except Exception, e:
            sys.exit('[ERROR] Exception when trying to get the confidence ratings: %s' % e)

        result = confidence[0].tolist()

        # get category with highest confidence rating
        max_value = float(max(result))
        max_value_index = result.index(max_value)

        # get second highest confidence rating
        result[max_value_index] = -2
        second_value = float(max(result))
        second_value_index = result.index(second_value)

        # print '---------- File: %s' % text
        # print 'Highest confidence: %f for category <%s>.' % (max_value, categories[max_value_index])
        # print 'Second highest confidence: %f for category <%s>.' % (second_value, categories[second_value_index])

        primary = self.categories[max_value_index]
        secondary = self.categories[second_value_index]
        return primary, secondary


if __name__ == '__main__':
    try:
        # only two arguments on the cluster (no input_id)
        model = sys.argv[1]
        # input_id = sys.argv[2]
        # input_file = sys.argv[3]
        input_file = sys.argv[2]
    except Exception, e:
        sys.exit('[ERROR] Wrong input format: %s' % e)

    # currently not needed, files are created by the Mapper
    # try:
    #     # create a temporary file to feed to the classifier; will be deleted afterwards
    #     # (needed because the classifier always goes one level "below" input: string -> words, file -> file content)
    #     tmpId = uuid.uuid4().__str__()
    #     with open('/tmp/%s' % tmpId, 'w') as tmp:
    #         tmp.write(input_text)
    # except Exception, e:
    #     sys.exit('[ERROR] Failed to create the tmp file: %s' % e)

    classifier = TextClassifier(model)

    # classify the input - should be text only
    # TODO: discuss input format
    try:
        primary, secondary = classifier.categorize('/tmp/clfin/%s' % input_file)
        print '%s,%s' % (primary, secondary)
        # delete the tmp file as it is no longer needed
        os.remove('/tmp/clfin/%s' % input_file)
    except Exception, e:
        sys.exit('Error when calling the classifier with input: %s' % e)
