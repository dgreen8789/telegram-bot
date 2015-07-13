
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author David
 */
public class JSONParser {

    /**
     * Parses a JSON-Encoded String into an array of {@code Update} Objects;
     *
     * @param s the JSON-encoded {@code String} to parse
     * @return an array of {@code Update} objects, or null if {@code s} is
     * invalid or malformed;
     */
    public static Update[] parse(String s) {
        //Check to make sure that the HTTP response was valid
        if (!s.startsWith("{\"ok\":true")) {
            System.out.println("INVALID");
            return null;
        }
        //Get the substring that represents the array of Update opjects
        s = s.substring(s.indexOf('[') + 1, s.length() - 2);
        //Parse the Updates, using the appropriate class
        Update[] parsedMessages = (Update[]) parseArray(s, Update[].class);
        System.out.println("parsed");
        return parsedMessages;
    }

    /**
     * Returns the {@code Class} associated with the {@code Field} named
     * {@code name}, in {@code c}
     *
     * @param name the name of the field
     * @param c the class to perform the lookup in
     * @return the {@code Class}
     */
    private static Class lookup(String name, Class c) {
        Field[] fields = c.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getName().equals(name)) {
                return fields[i].getType();
            }
        }
        return null;
    }

    /**
     * Parses Parses a JSON-Encoded String into an array of
     * {@code currentClass.getComponentType()} Objects;
     *
     * @param data the JSON-Encoded string, with no leading or ending brackets;
     * @param currentClass the Array class to attempt to parse {@code data} to
     * @return an array of {@code currentClass}
     */
    private static Object[] parseArray(String data, Class currentClass) {
        ArrayList<Object> array = new ArrayList<>();
        int index = 0;
        while (data.length() > index) {
            int index2 = getMatchingBraceIndex('{', '}', data, index);
            String x = data.substring(index, index2);
            Object z = parseObject(x, currentClass.getComponentType());
            array.add(z);
            index = index2 + 1;
        }
        Object[] z = (Object[]) Array.newInstance(currentClass.getComponentType(), array.size());
        for (int i = 0; i < z.length; i++) {
            z[i] = (currentClass.getComponentType().cast(array.get(i)));
        }
        return z;
    }

    /**
     * Parses Parses a JSON-Encoded String into an array of {@code currentClass}
     * Objects The method identifies all fields in the JSON string corresponding
     * to {@code currentClass}. If the field is a primitive, it is parsed
     * directly from {@code data}. Otherwise ,the method is recursively
     * called,using the substring representing the Object in JSON format, and
     * the {@code Class} returned by the "lookup" method last called. Array
     * classes use the "parseArray" method.
     *
     * @param data the JSON-Encoded string, with no leading or ending braces;
     * @param currentClass the Array class to attempt to parse {@code data} to
     * @return an Object of type {@code currentClass}
     */
    private static Object parseObject(String data, Class currentClass) {
        int index = 0;
        Object returnObj = null;
        try {
            returnObj = currentClass.newInstance();
        } catch (InstantiationException ex) {
        } catch (IllegalAccessException ex) {
        }
        while (data.length() > 1) {
            String name = data.substring(2, data.indexOf('"', 2));
            Class c = lookup(name, currentClass);
            index = name.length() + 3;
            Field f = null;
            try {
                f = currentClass.getDeclaredField(name);
            } catch (NoSuchFieldException ex) {
            } catch (SecurityException ex) {
            }
            if (c.isArray()) {
                index = data.indexOf('[');
                System.out.println("array of type " + c.toString());
                int index2 = getMatchingBraceIndex('[', ']', data, index);
                try {
                    f.set(returnObj, parseArray(data.substring(index, index2 - 1), c));
                } catch (IllegalArgumentException ex) {
                } catch (IllegalAccessException ex) {
                }
                index = index2;
            } else {
                if (c.equals(String.class)) {
                    String val;
                    val = parseString(name, data);
                    index += val.length() + 3;
                    if (val.trim().equals("")) {
                        val = "Sorry, Emojis are not supported as input yet";
                    }
                    try {
                        f.set(returnObj, val);
                    } catch (IllegalArgumentException ex) {
                    } catch (IllegalAccessException ex) {
                    }
                } else if (c.equals(int.class)) {
                    int val = parseInt(name, data);
                    try {
                        f.setInt(returnObj, val);
                    } catch (IllegalArgumentException ex) {
                    } catch (IllegalAccessException ex) {
                    }
                    index += Math.floor(Math.log10(val)) + 2;
                } else if (c.equals(double.class)) {
                    double val = parseDouble(name, data);
                    try {
                        f.setDouble(returnObj, val);
                    } catch (IllegalArgumentException ex) {
                    } catch (IllegalAccessException ex) {
                    }
                    index += Double.toString(val).length() + 1;
                } else if (c.equals(boolean.class)) {

                    boolean val = parseBoolean(name, data);
                    try {
                        f.setBoolean(returnObj, val);
                    } catch (IllegalArgumentException ex) {
                    } catch (IllegalAccessException ex) {
                    }
                    index += Boolean.toString(val).length() + 1;
                } else {
                    int index2 = getMatchingBraceIndex('{', '}', data, index + 1);
                    try {
                        f.set(returnObj, parseObject(data.substring(index + 1, index2), c));
                    } catch (IllegalArgumentException ex) {
                    } catch (IllegalAccessException ex) {
                    }
                    index = index2;
                }
            }
            data = data.substring(index);

        }
        return returnObj;
    }

    /**
     * Finds a String object in a JSON encoded String
     *
     * @param userParam the name of the parameter to parse in the JSON String
     * @param data the JSON string - does not have to be complete, but the
     * section "<userParam>:<value>" must exist, and be formatted properly.
     * @return an String representing the value in the JSON string, or null if
     * the parameter {@code userParam} was not found
     */
    private static String parseString(String userParam, String data) {
        if (data.contains(userParam)) {
            System.out.println("started");
            String z = getData(userParam, data);
            String x = new String(z.substring(1, z.length() - 1));
            String regexPattern = "\\\\u[\\p{XDigit}]{4}+\\\\u[\\p{XDigit}]{4}+";
            Pattern pattern = null;
            try {
                pattern = Pattern.compile(regexPattern);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Matcher matcher = pattern.matcher(new String(x.toCharArray()));
            if (matcher.find()) {
                char[] y = new char[data.length()];
                Arrays.fill(y, ' ');
                return new String(y);
                // int codePoint1 = Integer.parseInt(matcher.group().substring(3), 16);
                //int codePoint2 = Integer.parseInt(matcher.group().substring(3), 16);
                //System.out.println(codePoint1 + " " + codePoint2);
//                try {
//                    String match = matcher.group();
//                    System.out.println(match);
//                    String[] chars = match.split("\\\\u");
//                    char codePoint = (char) (Integer.parseInt(chars[1], 16) * 0xFFFF + Integer.parseInt(chars[2], 16));
//                    //System.out.println(codePoint + "");
//                    x = x.replace(match, "");//new String(new char[]{codePoint}));
//                    //System.out.println(x.contains(match));
//                    //System.out.println("replaced");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
            System.out.println("returned");
            return x;
        }
        return null;
    }

    /**
     * Finds an integer value in a JSON encoded String
     *
     * @param userParam the name of the parameter to parse in the JSON String
     * @param data the JSON string - does not have to be complete, but the
     * section "<userParam>:<value>" must exist, and be formatted properly.
     * @return an int representing the value in the JSON string, or 0 if the
     * parameter {@code userParam} was not found
     */
    private static int parseInt(String userParam, String data) {
        if (data.contains(userParam)) {
            return Integer.parseInt(getData(userParam, data));
        }
        return 0;
    }

    /**
     * Finds a double value in a JSON encoded String
     *
     * @param userParam the name of the parameter to parse in the JSON String
     * @param data the JSON string - does not have to be complete, but the
     * section "<userParam>:<value>" must exist, and be formatted properly.
     * @return an double representing the value in the JSON string, or 0 if the
     * parameter {@code userParam} was not found
     */
    private static double parseDouble(String userParam, String data) {
        if (data.contains(userParam)) {
            return Double.parseDouble(getData(userParam, data));
        }
        return 0;
    }

    /**
     * Finds a boolean value in a JSON encoded String
     *
     * @param userParam the name of the parameter to parse in the JSON String
     * @param data the JSON string - does not have to be complete, but the
     * section "<userParam>:<value>" must exist, and be formatted properly.
     * @return an boolean representing the value in the JSON string, or false if
     * the parameter {@code userParam} was not found
     */
    private static boolean parseBoolean(String userParam, String data) {
        if (data.contains(userParam)) {
            return Boolean.parseBoolean(getData(userParam, data));
        }
        return false;
    }

    /**
     * Finds the substring associated with a parameter in a JSON-Encoded String
     *
     * @param userParam the name of the parameter to parse in the JSON String
     * @param data the JSON string - does not have to be complete, but the
     * section "<userParam>:<value>" must exist, and be formatted properly.
     * @return the value associated with {@code userParam
     */
    private static String getData(String userParam, String data) {
        int q = data.indexOf(userParam);
        int end = data.indexOf(",", q);
        end = end < 0 ? data.length() - 1 : end;
        String val = data.substring(q + 2 + userParam.length(), end);
        return val;
    }

    /**
     * Finds the index of appropriate ending brace/bracket to a given starting
     * brace/bracket
     *
     * @param start the character representing the start of a brace or bracket,
     * either '{' or '['
     * @param end the character representing the end of a brace or bracket,
     * either '}' or ']'
     * @param str the {@code String} to search in.
     * @param startIndex the index of the first character matching {@code start}
     * @return the index of the appropriate ending brace/bracket, or
     * {@code str.length()} if not found.
     */
    private static int getMatchingBraceIndex(char start, char end, String str, int startIndex) {
        boolean inBetweenQuotes = false;
        boolean canFinish = false;
        int braceIndex = 0;
        do {
            char c = str.charAt(startIndex);
            if (c == '"' && str.charAt(startIndex - 1) != '\\') {
                inBetweenQuotes = !inBetweenQuotes;
            }

            if (c == start && !inBetweenQuotes) {
                braceIndex++;
                canFinish = true;
            }
            if (c == end && !inBetweenQuotes) {
                braceIndex--;
            }
            startIndex++;
            //System.out.println(braceIndex);
        } while (braceIndex != 0 && canFinish && startIndex <= str.length());

        return startIndex;
    }
}
