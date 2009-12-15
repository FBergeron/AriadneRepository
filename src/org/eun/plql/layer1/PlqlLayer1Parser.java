/* The following code was generated by JFlex 1.4.1 on 12/15/09 9:45 AM */

/*
Copyright (C) 2006  David Massart and Chea Sereyvath, European Schoolnet

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

package org.eun.plql.layer1;
import java.io.*;


/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.4.1
 * on 12/15/09 9:45 AM from the specification file
 * <tt>/Sandbox/eclipse/hmdb/plql2lucene/plql_layer1/src/conf/Layer1.flex</tt>
 */
class PlqlLayer1Parser {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int YYINITIAL = 0;
  public static final int STRING2 = 1;

  /** 
   * Translates characters to character classes
   */
  private static final String ZZ_CMAP_PACKED = 
    "\11\0\1\42\1\1\2\0\1\2\22\0\1\42\1\0\1\5\5\0"+
    "\1\11\1\12\3\0\1\34\1\14\1\4\12\13\1\0\1\3\1\4"+
    "\1\15\1\4\2\0\1\6\1\0\1\32\1\10\11\0\1\7\15\0"+
    "\1\41\4\0\1\31\1\0\1\17\1\16\1\24\1\0\1\25\1\35"+
    "\1\26\2\0\1\20\1\22\1\33\1\21\1\23\1\37\1\40\1\36"+
    "\1\30\3\0\1\27\uff87\0";

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\2\0\1\1\2\2\1\3\1\4\1\1\1\5\1\6"+
    "\1\7\1\10\1\11\7\1\1\12\1\13\1\14\1\13"+
    "\1\1\1\0\1\15\1\16\7\1\1\17\1\20\32\1";

