/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package model.parsers;


import junit.framework.Assert;

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
 * Created by PJ Fechner on 2/26/15.
 * Class for parsing USFM
 */
public class USFMParser {

    private static final String TAG = "USFMParser";

    private static final Pattern VERSE_REGEX = Pattern.compile("\\\\v\\s([0-9-])*\\s", Pattern.DOTALL);
    private static final Pattern NUMBER_REGEX = Pattern.compile("\\s*(\\d*)");
    private static final Pattern Q_NUMBER_REGEX = Pattern.compile("\\\\q\\d");
    private static final Pattern Q_REGEX = Pattern.compile("\\\\(q)\\d?\\ .*");
    private static final Pattern D_REGEX = Pattern.compile("\\\\d.*");

    private static final String QS_REGEX = "\\\\(qs)\\d?\\ .*\\\\qs\\*";

    private static final Pattern SP_REGEX = Pattern.compile("\\\\sp.*");
    private static final Pattern ADD_REGEX = Pattern.compile("\\\\add.*\\\\add\\*", Pattern.DOTALL);
    private static final Pattern FOOTNOTE_REGEX = Pattern.compile("\\\\f\\s+.*\\n*\\\\f[*]");
    private static final Pattern FOOTNOTE_TEXT_REGEX = Pattern.compile("\\\\f.*\\\\f\\*", Pattern.DOTALL);
    private static final Pattern FOOTNOTE_VERSE_REGEX = Pattern.compile("\\\\fqa.*\\\\f[*]");

    private static final Pattern SINGLE_CHAPTER_BOOK_NAME_REGEX = Pattern.compile("\\\\cl.(.*)");

    private static final String TAB = "&nbsp;&nbsp;&nbsp;&nbsp;";

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

