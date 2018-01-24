/*
   This is a port of the Swiss Ephemeris Free Edition, Version 2.00.00
   of Astrodienst AG, Switzerland from the original C Code to Java. For
   copyright see the original copyright notices below and additional
   copyright notes in the file named LICENSE, or - if this file is not
   available - the copyright notes at http://www.astro.ch/swisseph/ and
   following. 

   For any questions or comments regarding this port to Java, you should
   ONLY contact me and not Astrodienst, as the Astrodienst AG is not involved
   in this port in any way.

   Thomas Mack, mack@ifis.cs.tu-bs.de, 23rd of April 2001

*/
/* Copyright (C) 1997 - 2008 Astrodienst AG, Switzerland.  All rights reserved.

  License conditions
  ------------------

  This file is part of Swiss Ephemeris.

  Swiss Ephemeris is distributed with NO WARRANTY OF ANY KIND.  No author
  or distributor accepts any responsibility for the consequences of using it,
  or for whether it serves any particular purpose or works at all, unless he
  or she says so in writing.

  Swiss Ephemeris is made available by its authors under a dual licensing
  system. The software developer, who uses any part of Swiss Ephemeris
  in his or her software, must choose between one of the two license models,
  which are
  a) GNU public license version 2 or later
  b) Swiss Ephemeris Professional License

  The choice must be made before the software developer distributes software
  containing parts of Swiss Ephemeris to others, and before any public
  service using the developed software is activated.

  If the developer choses the GNU GPL software license, he or she must fulfill
  the conditions of that license, which includes the obligation to place his
  or her whole software project under the GNU GPL or a compatible license.
  See http://www.gnu.org/licenses/old-licenses/gpl-2.0.html

  If the developer choses the Swiss Ephemeris Professional license,
  he must follow the instructions as found in http://www.astro.com/swisseph/
  and purchase the Swiss Ephemeris Professional Edition from Astrodienst
  and sign the corresponding license contract.

  The License grants you the right to use, copy, modify and redistribute
  Swiss Ephemeris, but only under certain conditions described in the License.
  Among other things, the License requires that the copyright notices and
  this notice be preserved on all copies.

  Authors of the Swiss Ephemeris: Dieter Koch and Alois Treindl

  The authors of Swiss Ephemeris have no control or influence over any of
  the derived works, i.e. over software or services created by other
  programmers which use Swiss Ephemeris functions.

  The names of the authors or of the copyright holder (Astrodienst) must not
  be used for promoting any software, product or service which uses or contains
  the Swiss Ephemeris. This copyright notice is the ONLY place where the
  names of the authors can legally appear, except in cases where they have
  given special permission in writing.

  The trademarks 'Swiss Ephemeris' and 'Swiss Ephemeris inside' may be used
  for promoting such software, products or services.
*/
package swisseph;

import java.io.PrintWriter;
import java.io.StringWriter;


