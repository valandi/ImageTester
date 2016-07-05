import com.applitools.eyes.Eyes;
import com.applitools.eyes.Region;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

public class ImageStep extends TestUnit {
    //private static final String IMAGE_PATTERN = "([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)";
    private static final String IMAGE_EXT = "(\\.(?i)(jpg|png|gif|bmp))$";
    private static final String IMAGE_PATTERN = "([^\\s]+)" + IMAGE_EXT;
    private static final Pattern pattern = Pattern.compile(IMAGE_PATTERN);

    protected ImageStep(File file) {
        super(file);
    }

    @Override
    public void run(Eyes eyes) throws IOException {
        BufferedImage img = ImageIO.read(file_);
        eyes.checkImage(img, name());

    }

    @Override
    public String name() {
        return super.name().replaceAll(IMAGE_EXT, "");
    }

    protected static boolean supports(File file) {
        return pattern.matcher(file.getName()).matches();
    }
}
