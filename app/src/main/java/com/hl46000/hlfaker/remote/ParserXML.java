package com.hl46000.hlfaker.remote;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hl46000 on 10/4/17.
 */

public class ParserXML {
    public class ViewItem{
        private String text;
        private String contentDesception;
        private String packagename;
        private int[] centerCoordinates;
        private int[] bouns;
        private String id;

        public ViewItem(){
            text = "";
            contentDesception = "";
            packagename = "";
            centerCoordinates = new int[] {0,0};
            bouns = new int[] {0,0};
            id = "";
        }

        public String getText(){
            return text;
        }
        public void setText(String str){
            text = str;
        }
        public String getContentDesception(){
            return contentDesception;
        }
        public void setContentDesception(String str){
            contentDesception = str;
        }
        public String getPackagename(){
            return packagename;
        }
        public void setPackagename(String str){
            packagename = str;
        }
        public int[] getCenterCoordinates(){
            return centerCoordinates;
        }
        public void setCenterCoordinates(int[] coor){
            centerCoordinates = coor;
        }
        public int[] getBouns(){
            return bouns;
        }
        public void setBouns(int[] coor){
            bouns = coor;
        }
        public String getId(){
            return id;
        }
        public void setId(String str){
            id = str;
        }
    }
    private final String LAYOUT_FILE_PATH = "/sdcard/layout.xml";
    private final String LOG_TAG = "ParserXML";
    public List<ViewItem> listViewItem;

    public ParserXML(){
        listViewItem = new ArrayList<ViewItem>();
    }

    /**
     * Parser View Layout to List View Item
     * @return
     */
    public boolean parserLayout(){
        File xmlFile = new File(LAYOUT_FILE_PATH);
        if(!xmlFile.exists() && !xmlFile.canRead()){
            return false;
        }

        try{
            XmlPullParserFactory xmlPactory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlParser = xmlPactory.newPullParser();
            xmlParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlParser.setInput(new FileInputStream(xmlFile), null);
            listViewItem.clear();
            while (xmlParser.next() != XmlPullParser.END_DOCUMENT){
                if(xmlParser.getEventType() == XmlPullParser.START_TAG){
                    ViewItem item = new ViewItem();
                    if(xmlParser.getName().equals("node")){
                        item.setPackagename(xmlParser.getAttributeValue(null, "package"));
                        item.setContentDesception(xmlParser.getAttributeValue(null, "content-desc"));
                        item.setText(xmlParser.getAttributeValue(null, "text"));
                        item.setCenterCoordinates(getCenterCoor(xmlParser.getAttributeValue(null, "bounds")));
                        item.setBouns(getBouns(xmlParser.getAttributeValue(null, "bounds")));
                        item.setId(getID(xmlParser.getAttributeValue(null, "resource-id")));
                        listViewItem.add(item);
                        //Log.d(LOG_TAG, item.getText() + "|X,Y: " + item.getCenterCoordinates()[0] + "," + + item.getCenterCoordinates()[1]);
                    }
                }
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * Get Item ID
     * @param arg
     * @return
     */
    private String getID(String arg){
        String id = "";
        try{
            id = arg.split("/")[1];
            return id;
        }catch (Exception e){
            return id;
        }
    }

    /**
     * Get Bouns Coordinates of Item
     * @param coor
     * @return
     */
    private int[] getBouns(String coor){

        int x1, y1, x2, y2;
        int i = coor.indexOf("]");
        if(i == 0){
            return new int[] {0,0,0,0};
        }
        try {
            String coor1 = coor.substring(1, i);
            String coor2 = coor.substring(i + 2, coor.length() - 1);
            //Log.d(LOG_TAG, "Coor: " + coor);
            //Log.d(LOG_TAG, "i: " + i);
            //Log.d(LOG_TAG, "XY1: " + coor1);
            //Log.d(LOG_TAG, "XY2: " + coor2);
            String[] xy1 = coor1.split(",");
            String[] xy2 = coor2.split(",");
            x1 =  Integer.parseInt(xy1[0]);
            y1 =  Integer.parseInt(xy1[1]);
            x2 =  Integer.parseInt(xy2[0]);
            y2 =  Integer.parseInt(xy2[1]);
            return new int[] {x1, y1, x2, y2};
        }catch (Exception e){
            return new int[] {0,0,0,0};
        }
    }

    /**
     * Get Center Coordinates of Item
     * @param coor
     * @return
     */
    private int[] getCenterCoor(String coor){
        int x1, y1, x2, y2, x, y;
        int i = coor.indexOf("]");
        if(i == 0){
            return new int[] {0,0};
        }
        try {
            String coor1 = coor.substring(1, i);
            String coor2 = coor.substring(i + 2, coor.length() - 1);
            String[] xy1 = coor1.split(",");
            String[] xy2 = coor2.split(",");
            x1 =  Integer.parseInt(xy1[0]);
            y1 =  Integer.parseInt(xy1[1]);
            x2 =  Integer.parseInt(xy2[0]);
            y2 =  Integer.parseInt(xy2[1]);
            x = (x1 + x2)/2;
            y = (y1 + y2)/2;
            return new int[] {x, y};
        }catch (Exception e){
            return new int[] {0,0};
        }
    }
}
