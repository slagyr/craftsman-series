package urls;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class UrlTool {


  /**
   * the singleton instance of the utility package.
   */
  private static UrlTool instance;

  /**
   * DefaultConstructor.
   */
  private UrlTool() {
  }

  /**
   * Gets the singleton instance of the utility package.
   *
   * @return the singleton instance.
   */
  public static UrlTool getInstance() {
    if (instance == null) {
      instance = new UrlTool();
    }
    return instance;
  }

  /**
   * Parses the given name and returns the name elements as List of Strings.
   *
   * @param name the name, that should be parsed.
   * @return the parsed name.
   */
  private static List parseName(final String name) {
    final ArrayList list = new ArrayList();
    final StringTokenizer strTok = new StringTokenizer(name, "/");
    while (strTok.hasMoreElements()) {
      final String s = (String) strTok.nextElement();
      if (s.length() != 0) {
        list.add(s);
      }
    }
    return list;
  }

  /**
   * Transforms the name list back into a single string, separated with "/".
   *
   * @param name  the name list.
   * @param query the (optional) query for the URL.
   * @return the constructed name.
   */
  private static String formatName(final List name, final String query) {
    final StringBuffer b = new StringBuffer();
    final Iterator it = name.iterator();
    while (it.hasNext()) {
      b.append(it.next());
      if (it.hasNext()) {
        b.append("/");
      }
    }
    if (query != null) {
      b.append('?');
      b.append(query);
    }
    return b.toString();
  }

  /**
   * Compares both name lists, and returns the last common index shared
   * between the two lists.
   *
   * @param baseName the name created using the base url.
   * @param urlName  the target url name.
   * @return the number of shared elements.
   */
  private static int startsWithUntil(final List baseName, final List urlName) {
    final int minIdx = Math.min(urlName.size(), baseName.size());
    for (int i = 0; i < minIdx; i++) {
      final String baseToken = (String) baseName.get(i);
      final String urlToken = (String) urlName.get(i);
      if (!baseToken.equals(urlToken)) {
        return i;
      }
    }
    return minIdx;
  }

  /**
   * Creates a relative url by stripping the common parts of the the url.
   *
   * @param url     the to be stripped url
   * @param baseURL the base url, to which the <code>url</code> is relative
   *                to.
   * @return the relative url, or the url unchanged, if there is no relation
   *         beween both URLs.
   */
  public static String createRelativeURL(final URL url, final URL baseURL) {
    boolean sameProtocol = url.getProtocol().equals(baseURL.getProtocol());
    boolean sameHost = url.getHost().equals(baseURL.getHost());
    boolean samePort = url.getPort() == baseURL.getPort();
    if (sameProtocol && sameHost && samePort) {

      // If the URL contains a query, ignore that URL; do not
      // attemp to modify it...
      List urlName = parseName(getPath(url));
      List baseName = parseName(getPath(baseURL));
      String query = getQuery(url);

      if (!isPath(baseURL)) {
        baseName.remove(baseName.size() - 1);
      }

      // if both urls are identical, then return the plain file name...
      if (url.equals(baseURL)) {
        return (String) urlName.get(urlName.size() - 1);
      }

      int commonIndex = startsWithUntil(urlName, baseName);
      if (commonIndex == 0) {
        return url.toExternalForm();
      }

      if (commonIndex == urlName.size()) {
        // correct the base index if there is some weird mapping
        // detected,
        // fi. the file url is fully included in the base url:
        //
        // base: /file/test/funnybase
        // file: /file/test
        //
        // this could be a valid configuration whereever virtual
        // mappings are allowed.
        commonIndex -= 1;
      }

      final ArrayList retval = new ArrayList();
      if (baseName.size() >= urlName.size()) {
        final int levels = baseName.size() - commonIndex;
        for (int i = 0; i < levels; i++) {
          retval.add("..");
        }
      }

      retval.addAll(urlName.subList(commonIndex, urlName.size()));
      return formatName(retval, query);
    }
    return url.toExternalForm();
  }

  /**
   * Returns <code>true</code> if the URL represents a path, and
   * <code>false</code> otherwise.
   *
   * @param baseURL the URL.
   * @return A boolean.
   */
  private static boolean isPath(final URL baseURL) {
    if (getPath(baseURL).endsWith("/")) {
      return true;
    } else if (baseURL.getProtocol().equals("file")) {
      final File f = new File(getPath(baseURL));
      try {
        if (f.isDirectory()) {
          return true;
        }
      }
      catch (SecurityException se) {
        // ignored ...
      }
    }
    return false;
  }

  /**
   * Implements the JDK 1.3 method URL.getPath(). The path is defined
   * as URL.getFile() minus the (optional) query.
   *
   * @param url the URL
   * @return the path
   */
  private static String getQuery(final URL url) {
    final String file = url.getFile();
    final int queryIndex = file.indexOf('?');
    if (queryIndex == -1) {
      return null;
    }
    return file.substring(queryIndex + 1);
  }

  /**
   * Implements the JDK 1.3 method URL.getPath(). The path is defined
   * as URL.getFile() minus the (optional) query.
   *
   * @param url the URL
   * @return the path
   */
  private static String getPath(final URL url) {
    final String file = url.getFile();
    final int queryIndex = file.indexOf('?');
    if (queryIndex == -1) {
      return file;
    }
    return file.substring(0, queryIndex);
  }

  /**
   * Copies the InputStream into the OutputStream, until the end of the stream
   * has been reached. This method uses a buffer of 4096 kbyte.
   *
   * @param in  the inputstream from which to read.
   * @param out the outputstream where the data is written to.
   * @throws IOException if a IOError occurs.
   */
  public void copyStreams(final InputStream in, final OutputStream out)
    throws IOException {
    copyStreams(in, out, 4096);
  }

  /**
   * Copies the InputStream into the OutputStream, until the end of the stream
   * has been reached.
   *
   * @param in         the inputstream from which to read.
   * @param out        the outputstream where the data is written to.
   * @param buffersize the buffer size.
   * @throws IOException if a IOError occurs.
   */
  public void copyStreams(final InputStream in, final OutputStream out,
                          final int buffersize) throws IOException {
    // create a 4kbyte buffer to read the file
    final byte[] bytes = new byte[buffersize];

    // the input stream does not supply accurate available() data
    // the zip entry does not know the size of the data
    int bytesRead = in.read(bytes);
    while (bytesRead > -1) {
      out.write(bytes, 0, bytesRead);
      bytesRead = in.read(bytes);
    }
  }

  /**
   * Copies the contents of the Reader into the Writer, until the end of the
   * stream has been reached. This method uses a buffer of 4096 kbyte.
   *
   * @param in  the reader from which to read.
   * @param out the writer where the data is written to.
   * @throws IOException if a IOError occurs.
   */
  public void copyWriter(final Reader in, final Writer out)
    throws IOException {
    copyWriter(in, out, 4096);
  }

  /**
   * Copies the contents of the Reader into the Writer, until the end of the
   * stream has been reached.
   *
   * @param in         the reader from which to read.
   * @param out        the writer where the data is written to.
   * @param buffersize the buffer size.
   * @throws IOException if a IOError occurs.
   */
  public void copyWriter(final Reader in, final Writer out,
                         final int buffersize)
    throws IOException {
    // create a 4kbyte buffer to read the file
    final char[] bytes = new char[buffersize];

    // the input stream does not supply accurate available() data
    // the zip entry does not know the size of the data
    int bytesRead = in.read(bytes);
    while (bytesRead > -1) {
      out.write(bytes, 0, bytesRead);
      bytesRead = in.read(bytes);
    }
  }

  /**
   * Extracts the file name from the URL.
   *
   * @param url the url.
   * @return the extracted filename.
   */
  public String getFileName(final URL url) {
    final String file = url.getFile();
    final int last = file.lastIndexOf("/");
    if (last < 0) {
      return file;
    }
    return file.substring(last);
  }

  /**
   * Removes the file extension from the given file name.
   *
   * @param file the file name.
   * @return the file name without the file extension.
   */
  public String stripFileExtension(final String file) {
    final int idx = file.lastIndexOf(".");
    // handles unix hidden files and files without an extension.
    if (idx < 1) {
      return file;
    }
    return file.substring(0, idx);
  }

  /**
   * Returns the file extension of the given file name.
   * The returned value will contain the dot.
   *
   * @param file the file name.
   * @return the file extension.
   */
  public String getFileExtension(final String file) {
    final int idx = file.lastIndexOf(".");
    // handles unix hidden files and files without an extension.
    if (idx < 1) {
      return "";
    }
    return file.substring(idx);
  }

  /**
   * Checks, whether the child directory is a subdirectory of the base
   * directory.
   *
   * @param base  the base directory.
   * @param child the suspected child directory.
   * @return true, if the child is a subdirectory of the base directory.
   * @throws IOException if an IOError occured during the test.
   */
  public boolean isSubDirectory(File base, File child)
    throws IOException {
    base = base.getCanonicalFile();
    child = child.getCanonicalFile();

    File parentFile = child;
    while (parentFile != null) {
      if (base.equals(parentFile)) {
        return true;
      }
      parentFile = parentFile.getParentFile();
    }
    return false;
  }
}
