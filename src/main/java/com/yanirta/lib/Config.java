package com.yanirta.lib;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.ProxySettings;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.fluent.BatchClose;
import com.yanirta.BatchObjects.Batch;
import org.apache.commons.cli.CommandLine;

import java.util.ArrayList;

public class Config {
    public RectangleSize viewport;
    public String appName = "ImageTester";
    public float DocumentConversionDPI = 250;
    public boolean splitSteps = false;
    public String pages = null;
    public String pdfPass = null;
    public boolean includePageNumbers = false;
    public Logger logger = new Logger();
    public EyesUtilitiesConfig eyesUtilsConf;
    public BatchInfo flatBatch = null;
    public String forcedName = null;
    public String sequenceName = null;
    public boolean notifyOnComplete = false;
    public String apiKey;
    public String serverUrl;
    public ProxySettings proxy_settings = null;
    public String matchWidth = null;
    public String matchHeight = null;
    private ArrayList<String> batchesIdListForBatchClose;

    public void setViewport(String viewport) {
        if (viewport == null) return;
        String[] dims = viewport.split("x");
        if (dims.length != 2)
            throw new RuntimeException("invalid viewport-size, make sure the call is -vs <width>x<height>");
        this.viewport = new RectangleSize(
                Integer.parseInt(dims[0]),
                Integer.parseInt(dims[1]));
    }

    public void setProxy(String[] proxy) {
        if (proxy != null && proxy.length > 0)
            if (proxy.length == 1) {
                logger.reportDebug("Using proxy %s \n", proxy[0]);
                proxy_settings = new ProxySettings(proxy[0]);
            } else if (proxy.length == 3) {
                logger.reportDebug("Using proxy %s with user %s and pass %s \n", proxy[0], proxy[1], proxy[2]);
                proxy_settings = new ProxySettings(proxy[0], proxy[1], proxy[2]);
            } else
                throw new RuntimeException("Proxy setting are invalid");
    }

    public void setMatchSize(String size) {
        if (size == null)
            return;
        String[] dims = size.split("x");
        matchWidth = dims[0];
        if (dims.length > 1)
            matchHeight = dims[1];
        return;
    }

    //set batch related info
    public void setBatchInfo(CommandLine cmd) {
        //initialize batches Id List for batch close
        initializeBatchesIdList();

        //set notify on complete
        notifyOnComplete = cmd.hasOption("nc");

        //set batch- take flat batch if described- get environment variables values unless overwritten
        String batchNameToAdd = System.getenv("JOB_NAME");;
        String batchIdToAdd = System.getenv("APPLITOOLS_BATCH_ID");;


        //set flat batch- config.notify complete must be before this set
        if (cmd.hasOption("fb")) {
            //check if batch id was specified
            String batchValue = cmd.getOptionValue("fb");
            batchNameToAdd = batchValue;
            batchIdToAdd = null;

            if (batchValue.contains("<>")) {
                batchNameToAdd = batchValue.substring(0,batchValue.indexOf('<'));
                batchIdToAdd = batchValue.substring(batchValue.indexOf('>') + 1);
            }
        }

        //if flat batch name is not empty initialize flat batch
        if (Utils.ne(batchNameToAdd)) {
            flatBatch = new Batch(batchNameToAdd, this).batchInfo();
            //if flat batch id is not empty set batch id
            if (Utils.ne(batchIdToAdd)) {
                flatBatch.setId(batchIdToAdd);
            }
        }
    }

    //initialize batches id list
    public void initializeBatchesIdList() {
        batchesIdListForBatchClose = new ArrayList<>();
    }


    //add batch id to list
    public void addBatchIdToCloseList(String batchId) {
        batchesIdListForBatchClose.add(batchId);
    }

    //close batches
    public void closeBatches(){
        if (notifyOnComplete) {
            BatchClose batchClose = new BatchClose();
            batchClose.setApiKey(apiKey);
            if (serverUrl != null)
                batchClose.setUrl(serverUrl);
            if (proxy_settings != null)
                batchClose.setProxy(proxy_settings);
            batchClose.setBatchId(batchesIdListForBatchClose).close();
        }
    }
}
