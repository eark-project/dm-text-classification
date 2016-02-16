/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eu.eark.textclassification;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.lilyproject.client.LilyClient;
import org.lilyproject.mapreduce.LilyMapReduceUtil;
import org.lilyproject.repository.api.TableManager;

import java.io.IOException;

/**
 *
 * @author janrn
 */
public class TextClassifierJob extends Configured implements Tool {

    private String zkConnectString;
    public static final String TABLE_NAME = "tableName";
    private String tableName;
    private String inputFile;
    public static final String CLASSIFIER = "classifier";
    private String classifier;
    public static final String MODEL = "model";
    private String model;

    public static void main(String[] args) throws Exception {
        // Let <code>ToolRunner</code> handle generic command-line options
        int res = ToolRunner.run(new Configuration(), new TextClassifierJob(), args);
        System.exit(res);
    }

    @Override
    public int run(String[] args) throws Exception {
        int result = parseArgs(args);
        if (result != 0) {
            return result;
        }

        Configuration config = getConf();

        Job job = new Job(config, "TextClassifierJob");
        job.setJarByClass(TextClassifierJob.class);

        job.setMapperClass(TextClassifierMapper.class);
        job.setNumReduceTasks(0);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        // Mapper writes to Lily, so no Hadoop output
        job.setOutputFormatClass(NullOutputFormat.class);
        
        job.getConfiguration().set(LilyMapReduceUtil.ZK_CONNECT_STRING, zkConnectString);
        if (tableName != null) {
            try (LilyClient lilyClient = new LilyClient(zkConnectString, 30000)) {
                TableManager tableManager = lilyClient.getDefaultRepository().getTableManager();
                if (!tableManager.tableExists(tableName)) {
                    tableManager.createTable(tableName);
                }
                job.getConfiguration().set(TABLE_NAME, tableName);
            }
        }
        FileInputFormat.addInputPath(job, new Path(inputFile));
        
        job.getConfiguration().set(CLASSIFIER, classifier);
        job.getConfiguration().set(MODEL, model);

        // Launch the job
        boolean b = job.waitForCompletion(true);
        if (!b) {
            throw new IOException("error executing job!");
        }
        return 0;
    }

    @SuppressWarnings("static-access")
    protected int parseArgs(String[] args) {
        Options cliOptions = new Options();

        Option tableOption = OptionBuilder.isRequired(false).withArgName("table name").hasArg().withDescription("Lily table name").withLongOpt("table").create("t");
        cliOptions.addOption(tableOption);

        Option zkOption = OptionBuilder.isRequired().withArgName("connection-string").hasArg().withDescription("ZooKeeper connection string: hostname1:port,hostname2:port,...").withLongOpt("zookeeper").create("z");
        cliOptions.addOption(zkOption);

        // command line parameters (all mandatory) for text classification:
        // -i: a file that contains on every line <path>,<application/type> for every Lily object that should be classified - must be on HDFS!
        Option inputOption = OptionBuilder.isRequired().withArgName("input").hasArg().withDescription("File with paths and contentTypes (as saved in Lily)").withLongOpt("input").create("i");
        cliOptions.addOption(inputOption);
        
        // -c: the python file containing the classifier that will be used (requires a path on the local filesystem)
        Option classifierOption = OptionBuilder.isRequired().withArgName("classifier script").hasArg().withDescription("path to Python classifier script").withLongOpt("pyclf").create("c");
        cliOptions.addOption(classifierOption);
        
        // -m: the model used for classification (requires a path on the local filesystem, to <model>.pkl - all other .pkl files for this model must be in the same folder)
        Option modelOption = OptionBuilder.isRequired().withArgName("model").hasArg().withDescription("path to model").withLongOpt("model").create("m");
        cliOptions.addOption(modelOption);
        
        CommandLineParser parser = new PosixParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(cliOptions, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            System.out.println();

            HelpFormatter help = new HelpFormatter();
            help.printHelp(getClass().getSimpleName(), cliOptions, true);
            return 1;
        }

        tableName = cmd.getOptionValue(tableOption.getOpt());
        zkConnectString = cmd.getOptionValue(zkOption.getOpt());
        inputFile = cmd.getOptionValue(inputOption.getOpt());
        classifier = cmd.getOptionValue(classifierOption.getOpt());
        model = cmd.getOptionValue(modelOption.getOpt());

        return 0;
    }
}
