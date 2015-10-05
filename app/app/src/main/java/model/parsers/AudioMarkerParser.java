package model.parsers;

import android.net.Uri;

import org.spongycastle.util.Arrays;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.AudioMarker;
import utils.FileUtil;

/**
 * Created by Fechner on 10/2/15.
 */
public class AudioMarkerParser {

    private static final String TAG = "AudioMarkerParser";

    private static final Pattern TRACKS_REGEX = Pattern.compile("<xmpDM:Tracks>(.|\\n)*</xmpDM:Tracks>");

//    private static final String TRACKS

    public static List<AudioMarker> createAudioMarkers(Uri audioUri){

        Element description = getDescriptionElement(getNeededText(audioUri));
        long frameRate = getFrameRate(description);

        return getAudioMarkersFromDescription(description, frameRate);
    }

    private static String getNeededText(Uri audioUri){

        File audioFile = new File(audioUri.getPath());

        byte[] headerBytes = getHeaderBytes(audioFile);
        if(headerBytes == null){
            return null;
        }

        String headerText = new String(headerBytes);
        return getTracksText(headerText);
    }

    private static byte[] getHeaderBytes(File file){

        byte[] audioBytes = FileUtil.getbytesFromFile(file);
        if(audioBytes == null){
            return null;
        }
        return Arrays.copyOfRange(audioBytes, 0, 500 * 1000);
    }

    private static String getTracksText(String header){

        Matcher numberMatcher = TRACKS_REGEX.matcher(header);

        if(numberMatcher.find()) {
            return numberMatcher.group(0);
        }
        else{
            return null;
        }
    }

    private static long getFrameRate(Element description){

        NamedNodeMap nodes = description.getAttributes();
        String frameRateValue = nodes.getNamedItem("xmpDM:frameRate").getTextContent();
        frameRateValue = frameRateValue.replace("f", "");

        long frameRate = Long.parseLong(frameRateValue);
        return frameRate;
    }


    private static List<AudioMarker> getAudioMarkersFromDescription(Element descriptionElement, long frameRate){

        Element markers = XMLParser.getElement(descriptionElement.getChildNodes(), "xmpDM:markers");
        Element seq = XMLParser.getElement(markers.getChildNodes(), "rdf:Seq");

        List<AudioMarker> markerList = new ArrayList<>();

        NodeList nodes = seq.getElementsByTagName("rdf:li");

        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i) instanceof Element) {
                Element element = (Element) nodes.item(i);
                String tagName = element.getTagName();
                if(tagName.equalsIgnoreCase("rdf:li")){
                    AudioMarker marker = getMarkFromLi(element, frameRate);
                    if(marker != null) {
                        markerList.add(marker);
                    }
                }
            }
        }
        return markerList;
    }

    private static AudioMarker getMarkFromLi(Element liElement, long frameRate){

        Element description = XMLParser.getElement(liElement.getChildNodes(), "rdf:Description");
        if(description != null) {
            NamedNodeMap nodes = description.getAttributes();

            long startTime = Long.parseLong(nodes.getNamedItem("xmpDM:startTime").getTextContent());

            long duration = -1;
            Node durationNode = nodes.getNamedItem("xmpDM:duration");
            if (durationNode != null) {
                duration = Long.parseLong(durationNode.getTextContent());
            }

            return new AudioMarker(getTimeForMarker(startTime, frameRate), getTimeForMarker(duration, frameRate));
        }
        else{
            return  null;
        }
    }

    private static Element getDescriptionElement(String text){

        Document doc = XMLParser.getDomElement(text);
        NodeList nodes = doc.getElementsByTagName("xmpDM:Tracks");

        Element tracks = XMLParser.getElement(nodes, "xmpDM:Tracks");
        Element bag = XMLParser.getElement(tracks.getChildNodes(), "rdf:Bag");
        Element li = XMLParser.getElement(bag.getChildNodes(), "rdf:li");
        return XMLParser.getElement(li.getChildNodes(), "rdf:Description");
    }

    private static long getTimeForMarker(long markerValue, long frameRate){

        return markerValue / frameRate;
    }
}

