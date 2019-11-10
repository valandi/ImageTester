import org.junit.Test;

public class TestUtilities extends Base {

    @Test
    public void getImagesTest() {
        runBlackBox("src/test/TestData/b/c/JustPDF/Lorem3.pdf", "-gi");
    }
}
