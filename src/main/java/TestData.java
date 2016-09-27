//import org.testng.annotations.DataProvider;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileReader;
//import java.io.IOException;
//
///**
// * Created by Ihor on 9/27/2016. All rights reserved!
// */
//public class TestData {
//    @DataProvider(name="testData")
//    public Object[][] testData() throws IOException {
//        int numLines = 0;
//        int currentLine = 0;
//        String sent = "";
//        File file = new File("file.txt");
//
//        //counting lines from file
//        BufferedReader br = new BufferedReader(new FileReader(file));
//        while ((br.readLine()) != null){
//            numLines++;
//        }
//        br.close();
//
//        //extracting lines to send to test
//        String[][] testData = new String[numLines][2];
//        BufferedReader br2 = new BufferedReader(new FileReader(file));
//        while ((sent = br2.readLine()) != null){
//            String expected = sent.substring(50, 106) + "00" + sent.substring(106, 154);
//            testData[currentLine][0] = sent;
//            testData[currentLine][1] = expected;
//            currentLine++;
//        }
//        br2.close();
//        return testData;
//    }
//}
