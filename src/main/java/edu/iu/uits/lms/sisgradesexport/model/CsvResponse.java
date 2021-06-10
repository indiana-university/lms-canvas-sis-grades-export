package edu.iu.uits.lms.sisgradesexport.model;

import java.util.List;

public class CsvResponse {

    private final String filename;
    private final List<Object[]> records;
    private final List<String> columns;
    private boolean quoteStrings = true;

    public CsvResponse(List<Object[]> records, List<String> columns, String filename, boolean quoteStrings) {
        this.records = records;
        this.filename = filename;
        this.columns = columns;
        this.quoteStrings = quoteStrings;
    }
    public String getFilename() {
        return filename;
    }
    public List<Object[]> getRecords() {
        return records;
    }
    public List<String> getColumns() {
        return columns;
    }

    public boolean isQuoteStrings() {
        return quoteStrings;
    }
}
