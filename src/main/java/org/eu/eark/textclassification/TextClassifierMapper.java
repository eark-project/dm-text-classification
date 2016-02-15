/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eu.eark.textclassification;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.*;
import java.util.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.lilyproject.client.LilyClient;
import org.lilyproject.mapreduce.LilyMapReduceUtil;
import org.lilyproject.repository.api.LRepository;
import org.lilyproject.repository.api.LTable;
import org.lilyproject.repository.api.QName;
import org.lilyproject.repository.api.RecordId;
import org.lilyproject.repository.api.RecordNotFoundException;
import org.lilyproject.repository.api.RepositoryException;
import org.lilyproject.util.io.Closer;

import java.io.IOException;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.lilyproject.indexer.Indexer;
import org.lilyproject.repository.api.Record;
import org.xml.sax.SAXException;

/**
 *
 * @author janrn
 */

public class TextClassifierMapper extends Mapper<Object, Text, Text, Text> {
    /**
     * 
     * This class creates input for the classifier.py script and launches it via command line. 
     * Currently, input is a list of .txt files in a directory.
     * In order to be used on the cluster as a MapReduce job, it has to be slightly reworked: Take input from Lily,
     * and write back to Lily (in: uncategorized text, out: categories).
     * 
     * @param args the command line arguments
     * @throws pythoncommand.PythonException
     * 
     */
    private LilyClient lilyClient;
    private LRepository repository;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
    	super.setup(context);
	this.lilyClient = LilyMapReduceUtil.getLilyClient(context.getConfiguration());
	try {
		this.repository = lilyClient.getDefaultRepository();
	} catch (RepositoryException e) {
		throw new RuntimeException("Failed to get repository", e);
	}
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
	Closer.close(lilyClient);
	super.cleanup(context);
    }    
        
    protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        String c = ",";
        String value_string = value.toString();
        int i = value_string.lastIndexOf(c);
        String[] values =  {value_string.substring(0, i), value_string.substring(i)}; //should be able to handle "," in path
	String idPath = values[0];
        String fileType = values[1];
        Configuration conf = context.getConfiguration();
        
        String pyCode = "/home/janrn/classifier.py";
        String model = "/home/janrn/textmodels/newspapers.pkl";
        
        List<String> pythonCmd = new ArrayList<>();
        pythonCmd.add("python");
        pythonCmd.add(pyCode);
        pythonCmd.add(model);
        
        String result;
        // List<String> resultList = new ArrayList<String>(); // only needed if a list of files is provided to python (same for errorList)
        String category = "category";
        //String category_alt = "category_alternative";
        
        String error;
        // List<String> errorList = new ArrayList<String>();
        
        ProcessBuilder pb = new ProcessBuilder();
        
        // TODO: is an identifier needed? - adapt py script if yes (three input arguments!) - maybe on cluster, but not on earkdev
        // pythonCmd.add(fileID);
                
        // get input from Lily (content) and write if to the Hadoop filesystem
        String filename = RandomStringUtils.randomAlphanumeric(20);
        FileSystem fs = FileSystem.get(conf);
        Path pt = new Path(String.format("%s.out", filename));
        try {            
            String tableName = conf.get(TextClassifierJob.TABLE_NAME);
            LTable table = tableName == null ?
            repository.getDefaultTable() : repository.getTable(tableName);
            RecordId id = repository.getIdGenerator().newRecordId(idPath);
            
            // get the content fields' content - a BLOB
            InputStream contentstream = table.getBlob(id, q("content")).getInputStream();
            byte[] buff = new byte[4096];
            int len = 0;
            
            // this writes a copy of the BLOB to Hadoop - beware of the file type !
            FSDataOutputStream out = fs.create(pt);
            while ((len = contentstream.read(buff)) != -1) {
                out.write(buff, 0, len);
            }
            out.close();
        } catch (RecordNotFoundException e) {
            System.out.println("Record doesn't exist: " + idPath);
	} catch (InterruptedException | RepositoryException | IOException e) {
            throw new RuntimeException(e);
	}
        
        if ("application/pdf".equals(fileType)) {
            // special case: BLOB is a pdf - extract the text and write as plain text
            BodyContentHandler handler = new BodyContentHandler(-1); // -1 means no char limit on parse
            Metadata metadata = new Metadata();
            FSDataInputStream inputstream = new FSDataInputStream(fs.open(pt));
            ParseContext pcontext = new ParseContext();
            
            // parsing the document using PDF parser
            PDFParser pdfparser = new PDFParser();
            try {
                pdfparser.parse(inputstream, handler, metadata, pcontext);
            } catch (SAXException | TikaException ex) {
                Logger.getLogger(TextClassifierMapper.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            // write the content to file (metadata is omitted because not of interest) - pdf file is overwritten
            BufferedWriter br = new BufferedWriter(new OutputStreamWriter(fs.create(pt, true)));
            br.write(handler.toString());
            br.close();
        }
        
        // move the file from Hadoop to the local filesystem, so it can be accessed by the python script
        // local file will be deleted by the python script
        fs.moveToLocalFile(pt, new Path("/tmp/clfin"));
        
        // add the filename to the python command
        pythonCmd.add(String.format("%s.out", filename));
   
        pb.command(pythonCmd);
        //System.out.println(pb.command());
        
        try {
            // start the process
            Process p = pb.start();
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((result = stdInput.readLine()) != null) {
                // resultList.add(result);
                String[] categories = result.split(",");
                category = String.format(categories[0] + " " + categories[1]);
                //category_alt = categories[1];
                System.out.println(category);
                //System.out.println(category_alt);
            }
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while ((error = stdError.readLine()) != null) {
                // errorList.add(error);
                System.out.println(error);
                throw new PythonException(error);
            }
        } catch (IOException | PythonException ex) {
            Logger.getLogger(TextClassifierMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        // reset the StringBuilder (length 0) and remove the last item from pythonCmd (else the pythonCmd just gets longer)
        pythonCmd.remove(pythonCmd.size() - 1);
        // pythonCmd.remove(pythonCmd.size() - 1); // only if three arguments are passed
        
        // write the results to Lily:	
	try {
            String tableName = conf.get(TextClassifierJob.TABLE_NAME);
            LTable table = tableName == null ?
            repository.getDefaultTable() : repository.getTable(tableName);
            RecordId id = repository.getIdGenerator().newRecordId(idPath);
            Record record_category = table.read(id, q("textCategory"));
            record_category.setField(q("textCategory"), category);
            table.createOrUpdate(record_category);
            Indexer indexer = lilyClient.getIndexer();
            indexer.index(table.getTableName(), record_category.getId());

	} catch (RecordNotFoundException e) {
            System.out.println("Record doesn't exist!");
	} catch (Exception e) {
            throw new RuntimeException(e);
	}
        
    }
    
    private static QName q(String name) {
	return new QName("org.eu.eark", name);
    }
}
