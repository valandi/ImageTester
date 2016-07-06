import com.applitools.eyes.Eyes;
import org.ghost4j.document.DocumentException;
import org.ghost4j.document.PSDocument;
import org.ghost4j.renderer.RendererException;
import org.ghost4j.renderer.SimpleRenderer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.List;

public class PostscriptTest extends Test {
    private static final String PS_EXT = "(?i)(\\.ps)$";
    private static final String PS_PATTERN = "(.+)" + PS_EXT;
    private static final Pattern pattern = Pattern.compile(PS_PATTERN);

    protected PostscriptTest(File file, String appname) {
        super(file, appname);
    }

    @Override
    public void run(Eyes eyes) {
        PSDocument document = new PSDocument();
        SimpleRenderer renderer = new SimpleRenderer();
        renderer.setResolution(300);
        Exception ex = null;
        String res = null;

        try {
            document.load(file_);
            List<Image> images = renderer.render(document);

            eyes.open(appname_, name());

            int page = 1;
            for (Image step : images) {
                BufferedImage image = new BufferedImage(step.getWidth(null), step.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                eyes.checkImage(image, String.format("Page-%s", page++));
            }

            eyes.close();
        } catch (FileNotFoundException e) {
            res = "Error";
            ex = e;
        } catch (IOException e) {
            res = "Error";
            ex = e;
        } catch (RendererException e) {
            res = "Error";
            ex = e;
        } catch (DocumentException e) {
            res = "Error";
            ex = e;
        } catch (UnsatisfiedLinkError e) {
            System.out.printf("Please make sure tesseract is installed and in path!\n");
            res = "Error";
            e.printStackTrace();
        } finally {
            System.out.printf("\t[%s] - %s\n", res, name());
            if (ex != null) ex.printStackTrace();
            eyes.abortIfNotClosed();
        }
    }

    protected static boolean supports(File file) {
        return false;
        //return pattern.matcher(file.getName()).matches();
    }
}
