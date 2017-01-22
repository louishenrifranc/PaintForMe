package com.example.lh.painter.painter.helper;

/**
 * Created by lh on 17-01-21.
 */

public class DataBaseMessage {
    public String status;
    public String filename;
    public Integer nbFileToRetrieve;

    public DataBaseMessage(String filename, Integer nbFileToRetrieve) {
        this.filename = filename;
        this.status = "done";
        this.nbFileToRetrieve = nbFileToRetrieve;

    }

    public Integer getNbFileToRetrieve() {
        return nbFileToRetrieve;
    }

    public String getFilename() {
        return filename;
    }

    public String getStatus() {
        return status;
    }
}
