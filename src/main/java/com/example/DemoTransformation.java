package com.example;

import org.apache.commons.lang.RandomStringUtils;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.core.Result;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.logging.LoggingBuffer;

public class DemoTransformation {

    public static DemoTransformation instance;

    public static void main(String[] args) throws KettleException {

        KettleEnvironment.init();

        instance = new DemoTransformation();

        // Trans trans =
        // instance.runTransformationFromFileSystem("etl/csv-european-to-json.ktr");
        //
        // LoggingBuffer appender = KettleLogStore.getAppender();
        // String logText = appender.getBuffer(trans.getLogChannelId(),
        // false).toString();
        // System.out.println(
        // "************************************************************************************************");
        // System.out.println("LOG REPORT: Transformation generated the following log
        // lines:\n");
        // System.out.println(logText);
        // System.out.println("END OF LOG REPORT");
        // System.out.println(
        // "************************************************************************************************");

    }

    /**
     * This method executes a transformation defined in a ktr file
     * 
     * It demonstrates the following:
     * 
     * - Loading a transformation definition from a ktr file
     * - Setting named parameters for the transformation
     * - Setting the log level of the transformation
     * - Executing the transformation, waiting for it to finish
     * - Examining the result of the transformation
     * 
     * @param filename the file containing the transformation to execute (ktr file)
     * @return the transformation that was executed, or null if there was an error
     */
    public Trans runTransformationFromFileSystem(String filename) {

        try {
            System.out
                    .println("***************************************************************************************");
            System.out.println("Attempting to run transformation " + filename + " from file system");
            System.out.println(
                    "***************************************************************************************\n");
            // Loading the transformation file from file system into the TransMeta object.
            // The TransMeta object is the programmatic representation of a transformation
            // definition.
            TransMeta transMeta = new TransMeta(filename, (Repository) null);

            // The next section reports on the declared parameters and sets them to
            // arbitrary values
            // for demonstration purposes
            System.out.println("Attempting to read and set named parameters");
            String[] declaredParameters = transMeta.listParameters();
            for (int i = 0; i < declaredParameters.length; i++) {
                String parameterName = declaredParameters[i];

                // determine the parameter description and default values for display purposes
                String description = transMeta.getParameterDescription(parameterName);
                String defaultValue = transMeta.getParameterDefault(parameterName);
                // set the parameter value to an arbitrary string
                String parameterValue = RandomStringUtils.randomAlphanumeric(10);

                String output = String.format("Setting parameter %s to \"%s\" [description: \"%s\", default: \"%s\"]",
                        parameterName, parameterValue, description, defaultValue);
                System.out.println(output);

                // assign the value to the parameter on the transformation
                transMeta.setParameterValue(parameterName, parameterValue);
            }

            // Creating a transformation object which is the programmatic representation of
            // a transformation
            // A transformation object can be executed, report success, etc.
            Trans transformation = new Trans(transMeta);

            // adjust the log level
            transformation.setLogLevel(LogLevel.MINIMAL);

            System.out.println("\nStarting transformation");

            // starting the transformation, which will execute asynchronously
            transformation.execute(new String[0]);

            // waiting for the transformation to finish
            transformation.waitUntilFinished();

            // retrieve the result object, which captures the success of the transformation
            Result result = transformation.getResult();

            // report on the outcome of the transformation
            String outcome = String.format("\nTrans %s executed %s", filename,
                    (result.getNrErrors() == 0 ? "successfully" : "with " + result.getNrErrors() + " errors"));
            System.out.println(outcome);

            return transformation;
        } catch (Exception e) {

            // something went wrong, just log and return
            e.printStackTrace();
            return null;
        }
    }

}
