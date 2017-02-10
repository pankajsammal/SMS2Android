package com.example.sam29.sms2android;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sam29 on 07-Feb-17.
 */

public class XMLParser {

    private static final String ns = null;

    public static List parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private static List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List entries = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "ArrayOfMessage");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("Message")) {
                entries.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    public static class Entry {
        public final String Recepients;
        public final String Body;
        public final String IsIncoming;
        public final String IsRead;
        //public final String Attachments;
        public final String LocalTimestamp;
        public final String Sender;

        private Entry(String Recepients, String Body, String IsIncoming, String IsRead, String LocalTimestamp, String Sender) {
            this.Recepients = Recepients;
            this.Body = Body;
            this.IsRead = IsRead;
            //this.Attachments = Attachments;
            this.LocalTimestamp = LocalTimestamp;
            this.IsIncoming = IsIncoming;
            this.Sender = Sender;
        }
    }

    private static Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "Message");
        String recepients = null;
        String body = null;
        String isIncoming = null;
        String isRead = null;
        String localTimestamp = null;
        String sender = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("Recepients")) {
                recepients = readRecepients(parser);
            } else if (name.equals("Body")) {
                body = readTag(parser, "Body");
            } else if (name.equals("IsRead")) {
                isRead = readTag(parser, "IsRead");
            }
//            else if (name.equals("Attachments")) {
//                link = readLink(parser);
//            }
            else if (name.equals("LocalTimestamp")) {
                localTimestamp = readTag(parser, "LocalTimestamp");
            } else if (name.equals("IsIncoming")) {
                isIncoming = readTag(parser, "IsIncoming");
            } else if (name.equals("Sender")) {
                sender = readTag(parser, "Sender");
            } else {
                skip(parser);
            }
        }
        return new Entry(recepients, body, isIncoming, isRead, localTimestamp, sender);
    }

    private static String readTag(XmlPullParser parser, String tag) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, tag);
        String tags = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, tag);
        return tags;
    }

    private static String readRecepients(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "Recepients");
        String recepients = "";
        if (parser.nextTag() == XmlPullParser.START_TAG) {
            if (parser.next() == XmlPullParser.TEXT) {
                recepients = parser.getText();
                parser.nextTag();
                parser.nextTag();
                //parser.require(XmlPullParser.END_TAG, ns, "string");
                return recepients;
            }
        }
        //String summary = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "Recepients");
        return recepients;
    }

    private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }


}
