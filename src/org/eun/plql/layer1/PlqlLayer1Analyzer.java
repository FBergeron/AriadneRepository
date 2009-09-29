//### This file created by BYACC 1.8(/Java extension  1.13)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//###           04 Mar 02  -- Yuval Oren  -- improved java performance, added options
//###           14 Mar 02  -- Tomas Hurka -- -d support, static initializer workaround
//### Please send bug reports to tom@hukatronic.cz
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";



package org.eun.plql.layer1;



//#line 1 "/Sandbox/eclipse/hmdb/plql2lucene/plql_layer1/src/conf/Layer1.y"


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

  import java.io.*;
  import java.lang.String;
import java.util.StringTokenizer;  
//#line 41 "PlqlLayer1Analyzer.java"




public class PlqlLayer1Analyzer
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
  if (yydebug)
    System.out.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 500;  //maximum stack size
int statestk[] = new int[YYSTACKSIZE]; //state stack
int stateptr;
int stateptrmax;                     //highest index of stackptr
int statemax;                        //state when highest index reached
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
final void state_push(int state)
{
  try {
		stateptr++;
		statestk[stateptr]=state;
	 }
	 catch (ArrayIndexOutOfBoundsException e) {
     int oldsize = statestk.length;
     int newsize = oldsize * 2;
     int[] newstack = new int[newsize];
     System.arraycopy(statestk,0,newstack,0,oldsize);
     statestk = newstack;
     statestk[stateptr]=state;
  }
}
final int state_pop()
{
  return statestk[stateptr--];
}
final void state_drop(int cnt)
{
  stateptr -= cnt; 
}
final int state_peek(int relative)
{
  return statestk[stateptr-relative];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
final boolean init_stacks()
{
  stateptr = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.out.println("======================");
}


//########## SEMANTIC VALUES ##########
//public class PlqlLayer1AnalyzerVal is defined in PlqlLayer1AnalyzerVal.java


String   yytext;//user variable to return contextual strings
PlqlLayer1AnalyzerVal yyval; //used to return semantic vals from action routines
PlqlLayer1AnalyzerVal yylval;//the 'lval' (result) I got from yylex()
PlqlLayer1AnalyzerVal valstk[];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
void val_init()
{
  valstk=new PlqlLayer1AnalyzerVal[YYSTACKSIZE];
  yyval=new PlqlLayer1AnalyzerVal();
  yylval=new PlqlLayer1AnalyzerVal();
  valptr=-1;
}
void val_push(PlqlLayer1AnalyzerVal val)
{
  if (valptr>=YYSTACKSIZE)
    return;
  valstk[++valptr]=val;
}
PlqlLayer1AnalyzerVal val_pop()
{
  if (valptr<0)
    return new PlqlLayer1AnalyzerVal();
  return valstk[valptr--];
}
void val_drop(int cnt)
{
int ptr;
  ptr=valptr-cnt;
  if (ptr<0)
    return;
  valptr = ptr;
}
PlqlLayer1AnalyzerVal val_peek(int relative)
{
int ptr;
  ptr=valptr-relative;
  if (ptr<0)
    return new PlqlLayer1AnalyzerVal();
  return valstk[ptr];
}
//#### end semantic value section ####
public final static short NL=257;
public final static short AND=258;
public final static short LEFT_PATENTHESIS=259;
public final static short RIGHT_PATENTHESIS=260;
public final static short CHARSTRING1=261;
public final static short CHARSTRING2=262;
public final static short DOT=263;
public final static short OPERATORS=264;
public final static short STANDARD=265;
public final static short INTEGER=266;
public final static short REAL=267;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    1,    1,    1,    1,    2,    3,    3,    3,    3,
    4,    5,    6,    7,    8,    9,   10,   11,   11,   12,
   13,
};
final static short yylen[] = {                            2,
    1,    1,    3,    3,    3,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    3,    1,    3,    1,
    1,
};
final static short yydefred[] = {                         0,
    0,   13,   14,   21,   15,   16,    0,    0,    2,    6,
    7,    8,   11,   12,    9,   10,    0,    0,    0,    0,
    4,    0,   18,    3,    0,    0,    0,    0,   17,
};
final static short yydgoto[] = {                          7,
    8,    9,   10,   11,   12,   13,   14,   15,   16,   24,
   25,    0,   17,
};
final static short yysindex[] = {                      -257,
 -257,    0,    0,    0,    0,    0,    0, -241,    0,    0,
    0,    0,    0,    0,    0,    0, -260, -244, -257, -246,
    0, -241,    0,    0, -243, -246, -255, -240,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,    0,    0,    0,   22,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    1,    0,    0,    0,    0,    0, -239,    0,
};
final static short yygindex[] = {                         0,
   -1,    0,   -3,   -7,    0,    0,    0,    0,    0,    0,
    2,    0,    0,
};
final static int YYTABLESIZE=261;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         18,
    5,    1,   20,    2,    3,    2,    3,    4,    5,    6,
    5,    6,   23,   19,    2,   21,   19,   22,   23,   26,
   27,    1,   26,   29,   19,    0,    0,   28,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    5,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                          1,
    0,  259,  263,  261,  262,  261,  262,  265,  266,  267,
  266,  267,   20,  258,  261,  260,  258,   19,   26,  263,
  264,    0,  263,   27,  264,   -1,   -1,   26,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  260,
};
}
final static short YYFINAL=7;
final static short YYMAXTOKEN=267;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,"NL","AND","LEFT_PATENTHESIS","RIGHT_PATENTHESIS","CHARSTRING1",
"CHARSTRING2","DOT","OPERATORS","STANDARD","INTEGER","REAL",
};
final static String yyrule[] = {
"$accept : plql",
"plql : clause",
"clause : keywordClause",
"clause : standard DOT exactClause",
"clause : LEFT_PATENTHESIS clause RIGHT_PATENTHESIS",
"clause : clause AND clause",
"keywordClause : operand",
"operand : term1",
"operand : term2",
"operand : integer",
"operand : real",
"term1 : charString1",
"term2 : charString2",
"charString1 : CHARSTRING1",
"charString2 : CHARSTRING2",
"integer : INTEGER",
"real : REAL",
"exactClause : path OPERATORS operand",
"path : term1",
"path : path DOT path",
"operator : OPERATORS",
"standard : STANDARD",
};

