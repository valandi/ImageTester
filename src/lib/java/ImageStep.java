import com.applitools.eyes.Eyes;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.regex.Pattern;

public class ImageStep extends TestUnit {
    private static final Pattern pattern = Patterns.IMAGE;
    private BufferedImage img_;

    protected ImageStep(File file) {
        super(file);
    }

    public void run(Eyes eyes) throws IOException {
        if (hasRegionFile())
            throw new RuntimeException("Hurry, You found a bug! This is region step, it should be executed through it's regions");

        eyes.checkImage(getImage(), name());
    }

    public BufferedImage getImage() throws IOException {
        if (img_ == null) {
            img_ = ImageIO.read(file_);
        }
        return img_;
    }

    @Override
    public String name() {
        return super.name().replaceAll(Patterns.IMAGE_EXT, "");
    }

    protected static boolean supports(File file) {
        return pattern.matcher(file.getName()).matches();
    }

    public boolean hasRegionFile() {
        return RegionStep.supports(this);
    }

    public Collection<ITestable> getRegions() throws IOException {
        return RegionStep.getSteps(this);
    }
}
