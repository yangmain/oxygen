package vip.justlive.oxygen.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import vip.justlive.oxygen.core.exception.Exceptions;

/**
 * string utils
 *
 * @author wubo
 * @since 2.1.2
 */
@UtilityClass
public class Strings {

  public static final String AND = "&";
  public static final String ANY = "*";
  public static final String ANY_PATH = "/*";
  public static final String CLOSE_BRACE = "}";
  public static final String CLOSE_BRACKET = "]";
  public static final String CLOSE_PAREN = ")";
  public static final String COLON = ":";
  public static final String COMMA = ",";
  public static final String DASH = "-";
  public static final String DOLLAR = "$";
  public static final String DOT = ".";
  public static final String DOUBLE_DOLLAR = "$$";
  public static final String DOUBLE_QUOTATION_MARK = "\"";
  public static final String EMPTY = "";
  public static final String EQUAL = "=";
  public static final String OCTOTHORP = "#";
  public static final String OPEN_BRACE = "{";
  public static final String OPEN_BRACKET = "[";
  public static final String OPEN_PAREN = "(";
  public static final String PLUS = "+";
  public static final String QUESTION_MARK = "?";
  public static final String SEMICOLON = ";";
  public static final String SLASH = "/";
  public static final String UNDERSCORE = "_";
  public static final String UNKNOWN = "unknown";
  public static final String FILE_PREFIX = "file:";
  public static final String ALL_CLASSPATH_PREFIX = "classpath*:";
  public static final String CLASSPATH_PREFIX = "classpath:";
  public static final String[] EMPTY_ARRAY = new String[0];

  /**
   * 字符串是否有值
   *
   * @param text 字符串
   * @return true为有值
   */
  public static boolean hasText(String text) {
    return text != null && text.trim().length() > 0;
  }

  /**
   * 获取第一个不为null或空字符串的值
   *
   * @param first first value
   * @param second second value
   * @param others other values
   * @return nonEmpty
   */
  public static String firstNonNull(String first, String second, String... others) {
    String str = firstOrNull(first, second, others);
    if (str != null) {
      return str;
    }
    throw Exceptions.fail("no text have non-empty value");
  }


  /**
   * 获取第一个不为null或空字符串的值
   *
   * @param first first value
   * @param second second value
   * @param others other values
   * @return nonEmpty
   */
  public static String firstOrNull(String first, String second, String... others) {
    if (hasText(first)) {
      return first;
    }
    if (hasText(second)) {
      return second;
    }
    if (others != null) {
      for (String str : others) {
        if (hasText(str)) {
          return str;
        }
      }
    }
    return null;
  }

  /**
   * Jaccard 相似度,集合的交集与集合的并集的比例
   *
   * @param a 第一个比对字符串
   * @param b 第二个比对字符串
   * @return float
   */
  public static float jaccard(String a, String b) {
    if (a == null && b == null) {
      return 1f;
    }
    if (a == null || b == null) {
      return 0f;
    }
    Set<Integer> aChar = a.chars().boxed().collect(Collectors.toSet());
    Set<Integer> bChar = b.chars().boxed().collect(Collectors.toSet());
    // 交集数量
    int intersection = MoreObjects.intersection(aChar, bChar).size();
    if (intersection == 0) {
      return 0f;
    }
    // 并集数量
    int union = MoreObjects.union(aChar, bChar).size();
    return ((float) intersection) / (float) union;
  }

  /**
   * Sorensen Dice 相似度系数,集合交集的2倍除以两个集合相加
   *
   * @param a 第一个比对字符串
   * @param b 第二个比对字符串
   * @return float
   */
  public static float sorensenDice(String a, String b) {
    if (a == null && b == null) {
      return 1f;
    }
    if (a == null || b == null) {
      return 0F;
    }
    Set<Integer> aChars = a.chars().boxed().collect(Collectors.toSet());
    Set<Integer> bChars = b.chars().boxed().collect(Collectors.toSet());
    // 交集数量
    int intersect = MoreObjects.intersection(aChars, bChars).size();
    if (intersect == 0) {
      return 0F;
    }
    // 全集，两个集合直接加起来
    int aSize = aChars.size();
    int bSize = bChars.size();
    return (2 * (float) intersect) / ((float) (aSize + bSize));
  }