//#line 162 "/Sandbox/eclipse/hmdb/plql2lucene/plql_layer1/src/conf/Layer1.y"


	private PlqlLayer1Parser lexer;
	private String query ;
	private static boolean DISPLAY_OUTPUT = false;
	
	private int yylex () {
		int yyl_return = -1;
		try {
			yylval = new PlqlLayer1AnalyzerVal(0);
			yyl_return = lexer.yylex();
		}
		catch (IOException e) {
			System.err.println("IO error :"+e);
		}
		return yyl_return;
	} 

	public void yyerror (String error) {
		System.err.println ("Syntax Error\n" + error);
	}

	public PlqlLayer1Analyzer(Reader r) {
		lexer = new PlqlLayer1Parser(r, this);
	}
	

	public void parse(){
	    yyparse() ;
	}
	
    public String getQuery() {
        return query ;
    }	
//#line 304 "PlqlLayer1Analyzer.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}





//The following are now global, to aid in error reporting
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string


//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
{
boolean doaction;

//GAP start
boolean flag = false;
//GAP end

  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    if (yydebug) debug("loop"); 
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = yylex();  //get next token
        if (yydebug) debug(" next yychar:"+yychar);
        //#### ERROR CHECK ####
        if (yychar < 0)    //it it didn't work/error
          {
          yychar = 0;      //change it to default string (no -1!)
          if (yydebug)
            yylexdebug(yystate,yychar);
          }
        }//yychar<0
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
      if ((yyn != 0) && (yyn += yychar) >= 0 &&
          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
        {
        if (yydebug)
          debug("state "+yystate+", shifting to state "+yytable[yyn]);
        //#### NEXT STATE ####
        yystate = yytable[yyn];//we are in a new state
        state_push(yystate);   //save it
        
     // GAP start
		if (yylval.sval.equalsIgnoreCase("="))
			flag = true;
		else if (flag == true) {
			String temp = yylval.sval;
			temp = temp.replaceAll("[:]", "\\\\:");
			yylval = new PlqlLayer1AnalyzerVal(temp);
		}
		// GAP end
        
        val_push(yylval);      //push our lval as the input for next rule
        yychar = -1;           //since we have 'eaten' a token, say we need another
        if (yyerrflag > 0)     //have we recovered an error?
           --yyerrflag;        //give ourselves credit
        doaction=false;        //but don't process yet
        break;   //quit the yyn=0 loop
        }

    yyn = yyrindex[yystate];  //reduce
    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
      {   //we reduced!
      if (yydebug) debug("reduce");
      yyn = yytable[yyn];
      doaction=true; //get ready to execute
      break;         //drop down to actions
      }
    else //ERROR RECOVERY
      {
      if (yyerrflag==0)
        {
        yyerror("syntax error");
        yynerrs++;
        }
      if (yyerrflag < 3) //low error count?
        {
        yyerrflag = 3;
        while (true)   //do until break
          {
          if (stateptr<0)   //check for under & overflow here
            {
            yyerror("stack underflow. aborting...");  //note lower case 's'
            return 1;
            }
          yyn = yysindex[state_peek(0)];
          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
            if (yydebug)
              debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
            yystate = yytable[yyn];
            state_push(yystate);
            val_push(yylval);
            doaction=false;
            break;
            }
          else
            {
            if (yydebug)
              debug("error recovery discarding state "+state_peek(0)+" ");
            if (stateptr<0)   //check for under & overflow here
              {
              yyerror("Stack underflow. aborting...");  //capital 'S'
              return 1;
              }
            state_pop();
            val_pop();
            }
          }
        }
      else            //discard this token
        {
        if (yychar == 0)
          return 1; //yyabort
        if (yydebug)
          {
          yys = null;
          if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          if (yys == null) yys = "illegal-symbol";
          debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
          }
        yychar = -1;  //read another
        }
      }//end error recovery
    }//yyn=0 loop
    if (!doaction)   //any reason not to proceed?
      continue;      //skip action
    yym = yylen[yyn];          //get count of terminals on rhs
    if (yydebug)
      debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 1:
