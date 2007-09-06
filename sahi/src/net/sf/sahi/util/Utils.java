/**
 * Sahi - Web Automation and Test Tool
 *
 * Copyright  2006  V Narayan Raman
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.sf.sahi.util;

import java.io.*;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: nraman Date: Jun 26, 2005 Time: 4:52:58 PM
 */
public class Utils {
    public static String escapeDoubleQuotesAndBackSlashes(String line) {
        if (line == null)
            return null;
        return line.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\"");
    }

    public static byte[] getBytes(InputStream in) throws IOException {
        return getBytes(in, -1);
    }

    public static byte[] getBytes(InputStream in, int contentLength)
            throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int c = ' ';
        int count = 0;
        try {
            while ((contentLength == -1 || count < contentLength)
                    && (c = in.read()) != -1) {
                count++;
                out.write(c);
            }
        } catch (SocketTimeoutException ste) {
            ste.printStackTrace();
        }
        return out.toByteArray();
    }

    public static byte[] readURL(String url) {
        byte[] data = null;
        InputStream inputStream = null;
        try {
            inputStream = new URL(url).openStream();
            data = getBytes(inputStream, -1);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    static Map fileCache = new HashMap();

    public static byte[] readCachedFile(String fileName) {
        if (!fileCache.containsKey(fileName)) {
            fileCache.put(fileName, readFile(fileName));
        }
        return (byte[]) fileCache.get(fileName);
    }

    public static byte[] readFile(String fileName) {
        File file = new File(fileName);
        return readFile(file);
    }

    public static byte[] readFile(File file) {
        if (file != null && file.isDirectory()) {
            throw new FileIsDirectoryException();
        }
        byte[] data = null;
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            data = getBytes(inputStream, -1);
        } catch (IOException e) {
            throw new FileNotFoundRuntimeException(e);
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    // Returns a string than has all its non-ASCII characters converted to its
    // ASCII
    // equivalent. Eg : By passing "�l�phant", the function would return
    // "Elephant".
    public static String convertStringToASCII(String s) {
        return s.replaceAll("(�|�|�|�)", "e").replaceAll("(�|�|�|�)", "u")
                .replaceAll("(�|�|�|�|�|�)", "a").replaceAll("�", "ae")
                .replaceAll("(�|�|�|�)", "i").replaceAll("(�|�|�|�|�|�)", "o")
                .replaceAll("(�|�)", "y").replaceAll("�", "n").replaceAll("�",
                        "c").replaceAll("(�|�|�|�|�|�)", "A").replaceAll("�",
                        "AE").replaceAll("�", "C").replaceAll("(�|�|�|�)", "E")
                .replaceAll("(�|�|�|�)", "I").replaceAll("�", "N").replaceAll(
                        "(�|�|�|�|�|�)", "O").replaceAll("(�|�|�|�)", "U")
                .replaceAll("�", "Y");
    }

    public static synchronized String createLogFileName(String scriptFileName) {
        scriptFileName = new File(scriptFileName).getName();
        String date = new SimpleDateFormat("ddMMMyyyy__HH_mm_ss")
                .format(new Date());
        return convertStringToASCII(scriptFileName.replaceAll("[.].*$", "")
                + "__" + date);
    }

    public static File getRelativeFile(File parent, String s2) {
        File sf2 = new File(s2);
        if (sf2.isAbsolute())
            return sf2;
        if (!parent.isDirectory())
            parent = parent.getParentFile();
        File file = new File(parent, s2);
        return file;
    }

    public static String concatPaths(String s1, String s2) {
        File sf2 = new File(s2);
        if (sf2.isAbsolute())
            return s2;
        File parent = new File(s1);
        if (!parent.isDirectory())
            parent = parent.getParentFile();
        File file = new File(parent, s2);
        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            return file.getAbsolutePath();
        }
    }

    public static ArrayList getTokens(String s) {
        ArrayList tokens = new ArrayList();
        int ix1 = 0;
        int ix2 = -1;
        int len = s.length();
        while (ix1 < len && (ix2 = s.indexOf('\n', ix1)) != -1) {
            String token = s.substring(ix1, ix2 + 1);
            tokens.add(token);
            ix1 = ix2 + 1;
        }
        if (ix2 == -1) {
            String token = s.substring(ix1);
            tokens.add(token);
        }
        return tokens;
    }

    public static boolean isBlankOrNull(String s) {
        return (s == null || "".equals(s));
    }

    public static String substitute(String content, Properties substitutions) {
        String patternStr = "";
        int i = 0;
        Enumeration keys = substitutions.keys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            patternStr += (i++ == 0 ? "" : "|") + "\\$" + key;
        }
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(content);

        StringBuffer buf = new StringBuffer();
        while (matcher.find()) {
            String key = matcher.group(0).substring(1);
            String replaceStr = substitutions.getProperty(key).replaceAll(
                    "\\$", "SDLR");
            matcher.appendReplacement(buf, replaceStr);
        }
        matcher.appendTail(buf);
        return buf.toString().replaceAll("SDLR", "\\$");
    }

    public static String makeString(String s) {
        if (s == null)
            return null;
        return escapeDoubleQuotesAndBackSlashes(s).replaceAll("\n", "\\\\n")
                .replaceAll("\r", "");

    }

    public static String escapeQuotesForXML(String input) {
        return input.replaceAll("\"", "&quot;");
    }

    public static String escapeQuotes(String input) {
        return input.replaceAll("\"", "\\\\\"");
    }

    public static String stripChildSessionId(String sessionId) {
        return sessionId.replaceFirst("sahix[0-9]+x", "");
    }

    public static void deleteDir(File dir) {
        try {
            if (dir.exists()) {
                File[] files = dir.listFiles();
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        deleteDir(files[i]);
                    } else {
                        files[i].delete();
                    }
                }
                dir.delete();
            }
        } catch (Exception e) {
        }
    }

    public static String makePathOSIndependent(String path) {
        String separator = System.getProperty("file.separator");
        return path.replace('/', separator.charAt(0));
    }

    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase()
                .startsWith("windows");
    }

    public static boolean isWindowsNT() {
        return System.getProperty("os.name").equals("Windows NT");
    }

    public static boolean isWindows95() {
        return System.getProperty("os.name").equals("Windows 95");
    }

    public static String[] getCommandTokens(String commandString){
        String[] ar = commandString.split("\"");
        ArrayList tokens = new ArrayList();
        for (int i=0; i<ar.length; i++){
            if (commandString.indexOf("\""+ar[i]+"\"") != -1){
                tokens.add(ar[i]);
            }else{
                String[] subtokens = ar[i].split(" ");
                for (int j=0; j<subtokens.length; j++){
                    tokens.add(subtokens[j]);
                }
            }
        }
        return (String[]) tokens.toArray(new String[0]);
    }
}
