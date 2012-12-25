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
package de.jufickel.guiff.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.Attributes2;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Juergen Fickel
 * @version 24.12.2012
 */
@NotThreadSafe
final class AudioStreamCodecHandler extends DefaultHandler implements StreamCodecExtractor {

    private final List<Codec> audioStreamCodecs = new ArrayList<Codec>();

    private AudioStreamCodecHandler() {
        super();
    }

    public static AudioStreamCodecHandler newInstance() {
        return new AudioStreamCodecHandler();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (isStream(qName)) {
            final Attributes2 extendedAttributes = (Attributes2) attributes;
            if (isAudioStream(extendedAttributes)) {
                addAudioStreamCodecToList(extendedAttributes.getValue("codec_name"));
            }
        }
        super.startElement(uri, localName, qName, attributes);
    }

    private static boolean isStream(final String qName) {
        return "stream".equals(qName);
    }

    private static boolean isAudioStream(final Attributes2 extendedAttributes) {
        return "audio".equals(extendedAttributes.getValue("codec_type"));
    }

    private void addAudioStreamCodecToList(final String codecName) {
        final String codecNameUpper = codecName.toUpperCase();
        audioStreamCodecs.add(tryToFindAppropriateCodec(codecNameUpper));            
    }

    private static Codec tryToFindAppropriateCodec(final String codecNameUpper) {
        try {
            return Codec.valueOf(codecNameUpper);
        } catch (final IllegalArgumentException e) {
            return Codec.OTHER;
        }
    }

    /**
     * @return an unmodifiable List with all audio stream
     *         {@link Codec}s the input file contains.
     * @see de.jufickel.guiff.core.StreamCodecExtractor#getStreamCodecs()
     */
    @Override
    public List<Codec> getStreamCodecs() {
        return Collections.unmodifiableList(audioStreamCodecs);
    }

}