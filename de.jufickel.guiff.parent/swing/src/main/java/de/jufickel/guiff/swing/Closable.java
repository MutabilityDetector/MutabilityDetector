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

/**
 * Implementors of this interfaces are classes which wrap Swing GUI
 * elements. Through the herein defined methods the implementor should
 * take the necessary action to close the GUI element (e. g. by 
 * calling {@code setVisible(false)}.
 *
 * @author Juergen Fickel <juergen.fickel@htwg-konstanz.de>
 * @version 11.11.2011
 */
interface Closable {

  /**
   * Close this component.
   */
  void close();

}
