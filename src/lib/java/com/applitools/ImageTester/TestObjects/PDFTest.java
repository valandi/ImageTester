package com.applitools.ImageTester.TestObjects;

import com.applitools.ImageTester.Interfaces.IResultsReporter;
import com.applitools.ImageTester.Patterns;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.images.Eyes;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by yanir on 30/01/2016.
 */
public class PDFTest extends Test {
    private static final Pattern pattern = Patterns.PDF;
    private float dpi_;
    private String pdfPassword;
    private String pages_;
    private boolean includePagesInTestName_;

    public void setPages(String pages, boolean includePagesInTestName) throws IOException {
        this.pages_ = pages;
        this.includePagesInTestName_ = includePagesInTestName;
    }

    protected PDFTest(File file, String appname) {
        this(file, appname, 300f, null);
    }

    public PDFTest(File file, String appname, float dpi, RectangleSize viewport) {
        this(file, appname, dpi, viewport, null);
    }

    public PDFTest(String file, String appname, float dpi, RectangleSize viewport) {
        this(new File(file), appname, dpi, viewport, null);
    }

    public PDFTest(String file, String appname, float dpi, RectangleSize viewport, IResultsReporter reporter) {
        this(new File(file), appname, dpi, viewport, reporter);
    }

    public PDFTest(File file, String appname, float dpi, RectangleSize viewport, IResultsReporter reporter) {
        super(file, appname, viewport, reporter);
        this.dpi_ = dpi;
    }

    @Override
    public void run(Eyes eyes) {
        Exception ex = null;
        TestResults result = null;


        try {
            PDDocument document = PDDocument.load(file_, pdfPassword);
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            List<Integer> pagesList_ = setPagesList(document, pages_);

            eyes.open(appname_, name(), viewportSize_);
            for (int i = 0; i < pagesList_.size(); i++) {
                BufferedImage bim = pdfRenderer.renderImageWithDPI(pagesList_.get(i) - 1, dpi_);
                eyes.checkImage(bim, String.format("Page-%s", pagesList_.get(i)));
            }
            result = eyes.close(false);
            reporter_.onTestFinished(name(), result);
            handleResultsDownload(result);
            document.close();
        } catch (IOException e) {
            ex = e;
            System.out.printf("Error closing test %s \nPath: %s \nReason: %s \n", e.getMessage());

        } catch (Exception e) {
            System.out.printf("Oops, something went wrong while processing the file %s! \n", file_.getName());
            System.out.print(e);
            e.printStackTrace();
        } finally {
            if (ex != null) ex.printStackTrace();
            eyes.abortIfNotClosed();
        }
    }

    public static boolean supports(File file) {
        return pattern.matcher(file.getName()).matches();
    }

    protected void setDpi(float dpi) {
        this.dpi_ = dpi;
    }

    public String getPdfPassword() {
        return pdfPassword;
    }

    public void setPdfPassword(String pdfPassword) {
        this.pdfPassword = pdfPassword;
    }

    public List<Integer> setPagesList(PDDocument document, String pages) throws IOException {
        if (pages != null) return parsePagesToList(pages);
        else {
            ArrayList<Integer> list = new ArrayList<Integer>();
            for (int page = 0; page < document.getNumberOfPages(); ++page) {
                list.add(page + 1);
            }
            return list;
        }
    }

    @Override
    public String name() {
        String pagesText = "";
        if (pages_ != null && includePagesInTestName_)
            pagesText = " pages [" + pages_ + "]";
        return file_ == null ? name_ + pagesText : file_.getName() + pagesText;
    }
}
