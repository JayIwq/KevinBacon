
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.json.JSONObject;
import org.json.JSONArray;
import com.kevinbacon.wikipage.*;



public class KevinBacon {

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private static String encodeValue(String value) {
        try {
            return (URLEncoder.encode(value, "UTF-8")).replaceAll("%28", "(").replaceAll("%29", ")");


        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }


    public static JSONObject readJsonFromUrl(String url) throws IOException {
        InputStream is = new URL( url ).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText).getJSONObject("parse");
            return json;
        }
        catch(Exception e) {
            if ( e.getMessage().equals("JSONObject[\"parse\"] not found.")) {
                return null;
            }
            System.out.println(e);
        }
        finally {
            is.close();
        }
        return null;
    }

    public static ArrayList<WikiPage> getChildrenArray(JSONObject node, WikiPage current) {
        try {
            String uri = "https://en.wikipedia.org/w/api.php?format=json&prop=links&action=parse&page=";
            JSONArray childrenJsonArray = node.getJSONArray("links");
            ArrayList<WikiPage> childrenArray = new ArrayList<>();
            for(int i = 0; i < childrenJsonArray.length(); i++) {
                JSONObject insideObject = childrenJsonArray.getJSONObject(i);
                String child = insideObject.getString("*");
                Boolean exsits = insideObject.has("exists");
                if(exsits) {
                    childrenArray.add(new WikiPage(uri + encodeValue(child), current) );
                }
            }
            return childrenArray;
        }
        catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        String uri = "https://en.wikipedia.org/w/api.php?format=json&prop=links&action=parse&page=";
        String start = uri + encodeValue("Footloose (1984 film)");
        String end = uri + encodeValue("Kevin Bacon");

        switch (args.length) {
            case 0: break;
            case 1: start = uri + encodeValue(args[0]); break;
            case 2: start = uri + encodeValue(args[0]); end = uri + encodeValue(args[1]); break;
            default: break;
        }

        if(start.equals(end)) {
            System.out.println(0);
            System.exit(0);
        }

        Queue<WikiPage> queue = new LinkedList<>();
        LinkedList<WikiPage> solution = new LinkedList<>();

        queue.add(new WikiPage(start, null));

        while(!queue.isEmpty()) {

            WikiPage current = queue.poll();

            JSONObject json = readJsonFromUrl((uri + current.getTitle() ));
            ArrayList<WikiPage> children = null;
            if(json != null) {
                children = getChildrenArray(json, current);
            }

            if(children != null) {
                for (WikiPage child: children) {

                    if(end.equals(child.getTitle())) {
                        WikiPage parent = current.getParent();
                        solution.add(new WikiPage(end, parent));
                        solution.add(current);
                        while(parent != null && !parent.getTitle().isEmpty()) {
                            solution.add(current.getParent());
                            parent = current.getParent().getParent();
                        }
                        queue.clear();
                        break;
                    }
                    if(!queue.contains(child)) {
                        queue.add(child);
                    }

                }
            }

        }
        System.out.println(solution.size());
        System.exit(0);
    }
}