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

    private static final Pattern CHAPTER_REGEX = Pattern.compile("\\\\c(.*?)(\\\\c|$)", Pattern.DOTALL);
    private static final Pattern VERSE_REGEX = Pattern.compile("\\\\v\\s([0-9-])*\\s", Pattern.DOTALL);
    private static final Pattern NUMBER_REGEX = Pattern.compile("\\s*(\\d*)");
    private static final Pattern Q_NUMBER_REGEX = Pattern.compile("\\\\q\\d");
    private static final Pattern Q_REGEX = Pattern.compile("\\\\q[0-9]*\\s*\\n*.+");


    static public Map<String, String> parseUsfm(byte[] usfmBytes) throws CharacterCodingException {

        Charset utfSet = Charset.forName("UTF-8");
        CharsetDecoder decoder = utfSet.newDecoder();
        CharBuffer buffer = decoder.decode(ByteBuffer.wrap(usfmBytes));

        String usfmString = String.valueOf(buffer.array());
        ArrayList<String> chaptersArray = getChapters(usfmString);

        Map<String, String> chapters = new HashMap<String, String>();

        for (String chapter : chaptersArray) {

            String finalChapterText = "<div =\"chapter-div\">";
            Matcher numberMatcher = NUMBER_REGEX.matcher(chapter);

            String chapterNumber = "";
            while (numberMatcher.find()) {
                chapterNumber = numberMatcher.group(0);
                break;
            }

            int chapterStartIndex = chapter.indexOf("\\");
            chapter = chapter.substring(chapterStartIndex);

            chapter = replaceQs(chapter);
            chapter = replaceVerseTags(chapter);
            chapter = addLinebreaks(chapter);
            chapter = cleanUp(chapter);

            finalChapterText = "<p>" + chapter + "</p></div>";
//            System.out.println(finalChapterText);
            chapters.put(chapterNumber, finalChapterText);
        }

        return chapters;
    }

    static public ArrayList<String> getChapters(String chapters) {

        String[] chapterArray = chapters.split("\\\\c");

        ArrayList<String> chapterList = new ArrayList<String>();

        for (int i = 1; i < chapterArray.length; i++) {
            chapterList.add(chapterArray[i]);
        }

        return chapterList;
    }

    static public String replaceVerseTags(String text) {

        Matcher verseMatcher = VERSE_REGEX.matcher(text);

        ArrayList<String> verseText = new ArrayList<String>();
        while (verseMatcher.find()) {
            verseText.add(verseMatcher.group(0));
        }

        if (verseText.isEmpty()) {
            return text;
        }


        for (String verseString : verseText) {


            String verseLessString = verseString;

            verseLessString = verseLessString.replace("\\v ", "");
            verseLessString = "<span class=\"verse\"> " + verseLessString + "</span>";

            text = text.replace(verseString, verseLessString);
        }

        return text;
    }

    static public String replaceQs(String text) {


        Matcher qMatcher = Q_REGEX.matcher(text);

        ArrayList<String> qText = new ArrayList<String>();
        while (qMatcher.find()) {
            qText.add(qMatcher.group(0));
        }

        if (qText.isEmpty()) {
            return text;
        }

        String qlessTex = "";

        for (String qString : qText) {

            String qLessString = qString;
            Matcher numberMatcher = Q_NUMBER_REGEX.matcher(qLessString);

            String qNumber = "";
            while (numberMatcher.find()) {
                qNumber = (String) numberMatcher.group(0);
                break;
            }
            if(qNumber.length() < 1){
//                System.out.println("here");
            }
            else{
                qNumber = qNumber.substring(2);
//                System.out.println("or there");
            }

            qLessString = qLessString.replace("\\q" + qNumber, "<span class=\"q" + qNumber + "\">") + "</span>";

            text = text.replace(qString, qLessString);
        }

        return text;
    }

    static public String addLinebreaks(String text) {
        if (text.substring(0, 2).equalsIgnoreCase("\\p")) {
            text = text.substring(2);
        }
        text = text.replace("\\b", "<br/><br/>");
        String sRegex = "\\\\pi\\d*";
        text = text.replaceAll(sRegex, "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
        text = text.replace("\\p", "<br/>");
        return text;

    }

    static public String cleanUp(String text) {

        String sRegex = "\\\\s\\d*";
        text = text.replaceAll(sRegex, "");
        text = text.replace("\n", " ");
        return text;
    }
}
