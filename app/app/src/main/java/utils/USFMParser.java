package utils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Fechner on 2/26/15.
 */
public class USFMParser {

    static public  Map<String, String> parseUsfm(byte[] usfmBytes) throws CharacterCodingException {

        Charset utfSet = Charset.forName("UTF-8");
        CharsetDecoder decoder = utfSet.newDecoder();
        CharBuffer buffer = decoder.decode(ByteBuffer.wrap(usfmBytes));

        String usfmString = String.valueOf(buffer.array());
        String splitString = "!!!";

        usfmString = usfmString.replace("\\c ", splitString);
        String[] stringArray = usfmString.split(splitString);
        Map<String, String> chapters = new HashMap<String, String>();

        for(int i = 1; i < stringArray.length; i++){

            String chapter = stringArray[i];
            String chapterRex = "^\\d*";
            Pattern MY_PATTERN = Pattern.compile(chapterRex);
            Matcher m = MY_PATTERN.matcher(chapter);

            String chapterNumber = "";
            while (m.find()) {
                chapterNumber = m.group(0);
                break;
            }

            int chapterStartIndex = chapter.indexOf("\\v");
            chapter = chapter.substring(chapterStartIndex);

            chapter = chapter.replace("\\v ", splitString);
            String[] verses = chapter.split(splitString);
            String finalChapter = handleVerses(verses);
            chapters.put(chapterNumber, finalChapter);
        }

        return chapters;
    }

    static public String handleVerses(String[] verses){

        String finalChapterString = "";

        for(int i = 1; i < verses.length; i++){
            String verse = verses[i];

            String chapterRex = "^\\d+";
            Pattern MY_PATTERN = Pattern.compile(chapterRex);
            Matcher m = MY_PATTERN.matcher(verse);

            String verseNumber = "";
            while (m.find()) {
                verseNumber = m.group(0);
                break;
            }
//            System.out.println(verseNumber);

            int verseStartIndex = verse.indexOf(" ");
            verse = "<small> " + verseNumber + " </small>" +  verse.substring(verseStartIndex);

            verse = verse.replace("\\p", "<br/><br/>");

            verse = verse.replace("\\s", "!!!");

            String sRegex = "!!!\\d*";
            verse = verse.replaceAll(sRegex, "");
//            System.out.println(verse);
            verse = verse.replace("\\b", "");


            finalChapterString += verse;
        }

        return finalChapterString;
    }
}