            if (chapterNumber.trim().length() < 1) {
                Assert.fail();
            }
            int chapterStartIndex = chapter.indexOf("\\");
            if(chapterStartIndex > -1) {
                chapter = chapter.substring(chapterStartIndex);
                chapters.put(chapterNumber, chapter);
            }
        }

        return chapters;
    }

    public static String getSingleChapterBookName(String text){

        Matcher verseMatcher = SINGLE_CHAPTER_BOOK_NAME_REGEX.matcher(text);

        while (verseMatcher.find()) {
            String name = verseMatcher.group(0);
            name = name.substring(name.indexOf(" "), name.length()).trim();
            return name;
        }
        return "";
    }

    static public String getStringFromBytes(byte[] bytes) throws CharacterCodingException{

        Charset utfSet = Charset.forName("UTF-8");
        CharsetDecoder decoder = utfSet.newDecoder();
        CharBuffer buffer = decoder.decode(ByteBuffer.wrap(bytes));

        String byteString = String.valueOf(buffer.array());
        return byteString;
    }


    public String parseUsfmChapter(String chapter){

        footnoteNumber = 1;

        chapter = handleDs(chapter);
        chapter = handleSPs(chapter);
        chapter = handleAdds(chapter);
        chapter = handleQSelahs(chapter);
        chapter = replaceQs(chapter);
        chapter = replaceVerseTags(chapter);
        chapter = findFootnotes(chapter);
        chapter = addLineBreaks(chapter);
        chapter = cleanUp(chapter);

        String finalChapterText = "<div class=\"chapter-div\"><p>" + chapter + "</p></div>";

        return finalChapterText;
    }

    private ArrayList<String> getChapters(String chapters) {

        String[] chapterArray = chapters.split("\\\\c ");

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

    private String handleQSelahs(String text){

        return text.replaceAll(QS_REGEX, "<span class=\"selah\">Selah<br/></span></br>");
    }

    private String handleSPs(String text) {

        Matcher spMatcher = SP_REGEX.matcher(text);

        ArrayList<String> spText = new ArrayList<>();
        while (spMatcher.find()) {
            spText.add(spMatcher.group(0));
        }

        if (spText.isEmpty()) {
            return text;
        }

        for (String spString : spText) {

            String spLessString = spString.replace("\\sp ", "<br/><p class=\"sp\">") + "</p><br/>";
            text = text.replace(spString, spLessString);
        }

        return text;
    }

    private String handleAdds(String text) {

        Matcher addMatcher = ADD_REGEX.matcher(text);

        ArrayList<String> addText = new ArrayList<>();
        while (addMatcher.find()) {
            addText.add(addMatcher.group(0));
        }

        if (addText.isEmpty()) {
            return text;
        }

        for (String addString : addText) {

            String addLessString = addString.replace("\\add ", "[").replace(" \\add*", "]");
            text = text.replace(addString, addLessString);
        }

        return text;
    }

    private String handleDs(String text){

        Matcher dMatcher = D_REGEX.matcher(text);

        ArrayList<String> dText = new ArrayList<>();
        while (dMatcher.find()) {
            dText.add(dMatcher.group(0));
        }

        if (dText.isEmpty()) {
            return text;
        }

        for (String dString : dText) {

            String dLessString = dString.replace("\\d", "</p><p class=\"d\">") + "</p><p>";
            text = text.replace(dString, dLessString);
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
                qNumber = numberMatcher.group(0);
                break;
            }
            if(qNumber.length() >= 1){
                qNumber = qNumber.substring(2);
//                System.out.println("or there");
            }

            if(qLessString.replace("\\q" + qNumber, "").replace("\\s", "").trim().length() > 4){
                qLessString = qLessString.replace("\\q" + qNumber, "<span class=\"q" + qNumber + "\">") + "</span>";
                text = text.replace(qString, qLessString);
            }
        }

        return text;
    }

    private String findFootnotes(String text) {

        Matcher verseMatcher = FOOTNOTE_REGEX.matcher(text);

        ArrayList<String> verseText = new ArrayList<>();
        while (verseMatcher.find()) {
            verseText.add(verseMatcher.group(0));
        }

        if(verseText.size() > 0) {
            for(String footnote : verseText){
                String footnoteText = findFootnoteText(footnote);
                String footnoteNumberText = "<sup class=\"footnote-number\">" + Integer.toString(this.footnoteNumber) + "</sup>";
//                String footnoteVerse = findFootnoteVerseText(footnote) + footnoteNumberText;
                text = text.replace(footnote, footnoteNumberText);
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
                footnote = footnote.replaceAll("\\\\f(\\w|\\+|\\*|\\s\\W)*", "");
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
        text = text.replace("\\b", "<br/>");
        String sRegex = "\\\\pi\\d*";
        text = text.replaceAll(sRegex, "<br/>" + TAB);
        text = text.replace("\\p", "<br/>");
        return text;

    }

    private String cleanUp(String text) {

        String sRegex = "\\\\(\\S)*\\s*";
        text = text.replaceAll(sRegex, "");
        text = text.replace("\n", " ");
        text = text.replace("\\m ", "");
        text = text.replace("\\q ", "");
        return text;
    }

    public static String getTextCss(int textSize, String textDirection){

        String css = "<style type=\"text/css\">\n" +
                ".selah {text-align: right; font-style: italic; float: right; padding-right: 1em;}\n" +
                ".verse { font-size: 9pt}\n" +
                ".q, .q1, .q2 { margin:0; display: block; padding:0;}\n" +
                ".q, .q1 { padding-left: 1em; }\n" +
                ".q2 { padding-left: 2em; }\n" +
                ".q3 { padding-left: 3em; }\n" +
                ".d {font-style: italic; text-align: center; padding: 0px; line-height: 0.9; font-size: " + Integer.toString(textSize - 2) + "pt; width: 90%; padding: 0 5% 0 5%;}\n" +
                "p { width:96%; font-size: " + Integer.toString(textSize) + "pt; text-align: justify; line-height: 1.3; padding:5px; unicode-bidi:bidi-override; direction:" +
                textDirection + ";}\n" +
                ".footnote {font-size: 11pt;}\n" +
                "sup {font-size: 9pt;}\n" +
                "sp {font-style: italic;}\n" +

                "</style>\n";
        return css;
    }
}
