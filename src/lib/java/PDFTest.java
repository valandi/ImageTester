import com.applitools.eyes.Eyes;
import com.applitools.eyes.TestResults;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Created by yanir on 30/01/2016.
 */
public class PDFTest extends Test {
    private static final String PDF_EXT = "(?i)(\\.Pdf)$";
    private static final String PDF_PATTERN = "(.+)" + PDF_EXT;
    private static final Pattern pattern = Pattern.compile(PDF_PATTERN);

    protected PDFTest(File file, String appname) {
        super(file, appname);
    }

    @Override
    public void run(Eyes eyes) {
        String res = null;
        Exception ex = null;

        try {
            PDDocument document = PDDocument.load(file_);
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            eyes.open(appname_, name());

            for (int page = 0; page < document.getNumberOfPages(); ++page) {
//                BufferedImage bim = pdfRenderer.renderImage(page);
                BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300f);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                ImageIO.write(bim, "png", stream);
                eyes.checkImage(stream.toByteArray(), String.format("Page-%s", page));
            }
            TestResults result = eyes.close(false);
            res = result.isNew() ? "New" : (result.isPassed() ? "Passed" : "Failed");
            document.close();
        } catch (IOException e) {
            res = "Error";
            ex = e;
        } finally {
            System.out.printf("\t[%s] - %s\n", res, name());
            if (ex != null) ex.printStackTrace();
            eyes.abortIfNotClosed();
        }
    }

    protected static boolean supports(File file) {
        return pattern.matcher(file.getName()).matches();
    }
}
