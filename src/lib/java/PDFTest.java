import com.applitools.eyes.Eyes;
import com.applitools.eyes.TestResults;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Created by yanir on 30/01/2016.
 */
public class PDFTest extends Test {
    private static final Pattern pattern = Patterns.PDF;
    private float dpi_;
    private String pdfPassword;
    protected PDFTest(File file, String appname) {
        this(file, appname, 300f);
    }
    protected PDFTest(File file, String appname, float dpi) {
        super(file, appname);
        this.dpi_=dpi;
    }

    @Override
    public void run(Eyes eyes) {
        Exception ex = null;
        TestResults result=null;

        try {
            PDDocument document = PDDocument.load(file_,pdfPassword);
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            eyes.open(appname_, name());

            for (int page = 0; page < document.getNumberOfPages(); ++page) {
                if (this.getPagesToInclude().contains(page+1))
                {
                    BufferedImage bim = pdfRenderer.renderImageWithDPI(page, dpi_);
                    eyes.checkImage(bim, String.format("Page-%s", page));
                }
            }
            result = eyes.close(false);
            printTestResults(result);
            handleResultsDownload(result);
            document.close();
        } catch (IOException e) {
            ex = e;
            System.out.printf("Error closing test %s \nPath: %s \nReason: %s \n",e.getMessage());

        } catch (Exception e) {
            System.out.println("Oops, something went wrong!");
            System.out.print(e);
            e.printStackTrace();
        } finally {
            if (ex != null) ex.printStackTrace();
            eyes.abortIfNotClosed();
        }
    }

    protected static boolean supports(File file) {
        return pattern.matcher(file.getName()).matches();
    }

    protected void setDpi(float dpi){
        this.dpi_ =dpi;
    }

    public String getPdfPassword() {return pdfPassword;}

    public void setPdfPassword(String pdfPassword) {this.pdfPassword = pdfPassword;}
}