class FileData
		{
  final byte SEI_FILE_NMAXPLAN=50;

  // The error handling and error strings are different from the original C version.
  // If required, one would have to rewrite some code
  String serr_file_damage = "Ephemeris file %s is damaged (0). ";

  String fnam;          /* ephemeris file name */
  int fversion;         /* version number of file */
  String astnam;        /* asteroid name, if asteroid file */
  int sweph_denum;     /* DE number of JPL ephemeris, which this file
                         * is derived from. */
  double tfstart = 1;       /* file may be used from this date */
  double tfend = 0;         /*      through this date          */
  int iflg;             /* byte reorder flag and little/bigendian flag */
  short npl;            /* how many planets in file */
  int ipl[] = new int[SEI_FILE_NMAXPLAN]; /* planet numbers */

  void clearData() {
    int j;
    fnam="";
    fversion=0;
    astnam="";
    sweph_denum=0;
    tfstart=1.0;
    tfend=0.0;
    iflg=0;
    npl=0;
    for(j=0; j<SEI_FILE_NMAXPLAN; j++) { ipl[j]=0; }
  }


  private int label_file_damage(StringBuffer serr, String suberror) {
    if (serr != null) {
      serr.setLength(0);
      if (serr_file_damage.length() + fnam.length() < SwissData.AS_MAXCH) {
        serr.append(serr_file_damage.replaceFirst("%s", fnam));
        serr.append(suberror);
      } else {
        serr.append(serr_file_damage.replaceFirst("%s", fnam)).append(suberror);
      }
    }
    clearData();
    System.out.println(serr);
    return SweConst.ERR;
  }

  /*
   * The following C code (by Rob Warnock rpw3@sgi.com) does CRC-32 in
   * BigEndian/BigEndian byte/bit order. That is, the data is sent most
   * significant byte first, and each of the bits within a byte is sent most
   * significant bit first, as in FDDI. You will need to twiddle with it to do
   * Ethernet CRC, i.e., BigEndian/LittleEndian byte/bit order.
   *
   * The CRCs this code generates agree with the vendor-supplied Verilog models
   * of several of the popular FDDI "MAC" chips.
   */
  /* unsigned long [...] */
  static long crc32_table[]=null;
  /* Initialized first time "crc32()" is called. If you prefer, you can
   * statically initialize it at compile time. [Another exercise.]
   */

  long swi_crc32(/*unsigned???*/ byte[] buf, int len) {
    int pn;
    /*unsigned*/ long crc;
    if (crc32_table==null) {  /* if not already done, */
      init_crc32();   /* build table */
    }
    crc = 0xffffffffL;       /* preload shift register, per CRC-32 spec */
    for (pn = 0; len > 0; ++pn, --len) {
      crc = ((crc << 8)&0xffffffffL) ^ crc32_table[(int)((crc >> 24) ^ ((long)buf[pn]&0xff))];
    }
    return ~crc;            /* transmit complement, per CRC-32 spec */
  }

  /*
   * Build auxiliary table for parallel byte-at-a-time CRC-32.
   */
  static final int CRC32_POLY=0x04c11db7;    /* AUTODIN II, Ethernet, & FDDI */

  void init_crc32() {
    long i, j;
    long c;
    crc32_table = new long[256];
    for (i = 0; i < 256; ++i) {
      for (c = i << 24, j = 8; j > 0; --j) {
        c = (c & 0x80000000L)!=0 ? (c << 1) ^ CRC32_POLY : (c << 1);
      }
      c=c & 0xffffffffL;
      crc32_table[(int)i] = c;
    }
  }





} // Ende der Klasse FileData.


//////////////////////////////////////////////////////////////////////////////
// Anmerkungen: //////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////
// String fnam;     Fuer Ausgaben von Fehlermeldungen und zum
//                  Zwischenspeichern.
// int fversion;    Wird nicht genutzt, aber aus der Datei ausgelesen.
//                  Sollte zugreifbar sein.
// String astnam;   Wird aus der Datei ausgelesen und in "swe_get_planet_name"
//                  zurueckgeliefert.
// int sweph_denum; Wird aus der Datei ausgelesen und in "sweph"
//                  einmal genutzt (4 Bytes uebrigens):
//                  if (fdp->sweph_denum >= 403 && ipl < SEI_ANYBODY) {
//                    swi_IERS_FK5(xp, xp, 1);
//                    [...]
//                  }
// java.io.RandomAccessFile fptr;
// double tfstart;  Beginn und Ende des Zeitraumes, ueber den die Datei
// double tfend;    Daten enthaelt. Wird ausgewertet.
// int iflg;        Enthaelt zwei Flags in Bit 1 und Bit 2: "little endian /
//                  big endian" und "reorder Bytes". sizeof(long), vermutlich
//                  4 Bytes=int;
// short npl;       Wird aus der Datei mit zwei Bytes ausgelesen. Anzahl der
//                  Planeten in der Datei. Aufgrund dieser Information werden
//                  "npl"-mal Konstanten ueber die Planeten aus der Datei
//                  ausgelesen (PlanData p: p.lndx0; p.iflg; p.ncoe; p.rmax;
//                  p.tfstart bis p.dperi; p.refep und FileData.ipl).
// short ipl[];     2 Bytes==int. Offenbar Nummer der Planeten...
