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
import org.lilyproject.repository.api.RepositoryException;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

/**
 *
 * @author janrn
 */
public class TextClassifierJob extends Configured implements Tool {

    private String zkConnectString;
    public static final String TABLE_NAME = "tableName";
    private String tableName;
    // private static String inputFile;

    public static void main(String[] args) throws Exception {
        // URL queue = new URL(args[0]); // Solr queue as parameter
        // inputFile = args[0]; // file with list of lily IDs as input

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
        //job.setOutputFormatClass(TextOutputFormat.class);
        //FileOutputFormat.setOutputPath(job, new Path("output"));
        //MultipleOutputs.addNamedOutput(job, "out", TextOutputFormat.class, LongWritable.class, Text.class);
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
        FileInputFormat.addInputPath(job, new Path("input"));

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

        return 0;
    }
}