  /**
   * 莱文斯坦距离,指两个字串之间，由一个转成另一个所需的最少编辑操作次数
   *
   * @param a 第一个比对字符串
   * @param b 第二个比对字符串
   * @return float
   */
  public static float levenshtein(String a, String b) {
    if (a == null && b == null) {
      return 1f;
    }
    if (a == null || b == null) {
      return 0f;
    }
    if (a.isEmpty() && b.isEmpty()) {
      return 1f;
    }
    return 1 - ((float) editDis(a, b) / Math.max(a.length(), b.length()));
  }

  /**
   * 汉明距离,仅用来计算两个等长字符串中不一致的字符个数
   *
   * @param a 第一个比对字符串
   * @param b 第二个比对字符串
   * @return float
   */
  public static float hanming(String a, String b) {
    if (a == null || b == null) {
      return 0f;
    }
    if (a.length() != b.length()) {
      return 0f;
    }

    int disCount = 0;
    for (int i = 0; i < a.length(); i++) {
      if (a.charAt(i) != b.charAt(i)) {
        disCount++;
      }
    }
    return (float) disCount / (float) a.length();
  }

  /**
   * 余弦相似性
   *
   * @param a 第一个比对字符串
   * @param b 第二个比对字符串
   * @return float
   */
  public static float cos(String a, String b) {
    if (a == null || b == null) {
      return 0f;
    }
    Set<Integer> aChar = a.chars().boxed().collect(Collectors.toSet());
    Set<Integer> bChar = b.chars().boxed().collect(Collectors.toSet());

    // 统计字频
    Map<Integer, Integer> aMap = new HashMap<>();
    Map<Integer, Integer> bMap = new HashMap<>();
    for (Integer a1 : aChar) {
      aMap.put(a1, aMap.getOrDefault(a1, 0) + 1);
    }
    for (Integer b1 : bChar) {
      bMap.put(b1, bMap.getOrDefault(b1, 0) + 1);
    }

    // 向量化
    Set<Integer> union = MoreObjects.union(aChar, bChar);
    int[] aVec = new int[union.size()];
    int[] bVec = new int[union.size()];
    List<Integer> collect = new ArrayList<>(union);
    for (int i = 0; i < collect.size(); i++) {
      aVec[i] = aMap.getOrDefault(collect.get(i), 0);
      bVec[i] = bMap.getOrDefault(collect.get(i), 0);
    }

    // 分别计算三个参数
    int p1 = 0;
    for (int i = 0; i < aVec.length; i++) {
      p1 += (aVec[i] * bVec[i]);
    }

    float p2 = 0f;
    for (int i : aVec) {
      p2 += (i * i);
    }
    p2 = (float) Math.sqrt(p2);

    float p3 = 0f;
    for (int i : bVec) {
      p3 += (i * i);
    }
    return ((float) p1) / (p2 * (float) Math.sqrt(p3));
  }

  private static int editDis(String a, String b) {
    int aLen = a.length();
    int bLen = b.length();
    if (aLen == 0 || bLen == 0) {
      return 0;
    }
    int[][] v = new int[aLen + 1][bLen + 1];
    for (int i = 0; i <= aLen; ++i) {
      for (int j = 0; j <= bLen; ++j) {
        if (i == 0) {
          v[i][j] = j;
        } else if (j == 0) {
          v[i][j] = i;
        } else if (a.charAt(i - 1) == b.charAt(j - 1)) {
          v[i][j] = v[i - 1][j - 1];
        } else {
          v[i][j] = 1 + Math.min(v[i - 1][j - 1], Math.min(v[i][j - 1], v[i - 1][j]));
        }
      }
    }
    return v[aLen][bLen];
  }
}
