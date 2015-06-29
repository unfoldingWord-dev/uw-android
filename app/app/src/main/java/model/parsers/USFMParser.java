package model.parsers;


import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Fechner on 2/26/15.
 */
public class USFMParser {

    private static final Pattern VERSE_REGEX = Pattern.compile("\\\\v\\s([0-9-])*\\s", Pattern.DOTALL);
    private static final Pattern NUMBER_REGEX = Pattern.compile("\\s*(\\d*)");
    private static final Pattern Q_NUMBER_REGEX = Pattern.compile("\\\\q\\d");
    private static final Pattern Q_REGEX = Pattern.compile("\\\\q[0-9]*\\s*\\n*.+");

    private static final Pattern FOOTNOTE_REGEX = Pattern.compile("(\\\\f)(\\s+)((.*)(\\n*)){1,5}(\\\\f[*])");
    private static final Pattern FOOTNOTE_TEXT_REGEX = Pattern.compile("(\\\\f.)(\\s)*(\\+)(\\s)(\\\\ft)*\\s*(.)*\\n(\\\\fqa)");
    private static final Pattern FOOTNOTE_VERSE_REGEX = Pattern.compile("\\\\fqa.*\\\\f[*]");

    private static final String TAB = "&nbsp;&nbsp;&nbsp;&nbsp;";

    private static USFMParser instance = null;

    private int footnoteNumber = 1;

    /**
     * Processes usfm and returns a map with key of the chapter number and object of the parsed usfm text for that chapter
     * @param usfmBytes
     * @return
     * @throws CharacterCodingException
     */
    public Map<String, String> getChaptersFromUsfm(byte[] usfmBytes) throws CharacterCodingException {


        String text = getStringFromBytes(usfmBytes);
        ArrayList<String> chaptersArray = getChapters(text);

        Map<String, String> chapters = new HashMap<String, String>();

        for (String chapter : chaptersArray) {

            Matcher numberMatcher = NUMBER_REGEX.matcher(chapter);

            String chapterNumber = "";
            while (numberMatcher.find()) {
                chapterNumber = numberMatcher.group(0);
                break;
            }

            int chapterStartIndex = chapter.indexOf("\\");
            chapter = chapter.substring(chapterStartIndex);
            chapters.put(chapterNumber, chapter);
        }

        return chapters;
    }



    public String getStringFromBytes(byte[] bytes) throws CharacterCodingException{

        Charset utfSet = Charset.forName("UTF-8");
        CharsetDecoder decoder = utfSet.newDecoder();
        CharBuffer buffer = decoder.decode(ByteBuffer.wrap(bytes));

        String byteString = String.valueOf(buffer.array());
        return byteString;
    }


    public String parseUsfmChapter(String chapter){

        footnoteNumber = 1;
        String finalChapterText = "<div =\"chapter-div\">";

        chapter = replaceQs(chapter);
        chapter = replaceVerseTags(chapter);
        chapter = findFootnotes(chapter);
        chapter = addLineBreaks(chapter);
        chapter = cleanUp(chapter);

        finalChapterText += "<p>" + TAB + chapter + "</p></div>";

        return finalChapterText;
    }

    private ArrayList<String> getChapters(String chapters) {

        String[] chapterArray = chapters.split("\\\\c");

        ArrayList<String> chapterList = new ArrayList<String>();

        chapterList.addAll(Arrays.asList(chapterArray).subList(1, chapterArray.length));

        return chapterList;
    }

    private String replaceVerseTags(String text) {

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

    private String replaceQs(String text) {

        Matcher qMatcher = Q_REGEX.matcher(text);

        ArrayList<String> qText = new ArrayList<String>();
        while (qMatcher.find()) {
            qText.add(qMatcher.group(0));
        }

        if (qText.isEmpty()) {
            return text;
        }

        for (String qString : qText) {

            String qLessString = qString;
            Matcher numberMatcher = Q_NUMBER_REGEX.matcher(qLessString);

            String qNumber = "";
            while (numberMatcher.find()) {
                qNumber = (String) numberMatcher.group(0);
                break;
            }
            if(qNumber.length() >= 1){
                qNumber = qNumber.substring(2);
//                System.out.println("or there");
            }

            qLessString = qLessString.replace("\\q" + qNumber, "<span class=\"q" + qNumber + "\">") + "</span>";
            text = text.replace(qString, qLessString);
        }

        return text;
    }

    private String findFootnotes(String text){

        Matcher verseMatcher = FOOTNOTE_REGEX.matcher(text);

        ArrayList<String> verseText = new ArrayList<String>();
        while (verseMatcher.find()) {
            verseText.add(verseMatcher.group(0));
        }

        if(verseText.size() > 0){
            for(String footnote : verseText){
                String footnoteText = findFootnoteText(footnote);
                String footnoteNumberText = "<sup class=\"footnote-number\">" + Integer.toString(this.footnoteNumber) + "</sup>";
                String footnoteVerse = findFootnoteVerseText(footnote) + footnoteNumberText;
                text = text.replace(footnote, footnoteVerse);
                text = text + "<p class=\"footnote\">" + footnoteNumberText + footnoteText  + "</p>";
                footnoteNumber++;
            }
            return text;
        }
        else{
            return text;
        }
    }

    private String findFootnoteVerseText(String text){

        Matcher verseMatcher = FOOTNOTE_VERSE_REGEX.matcher(text);

        ArrayList<String> verseText = new ArrayList<String>();
        while (verseMatcher.find()) {
            verseText.add(verseMatcher.group(0));
        }

        if(verseText.size() > 0){
            for(String footnote : verseText){
                footnote = footnote.replaceAll("\\\\fqa*\\s*", "");
                footnote = footnote.replaceAll("\\\\f[*]", "");
                return footnote;
            }
        }
        return text;
    }

    private String findFootnoteText(String text){

        Matcher verseMatcher = FOOTNOTE_TEXT_REGEX.matcher(text);

        ArrayList<String> verseText = new ArrayList<String>();
        while (verseMatcher.find()) {
            verseText.add(verseMatcher.group(0));
        }

        if(verseText.size() > 0){
            for(String footnote : verseText){
                footnote = footnote.replaceAll("(\\\\f.)(\\s)*(\\+)(\\s)(\\\\ft)*\\s*", "");
                footnote = footnote.replaceAll("\\\\fqa", "");
                return footnote;
            }
        }
        return text;
    }

    private String addLineBreaks(String text) {

        if (text.substring(0, 2).equalsIgnoreCase("\\p")) {
            text = text.substring(2);
        }
        text = text.replace("\\b", "<br/><br/>");
        String sRegex = "\\\\pi\\d*";
        text = text.replaceAll(sRegex, "<br/>" + TAB);
        text = text.replace("\\p", "<br/>" + TAB);
        return text;

    }

    private String cleanUp(String text) {

        String sRegex = "\\\\(\\S)*\\s*";
        text = text.replaceAll(sRegex, "");
        text = text.replace("\n", " ");
        text = text.replace("\\m ", "");
        return text;
    }
}