//#line 61 "/Sandbox/eclipse/hmdb/plql2lucene/plql_layer1/src/conf/Layer1.y"
{  	yyval.sval = val_peek(0).sval;
							query = yyval.sval;
						}
break;
case 2:
//#line 66 "/Sandbox/eclipse/hmdb/plql2lucene/plql_layer1/src/conf/Layer1.y"
{
                    if (DISPLAY_OUTPUT) System.out.println("rule number = 1-2.1 : " + val_peek(0).sval);
                    }
break;
case 3:
//#line 69 "/Sandbox/eclipse/hmdb/plql2lucene/plql_layer1/src/conf/Layer1.y"
{	
					val_peek(2).sval = val_peek(2).sval + "." + val_peek(0).sval;	
					if (DISPLAY_OUTPUT) System.out.println("rule number = 1-2.2 : " + val_peek(2).sval);
				}
break;
case 4:
//#line 73 "/Sandbox/eclipse/hmdb/plql2lucene/plql_layer1/src/conf/Layer1.y"
{	
					val_peek(2).sval = " ( " + val_peek(1).sval + " ) "; 
					if (DISPLAY_OUTPUT) System.out.println("rule number = 1-2.3 : " + val_peek(2).sval);
					}
break;
case 5:
//#line 77 "/Sandbox/eclipse/hmdb/plql2lucene/plql_layer1/src/conf/Layer1.y"
{ 	
					val_peek(2).sval = val_peek(2).sval + " AND " + val_peek(0).sval; 
    				if (DISPLAY_OUTPUT) System.out.println("rule number = 1-2.4 : " + val_peek(2).sval);
					}
break;
case 6:
//#line 83 "/Sandbox/eclipse/hmdb/plql2lucene/plql_layer1/src/conf/Layer1.y"
{
                    if (DISPLAY_OUTPUT) System.out.println("rule number = 1-3 : " + val_peek(0).sval);
                    }
break;
case 7:
//#line 89 "/Sandbox/eclipse/hmdb/plql2lucene/plql_layer1/src/conf/Layer1.y"
{
                    if (DISPLAY_OUTPUT) System.out.println("rule number = 1-4.1 : " + val_peek(0).sval);
                    }
break;
case 8:
//#line 92 "/Sandbox/eclipse/hmdb/plql2lucene/plql_layer1/src/conf/Layer1.y"
{
                    if (DISPLAY_OUTPUT) System.out.println("rule number = 1-4.2 : " + val_peek(0).sval);
                    }
break;
case 9:
//#line 95 "/Sandbox/eclipse/hmdb/plql2lucene/plql_layer1/src/conf/Layer1.y"
{
                    if (DISPLAY_OUTPUT) System.out.println("rule number = 1-4.3 : " + val_peek(0).sval);
                    }
break;
case 10:
//#line 98 "/Sandbox/eclipse/hmdb/plql2lucene/plql_layer1/src/conf/Layer1.y"
{
                    if (DISPLAY_OUTPUT) System.out.println("rule number = 1-4.4 : " + val_peek(0).sval);
                    }
break;
case 11:
//#line 104 "/Sandbox/eclipse/hmdb/plql2lucene/plql_layer1/src/conf/Layer1.y"
{
                    if (DISPLAY_OUTPUT) System.out.println("rule number = 1-5 : " + val_peek(0).sval);
                    }
break;
case 12:
//#line 109 "/Sandbox/eclipse/hmdb/plql2lucene/plql_layer1/src/conf/Layer1.y"
{
                    if (DISPLAY_OUTPUT) System.out.println("rule number = 1-6 : " + val_peek(0).sval);
                    }
