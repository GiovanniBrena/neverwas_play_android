package com.neverwasradio.neverwasplayer.Core;

import com.neverwasradio.neverwasplayer.Model.NWProgram;


import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class XMLProgramParser
{
    public static void main(String[] args)
    {
        System.out.println("Starting...");
        try {
            new XMLProgramParser().start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Finish");
    }

    public static ArrayList<NWProgram> getPrograms() {

        try {
            return new XMLProgramParser().start();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private ArrayList<NWProgram> start() throws Exception
    {
        ArrayList<NWProgram> programList = new ArrayList<NWProgram>();

        System.out.println("Connecting...");
        URL url = new URL("http://mpwebtest.altervista.org/NWpalinsesto.xml");
        URLConnection connection = url.openConnection();

        Document doc = parseXML(connection.getInputStream());
        NodeList descNodes = doc.getElementsByTagName("program");

        int n = descNodes.getLength();
        if(n<1) {
            System.out.println("Error, wrong Url or list empty");
        }

        else {
            for(int i=0; i<n;i++)
            {

                NWProgram p = new NWProgram(descNodes.item(i));
                programList.add(p);

            }
        }

        return programList;
    }

    private Document parseXML(InputStream stream)
            throws Exception
    {
        DocumentBuilderFactory objDocumentBuilderFactory = null;
        DocumentBuilder objDocumentBuilder = null;
        Document doc = null;
        try
        {
            objDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
            objDocumentBuilder = objDocumentBuilderFactory.newDocumentBuilder();

            doc = objDocumentBuilder.parse(stream);
        }
        catch(Exception ex)
        {
            throw ex;
        }

        return doc;
    }

}