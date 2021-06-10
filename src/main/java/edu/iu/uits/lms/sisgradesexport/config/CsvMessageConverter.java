package edu.iu.uits.lms.sisgradesexport.config;

import com.opencsv.CSVWriter;
import edu.iu.uits.lms.sisgradesexport.model.CsvResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.List;

@Slf4j
public class CsvMessageConverter extends AbstractHttpMessageConverter<CsvResponse> {

    public static final MediaType MEDIA_TYPE = new MediaType("text", "csv", Charset.forName("utf-8"));

    public CsvMessageConverter() {
        super(MEDIA_TYPE);
    }

    protected boolean supports(Class<?> clazz) {
        return CsvResponse.class.equals(clazz);
    }

    protected void writeInternal(CsvResponse response, HttpOutputMessage output) throws IOException, HttpMessageNotWritableException {
        OutputStream out;
        CSVWriter writer;
        List<Object[]> dataset;

        output.getHeaders().setContentType(MEDIA_TYPE);
        output.getHeaders().set("Content-Disposition", "attachment; filename=\"" + response.getFilename() + "\"");
        out = output.getBody();

        char quoteChar = CSVWriter.DEFAULT_QUOTE_CHARACTER;
        if (!response.isQuoteStrings()) {
            quoteChar = CSVWriter.NO_QUOTE_CHARACTER;
        }
        writer = new CSVWriter(new OutputStreamWriter(out), ',', quoteChar, '"', "\n");

        log.debug("CSV Columns: " + response.getColumns().size());
        log.debug("CSV Rows: " + response.getRecords().size());

        writer.writeNext(response.getColumns().toArray(new String[0]));

        dataset = response.getRecords();
        for(Object[] datasetRow : dataset) {
            writer.writeNext((String[])datasetRow);
        }
        writer.flush();
        writer.close();
    }

    @Override
    protected CsvResponse readInternal(Class<? extends CsvResponse> type, HttpInputMessage him) throws IOException, HttpMessageNotReadableException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
