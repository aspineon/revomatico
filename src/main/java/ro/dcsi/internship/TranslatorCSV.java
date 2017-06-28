package ro.dcsi.internship;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import de.siegmar.fastcsv.reader.*;
import de.siegmar.fastcsv.writer.*;

/**
 * Created by Catalin on 6/19/2017.
 */
public class TranslatorCSV extends Translator {
    private CsvParser parser;
    private CsvRow lastRow;
    private CsvAppender writer;
    private boolean headersWritten;
    private boolean hasNext;

    public TranslatorCSV() {
        this.parser = null;
        this.lastRow = null;
        this.hasNext = false;
        this.headersWritten = false;
        this.writer = null;
    }

    @Override
    public List<List<String>> readBulk(int nMax) {
        if (!hasNext())
            return null;

        // Init
        List<List<String>> result;
        List<String> buffer;
        int i, numberOfFields, currentRows;

        currentRows = 0;
        result = new ArrayList<List<String>>();
        numberOfFields = lastRow.getFieldCount();

        // Parse nMax rows
        while (lastRow != null && currentRows < nMax) {

            // Parse current row
            buffer = new ArrayList<String>();
            currentRows++;
            for (i = 0; i < numberOfFields; i++) {
                buffer.add(lastRow.getField(i));
            }
            result.add(buffer);

            // Get next row
            try {
                lastRow = parser.nextRow();
            } catch (IOException e) {
                System.err.print("Parser error!");
                e.printStackTrace();
            }

        }
        if (lastRow == null)
            hasNext = false;

        return result;
    }

    public boolean hasNext() {
        return hasNext;
    }

    private List<String> getDefaultHeaders() {
        List<String> headers = new ArrayList<String>();
        headers.add("name");
        headers.add("email");
        return headers;
    }

    private void resizeHeaders(int n) {
        if (headers.size() > n)
            headers = headers.subList(0, n);

        while (headers.size() < n)
            headers.add("Unknown");
    }

    private void InitHeaders() {
        if (parser == null)
            return;

        try {
            lastRow = parser.nextRow();
        } catch (IOException e) {
            System.err.print("Parser error!");
            e.printStackTrace();
            this.headers = getDefaultHeaders();
            return;
        }

        // Check if current field is header
        int i, numberOfFields;
        double match;
        List<String> defaultHeader, currentRow;
        String buffer;

        // Init values
        match = 0;
        numberOfFields = lastRow.getFieldCount();
        currentRow = new ArrayList<String>();
        defaultHeader = getDefaultHeaders();

        // Parse current row
        for (i = 0; i < numberOfFields; i++) {
            buffer = lastRow.getField(i);
            currentRow.add(buffer);
            if (defaultHeader.contains(buffer.toLowerCase()))
                match++;
        }

        // confirm current field is headers
        if (match > 0) {
            this.headers = currentRow;
            lastRow = null;
        } else {
            this.headers = defaultHeader;
            resizeHeaders(numberOfFields);
        }

    }

    @Override
    public void setInputFile(String inputFile) {

        // Open file
        super.inputFile = inputFile;
        try {
            Reader input = new BufferedReader(new FileReader(inputFile));
            CsvReader csv = new CsvReader();
            csv.setFieldSeparator(';');
            this.parser = csv.parse(input);
        } catch (FileNotFoundException e) {
            System.err.print("File not found!");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.print("Parser error!");
            e.printStackTrace();
        }

        // Init headers
        InitHeaders();

        // Populate hasNext
        if (lastRow != null)
            this.hasNext = true;

        // Try to get next row
        else {
            try {
                lastRow = this.parser.nextRow();
            } catch (IOException e) {
                System.err.print("Parser error!");
                e.printStackTrace();
            }

            if (lastRow != null)
                this.hasNext = true;

        }

    }

    @Override
    public void writeBulk(List<List<String>> userList) {
        if (!headersWritten) {

            if (headers == null) {
                headers = getDefaultHeaders();
                resizeHeaders(userList.get(0).size());
            }

            headersWritten = true;
            try {
                for (String s : headers)
                    writer.appendField(s);
                writer.endLine();
            } catch (IOException e) {
                System.err.print("Write to file Error");
                e.printStackTrace();
            }
            headersWritten = true;
        }

        // Write each field
        for (List<String> list : userList) {
            try {
                for (String field : list) {
                    writer.appendField(field);
                }
                writer.endLine();
            }

            catch (IOException e) {
                System.err.print("Write to file Error");
                e.printStackTrace();
            }

        }
        try {
            writer.flush();
        } catch (IOException e) {
            System.err.print("Flush Error!");
            e.printStackTrace();
        }

    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
        // Opening file;
        File output = new File(outputFile);
        CsvWriter csv = new CsvWriter();
        csv.setFieldSeparator(';');
        try {
            this.writer = csv.append(new FileWriter(output));
        } catch (IOException e) {
            throw new RuntimeException("Open file for Write Error!");
        }

        if (this.headers != null) {
            headersWritten = true;
            try {
                for (String s : headers)
                    writer.appendField(s);
                writer.endLine();
            } catch (IOException e) {
                System.err.print("Write to file Error");
                e.printStackTrace();
            }
        }
    }
}
