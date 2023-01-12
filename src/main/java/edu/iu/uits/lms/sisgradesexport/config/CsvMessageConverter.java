package edu.iu.uits.lms.sisgradesexport.config;

/*-
 * #%L
 * sis-grades-export
 * %%
 * Copyright (C) 2015 - 2023 Indiana University
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Indiana University nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

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