  private static int [] zzUnpackAction() {
    int [] result = new int[63];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\43\0\106\0\106\0\151\0\106\0\214\0\257"+
    "\0\214\0\214\0\322\0\365\0\214\0\u0118\0\u013b\0\u015e"+
    "\0\u0181\0\u01a4\0\u01c7\0\u01ea\0\214\0\214\0\214\0\u020d"+
    "\0\u0230\0\365\0\365\0\106\0\u0253\0\u0276\0\u0299\0\u02bc"+
    "\0\u02df\0\u0302\0\u0325\0\214\0\106\0\u0348\0\u036b\0\u038e"+
    "\0\u03b1\0\u03d4\0\u03f7\0\u041a\0\u043d\0\u0460\0\u0483\0\u04a6"+
    "\0\u04c9\0\u04ec\0\u050f\0\u0532\0\u0555\0\u0578\0\u059b\0\u05be"+
    "\0\u05e1\0\u0604\0\u0627\0\u064a\0\u066d\0\u0690\0\u06b3";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[63];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\3\1\4\1\5\1\6\1\0\1\7\1\10\2\3"+
    "\1\11\1\12\1\13\1\14\1\15\1\16\1\3\1\17"+
    "\1\20\1\21\1\22\2\3\1\23\2\3\1\10\4\3"+
    "\1\24\2\3\1\0\1\25\5\26\1\27\33\26\1\30"+
    "\1\26\4\3\2\0\3\3\2\0\1\3\2\0\23\3"+
    "\2\0\1\3\1\4\2\3\2\0\3\3\2\0\1\3"+
    "\2\0\23\3\45\0\4\3\2\0\1\3\1\31\1\3"+
    "\2\0\1\3\2\0\15\3\1\31\5\3\2\0\4\3"+
    "\2\0\3\3\2\0\1\13\1\32\1\0\23\3\15\0"+
    "\1\33\27\0\4\3\2\0\3\3\2\0\1\3\2\0"+
    "\1\3\1\34\21\3\2\0\4\3\2\0\3\3\2\0"+
    "\1\3\2\0\3\3\1\35\17\3\2\0\4\3\2\0"+
    "\3\3\2\0\1\3\2\0\13\3\1\36\7\3\2\0"+
    "\4\3\2\0\3\3\2\0\1\3\2\0\5\3\1\37"+
    "\1\40\14\3\2\0\4\3\2\0\3\3\2\0\1\3"+
    "\2\0\22\3\1\41\2\0\4\3\2\0\3\3\2\0"+
    "\1\3\2\0\2\3\1\42\20\3\2\0\4\3\2\0"+
    "\3\3\2\0\1\3\2\0\21\3\1\43\1\3\7\0"+
    "\1\44\35\0\4\3\2\0\2\3\1\45\2\0\1\3"+
    "\2\0\1\45\22\3\2\0\4\3\2\0\3\3\2\0"+
    "\1\3\2\0\4\3\1\34\16\3\2\0\4\3\2\0"+
    "\3\3\2\0\1\3\2\0\10\3\1\46\12\3\2\0"+
    "\4\3\2\0\3\3\2\0\1\3\2\0\6\3\1\47"+
    "\14\3\2\0\4\3\2\0\3\3\2\0\1\3\2\0"+
    "\12\3\1\50\10\3\2\0\4\3\2\0\3\3\2\0"+
    "\1\3\2\0\3\3\1\51\17\3\2\0\4\3\2\0"+
    "\3\3\2\0\1\3\2\0\3\3\1\52\17\3\2\0"+
    "\4\3\2\0\3\3\2\0\1\3\2\0\10\3\1\34"+
    "\12\3\2\0\4\3\2\0\3\3\2\0\1\3\2\0"+
    "\16\3\1\53\4\3\2\0\4\3\2\0\3\3\2\0"+
    "\1\3\2\0\7\3\1\34\13\3\2\0\4\3\2\0"+
    "\3\3\2\0\1\3\2\0\13\3\1\54\7\3\2\0"+
    "\4\3\2\0\3\3\2\0\1\3\2\0\12\3\1\55"+
    "\10\3\2\0\4\3\2\0\3\3\2\0\1\3\2\0"+
    "\11\3\1\34\11\3\2\0\4\3\2\0\3\3\2\0"+
    "\1\3\2\0\5\3\1\56\15\3\2\0\4\3\2\0"+
    "\3\3\2\0\1\3\2\0\1\57\22\3\2\0\4\3"+
    "\2\0\3\3\2\0\1\3\2\0\3\3\1\60\17\3"+
    "\2\0\4\3\2\0\3\3\2\0\1\3\2\0\4\3"+
    "\1\61\16\3\2\0\4\3\2\0\3\3\2\0\1\3"+
    "\2\0\13\3\1\62\7\3\2\0\4\3\2\0\3\3"+
    "\2\0\1\3\2\0\1\3\1\63\21\3\2\0\4\3"+
    "\2\0\3\3\2\0\1\3\2\0\17\3\1\34\3\3"+
    "\2\0\4\3\2\0\3\3\2\0\1\3\2\0\12\3"+
    "\1\64\10\3\2\0\4\3\2\0\3\3\2\0\1\3"+
    "\2\0\3\3\1\65\17\3\2\0\4\3\2\0\3\3"+
    "\2\0\1\3\2\0\13\3\1\66\7\3\2\0\4\3"+
    "\2\0\3\3\2\0\1\3\2\0\2\3\1\34\20\3"+
    "\2\0\4\3\2\0\3\3\2\0\1\3\2\0\14\3"+
    "\1\67\6\3\2\0\4\3\2\0\3\3\2\0\1\3"+
    "\2\0\3\3\1\70\17\3\2\0\4\3\2\0\3\3"+
    "\2\0\1\3\2\0\2\3\1\71\20\3\2\0\4\3"+
    "\2\0\3\3\2\0\1\3\2\0\2\3\1\72\20\3"+
    "\2\0\4\3\2\0\3\3\2\0\1\3\2\0\6\3"+
    "\1\73\14\3\2\0\4\3\2\0\3\3\2\0\1\3"+
    "\2\0\1\3\1\74\21\3\2\0\4\3\2\0\3\3"+
    "\2\0\1\3\2\0\12\3\1\75\10\3\2\0\4\3"+
    "\2\0\3\3\2\0\1\3\2\0\10\3\1\76\12\3"+
    "\2\0\4\3\2\0\3\3\2\0\1\3\2\0\3\3"+
    "\1\77\17\3\2\0\4\3\2\0\3\3\2\0\1\3"+
    "\2\0\15\3\1\34\5\3\2\0";

  private static int [] zzUnpackTrans() {
    int [] result = new int[1750];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String ZZ_ERROR_MSG[] = {
    "Unkown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\2\0\4\1\1\11\1\1\2\11\2\1\1\11\7\1"+
    "\3\11\2\1\1\0\11\1\1\11\33\1";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[63];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char zzBuffer[] = new char[ZZ_BUFFERSIZE];

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the textposition at the last state to be included in yytext */
  private int zzPushbackPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /** number of newlines encountered up to the start of the matched text */
  private int yyline;

  /** the number of characters up to the start of the matched text */
  private int yychar;

  /**
   * the number of characters from the last newline up to the start of the 
   * matched text
   */
  private int yycolumn;

  /** 
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean zzEOFDone;

  /* user code: */
  private PlqlLayer1Analyzer yyparser;
  
  private String temp;

  public PlqlLayer1Parser(java.io.Reader r, PlqlLayer1Analyzer yyparser) {
    this(r);
    this.yyparser = yyparser;
  }


  /**
   * Creates a new scanner
   * There is also a java.io.InputStream version of this constructor.
   *
   * @param   in  the java.io.Reader to read input from.
   */
  PlqlLayer1Parser(java.io.Reader in) {
    this.zzReader = in;
  }

  /**
   * Creates a new scanner.
   * There is also java.io.Reader version of this constructor.
   *
   * @param   in  the java.io.Inputstream to read input from.
   */
  PlqlLayer1Parser(java.io.InputStream in) {
    this(new java.io.InputStreamReader(in));
  }

  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    char [] map = new char[0x10000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 108) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }


  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   * 
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {

    /* first: make room (if you can) */
    if (zzStartRead > 0) {
      System.arraycopy(zzBuffer, zzStartRead,
                       zzBuffer, 0,
                       zzEndRead-zzStartRead);

      /* translate stored positions */
      zzEndRead-= zzStartRead;
      zzCurrentPos-= zzStartRead;
      zzMarkedPos-= zzStartRead;
      zzPushbackPos-= zzStartRead;
      zzStartRead = 0;
    }

    /* is the buffer big enough? */
    if (zzCurrentPos >= zzBuffer.length) {
      /* if not: blow it up */
      char newBuffer[] = new char[zzCurrentPos*2];
      System.arraycopy(zzBuffer, 0, newBuffer, 0, zzBuffer.length);
      zzBuffer = newBuffer;
    }

    /* finally: fill the buffer with new input */
    int numRead = zzReader.read(zzBuffer, zzEndRead,
                                            zzBuffer.length-zzEndRead);

    if (numRead < 0) {
      return true;
    }
    else {
      zzEndRead+= numRead;
      return false;
    }
  }

    
  /**
   * Closes the input stream.
   */
  public final void yyclose() throws java.io.IOException {
    zzAtEOF = true;            /* indicate end of file */
    zzEndRead = zzStartRead;  /* invalidate buffer    */

    if (zzReader != null)
      zzReader.close();
  }


  /**
   * Resets the scanner to read from a new input stream.
   * Does not close the old reader.
   *
   * All internal variables are reset, the old input stream 
   * <b>cannot</b> be reused (internal buffer is discarded and lost).
   * Lexical state is set to <tt>ZZ_INITIAL</tt>.
   *
   * @param reader   the new input stream 
   */
  public final void yyreset(java.io.Reader reader) {
    zzReader = reader;
    zzAtBOL  = true;
    zzAtEOF  = false;
    zzEndRead = zzStartRead = 0;
    zzCurrentPos = zzMarkedPos = zzPushbackPos = 0;
    yyline = yychar = yycolumn = 0;
    zzLexicalState = YYINITIAL;
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final String yytext() {
    return new String( zzBuffer, zzStartRead, zzMarkedPos-zzStartRead );
  }


  /**
   * Returns the character at position <tt>pos</tt> from the 
   * matched text. 
   * 
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch. 
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer[zzStartRead+pos];
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of 
   * yypushback(int) and a match-all fallback rule) this method 
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  } 


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Contains user EOF-code, which will be executed exactly once,
   * when the end of file is reached
   */
  private void zzDoEOF() throws java.io.IOException {
    if (!zzEOFDone) {
      zzEOFDone = true;
      yyclose();
    }
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public int yylex() throws java.io.IOException {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    char [] zzBufferL = zzBuffer;
    char [] zzCMapL = ZZ_CMAP;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;
  
      zzState = zzLexicalState;


      zzForAction: {
        while (true) {
    
          if (zzCurrentPosL < zzEndReadL)
            zzInput = zzBufferL[zzCurrentPosL++];
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = zzBufferL[zzCurrentPosL++];
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput] ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          int zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
        case 6: 
          { yyparser.yylval = new PlqlLayer1AnalyzerVal((String)yytext());
					return PlqlLayer1Analyzer.RIGHT_PARENTHESIS;
          }
        case 17: break;
        case 3: 
          { yyparser.yylval = new PlqlLayer1AnalyzerVal((String)yytext());
					return PlqlLayer1Analyzer.SEMICOLON;
          }
        case 18: break;
        case 13: 
          { String tempNumber = yytext().trim();					
						yyparser.yylval = new PlqlLayer1AnalyzerVal(Double.parseDouble(tempNumber));
						//System.out.println(Double.parseDouble(tempNumber));
						return PlqlLayer1Analyzer.REAL;
          }
        case 19: break;
        case 12: 
          { yybegin( YYINITIAL ) ;
					yyparser.yylval = new PlqlLayer1AnalyzerVal("\"" + temp.replaceAll("[:]", "\\\\:") + "\""); 
					return PlqlLayer1Analyzer.CHARSTRING2;
          }
        case 20: break;
        case 8: 
          { yyparser.yylval = new PlqlLayer1AnalyzerVal((String)yytext());
					return PlqlLayer1Analyzer.DOT;
          }
        case 21: break;
        case 16: 
          { yyparser.yylval = new PlqlLayer1AnalyzerVal((String)yytext());
					return PlqlLayer1Analyzer.AND;
          }
        case 22: break;
        case 9: 
          { yyparser.yylval = new PlqlLayer1AnalyzerVal((String)yytext());
					return PlqlLayer1Analyzer.OPERATORS;
          }
        case 23: break;
        case 2: 
          { return PlqlLayer1Analyzer.NL;
          }
        case 24: break;
        case 1: 
          { yyparser.yylval = new PlqlLayer1AnalyzerVal((String)yytext());
					return PlqlLayer1Analyzer.CHARSTRING1;
          }
        case 25: break;
        case 7: 
          { String tempNumber = yytext().trim();					
						yyparser.yylval = new PlqlLayer1AnalyzerVal(Integer.parseInt(tempNumber));
						//System.out.println(Integer.parseInt(tempNumber));
						return PlqlLayer1Analyzer.INTEGER;
          }
        case 26: break;
        case 11: 
          { temp += yytext() ;
          }
        case 27: break;
        case 15: 
          { temp += "\"" ;
          }
        case 28: break;
        case 4: 
          { yybegin( STRING2 ) ; temp = "" ;
          }
        case 29: break;
        case 5: 
          { yyparser.yylval = new PlqlLayer1AnalyzerVal((String)yytext()); 
					return PlqlLayer1Analyzer.LEFT_PARENTHESIS;
          }
        case 30: break;
        case 14: 
          { yyparser.yylval = new PlqlLayer1AnalyzerVal((String)yytext());
					return PlqlLayer1Analyzer.STANDARD;
          }
        case 31: break;
        case 10: 
          { 
          }
        case 32: break;
        default: 
          if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
            zzAtEOF = true;
            zzDoEOF();
              { return 0; }
          } 
          else {
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }


}
