package Tests;

import com.yanirta.ImageTester;
import org.junit.Test;

public class TestFullCommands {
    @Test
    public void testFolderSimple() {
        ImageTester.main("-f src/test/TestData -debug".split(" "));
    }

    @Test
    public void testPDFSimple() {
        ImageTester.main("-f src/test/TestData/b/c/JustPDF/Lorem2.pdf -debug".split(" "));
    }

    @Test
    public void testPDFSplit(){
        ImageTester.main("-f src/test/TestData/b/c/JustPDF/Lorem3.pdf -th 10 -debug -st".split(" "));
    }

    @Test
    public void PDFSplitWithUtilities(){
        ImageTester.main("-f src/test/TestData/diffs/ -th 10 -debug -st -gg -of Artifacts".split(" "));
    }

    @Test
    public void testPDFPages(){
        ImageTester.main("-f src/test/TestData/b/c/JustPDF/Lorem3.pdf -th 10 -sp 1,2,4-5 -debug".split(" "));
    }

    @Test
    public void testPDFPagesSplitted(){
        ImageTester.main("-f src/test/TestData/b/c/JustPDF/Lorem3.pdf -th 10 -sp 1,2,4-5 -debug -st".split(" "));
    }

    @Test
    public void testWithSecureProxy(){
        ImageTester.main("-f src/test/TestData/b/c/JustPDF/Lorem3.pdf -th 10 -sp 1,2,4-5 -pr http://my.proxy.com:8080,user,pass -debug -st".split(" "));
    }
}
