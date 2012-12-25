/**
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <${email}> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and
 * you think this stuff is worth it, you can buy me a beer in return.
 * ${author}.
 *
 * ---------------------------------------------------------------------
 *
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <${email}> schrieb diese Datei. Solange Sie diesen Vermerk nicht
 * entfernen, können Sie mit dem Material machen, was Sie möchten. Wenn
 * wir uns eines Tages treffen und Sie denken, das Material ist es wert,
 * können Sie mir dafür ein Bier ausgeben.
 * ${author}.
 */
package de.jufickel.guiff.swing;

import static java.lang.String.format;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.concurrent.ThreadSafe;
import javax.swing.ImageIcon;

/**
 * @author Juergen Fickel
 * @version 23.12.2012
 */
@ThreadSafe
final class IconFactory {

    private static final String IMAGE_DIR = "/de/jufickel/guiff/swing/images/%s";
    private static final IconFactory INSTANCE = new IconFactory();

    private final Map<String, ImageIcon> imageIconCache;
    private final Lock lock;
    private final Toolkit toolkit;
    private final Class<IconFactory> thisClass;
    
    private IconFactory() {
        super();
        imageIconCache = new HashMap<String, ImageIcon>();
        lock = new ReentrantLock();
        toolkit = Toolkit.getDefaultToolkit();
        thisClass = IconFactory.class;
    }
    
    public static IconFactory getInstance() {
        return INSTANCE;
    }
    
    public ImageIcon smallCloseIcon() {
        return getImageIconFromCacheOrInstantiate("16/system-log-out.png");
    }

    private ImageIcon getImageIconFromCacheOrInstantiate(final String key) {
        lock.lock();
        ImageIcon result = imageIconCache.get(key);
        if (null == result) {
            result = getImageIconFor(key);
            imageIconCache.put(key, result);
        }
        lock.unlock();
        return result;
    }

    private ImageIcon getImageIconFor(final String imageName) {
        final String resourceName = format(IMAGE_DIR, imageName);
        final URL resourceUrl = thisClass.getResource(resourceName);
        final Image image = toolkit.getImage(resourceUrl);
        return new ImageIcon(image);
    }

    public ImageIcon bigCloseIcon() {
        return getImageIconFromCacheOrInstantiate("32/system-log-out.png");
    }

    public ImageIcon smallAboutIcon() {
        return getImageIconFromCacheOrInstantiate("16/dialog-information.png");
    }

    public ImageIcon bigAboutIcon() {
        return getImageIconFromCacheOrInstantiate("32/dialog-information.png");
    }

    public ImageIcon smallOpenDocumentIcon() {
        return getImageIconFromCacheOrInstantiate("16/document-open.png");
    }

    public ImageIcon bigOpenDocumentIcon() {
        return getImageIconFromCacheOrInstantiate("32/document-open.png");
    }

    public ImageIcon smallStartHereIcon() {
        return getImageIconFromCacheOrInstantiate("16/start-here.png");
    }

    public ImageIcon bigStartHereIcon() {
        return getImageIconFromCacheOrInstantiate("32/start-here.png");
    }

}