break;
case 13:
//#line 114 "/Sandbox/eclipse/hmdb/plql2lucene/plql_layer1/src/conf/Layer1.y"
{   
                    if (DISPLAY_OUTPUT) System.out.println("rule number = 1-7 : " + val_peek(0).sval);
					}
break;
case 14:
//#line 119 "/Sandbox/eclipse/hmdb/plql2lucene/plql_layer1/src/conf/Layer1.y"
{	
                    if (DISPLAY_OUTPUT) System.out.println("rule number = 1-8 : " + val_peek(0).sval);
					}
break;
case 15:
//#line 124 "/Sandbox/eclipse/hmdb/plql2lucene/plql_layer1/src/conf/Layer1.y"
{	
                    if (DISPLAY_OUTPUT) System.out.println("rule number = 1-9 : " + val_peek(0).ival);
					}
break;
case 16:
//#line 129 "/Sandbox/eclipse/hmdb/plql2lucene/plql_layer1/src/conf/Layer1.y"
{	
				if (DISPLAY_OUTPUT) System.out.println("rule number = 1-10 : " + val_peek(0).dval);
                }
break;
case 17:
//#line 135 "/Sandbox/eclipse/hmdb/plql2lucene/plql_layer1/src/conf/Layer1.y"
{	
					val_peek(2).sval = val_peek(2).sval.toLowerCase() + ":"  + val_peek(0).sval; 
					if (DISPLAY_OUTPUT) System.out.println("rule number = 1-11 : " + val_peek(2).sval );
					}
break;
case 18:
//#line 141 "/Sandbox/eclipse/hmdb/plql2lucene/plql_layer1/src/conf/Layer1.y"
{
					if (DISPLAY_OUTPUT) System.out.println("rule number = 1-12.1 : " + val_peek(0).sval);
					}
break;
case 19:
//#line 144 "/Sandbox/eclipse/hmdb/plql2lucene/plql_layer1/src/conf/Layer1.y"
{	
					val_peek(2).sval = (val_peek(2).sval + val_peek(1).sval + val_peek(0).sval).toLowerCase(); 
					if (DISPLAY_OUTPUT) System.out.println("rule number = 1-12.2 : " + val_peek(2).sval);
					}
break;
case 20:
//#line 151 "/Sandbox/eclipse/hmdb/plql2lucene/plql_layer1/src/conf/Layer1.y"
{
                    if (DISPLAY_OUTPUT) System.out.println("rule number = 1-13 : " + val_peek(0).sval);
            }
break;
case 21:
//#line 158 "/Sandbox/eclipse/hmdb/plql2lucene/plql_layer1/src/conf/Layer1.y"
{
                    if (DISPLAY_OUTPUT) System.out.println("rule number = 1-14 : " + val_peek(0).sval);
                }
break;
//#line 582 "PlqlLayer1Analyzer.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
    }//switch
    //#### Now let's reduce... ####
    if (yydebug) debug("reduce");
    state_drop(yym);             //we just reduced yylen states
    yystate = state_peek(0);     //get new state
    val_drop(yym);               //corresponding value drop
    yym = yylhs[yyn];            //select next TERMINAL(on lhs)
    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
      {
      if (yydebug) debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = yylex();        //get next character
        if (yychar<0) yychar=0;  //clean, if necessary
        if (yydebug)
          yylexdebug(yystate,yychar);
        }
      if (yychar == 0)          //Good exit (if lex returns 0 ;-)
         break;                 //quit the loop--all DONE
      }//if yystate
    else                        //else not done yet
      {                         //get next state and push, for next yydefred[]
      yyn = yygindex[yym];      //find out where to go
      if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn]; //get new state
      else
        yystate = yydgoto[yym]; //else go to new defred
      if (yydebug) debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
      state_push(yystate);     //going again, so push state & val...
      val_push(yyval);         //for next action
      }
    }//main loop
  return 0;//yyaccept!!
}
//## end of method parse() ######################################



//## run() --- for Thread #######################################
/**
 * A default run method, used for operating this parser
 * object in the background.  It is intended for extending Thread
 * or implementing Runnable.  Turn off with -Jnorun .
 */
public void run()
{
  yyparse();
}
//## end of method run() ########################################



//## Constructors ###############################################
/**
 * Default constructor.  Turn off with -Jnoconstruct .

 */
public PlqlLayer1Analyzer()
{
  //nothing to do
}


/**
 * Create a parser, setting the debug to true or false.
 * @param debugMe true for debugging, false for no debug.
 */
public PlqlLayer1Analyzer(boolean debugMe)
{
  yydebug=debugMe;
}
//###############################################################



}
//################### END OF CLASS ##############################
