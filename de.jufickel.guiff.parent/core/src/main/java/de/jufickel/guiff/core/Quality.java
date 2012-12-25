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

/**
 * 
 *
 * @author Juergen Fickel
 * @version 23.12.2012
 */
public interface Quality {
    
    String value();

    enum MP3 implements Quality {
        LOW(64),
        NORMAL(128),
        HIGH(192);
        
        private final String value;

        private MP3(final int intValue) {
            this.value = Integer.toString(intValue);
        }

        @Override
        public String value() {
            return value;
        }
        
    }

}
