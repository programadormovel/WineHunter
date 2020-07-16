package br.com.buscarvinhos;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchMain {

    private List<MyLink> links = new ArrayList<MyLink>();
    private int MAX_LINKS = 100;
    private int count = 0;

    public static void main(String[] args) {

        SearchMain searchMain = new SearchMain();
        searchMain.init("http://www.frameworksystem.com");
        searchMain.navigate();
        searchMain.printLinks();
    }

    public void init(String url) {
        MyLink myLink = new MyLink();
        myLink.setLink(url);
        links.add(myLink);
    }

    private void navigate() {

        while ((count < links.size()) && (links.size() < MAX_LINKS)) {

            MyLink parentLink = links.get(count);

            if (!parentLink.isVisited()) {
                String page = visitPage(parentLink);
                List<String> list = findHref(page);
                addLinks(parentLink, list);

                count++;

            }

        }

        System.out.println("�������������������");
        System.out.println(" Aborting after " + MAX_LINKS + " storaged links in cache");
        System.out.println("�������������������");
    }

    public void printLinks() {

        for (MyLink myLink : links) {
            System.out.println(myLink.getLink() + " � visited: " + myLink.isVisited() + " � level: " + myLink.getLevel());
        }

    }

    private void addLinks(MyLink parentLink, List<String> list) {

        for (String link : list) {

            MyLink myLink = new MyLink();
            myLink.setLink(link);
            myLink.setLevel(parentLink.getLevel() + 1);

            if (link.contains(links.get(0).getLink()) &&
                    (!link.contains("tag")) &&
                    (!link.contains("respond")) &&
                    (!link.contains("wp-login")) &&
                    (!link.contains("comments")) &&
                    (!link.contains("jpg")) &&
                    (!link.contains("png")) &&
                    (!links.contains(myLink))) {
                links.add(myLink);
            }
        }

    }

    private List<String> findHref(String page) {

        String regex = "\\b" + "a href" + "\\b";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(page);

        List<String> list = new ArrayList<String>();

        while (matcher.find() == true) {
            int end = matcher.end();
            String hrefQuote = page.substring(end + 1, end + 2);
            int index = page.indexOf(hrefQuote, end + 2);

            if (page.substring(index - 1, index).equals("/"))
                index = index - 1;

            String word = page.substring(end + 2, index);
            list.add(word);
        }

        return list;

    }

    private String visitPage(MyLink myLink) {

        try {

            System.out.println("Visiting: " + myLink.getLink() + " � total links in cache: " + links.size());

            myLink.setVisited(true);

            URL url = new URL(myLink.getLink());
            URLConnection conn = url.openConnection();
            InputStream inputStream = conn.getInputStream();

            final int bufferSize = 1024;
            final char[] buffer = new char[bufferSize];
            final StringBuilder out = new StringBuilder();
            Reader in = new InputStreamReader(inputStream, "UTF-8");
            for (; ; ) {
                int rsz = in.read(buffer, 0, buffer.length);
                if (rsz < 0)
                    break;
                out.append(buffer, 0, rsz);
            }

            return out.toString();


        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    private class MyLink {

        private String link;
        private boolean visited = false;
        private int level = 0;

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public boolean isVisited() {
            return visited;
        }

        public void setVisited(boolean visited) {
            this.visited = visited;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((link == null) ? 0 : link.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            MyLink other = (MyLink) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (link == null) {
                if (other.link != null)
                    return false;
            } else if (!link.equals(other.link))
                return false;
            return true;
        }

        private SearchMain getOuterType() {
            return SearchMain.this;
        }


    }


}